import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {
        Database usersDatabase = new Database();
        IOMultiplatformProcessor ioMultiplatformProcessor = new IOMultiplatformProcessor(usersDatabase);
        GameFortune game = new GameFortune(new ArrayList<User>(usersDatabase.getUsers().values()), ioMultiplatformProcessor);
        game.start();// здесь надо пихать в игру не всю базу а только нескольких игроков которые будут играть собсна

        while (true) {
            if (game.isGameFinished())
                break;
            if (ioMultiplatformProcessor.isHasUnprocessedRequests()) {
                game.processRequest(ioMultiplatformProcessor.pollRequest());
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
