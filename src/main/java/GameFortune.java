import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

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
    private List<wheelSector> wheel;
    private Random rnd;
    private TechnicalCommands techComm;
    private List<Question> questionList;
    @Getter
    private boolean isGameFinished = false;
    private Map<String, ArrayList<String>> phrases;
    private int currentPoints;

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
        generateWheel();
        techComm = new TechnicalCommands(this);
        try {
            phrases = XMLParcer.parse("src/main/texts/TestMy.xml");
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    void generateWheel()
    {
        this.wheel = new ArrayList<wheelSector>();
        for (int i=0;i<10;i++) {
            wheel.add(wheelSector.POINTS);
        }
        for (int i=0;i<2;i++) {
            wheel.add(wheelSector.ZERO);
        }
        wheel.add(wheelSector.OPENLETTER);
        wheel.add(wheelSector.PRIZE);
        wheel.add(wheelSector.BANKRUPT);
    }

    void start() {
        sendAll(getPhrase("GREETINGS_START_GAME"));
        sendAll(getPhrase("TASK_IS:"));
        sendAll(question.getQuestion());
        sendAll(currentWord.toString());
        sendAll(activePlayer.getName() + getPhrase("PLAYER_BEGINS_THE_ROUND"));
        IOprocessor.sendMes(new Request(activePlayer, getPhrase("GAME_RULES")));
    }

    private void sendAll(String message) {
        for (User player : players) {
            IOprocessor.sendMes(new Request(player, message));
        }
    }


    void processRequest(Request request) {
        System.out.println(request);
        String userMessage = request.getMessage();

        if (userMessage.startsWith("!")) {
            if (techComm.listOfCommands.containsKey(userMessage))
                techComm.listOfCommands.get(userMessage).execute(request.getUser());
            else
                IOprocessor.sendMes(new Request(request.getUser(), "команда отсутствует"));
            return;
        }
        if (request.getUserID()!=activePlayer.getId())// здесь вроде норм но почему то я парсил обе строки к стрингу надо потестить с телегой
        //if (!request.getUser().getId().toString().equals(activePlayer.getId().toString()))
        {
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
                    sendAll(String.format(getPhrase("GUESSING_A_LETTER_FOR_ALL"), activePlayer.getName(), userMessage));
                    activePlayer.addScore(currentPoints);
                    for (int i = 0; i < currentWord.length(); i++)
                        if (question.getAnswer().charAt(i) == userMessage.charAt(0))
                            currentWord.replace(i, i + 1, userMessage);
                    sendAll(currentWord.toString());
                    if (currentWord.toString().toLowerCase().equals(question.getAnswer().toLowerCase()))
                        win();
                    activePlayerAnswerStatus = answerStatus.OTHER;
                    break;
                }
                IOprocessor.sendMes(new Request(activePlayer, getPhrase("GUESSING_WRONG_LETTER_FOR_PLAYER")));
                sendAll(String.format(getPhrase("GUESSING_WRONG_LETTER_FOR_ALL"), activePlayer.getName(), userMessage));
                nextPlayer();
                break;
            case WORD:
                if (userMessage.toLowerCase().equals(question.getAnswer().toLowerCase())) {
                    IOprocessor.sendMes(new Request(activePlayer, getPhrase("GUESSING_RIGHT_WORD_FOR_PLAYER")));
                    sendAll(String.format(getPhrase("GUESSING_RIGHT_WORD_FOR_ALL"), activePlayer.getName(), question.getAnswer()));
                    win();
                } else {
                    IOprocessor.sendMes(new Request(activePlayer, getPhrase("GUESSING_WRONG_WORD_FOR_PLAYER")));
                    sendAll(String.format(getPhrase("GUESSING_WRONG_WORD_FOR_ALL"), activePlayer.getName(), userMessage));
                    nextPlayer();
                }
                break;
            case PRIZE:
                if (userMessage.toLowerCase().equals("приз")) {
                    sendAll("игрок выбрал приз");// здесь надо сделать чтобы был выбор приз или деньги и подгружать призы из списка
                    nextPlayer();
                    break;
                }
                if (userMessage.toLowerCase().equals("деньги")) {
                    sendAll("игрок выбрал деньги");
                    activePlayer.addScore(100);
                    nextPlayer();
                    break;
                }
                if (userMessage.toLowerCase().equals("играем"))
                {
                    sendAll("игрок решил продолжить игру");
                    activePlayerAnswerStatus = answerStatus.LETTER;
                    currentPoints = 500;
                    break;
                }
                sendAll("что то я вас не понял");
                break;
            case LETTEROPENING:
                int letterIndex = 0;
                try
                {
                    letterIndex = Integer.parseInt(userMessage);
                    letterIndex--;
                }
                catch (NumberFormatException e)
                {
                    IOprocessor.sendMes(new Request(activePlayer, getPhrase("LETTER_OPENING_NOT_A_NUMBER")));
                    break;
                }
                if (letterIndex>=currentWord.length()) {// здесь возможно >
                    IOprocessor.sendMes(new Request(activePlayer, getPhrase("LETTER_OPENING_TOO_BIG")));
                    break;
                }
                if (currentWord.charAt(letterIndex)=='-')
                {
                    for (int i = 0; i < currentWord.length(); i++)
                        if (question.getAnswer().charAt(i) == question.getAnswer().charAt(letterIndex))
                            currentWord.replace(i, i + 1, Character.toString(question.getAnswer().charAt(letterIndex)));
                    sendAll(currentWord.toString());
                    if (currentWord.toString().toLowerCase().equals(question.getAnswer().toLowerCase()))
                        win();
                    activePlayerAnswerStatus = answerStatus.OTHER;
                    break;
                }
                IOprocessor.sendMes(new Request(activePlayer, getPhrase("LETTER_OPENING_ALREADY_OPENED")));
                break;
            case OTHER:
                if (userMessage.toLowerCase().equals("буква")) {
                    IOprocessor.sendMes(new Request(activePlayer, getPhrase("NAME_A_LETTER")));
                    activePlayerAnswerStatus = answerStatus.LETTER;
                    wheelRoll();
                    break;
                }
                if (userMessage.toLowerCase().equals("слово")) {
                    IOprocessor.sendMes(new Request(activePlayer, getPhrase("NAME_A_WORD")));
                    activePlayerAnswerStatus = answerStatus.WORD;
                    break;
                }
                IOprocessor.sendMes(new Request(activePlayer, getPhrase("GAME_RULES")));
                break;


        }
    }

    private void nextPlayer() {
        activePlayerIndex = (activePlayerIndex+ 1) % players.size();
        activePlayer = players.get(activePlayerIndex);
        activePlayerAnswerStatus = answerStatus.OTHER;
        sendAll("В игру вступает " + activePlayer.getName());
        IOprocessor.sendMes(new Request(activePlayer, getPhrase("GAME_RULES")));
    }

    private void win() {
        IOprocessor.sendMes(new Request(activePlayer, getPhrase("VICTORY_FOR_PLAYER")));
        sendAll(String.format(getPhrase("VICTORY_FOR_ALL"), activePlayer.getName()));
        isGameFinished = true;
    }

    private void wheelRoll() {
        wheelSector sector = wheel.get(rnd.nextInt(wheel.size()));
        switch (sector){
            case ZERO:
                sendAll("ноль на барабане, следующий игрок");
                nextPlayer();
                break;
            case PRIZE:
                sendAll("сектор приз на барабане");
                IOprocessor.sendMes(new Request(activePlayer, "приз, деньги, играем?"));
                activePlayerAnswerStatus = answerStatus.PRIZE;
                break;
            case OPENLETTER:
                sendAll("сектор плюс на барабане");
                sendAll("скажите номер буквы которую хотите открыть");
                activePlayerAnswerStatus = answerStatus.LETTEROPENING;
                break;
            case BANKRUPT:
                sendAll("а вы банкрот");
                activePlayer.bankrupt();
                nextPlayer();
                break;
            case POINTS:
                currentPoints = (rnd.nextInt(5)+1)*100;
                sendAll("points "+currentPoints);
                break;
        }
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

    enum answerStatus {LETTER, WORD, PRIZE, LETTEROPENING, OTHER}
    enum wheelSector {OPENLETTER, BANKRUPT, ZERO, PRIZE, POINTS}
}
