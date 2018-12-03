import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameFortune {

    private User activePlayer;
    private int activePlayerIndex;
    private ArrayList<User> players;
    @Getter
    private Question question2;
    @Getter
    private StringBuilder currentWord;
    @Getter
    private IOMultiplatformProcessor processor;
    private answerStatus activePlayerAnswerStatus;
    private ArrayList<Integer> wheel;
    private Random rnd;
    private Integer currentWheelSectorIndex;
    private TechnicalCommands techComm;
    private List<Question> questionList;

    GameFortune(ArrayList<User> players, IOMultiplatformProcessor processor){
        this.players = players;
        this.processor = processor;
        this.questionList = new Parser("C:\\Users\\danii\\IdeaProjects\\MillionBotGame\\src\\main\\java\\Questions.txt").toListQuestions();//хз почему но relative path не работает пофикси плез
        this.rnd = new Random();
        question2 = questionList.get(rnd.nextInt(questionList.size()));
        activePlayer = this.players.get(0);
        activePlayerIndex = 0;
        activePlayerAnswerStatus = answerStatus.OTHER;
        currentWord = new StringBuilder();
        for(int i = 0; i<question2.getAnswer().length(); i++)// здесь возможно как то покрасивее можно сделать
            currentWord.append("-");
        wheel = new ArrayList<Integer>();
        wheel.add(100);
        wheel.add(50);
        wheel.add(0);// это надо сделать покрасивее (возможно сделаю словарь лямбд)
        techComm = new TechnicalCommands(this);

    }

    public void start()
    {
        sendAll("добро пожаловать на капиталшоу поле чудес!!!");
        sendAll("вот задание:");
        sendAll(question2.getQuestion());
        sendAll(currentWord.toString());
        sendAll(activePlayer.getId().toString()+" начинает игру!!!");
        sendAll("чтобы назвать букву скажите буква, чтобы назвать слово скажите слово");
    }

    private void sendAll(String message)
    {
        for (User player : players)
        {
            processor.sendMes(new Request(player,  message));
        }
    }


    public void next(Request request)
    {
        String answer = request.getMessage();
        if (answer.startsWith("!"))
        {
            if (techComm.listOfCommands.containsKey(answer))
                techComm.listOfCommands.get(answer).execute(request.getUser());
            else
                processor.sendMes(new Request(request.getUser(), "команда отсутствует"));
            return;
        }
        if (!request.getUser().getId().toString().equals(activePlayer.getId().toString())) {
            return;
        }
        switch (activePlayerAnswerStatus) {
            case LETTER:
                if (answer.length()>1) {
                    processor.sendMes(new Request(activePlayer, "кажется у вас не буква"));
                    break;
                }
                if (currentWord.indexOf(answer)!=-1)
                {
                    processor.sendMes(new Request(activePlayer, "а такая буква уже была"));
                    break;
                }
                if (question2.getAnswer().contains(answer))
                {
                    processor.sendMes(new Request(activePlayer, "да, вы угадали букву"));
                    sendAll(activePlayer.getId().toString()+" угадывает букву "+answer);
                    activePlayer.scorePoints(wheel.get(currentWheelSectorIndex));
                    for (int i = 0; i<currentWord.length(); i++)
                        if (question2.getAnswer().charAt(i) == answer.charAt(0))
                            currentWord.replace(i, i+1, answer);
                    sendAll(currentWord.toString());
                    if (currentWord.toString().equals(question2.getAnswer()))
                        win();
                    activePlayerAnswerStatus = answerStatus.OTHER;
                    break;
                }
                processor.sendMes(new Request(activePlayer, "а такой буквы тут нет"));
                sendAll(activePlayer.getId().toString() + "назвал букву "+answer+" и не угадал");
                nextPlayer();
                break;
            case WORD:
                if (answer.equals(question2.getAnswer()))
                {
                    processor.sendMes(new Request(activePlayer, "да, вы угадали слово"));
                    sendAll(activePlayer.getId().toString() + " угадал слово " + question2.getAnswer());
                    win();
                }
                else {
                    processor.sendMes(new Request(activePlayer, "нет, это не то слово"));
                    sendAll(activePlayer.getId().toString() + " не угадал слово");
                    nextPlayer();
                }
                break;
            case OTHER:
                if (answer.equals("буква")) {
                    wheelRoll();
                    processor.sendMes(new Request(activePlayer, "называйте букву"));
                    activePlayerAnswerStatus = answerStatus.LETTER;
                    break;
                }
                if (answer.equals("слово")){
                    processor.sendMes(new Request(activePlayer, "называйте слово"));
                    activePlayerAnswerStatus = answerStatus.WORD;
                    break;
                }
                processor.sendMes(new Request(activePlayer, "что то я не понял вас"));
                break;


        }
    }

    private void nextPlayer()
    {
        activePlayerIndex = (activePlayerIndex+1)%players.size();
        activePlayer = players.get(activePlayerIndex);
        activePlayerAnswerStatus = answerStatus.OTHER;
        sendAll("В игру вступает "+activePlayer.getId().toString());
        processor.sendMes(new Request(activePlayer, "чтобы назвать букву скажите буква, чтобы назвать слово скажите слово"));
    }

    private void win()
    {
        sendAll(activePlayer.getId().toString()+" выиграл!!!");// и вот здесь надо как то сказать main что типа игра закончилась (мы с лехой делали флаги которые отсылаются в обработчик)
    }

    private void wheelRoll()
    {
        sendAll("Вращаем барабан...");
        currentWheelSectorIndex = rnd.nextInt(wheel.size());
        //if (wheel.get(currentWheelSectorIndex)==0) {
        //    sendAll("упс да у вас 0 очков ходит следующий игрок");
        //    nextPlayer();
        //}
        //else
        //{
            sendAll(wheel.get(currentWheelSectorIndex).toString() + "на барабане");
        //}


    }

    enum answerStatus{LETTER, WORD, OTHER}
}
