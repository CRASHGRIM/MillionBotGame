import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

public class GameFortune {

    @Getter
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
    private Map<String, ArrayList<String>> userCommands;
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
            userCommands = XMLParcer.parse("src/main/texts/GameCommands.xml");
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
        sendAllExceptActive(activePlayer.getName() + getPhrase("PLAYER_BEGINS_THE_ROUND"));
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
                IOprocessor.sendMes(new Request(request.getUser(), "no such command"));
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
        sendAll(String.format(getPhrase("NEXT_PLAYER_FOR_ALL"), activePlayer.getName()));
        IOprocessor.sendMes(new Request(activePlayer, getPhrase("GAME_RULES")));
    }

    private void win() {
        IOprocessor.sendMes(new Request(activePlayer, getPhrase("VICTORY_FOR_PLAYER")));
        sendAllExceptActive(String.format(getPhrase("VICTORY_FOR_ALL"), activePlayer.getName()));
        isGameFinished = true;
    }

    private void wheelRoll() {
        wheelSector sector = wheel.get(rnd.nextInt(wheel.size()));
        switch (sector){
            case ZERO:
                sendAll(getPhrase("ZERO"));
                nextPlayer();
                break;
            case PRIZE:
                sendAll(getPhrase("PRIZE"));
                IOprocessor.sendMes(new Request(activePlayer, getPhrase("PRIZE_FOR_ACTIVE")));
                activePlayerAnswerStatus = answerStatus.PRIZE;
                break;
            case OPENLETTER:
                sendAll(getPhrase("PLUS"));
                IOprocessor.sendMes(new Request(activePlayer, getPhrase("PLUS_FOR_ACTIVE")));
                activePlayerAnswerStatus = answerStatus.LETTEROPENING;
                break;
            case BANKRUPT:
                sendAll(getPhrase("BANKRUPT"));
                activePlayer.bankrupt();
                nextPlayer();
                break;
            case POINTS:
                currentPoints = (rnd.nextInt(5)+1)*100;
                sendAll(String.format(getPhrase("POINTS"), currentPoints));
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

    private boolean checkPhrase(String situation, String input)
    {
        try {
            if (phrases.get(situation).contains(input))
                return true;
            return false;
        }
        catch (Exception e)
        {
            return false;
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
        if (checkPhrase("LETTER_GUESSING", phrase.toLowerCase())) {
            IOprocessor.sendMes(new Request(activePlayer, getPhrase("NAME_A_LETTER")));
            activePlayerAnswerStatus = answerStatus.LETTER;
            wheelRoll();
            return;
        }
        if (checkPhrase("WORD_GUESSING", phrase.toLowerCase())) {
            IOprocessor.sendMes(new Request(activePlayer, getPhrase("NAME_A_WORD")));
            activePlayerAnswerStatus = answerStatus.WORD;
            return;
        }
        IOprocessor.sendMes(new Request(activePlayer, getPhrase("GAME_RULES")));
    }

    private void processPrize(String phrase)
    {
        if (checkPhrase("PRIZE_CHOOSING", phrase.toLowerCase())) {
            int prizeScore = rnd.nextInt(20)*10;
            sendAllExceptActive(String.format(getPhrase("PRIZE_PRIZE_FOR_ALL"), activePlayer.getName(), prizeScore));
            IOprocessor.sendMes(new Request(activePlayer, String.format(getPhrase("PRIZE_PRIZE_FOR_ACTIVE"), prizeScore)));
            activePlayer.addScore(prizeScore);
            nextPlayer();
            return;
        }
        if (checkPhrase("MONEY_CHOOSING", phrase.toLowerCase())) {
            sendAllExceptActive(String.format(getPhrase("PRIZE_MONEY_FOR_ALL"), activePlayer.getName()));
            activePlayer.addScore(100);
            IOprocessor.sendMes(new Request(activePlayer, getPhrase("PRIZE_MONEY_FOR_ACTIVE")+"100"));
            nextPlayer();
            return;
        }
        if (checkPhrase("GAME_CONTINUE_CHOOSING", phrase.toLowerCase()))
        {
            sendAllExceptActive(String.format(getPhrase("PRIZE_LETTER_FOR_ALL"), activePlayer.getName()));
            IOprocessor.sendMes(new Request(activePlayer, getPhrase("PRIZE_LETTER_FOR_ACTIVE")));
            activePlayerAnswerStatus = answerStatus.LETTER;
            currentPoints = 500;
            return;
        }
        IOprocessor.sendMes(new Request(activePlayer, getPhrase("PRIZE_WRONG_INPUT")));
    }


    enum answerStatus {LETTER, WORD, PRIZE, LETTEROPENING, OTHER}
    enum wheelSector {OPENLETTER, BANKRUPT, ZERO, PRIZE, POINTS}
}
