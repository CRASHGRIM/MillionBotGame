import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Database {

    //private ArrayList<User> users;
    @Getter//это костыль потом исправлю такого не должно быть(там в одном месте нужен список игроков пофиксится когда будет менюха)
    private Map<Integer, User> users;

    Database()
    {
        this.users = new HashMap<>();
        users.put(0, new User(User.Platform.CONSOLE, 0));
        users.put(596865644, new User(User.Platform.TELEGRAM, 596865644));
//        users.add(new User(User.Platform.VK, 83229217));
//        users.add(new User(User.Platform.VK, 251093754));
    }

    public User getUser(int userID)
    {
        if (users.containsKey(userID))
            return users.get(userID);
        System.out.println("NO_USER");// здесь возможно как то получше обработать отсутствие юзера
        return new User(User.Platform.CONSOLE, 0);
    }

    public void putUser(User user)
    {
        users.put(user.getId(), user);
    }

}
