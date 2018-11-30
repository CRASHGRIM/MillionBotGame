import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayDeque;

public class IOMultiplatformProcessor extends Thread {
    private ArrayDeque<Request> bufer = new ArrayDeque<Request>();
//    private VkIO vkIO = new VkIO(this);
    private TelegramBot telegramBot;
    private ConsoleIO consoleIO;
    private static String BOT_NAME = "USER";
    private static String BOT_TOKEN = "578074240:AAEzKIim6j6yusyvsufNS41Z3_G6-a7TvPU";

    public IOMultiplatformProcessor() {
        consoleIO = new ConsoleIO(this);
        new Thread(consoleIO).start();
        try {
            ApiContextInitializer.init(); // init api
            this.telegramBot = new TelegramBot(BOT_TOKEN, BOT_NAME, this);
            TelegramBotsApi telegramBotApi = new TelegramBotsApi();
            telegramBotApi.registerBot(this.telegramBot);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
        System.out.println("Working");
    }

    void sendRequest(Request request) {
        bufer.add(request);
    }

    private void processRequest() {
        Request request = bufer.poll();
        //ToDo запросы надо пересылать обработчику
        //Я просто принчу
        //System.out.println(request);
        sendMes(request.user, request.request);
    }

    public void sendMes(User user, String message) {
        switch (user.getPlatform()) {
            case CONSOLE:
                consoleIO.sendMes(message);
                break;
            case VK:
//                vkIO.sendMessage(user.getId(), message);
                break;
            case TELEGRAM:
                telegramBot.sendMsg(message, user.getId());
                //throw new UnsupportedOperationException();
        }
    }

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            while (!bufer.isEmpty()) {
                processRequest();
            }
        }
    }
}
