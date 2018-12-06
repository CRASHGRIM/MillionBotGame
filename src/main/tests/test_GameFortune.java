import java.util.ArrayList;

public class test_GameFortune {

    IOMultiplatformProcessor processor = new test_ProcessorClass();

    public void test_firstStep(){
        ArrayList<User> players = new ArrayList<User>();
        User user = new User(User.Platform.CONSOLE, 0);
        players.add(user);
        GameFortune game = new GameFortune(players, processor);
        game.processRequest(new Request(user, "hello"));
    }

}
