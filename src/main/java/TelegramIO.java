import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;


public class TelegramIO extends TelegramLongPollingBot implements IOInterface{

    private IOMultiplatformProcessor ioMultiplatformProcessor;

    TelegramIO(IOMultiplatformProcessor processor) {
        super();
        ioMultiplatformProcessor = processor;
    }

    @Override
    public void onUpdateReceived(Update update) {
        int userID = update.getMessage().getFrom().getId();
        Message message = update.getMessage();
        ioMultiplatformProcessor.pushRequest(new Request(new User(User.Platform.TELEGRAM, userID), message.getText()));
    }
    @Override
    public void sendMessage(int chatID, String s) {
        try {
            execute(new SendMessage().setChatId((long) chatID).setText(s).enableMarkdown(false));
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getBotUsername() {
        return Config.TELEGRAM_BOT_NAME;
    }

    @Override
    public String getBotToken() {
        return FileUtils.readToken(Config.FILE_NAME_TG_TOKEN);
    }


}
