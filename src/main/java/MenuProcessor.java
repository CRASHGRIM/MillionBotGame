import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;

public class MenuProcessor {
    private IOMultiplatformProcessor ioMultiplatformProcessor;
    private ArrayList<User> unitedLobby;
    private LocalDataBase localDataBase;
    private MySQL dataBase;
    private LinkedList<User> userQueue;
    private HashMap<Integer, GameFortune> lobbiesDict;
    private int currentgameIndex;
    private MainProcessor mainProcessor;

    public MenuProcessor(IOMultiplatformProcessor ioMultiplatformProcessor, MainProcessor mainProcessor) {
        this.ioMultiplatformProcessor = ioMultiplatformProcessor;
        this.mainProcessor = mainProcessor;
    }

    public void processRequest(Request request) {
        switch (request.getMessage().toLowerCase()) {
            case "!start":
                ioMultiplatformProcessor.sendMes(request.getUser(), "Вы в очереди. Ожидайте.");
                mainProcessor.getMatchMakingProcessor().addUserToLobby(request.getUser());//TODO сделать проверку что уже в лобби
                break;
            default:
                ioMultiplatformProcessor.sendMes(request.getUser(), "Неправильная команда.");
                ioMultiplatformProcessor.sendMes(request.getUser(), "Чтобы начать игру введите !start.");
                break;
        }
    }


}
