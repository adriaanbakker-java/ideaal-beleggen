package nl.ideaalbeleggen;


// maincontroller stub, no need for a separate controller here
public class MainController {
    public static class LocalLogging implements LogInterface {
        @Override
        public  void printMessage(String aMessage) {
            logInTextArea(aMessage);
        }
    }


    public static void logInTextArea(String message) {
        System.out.println(message);
    }
}
