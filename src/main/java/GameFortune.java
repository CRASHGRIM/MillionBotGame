import java.util.ArrayList;

public class GameFortune {

    private User activePlayer;
    private int activePlayerIndex;
    private ArrayList<User> players;
    private String question;
    private String rightWord;
    private StringBuilder currentWord;
    private IOMultiplatformProcessor processor;
    private answerStatus activePlayerAnswerStatus;

    GameFortune(ArrayList<User> players, IOMultiplatformProcessor processor){
        this.players = players;
        this.processor = processor;
        activePlayer = this.players.get(0);
        activePlayerIndex = 0;
        activePlayerAnswerStatus = answerStatus.OTHER;
        question = "4 колеса, руль";// здесь потом сделаем подгрузку из какого нибудь словаря
        rightWord = "машина";
        currentWord = new StringBuilder();
        for(int i = 0; i<rightWord.length(); i++)// здесь возможно как то покрасивее можно сделать
            currentWord.append("*");
    }

    public void start()
    {
        for (User player : players)
        {
            processor.sendMes(new Request(player, "добро пожаловать на капиталшоу поле чудес!!!"));
            processor.sendMes(new Request(player, "вот задание:"));
            processor.sendMes(new Request(player, question));
            processor.sendMes(new Request(player, currentWord.toString()));
            processor.sendMes(new Request(player,  activePlayer.getId().toString()+"начинает игру!!!"));
            processor.sendMes(new Request(player, "чтобы назвать букву скажите буква, чтобы назвать слово скажите слово"));
        }
    }


    public void next(Request request)
    {
        String answer = request.getMessage();
        if (request.getUser().getId() != activePlayer.getId())
            return;
        switch (activePlayerAnswerStatus) {
            case LETTER:
                if (answer.length()>1) {
                    processor.sendMes(new Request(activePlayer, "кажется у вас не буква"));
                    break;
                }
                if (currentWord.indexOf(answer)!=-1)
                {
                    processor.sendMes(new Request(activePlayer, "а такая бува уже была"));
                    break;
                }
                if (rightWord.contains(answer))
                {
                    processor.sendMes(new Request(activePlayer, "да, вы угадали букву"));
                    for (int i = 0; i<currentWord.length(); i++)
                        if (rightWord.charAt(i) == answer.charAt(0))
                            currentWord.replace(i, i+1, answer);
                    processor.sendMes(new Request(activePlayer, currentWord.toString()));
                    activePlayerAnswerStatus = answerStatus.OTHER;
                    // здесь еще надо прокрутку барабана запилить
                    break;
                }
                processor.sendMes(new Request(activePlayer, "а такой буквы тут нет"));
                nextPlayer();
                break;
            case WORD:
                if (answer.equals(rightWord))// здесь еще надо сделать чтобы игрок выигрывал собсна
                    processor.sendMes(new Request(activePlayer, "да, вы угадали слово"));
                else {
                    processor.sendMes(new Request(activePlayer, "нет, это не то слово"));
                    nextPlayer();
                }
                break;
            case OTHER:
                if (answer.equals("буква")) {
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
    }

    enum answerStatus{LETTER, WORD, OTHER}
}
