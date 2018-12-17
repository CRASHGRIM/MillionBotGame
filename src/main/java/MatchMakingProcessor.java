import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

public class MatchMakingProcessor {

    private IOMultiplatformProcessor ioMultiplatformProcessor;
    private MySQL dataBase;
    private LinkedList<User> userQueue;
    @Getter
    private HashMap<Integer, GameFortune> lobbiesDict;
    private int currentgameIndex;

    MatchMakingProcessor(IOMultiplatformProcessor ioMultiplatformProcessor, MySQL dataBase)
    {
        this.ioMultiplatformProcessor = ioMultiplatformProcessor;
        this.dataBase = dataBase;
        this.userQueue = new LinkedList<>();
        this.lobbiesDict = new HashMap<>();
        this.currentgameIndex = 0;
    }

    public void addUserToLobby(User user) {
        dataBase.refreshReady(user.getId());
        userQueue.add(user);
        //userQueue.element() //здесь надо проверить первого юзера на то что он слишком долго в очереди
        if (userQueue.size()>=Config.USERS_IN_LOBBY)
        {
            ArrayList<User> usersGoingToGame = new ArrayList<>();
            while(usersGoingToGame.size()<Config.USERS_IN_LOBBY) {
                User userGoingToGame = userQueue.pollFirst();
                userGoingToGame.setCurrentGameIdentifier(currentgameIndex);
                usersGoingToGame.add(userGoingToGame);
                dataBase.userInGame(userGoingToGame);
                dataBase.setGame(currentgameIndex, userGoingToGame.getId());
            }
            lobbiesDict.put(currentgameIndex, new GameFortune(usersGoingToGame, this.ioMultiplatformProcessor));
            lobbiesDict.get(currentgameIndex).start();
            currentgameIndex += 1;//здесь обработать максинт
        }
    }
}
