import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

public class Main {

    private static void tryStartLobbyes(ArrayList<Lobby> lobbyes, HashMap<String, Integer> usersGamesForTags, ArrayList<GameFortune> games, IOMultiplatformProcessor ioMultiplatformProcessor) {
        for (int i = 0; i < lobbyes.size(); ++i) {
            if (lobbyes.get(i) != null && lobbyes.get(i).isGameCanStart()) {
                lobbyes.get(i).startGame();
                for (User user: lobbyes.get(i).usersInLobby) {
                    usersGamesForTags.put(user.getTag(), i);
                }
                GameFortune game = new GameFortune(lobbyes.get(i).usersInLobby, ioMultiplatformProcessor);
                games.add(game);
                game.start();
            }
        }
    }

    private static void processFinishedGames(ArrayList<Lobby> lobbyes, HashMap<String, Integer> usersGamesForTags, ArrayList<GameFortune> games) {
        for (int i = 0; i < games.size(); ++i) {
            if (games.get(i) != null && games.get(i).isGameFinished()) {
                games.set(i, null);
                for (User user : lobbyes.get(i).getUsersInLobby())
                    usersGamesForTags.remove(user.getTag());
                lobbyes.set(i, null);
            }
        }
    }

    public static void main(String[] args) {
        IOMultiplatformProcessor ioMultiplatformProcessor = new IOMultiplatformProcessor();
        UsersDataBase usersDataBase = new UsersDataBase(ioMultiplatformProcessor);
        RegistartionProcessor registartionProcessor = new RegistartionProcessor(ioMultiplatformProcessor, usersDataBase);
        ArrayList<Lobby> lobbyes = new ArrayList<>();
        HashMap<String, Integer> usersGamesForTags = new HashMap<String, Integer>();
        MenuProcessor menuProcessor = new MenuProcessor(ioMultiplatformProcessor, lobbyes, usersDataBase);
        ArrayList<GameFortune> games = new ArrayList<GameFortune>();

        while (true) {
            processFinishedGames(lobbyes, usersGamesForTags, games);
            tryStartLobbyes(lobbyes, usersGamesForTags, games, ioMultiplatformProcessor);
            if (ioMultiplatformProcessor.isHasUnprocessedRequests()) {
                Request request = ioMultiplatformProcessor.pollRequest();
                if (usersDataBase.isUserNotRegister(request.getUser())) { //Незарегистрированный
                    registartionProcessor.processRequest(request);
                } else if (!usersGamesForTags.containsKey(request.getUser().getTag())) { //Не в игре
                    menuProcessor.processRequest(request);
                } else
                    games.get(usersGamesForTags.get(request.getUser().getTag())).processRequest(request);
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
