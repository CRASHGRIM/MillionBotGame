import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.*;

public class GameFortune {

    private User activePlayer;
    private int activePlayerIndex;
    private ArrayList<User> players;
    @Getter
    private Question question;
    @Getter
    private StringBuilder currentWord;
    @Getter
    private IOMultiplatformProcessor IOprocessor;
    private answerStatus activePlayerAnswerStatus;
    private List<Integer> wheel;
    private Random rnd;
    private Integer currentWheelSectorIndex;
    private TechnicalCommands techComm;
    private List<Question> questionList;
    @Getter
    private boolean isGameFinished = false;
    private Map<String, ArrayList<String>> phrases;

    GameFortune(ArrayList<User> players, IOMultiplatformProcessor ioProcessor) {
        this.players = players;
        this.IOprocessor = ioProcessor;
        this.questionList = FileUtils.questionsFileToQuestionsList(Config.FILE_NAME_QUESTIONS_TXT);
        this.rnd = new Random();
        question = questionList.get(rnd.nextInt(questionList.size()));
        activePlayerIndex = 0;
        activePlayer = this.players.get(activePlayerIndex);
        activePlayerAnswerStatus = answerStatus.OTHER;
        currentWord = new StringBuilder();
        currentWord.append(StringUtils.repeat("-", question.getAnswer().length()));
        wheel = Arrays.asList(100, 50, 0); //List не исменяемый. Чтобы что то поменять, нужно переделать в ArrayList
        techComm = new TechnicalCommands(this);
        try {
            phrases = XMLParcer.parse("src/main/texts/TestMy.xml");
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    void start() {
        sendAll(getPhrase("GREETINGS_START_GAME"));
        sendAll(getPhrase("TASK_IS:"));
        sendAll(question.getQuestion());
        sendAll(currentWord.toString());
        sendAll(activePlayer.getId().toString() + getPhrase("PLAYER_BEGINS_THE_ROUND"));
        IOprocessor.sendMes(new Request(activePlayer, getPhrase("GAME_RULES")));
    }

    private void sendAll(String message) {
        for (User player : players) {
            IOprocessor.sendMes(new Request(player, message));
        }
    }


    void processRequest(Request request) {
        String userMessage = request.getMessage();

        if (userMessage.startsWith("!")) {
            if (techComm.listOfCommands.containsKey(userMessage))
                techComm.listOfCommands.get(userMessage).execute(request.getUser());
            else
                IOprocessor.sendMes(new Request(request.getUser(), "команда отсутствует"));
            return;
        }

        if (!request.getUser().getId().toString().equals(activePlayer.getId().toString())) {
            return;
        }

        switch (activePlayerAnswerStatus) {
            case LETTER:
                if (userMessage.length() > 1) {
                    IOprocessor.sendMes(new Request(activePlayer, getPhrase("NOT_A_LETTER")));
                    break;
                }
                if (currentWord.indexOf(userMessage) != -1) {
                    IOprocessor.sendMes(new Request(activePlayer, getPhrase("LETTER_THAT_ALREADY_WAS")));
                    break;
                }
                if (question.getAnswer().toLowerCase().contains(userMessage.toLowerCase())) {
                    IOprocessor.sendMes(new Request(activePlayer, getPhrase("GUESSING_A_LETTER_FOR_PLAYER")));
                    IOprocessor.sendMes(new Request(activePlayer, getPhrase("GAME_RULES")));
                    sendAll(activePlayer.getId().toString() + " угадывает букву " + userMessage);
                    activePlayer.addScore(wheel.get(currentWheelSectorIndex));
                    for (int i = 0; i < currentWord.length(); i++)
                        if (question.getAnswer().charAt(i) == userMessage.charAt(0))
                            currentWord.replace(i, i + 1, userMessage);
                    sendAll(currentWord.toString());
                    if (currentWord.toString().toLowerCase().equals(question.getAnswer().toLowerCase()))
                        win();
                    activePlayerAnswerStatus = answerStatus.OTHER;
                    break;
                }
                IOprocessor.sendMes(new Request(activePlayer, "А такой буквы тут нет"));
                sendAll(activePlayer.getId().toString() + " назвал букву " + userMessage + " и не угадал");
                nextPlayer();
                break;
            case WORD:
                if (userMessage.toLowerCase().equals(question.getAnswer().toLowerCase())) {
                    IOprocessor.sendMes(new Request(activePlayer, getPhrase("GUESSING_RIGHT_WORD")));
                    sendAll(activePlayer.getId().toString() + " угадал слово \"" + question.getAnswer() + "\"");
                    win();
                } else {
                    IOprocessor.sendMes(new Request(activePlayer, getPhrase("GUESSING_WRONG_WORD")));
                    sendAll(activePlayer.getId().toString() + " не угадал слово");
                    nextPlayer();
                }
                break;
            case OTHER:
                if (userMessage.toLowerCase().equals("буква")) {
                    wheelRoll();
                    IOprocessor.sendMes(new Request(activePlayer, getPhrase("NAME_A_LETTER")));
                    activePlayerAnswerStatus = answerStatus.LETTER;
                    break;
                }
                if (userMessage.toLowerCase().equals("слово")) {
                    IOprocessor.sendMes(new Request(activePlayer, getPhrase("NAME_A_WORD")));
                    activePlayerAnswerStatus = answerStatus.WORD;
                    break;
                }
                IOprocessor.sendMes(new Request(activePlayer, "Скажите \"буква\" или \"слово\""));
                break;


        }
    }

    private void nextPlayer() {
        activePlayerIndex = (activePlayerIndex+ 1) % players.size();
        activePlayer = players.get(activePlayerIndex);
        activePlayerAnswerStatus = answerStatus.OTHER;
        sendAll("В игру вступает " + activePlayer.getId().toString());
        IOprocessor.sendMes(new Request(activePlayer, getPhrase("GAME_RULES")));
    }

    private void win() {
        sendAll(activePlayer.getId().toString() + " выиграл!!!");// и вот здесь надо как то сказать main что типа игра закончилась (мы с лехой делали флаги которые отсылаются в обработчик)
        isGameFinished = true;
    }

    private void wheelRoll() {
        sendAll(getPhrase("ROLL"));
        currentWheelSectorIndex = rnd.nextInt(wheel.size());
        sendAll(wheel.get(currentWheelSectorIndex).toString() + " на барабане");
    }

    private String getPhrase(String situation)
    {
        try {
            return phrases.get(situation).get(rnd.nextInt(phrases.get(situation).size()));
        }
        catch (Exception e)
        {
            return situation;
        }
    }

    enum answerStatus {LETTER, WORD, OTHER}
}
