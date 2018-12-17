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
        for (int i=0; i<userQueue.size();i++)
        {
            if (userQueue.get(i).getId()==user.getId())
                return;
        }
        userQueue.add(user);
       // dataBase.refreshReady(user.getId());
        //userQueue.add(user);//надо проверить что юзер уже в очереди
        //userQueue.element() //здесь надо проверить первого юзера на то что он слишком долго в очереди
        if (userQueue.size()>=Config.USERS_IN_LOBBY)
        {
            ArrayList<User> usersGoingToGame = new ArrayList<>();
            while(usersGoingToGame.size()<Config.USERS_IN_LOBBY) {
                User userGoingToGame = userQueue.pollFirst();
                userGoingToGame.setName(dataBase.getUserName(userGoingToGame.getId()));
                userGoingToGame.setCurrentGameIdentifier(currentgameIndex);
                usersGoingToGame.add(userGoingToGame);
                dataBase.userInGame(userGoingToGame);
                dataBase.setGame(currentgameIndex, userGoingToGame.getId());
            }
            lobbiesDict.put(currentgameIndex, new GameFortune(usersGoingToGame, this.ioMultiplatformProcessor));
            lobbiesDict.get(currentgameIndex).start();
            if (currentgameIndex==Integer.MAX_VALUE)
                currentgameIndex=0;
            else
                currentgameIndex+=1;
        }
    }
    public void checkQueueForTooLongWaiting()
    {
        //здесь нужно проверить что юзер в очереди слишком долго ждет и если это так послать ему что игра не нашлась

    }
}
