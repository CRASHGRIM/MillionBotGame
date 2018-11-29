import java.util.ArrayDeque;

public class IOMultiplatformProcessor extends Thread {
    private ArrayDeque<Request> bufer = new ArrayDeque<Request>();
//    private VkIO vkIO = new VkIO(this);
    private ConsoleIO consoleIO = new ConsoleIO(this);

    public IOMultiplatformProcessor() {
        consoleIO.start();
    }

    void sendRequest(Request request) {
        bufer.add(request);
    }

    private void processRequest() {
        Request request = bufer.poll();
        //ToDo запросы надо пересылать обработчику
        //Я просто принчу
        System.out.println(request);
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
                //ToDo сюда интегрируй телеграм по аналогии с вк
                throw new UnsupportedOperationException();
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
