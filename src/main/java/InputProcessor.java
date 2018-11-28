import java.util.ArrayDeque;

public class InputProcessor extends Thread {
    ArrayDeque<Request> bufer = new ArrayDeque<Request>();

    public void sendRequest(Request request) {
        bufer.add(request);
    }

    public void processRequest() {
        //ТУТ ОБРАБОТКА ЗАПРОСОВ, Я ИХ ПРОСТО ПРИНТЮ
        System.out.println(bufer.poll());
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
