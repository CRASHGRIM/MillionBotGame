import java.util.HashMap;
import java.util.Map;

interface Operation {
    void execute(User player);
}

public class TechnicalCommands {
    public Map<String, Operation> listOfCommands = new HashMap<>();

    public TechnicalCommands(GameFortune game) {
        listOfCommands.put("!help", (User player) -> game.getProcessor().sendMes(new Request(player, help())));
        listOfCommands.put("!score", (User player) -> game.getProcessor().sendMes(new Request(player, player.getScore().toString())));
        listOfCommands.put("!question", (User player) -> game.getProcessor().sendMes(new Request(player, game.getQuestion())));
        listOfCommands.put("!currentWord", (User player) -> game.getProcessor().sendMes(new Request(player, game.getCurrentWord().toString())));
}

    private void print(String string){
        System.out.println(string);
    }


    private String help() {
        return "Вы играете в игру поле чудес" +
                "\nцель игры: первым угадать загаданное слово\n" +
                "\nвот технические команды которые доступны\n" +
                this.listOfCommands.keySet();
    }
}
