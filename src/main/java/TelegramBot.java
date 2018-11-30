//import org.telegram.telegrambots.api.objects.Update;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.HashMap;
import java.util.Map;


public class TelegramBot extends TelegramLongPollingBot {

    private String BOT_NAME;
    private String BOT_TOKEN;
    private IOMultiplatformProcessor MultiplatformProcessor;



    public TelegramBot(String botToken, String botUsername, IOMultiplatformProcessor processor) {

        super();
        BOT_NAME = botUsername;
        BOT_TOKEN = botToken;
        MultiplatformProcessor = processor;
    }

    @Override
    public void onUpdateReceived(Update update) {
        int userID = update.getMessage().getFrom().getId();
        Message message = update.getMessage();
        long chatId = message.getChatId();
        MultiplatformProcessor.sendRequest(new Request(new User(User.Platform.TELEGRAM, userID), message.getText()));
    }

    public void sendMsg(String s, long chatID) {
        try {
            execute(new SendMessage().setChatId(chatID).setText(s).enableMarkdown(true));
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getBotUsername() {
        return BOT_NAME;
    }

    @Override
    public String getBotToken() {
        return BOT_TOKEN;
    }


}
