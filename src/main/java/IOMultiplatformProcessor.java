import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayDeque;

public class IOMultiplatformProcessor {
    private ArrayDeque<Request> bufer = new ArrayDeque<Request>();
    private VkIO vkIO;
    private TelegramIO telegramIO;
    private ConsoleIO consoleIO;

    public IOMultiplatformProcessor() {
        if (Config.IS_CONSOLE_RUN) {
            consoleIO = new ConsoleIO(this);
            consoleIO.start();
        }

        if (Config.IS_VK_RUN) {
            vkIO = new VkIO(this);
        }

        if (Config.IS_TG_RUN) {
            try {
                ApiContextInitializer.init(); // init api
                this.telegramIO = new TelegramIO(this);
                TelegramBotsApi telegramBotApi = new TelegramBotsApi();
                telegramBotApi.registerBot(this.telegramIO);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }

    void pushRequest(Request request) {
        bufer.add(request);
    }

    Boolean isHasUnprocessedRequests() {
        return !bufer.isEmpty();
    }

    Request pollRequest() {
        return bufer.poll();
    }

    void sendMes(Request request) {
        sendMes(request.getUser(), request.getMessage());
    }

    void sendMes(User user, String message) {
        //ToDo общий интерфейс (map)
        switch (user.getPlatform()) {
            case CONSOLE:
                if (Config.IS_CONSOLE_RUN)
                    consoleIO.sendMes(message);
                break;
            case VK:
                if (Config.IS_VK_RUN)
                    vkIO.sendMessage(user.getId(), message);
                break;
            case TELEGRAM:
                if (Config.IS_TG_RUN)
                    telegramIO.sendMsg(message, user.getId());
        }
    }
}
