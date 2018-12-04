import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {
        IOMultiplatformProcessor ioMultiplatformProcessor = new IOMultiplatformProcessor();

        ArrayList<User> users = new ArrayList<User>();
        users.add(new User(User.Platform.CONSOLE, 0));
//        users.add(new User(User.Platform.TELEGRAM, 596865644));
//        users.add(new User(User.Platform.VK, 83229217));
        users.add(new User(User.Platform.VK, 251093754));
        GameFortune game = new GameFortune(users, ioMultiplatformProcessor);
        game.start();

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
