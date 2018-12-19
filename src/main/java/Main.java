public class Main {

    public static void main(String[] args) {
        try {
            MainProcessor mainProcessor = new MainProcessor();
            mainProcessor.start();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        //Я люблю пингвинов
    }
}
