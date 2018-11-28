import java.util.Scanner;

public class ConsoleInput extends Thread {

    InputProcessor inputProcessor;

    public ConsoleInput(InputProcessor inputProcessor) {
        this.inputProcessor = inputProcessor;
    }

    public void run() {
        while (true) {
            Scanner sc = new Scanner(System.in);

            inputProcessor.sendRequest(new Request(0, sc.nextLine()));
        }
    }
}
