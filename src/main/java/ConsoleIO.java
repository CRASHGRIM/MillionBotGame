import java.util.Scanner;

public class ConsoleIO extends Thread {

    private IOMultiplatformProcessor IOMultiplatformProcessor;

    ConsoleIO(IOMultiplatformProcessor IOMultiplatformProcessor) {
        this.IOMultiplatformProcessor = IOMultiplatformProcessor;
    }

    public void run() {
        while (true) {
            Scanner sc = new Scanner(System.in);
            IOMultiplatformProcessor.pushRequest(new Request(new User(User.Platform.CONSOLE, 0), sc.nextLine()));
        }
    }

    void sendMes(String message) {
        System.out.println(message);
    }
}
