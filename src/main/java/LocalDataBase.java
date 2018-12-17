import java.util.*;

public class LocalDataBase {//здесь возможно что то лишнее

    private IOMultiplatformProcessor ioMultiplatformProcessor;
    private ArrayList<User> users = new ArrayList<>();
    private ArrayList<String> usersNames = new ArrayList<>();
    private ArrayList<String> usersTags = new ArrayList<>(); //Tag [platform:id]

    LocalDataBase(IOMultiplatformProcessor ioMultiplatformProcessor) {
        this.ioMultiplatformProcessor = ioMultiplatformProcessor;
    }

    public User getUser (String tag) {
        return users.get(usersTags.indexOf(tag));
    }

    boolean isUserNotRegister(User user) {
        String userTag = user.getPlatform() + ":" + user.getId();
        return !usersTags.contains(userTag);
    }

    public void addUser(User user) {
        if (isUserAlreadyRegister(user.getTag())) {//здесь корявая проверка на то что юзер уже зарегистрирован
            ioMultiplatformProcessor.sendMes(new Request(user, "Вы уже зарегистрированы!"));
            return;
        }
        users.add(user);
        usersNames.add(user.getName());
        usersTags.add(user.getTag());
    }

    private boolean isUserAlreadyRegister(String userTag) {
        return usersTags.contains(userTag);
    }

    public boolean isUserNameAlreadyUsed(String userName) {
        return usersNames.contains(userName);
    }

}

