import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;


public class TelegramIO extends TelegramLongPollingBot {

    private String BOT_NAME = "USER";
    private String BOT_TOKEN = "578074240:AAEzKIim6j6yusyvsufNS41Z3_G6-a7TvPU";
    private IOMultiplatformProcessor MultiplatformProcessor;


    public TelegramIO(IOMultiplatformProcessor processor) {
        super();
        MultiplatformProcessor = processor;
    }

    @Override
    public void onUpdateReceived(Update update) {
        int userID = update.getMessage().getFrom().getId();
        Message message = update.getMessage();
        long chatId = message.getChatId();
        MultiplatformProcessor.pushRequest(new Request(new User(User.Platform.TELEGRAM, userID), message.getText()));
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
