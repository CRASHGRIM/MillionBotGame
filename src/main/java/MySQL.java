import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class MySQL {
    private Connection connection;

    //private static String selectUserString = "SELECT * FROM users WHERE userID=?";
    private static String updateString = "UPDATE users SET score=score+? WHERE userID=?";
    private static String dropReadyDateString = "UPDATE users SET lastReady=null, isInGame=0 WHERE userID=?";
    private static String insertNewPlayerString =  "INSERT INTO users VALUES (?, ?, ?, 0, null, 0, null)";
    private static String checkRegisterString =  "SELECT * FROM users WHERE userID=?";
    private static String userInGameString =  "UPDATE users SET isInGame=1 WHERE userID=?";
    private static String userLeaveGameString =  "UPDATE users SET isInGame=0 WHERE userID=?";
    private static String selectPlayerString = "SELECT * FROM users WHERE userID=?";
    private static String refreshReadyString = "UPDATE users SET lastReady=? WHERE userID=?";
    private static String userSetGameString =  "UPDATE users SET gameIndex=? WHERE userID=?";
    private static String selectUsersInGameString =  "SELECT * FROM users WHERE gameIndex=?";

    private static PreparedStatement updateCommand;
    private static PreparedStatement dropReadyDateCommand;
    private static PreparedStatement insertNewPlayerCommand;
    private static PreparedStatement checkRegisterCommand;
    private static PreparedStatement userInGameCommand;
    private static PreparedStatement userLeaveCommand;
    private static PreparedStatement selectUserCommand;
    private static PreparedStatement refreshReadyCommand;
    private static PreparedStatement userSetGameCommand;
    private static PreparedStatement selectUsersInGameCommand;



    public MySQL(String url, String user, String password) {
        try {
            connection = DriverManager.getConnection(url, user, password);
            //selectUserCommand = connection.prepareStatement(selectUserString);
            updateCommand = connection.prepareStatement(updateString);
            dropReadyDateCommand = connection.prepareStatement(dropReadyDateString);
            insertNewPlayerCommand = connection.prepareStatement(insertNewPlayerString);
            checkRegisterCommand = connection.prepareStatement(checkRegisterString);
            userInGameCommand = connection.prepareStatement(userInGameString);
            userLeaveCommand = connection.prepareStatement(userLeaveGameString);
            selectUserCommand = connection.prepareStatement(selectPlayerString);
            refreshReadyCommand = connection.prepareStatement(refreshReadyString);
            userSetGameCommand = connection.prepareStatement(userSetGameString);
            selectUsersInGameCommand = connection.prepareStatement(selectUsersInGameString);
        } catch (SQLException sqlEx) {
            sqlEx.printStackTrace();
        }

    }

    public void updatePlayer(User user) {
        try {
            updateCommand.setInt(1, user.getScore());
            updateCommand.setInt(2, user.getId());
            updateCommand.executeUpdate();
        }
        catch (SQLException sqlEx) {
            sqlEx.printStackTrace();
        }
    }

    public void insertPlayer(User user){
        try {
            insertNewPlayerCommand.setInt(1, user.getId());// playerID
            insertNewPlayerCommand.setString(2, user.getName());//player name
            insertNewPlayerCommand.setString(3, user.getPlatform().toString());//platform
            insertNewPlayerCommand.executeUpdate();
        }
        catch (SQLException sqlEx) {
            sqlEx.printStackTrace();
        }
    }

    public void dropReady(User user){
        try {
            dropReadyDateCommand.setInt(1, user.getId());
            dropReadyDateCommand.executeUpdate();
        }
        catch (SQLException sqlEx) {
            sqlEx.printStackTrace();
        }
    }

    public boolean checkRegister(int userID){
        try {
            checkRegisterCommand.setInt(1, userID);
            ResultSet resultSet = checkRegisterCommand.executeQuery();
            return resultSet.next();
        }
        catch (SQLException sqlEx) {
            sqlEx.printStackTrace();
        }
        return false;
    }

    public void userInGame(User user){
        try {
            userInGameCommand.setInt(1, user.getId());
            userInGameCommand.executeUpdate();
        }
        catch (SQLException sqlEx) {
            sqlEx.printStackTrace();
        }
    }

    public void userLeavesGame(User user){
        try {
            userLeaveCommand.setInt(1, user.getId());
            userLeaveCommand.executeUpdate();
        }
        catch (SQLException sqlEx) {
            sqlEx.printStackTrace();
        }
    }

    public User loadInfo(int userID) throws SQLException {
        try {
            selectUserCommand.setInt(1, userID);
            ResultSet resultSet = selectUserCommand.executeQuery();
            if(resultSet.next()) {
                User.Platform enumPlatform;
                String platform = resultSet.getString("platform");
                switch (platform){
                    case Config.databaseConsoleTag:
                        enumPlatform = User.Platform.CONSOLE;
                    case Config.databaseVKTag:
                        enumPlatform = User.Platform.VK;
                    case Config.databaseTelegramTag:
                        enumPlatform = User.Platform.TELEGRAM;// возможно вынесу в отдельный метод
                        default:
                            enumPlatform = User.Platform.CONSOLE;
                }
                String userName = resultSet.getString("username");
                return new User(enumPlatform, userID, userName);
            }
        } catch (SQLException sqlEx) {
            sqlEx.printStackTrace();
        }
        throw new SQLException();
    }

    public boolean checkInGame(int userID){
        try {
            selectUserCommand.setInt(1, userID);
            ResultSet resultSet = selectUserCommand.executeQuery();
            if(resultSet.next()) {
                if (resultSet.getByte("isInGame")==1)
                    return true;
                return false;
            }
        } catch (SQLException sqlEx) {
            sqlEx.printStackTrace();
        }
        return false;//здесь возможно стоит бросать exception
    }

    public void refreshReady(int userID){
        try {
            refreshReadyCommand.setString(1, LocalDateTime.now().toString());
            refreshReadyCommand.setInt(2, userID);
            refreshReadyCommand.executeUpdate();
        }
        catch (SQLException sqlEx) {
            sqlEx.printStackTrace();
        }
    }

    public void setGame(int gameIndex, int userID)
    {
        try {
            userSetGameCommand.setInt(1, gameIndex);
            userSetGameCommand.setInt(2, userID);
            userSetGameCommand.executeUpdate();
        }
        catch (SQLException sqlEx) {
            sqlEx.printStackTrace();
        }
    }

    public int getGameIndex(int userID)
    {
        try {
            selectUserCommand.setInt(1, userID);
            ResultSet resultSet = selectUserCommand.executeQuery();
            if(resultSet.next()) {
                return resultSet.getInt("gameIndex");
            }
        } catch (SQLException sqlEx) {
            sqlEx.printStackTrace();
        }
        return 0;//здесь возможно стоит бросать exception
    }

    public ArrayList<User> getUsersInGame(int gameID)
    {
        try {
            ArrayList<User> outUsers = new ArrayList<User>();
            System.out.println(gameID);
            selectUsersInGameCommand.setInt(1, gameID);
            ResultSet resultSet = selectUsersInGameCommand.executeQuery();
            while(resultSet.next()) {
                User outUser = new User(SQLEnumToJavaEnum(resultSet.getString("platform")), resultSet.getInt("userID"), resultSet.getString("username"));
                outUsers.add(outUser);
            return outUsers;
            }
        } catch (SQLException sqlEx) {
            sqlEx.printStackTrace();
        }
        return new ArrayList<User>();//здесь возможно стоит бросать exception
    }

    private User.Platform SQLEnumToJavaEnum(String en)
    {
        switch (en){
            case Config.databaseConsoleTag:
                return User.Platform.CONSOLE;
            case Config.databaseVKTag:
                return User.Platform.VK;
            case Config.databaseTelegramTag:
                return User.Platform.TELEGRAM;
            default:
                return User.Platform.CONSOLE;
        }
    }

}
