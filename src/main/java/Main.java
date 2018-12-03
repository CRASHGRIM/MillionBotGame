import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {
        IOMultiplatformProcessor ioMultiplatformProcessor = new IOMultiplatformProcessor();

        ArrayList<User> arr = new ArrayList<User>();// здесь говнокод потом уберу наверное если не забуду
        //arr.add(new User(User.Platform.CONSOLE, 0));
        arr.add(new User(User.Platform.TELEGRAM, 596865644));
        arr.add(new User(User.Platform.VK, 83229217));
        GameFortune game = new GameFortune(arr, ioMultiplatformProcessor);
        game.start();

        while (true) {
            if (ioMultiplatformProcessor.isHasUnprocessedRequests()) {
                game.next(ioMultiplatformProcessor.pollRequest());// надо сделать какой то флаг того что игра завершилась
                //ioMultiplatformProcessor.sendMes(ioMultiplatformProcessor.pollRequest());
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
