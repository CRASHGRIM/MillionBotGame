public class Main {
    public static void main(String[] args) {

        InputProcessor inputProcessor = new InputProcessor();
        ConsoleInput consoleInput = new ConsoleInput(inputProcessor);
        inputProcessor.start();
        consoleInput.start();
    }
}
