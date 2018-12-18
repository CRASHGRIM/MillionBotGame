import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;

public class MainProcessor {
    private IOMultiplatformProcessor ioMultiplatformProcessor = new IOMultiplatformProcessor();
    private LocalDataBase localDataBase = new LocalDataBase(ioMultiplatformProcessor);
    private MySQL dataBase = new MySQL(Config.databaseURL, Config.databaseUser, Config.databasePassword);
    private RegistartionProcessor registartionProcessor = new RegistartionProcessor(ioMultiplatformProcessor, localDataBase, dataBase);
    private MenuProcessor menuProcessor = new MenuProcessor(ioMultiplatformProcessor, this);
    @Getter
    private MatchMakingProcessor matchMakingProcessor = new MatchMakingProcessor(ioMultiplatformProcessor, dataBase);

    public void start() {
        while (true) {
            if (ioMultiplatformProcessor.isHasUnprocessedRequests()) {
                Request request = ioMultiplatformProcessor.pollRequest();
                if (!dataBase.checkRegister(request.getUserID()))//Незарегистрированный
                {
                    registartionProcessor.processRequest(request);
                    continue;
                }
                if (localDataBase.isUserNotRegister(request.getUser())) { //есть в базе но нет в локальной
                    try {
                        registartionProcessor.putUser(dataBase.loadInfo(request.getUserID()));
                        dataBase.dropReady(request.getUser());
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                    continue;
                }
                if (!dataBase.checkInGame(request.getUserID())) { //Не в игре
                    menuProcessor.processRequest(request);
                    continue;
                }

                int gameIndex = dataBase.getGameIndex(request.getUserID());
                matchMakingProcessor.getLobbiesDict().get(gameIndex).processRequest(request);
                if (matchMakingProcessor.getLobbiesDict().get(gameIndex).isGameFinished())
                {
                    ArrayList<User> usersInGame = dataBase.getUsersInGame(gameIndex);
                    for (int i=0;i<usersInGame.size();i++)
                    {
                        ioMultiplatformProcessor.sendMes(new Request(usersInGame.get(i), "игра закончилась"));
                        dataBase.userLeavesGame(usersInGame.get(i));
                    }
                    dataBase.increaseUserScore(matchMakingProcessor.getLobbiesDict().get(gameIndex).getActivePlayer());
                    matchMakingProcessor.getLobbiesDict().remove(gameIndex);
                }

            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
