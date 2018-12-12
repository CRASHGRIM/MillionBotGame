import java.util.ArrayList;

public class MenuProcessor {
    IOMultiplatformProcessor ioMultiplatformProcessor;
    ArrayList<Lobby> lobbyes;
    UsersDataBase usersDataBase;

    public MenuProcessor(IOMultiplatformProcessor ioMultiplatformProcessor, ArrayList<Lobby> lobbyes, UsersDataBase usersDataBase) {
        this.ioMultiplatformProcessor = ioMultiplatformProcessor;
        this.lobbyes = lobbyes;
        this.usersDataBase = usersDataBase;
    }

    public void processRequest(Request request) {
        switch (request.getMessage().toLowerCase()) {
            case "!start":
                addUserToLobby(request.getUser());
                ioMultiplatformProcessor.sendMes(request.getUser(), "Вы в очереди. Ожидайте.");
                break;
            default:
                ioMultiplatformProcessor.sendMes(request.getUser(), "Неправильная команда.");
                ioMultiplatformProcessor.sendMes(request.getUser(), "Чтобы начать игру введите !start.");
                break;
        }
    }

    private void addUserToLobby(User user) {
        User userFromDatabase = usersDataBase.getUser(user.getTag());
        boolean isUserFindLobby = false;
        for (int i = 0; i < lobbyes.size(); ++i) {
            if (lobbyes.get(i) != null && lobbyes.get(i).getPlayersCount() < Config.USERS_IN_LOBBY) {
                lobbyes.get(i).addUserToLobby(userFromDatabase);
                isUserFindLobby = true;
            }
        }
        if (!isUserFindLobby)
            lobbyes.add(new Lobby(userFromDatabase));
    }
}
