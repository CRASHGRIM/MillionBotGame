import org.telegram.telegrambots.ApiContextInitializer;
//import org.telegram.telegrambots.api.objects.Update;
        import org.telegram.telegrambots.bots.TelegramLongPollingBot;
        import org.telegram.telegrambots.meta.TelegramBotsApi;
        import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
        import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
        import org.telegram.telegrambots.meta.api.methods.updates.GetUpdates;
        import org.telegram.telegrambots.meta.api.objects.Message;
        import org.telegram.telegrambots.meta.api.objects.Update;
        import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

        import java.awt.*;
        import java.sql.*;
        import java.util.HashMap;
        import java.util.List;
        import java.util.Map;
        import java.util.Stack;


public class TelegramIO extends TelegramLongPollingBot{

    IOMultiplatformProcessor IOMultiplatformProcessor;
    private final User.Platform platform = User.Platform.TELEGRAM;

    TelegramIO(IOMultiplatformProcessor IOMultiplatformProcessor)
    {
        this.IOMultiplatformProcessor = IOMultiplatformProcessor;
        ApiContextInitializer.init(); // Инициализируем api
        TelegramBotsApi telegramBotApi = new TelegramBotsApi();
        try {
            telegramBotApi.registerBot(this);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }

        System.out.println("Working");
    }

    @Override
    public void onUpdateReceived(Update update) {
        int userID = update.getMessage().getFrom().getId();
        Message message = update.getMessage();
        String chatId = message.getChatId().toString();
        System.out.println(message.getText());
        IOMultiplatformProcessor.sendRequest(new Request(new User(platform, userID), message.getText());

    }

    public void sendMsg(String s, String chatID) {
        // Create send metho
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatID);
        sendMessage.setText(s);
        try {
            execute(sendMessage);
        }
        catch (TelegramApiException e){
            e.printStackTrace();
        }
    }

    @Override
    public String getBotUsername() {
        return "USER";
    }

    @Override
    public String getBotToken() {
        return "578074240:AAEzKIim6j6yusyvsufNS41Z3_G6-a7TvPU";// это надо в конфиги вынести
    }


}
