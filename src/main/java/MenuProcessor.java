import java.util.ArrayList;

public class MenuProcessor {
    IOMultiplatformProcessor ioMultiplatformProcessor;
    ArrayList<Lobby> lobbies;
    ArrayList<User> unitedLobby;
    LocalDataBase localDataBase;
    MySQL dataBase;

    public MenuProcessor(IOMultiplatformProcessor ioMultiplatformProcessor, ArrayList<Lobby> lobbies, LocalDataBase localDataBase, MySQL dataBase) {
        this.ioMultiplatformProcessor = ioMultiplatformProcessor;
        this.lobbies = lobbies;
        this.localDataBase = localDataBase;
        this.dataBase = dataBase;
        this.unitedLobby = new ArrayList<>();
    }

    public void processRequest(Request request) {
        switch (request.getMessage().toLowerCase()) {
            case "!start":
                //ToDo обращаться не ко всем лобби
                addUserToLobby(request.getUser());
                dataBase.refreshReady(request.getUserID());
                ioMultiplatformProcessor.sendMes(request.getUser(), "Вы в очереди. Ожидайте.");
                break;
            default:
                ioMultiplatformProcessor.sendMes(request.getUser(), "Неправильная команда.");
                ioMultiplatformProcessor.sendMes(request.getUser(), "Чтобы начать игру введите !start.");
                break;
        }
    }

    private void addUserToLobby(User user) {//здесь нужно тупо создать лист и при добавлении нового юзера чекать что лист меньше чем количество игроков в игре
        unitedLobby.add(user);
        if (unitedLobby.size()==Config.USERS_IN_LOBBY){
            //здесь создаем игру и кидаем туда всех из лобби (потом сдлеать проверку на реади)
        }
        User userFromDatabase = localDataBase.getUser(user.getTag());
        boolean isUserFindLobby = false;
        for (int i = 0; i < lobbies.size(); ++i) {
            if (lobbies.get(i) != null && lobbies.get(i).getPlayersCount() < Config.USERS_IN_LOBBY) {
                lobbies.get(i).addUserToLobby(userFromDatabase);
                isUserFindLobby = true;
            }
        }
        if (!isUserFindLobby)
            lobbies.add(new Lobby(userFromDatabase));
    }
}
