import java.util.Scanner;

public class ConsoleIO extends Thread {

    IOMultiplatformProcessor IOMultiplatformProcessor;

    public ConsoleIO(IOMultiplatformProcessor IOMultiplatformProcessor) {
        this.IOMultiplatformProcessor = IOMultiplatformProcessor;
    }

    public void run() {
        while (true) {
            Scanner sc = new Scanner(System.in);
            IOMultiplatformProcessor.sendRequest(new Request(new User(User.Platform.CONSOLE, 0), sc.nextLine()));
        }
    }

    public void sendMes(String message) {
        System.out.println(message);
    }
}
