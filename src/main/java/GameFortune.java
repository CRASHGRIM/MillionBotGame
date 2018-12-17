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

    private void sendAllExceptActive(String message) {
        for (User player : players) {
            if (player.getId()!=activePlayer.getId())
                IOprocessor.sendMes(new Request(player, message));
        }
    }


    void processRequest(Request request) {
        String userMessage = request.getMessage().toLowerCase();

        if (userMessage.startsWith("!")) {
            if (techComm.listOfCommands.containsKey(userMessage))
                techComm.listOfCommands.get(userMessage).execute(request.getUser());
            else
                IOprocessor.sendMes(new Request(request.getUser(), "команда отсутствует"));
            return;
        }
        if (request.getUserID()!=activePlayer.getId())
        {
            return;
        }
        switch (activePlayerAnswerStatus) {
            case LETTER:
                processLetterGuessing(userMessage);
                break;
            case WORD:
                processWordGuessing(userMessage);
                break;
            case PRIZE:
                //ToDo команды в XML
                processPrize(userMessage);
                break;
            case LETTEROPENING:
                processLetterOpening(userMessage);
                break;
            case OTHER:
                processOther(userMessage);
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

    private void processLetterGuessing(String phrase)
    {
        if (phrase.length() > 1) {
            IOprocessor.sendMes(new Request(activePlayer, getPhrase("NOT_A_LETTER")));
            return;
        }
        if (currentWord.indexOf(phrase) != -1) {
            IOprocessor.sendMes(new Request(activePlayer, getPhrase("LETTER_THAT_ALREADY_WAS")));
            return;
        }
        if (question.getAnswer().contains(phrase)) {
            IOprocessor.sendMes(new Request(activePlayer, getPhrase("GUESSING_A_LETTER_FOR_PLAYER")));
            sendAllExceptActive(String.format(getPhrase("GUESSING_A_LETTER_FOR_ALL"), activePlayer.getName(), phrase));
            IOprocessor.sendMes(new Request(activePlayer, getPhrase("GAME_RULES")));
            activePlayer.addScore(currentPoints);
            for (int i = 0; i < currentWord.length(); i++)
                if (question.getAnswer().charAt(i) == phrase.charAt(0))
                    currentWord.replace(i, i + 1, phrase);
            sendAll(currentWord.toString());
            if (currentWord.toString().toLowerCase().equals(question.getAnswer().toLowerCase()))
                win();
            activePlayerAnswerStatus = answerStatus.OTHER;
            return;
        }
        IOprocessor.sendMes(new Request(activePlayer, getPhrase("GUESSING_WRONG_LETTER_FOR_PLAYER")));
        sendAllExceptActive(String.format(getPhrase("GUESSING_WRONG_LETTER_FOR_ALL"), activePlayer.getName(), phrase));
        nextPlayer();
    }

    private void processWordGuessing(String phrase)
    {
        if (phrase.equals(question.getAnswer())) {
            IOprocessor.sendMes(new Request(activePlayer, getPhrase("GUESSING_RIGHT_WORD_FOR_PLAYER")));
            sendAllExceptActive(String.format(getPhrase("GUESSING_RIGHT_WORD_FOR_ALL"), activePlayer.getName(), question.getAnswer()));
            win();
        } else {
            IOprocessor.sendMes(new Request(activePlayer, getPhrase("GUESSING_WRONG_WORD_FOR_PLAYER")));
            sendAllExceptActive(String.format(getPhrase("GUESSING_WRONG_WORD_FOR_ALL"), activePlayer.getName(), phrase));
            nextPlayer();
        }
    }

    private void processLetterOpening(String phrase)
    {
        int letterIndex = 0;
        try
        {
            letterIndex = Integer.parseInt(phrase);
            letterIndex--;
        }
        catch (NumberFormatException e)
        {
            IOprocessor.sendMes(new Request(activePlayer, getPhrase("LETTER_OPENING_NOT_A_NUMBER")));
            return;
        }
        if (letterIndex>=currentWord.length()) {// здесь возможно >
            IOprocessor.sendMes(new Request(activePlayer, getPhrase("LETTER_OPENING_TOO_BIG")));
            return;
        }
        if (currentWord.charAt(letterIndex)!='-') {
            IOprocessor.sendMes(new Request(activePlayer, getPhrase("LETTER_OPENING_ALREADY_OPENED")));
            return;
        }
        for (int i = 0; i < currentWord.length(); i++) {
            if (question.getAnswer().charAt(i) == question.getAnswer().charAt(letterIndex))
                currentWord.replace(i, i + 1, Character.toString(question.getAnswer().charAt(letterIndex)));
        }
        sendAll(currentWord.toString());
        if (currentWord.toString().toLowerCase().equals(question.getAnswer().toLowerCase()))
            win();
        activePlayerAnswerStatus = answerStatus.OTHER;
    }

    private void processOther(String phrase)
    {
        if (phrase.toLowerCase().equals("буква")) {
            IOprocessor.sendMes(new Request(activePlayer, getPhrase("NAME_A_LETTER")));
            activePlayerAnswerStatus = answerStatus.LETTER;
            wheelRoll();
            return;
        }
        if (phrase.toLowerCase().equals("слово")) {
            IOprocessor.sendMes(new Request(activePlayer, getPhrase("NAME_A_WORD")));
            activePlayerAnswerStatus = answerStatus.WORD;
            return;
        }
        IOprocessor.sendMes(new Request(activePlayer, getPhrase("GAME_RULES")));
    }

    private void processPrize(String phrase)
    {
        if (phrase.toLowerCase().equals("приз")) {
            sendAllExceptActive("игрок выбрал приз");// здесь надо сделать чтобы был выбор приз или деньги и подгружать призы из списка
            nextPlayer();
            return;
        }
        if (phrase.equals("деньги")) {
            sendAllExceptActive("игрок выбрал деньги");
            activePlayer.addScore(100);
            nextPlayer();
            return;
        }
        if (phrase.equals("играем"))
        {
            sendAllExceptActive("игрок решил продолжить игру");
            activePlayerAnswerStatus = answerStatus.LETTER;
            currentPoints = 500;
            return;
        }
        IOprocessor.sendMes(new Request(activePlayer, "что то я вас не понял"));
    }


    enum answerStatus {LETTER, WORD, PRIZE, LETTEROPENING, OTHER}
    enum wheelSector {OPENLETTER, BANKRUPT, ZERO, PRIZE, POINTS}
}
