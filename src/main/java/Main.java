import java.io.BufferedWriter;
import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {
        IOMultiplatformProcessor ioMultiplatformProcessor = new IOMultiplatformProcessor();
        UsersDataBase usersDataBase = new UsersDataBase(ioMultiplatformProcessor);
        RegistartionProcessor registartionProcessor = new RegistartionProcessor(ioMultiplatformProcessor, usersDataBase);

        Database usersDatabase = new Database();
        GameFortune game = new GameFortune(new ArrayList<User>(usersDatabase.getUsers().values()), ioMultiplatformProcessor);
        game.start();// здесь надо пихать в игру не всю базу а только нескольких игроков которые будут играть собсна

        while (true) {
            if (game.isGameFinished())
                break;
            if (ioMultiplatformProcessor.isHasUnprocessedRequests()) {
                Request request = ioMultiplatformProcessor.pollRequest();
                if (usersDataBase.isUserNotRegister(request.getUser()))
                    registartionProcessor.processRequest(request);
                else
                    game.processRequest(request);
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
