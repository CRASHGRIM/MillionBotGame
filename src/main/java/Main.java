public class Main {
    public static void main(String[] args) {
        IOMultiplatformProcessor ioMultiplatformProcessor = new IOMultiplatformProcessor();

        while (true) {
            if (ioMultiplatformProcessor.isHasUnprocessedRequests()) {
                ioMultiplatformProcessor.sendMes(ioMultiplatformProcessor.pollRequest());
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
