import lombok.Getter;
import lombok.ToString;


import java.util.ArrayList;
@ToString
public class Lobby {
    boolean isInGame = false;

    @Getter
    ArrayList<User> usersInLobby;

    public Lobby(User user) {
        usersInLobby = new ArrayList<>();
        addUserToLobby(user);
    }

    public int getPlayersCount() {
        return usersInLobby.size();
    }

    public void addUserToLobby(User user) {
        usersInLobby.add(user);
    }

    public boolean isGameCanStart() {
        return usersInLobby.size() == Config.USERS_IN_LOBBY && !isInGame;
    }

    public void startGame() {
        isInGame = true;
    }
}
