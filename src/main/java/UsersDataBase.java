import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedWriter;
import java.util.*;

public class UsersDataBase {

    private IOMultiplatformProcessor ioMultiplatformProcessor;
    private ArrayList<User> users = new ArrayList<>();
    private ArrayList<String> usersNames = new ArrayList<>();
    private ArrayList<String> usersTags = new ArrayList<>(); //Tag [platform:id]

    UsersDataBase(IOMultiplatformProcessor ioMultiplatformProcessor) {
        this.ioMultiplatformProcessor = ioMultiplatformProcessor;
    }

    boolean isUserNotRegister(User user) {
        String userTag = user.getPlatform() + ":" + user.getId();
        return !usersTags.contains(userTag);
    }

    public void addUser(User user) {
        String userTag = user.getPlatform() + ":" + user.getId();
        if (isUserAlreadyRegister(userTag)) {
            ioMultiplatformProcessor.sendMes(new Request(user, "Вы уже зарегистрированны!"));
            return;
        }
        if (isUserNameAlreadyUsed(user.getName())) {
            ioMultiplatformProcessor.sendMes(new Request(user, "Имя уже занято, попробуйте другое!"));
            return;
        }
        users.add(user);
        usersNames.add(user.getName());
        usersTags.add(userTag);
    }

    private boolean isUserAlreadyRegister(String userTag) {
        return usersTags.contains(userTag);
    }

    private boolean isUserNameAlreadyUsed(String userName) {
        return usersNames.contains(userName);
    }

    //ToDo Добавить сохранение
}

