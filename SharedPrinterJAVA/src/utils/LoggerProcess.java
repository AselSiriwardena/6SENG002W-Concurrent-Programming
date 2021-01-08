package utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


public class LoggerProcess {

    /**
     * Prints a given message with logging time
     *
     * @param message        The message to be logged
     * @param systemClass      Process that is responsible for the message
     * @param messageStatus    Whether the message is a success message (true), failure message (false),
     */

    private static final String ANSI_RESET  = "\u001B[0m";
    private static final String ANSI_BLACK  = "\u001B[30m";
    private static final String ANSI_BLUE   = "\u001B[34m";
    private static final String ANSI_GREEN  = "\u001B[32m";
    private static final String ANSI_RED    = "\u001B[31m";

    public static synchronized void logger(String message, Enums.SystemClass systemClass, Enums.MessageStatus messageStatus) {
        System.out.println(ANSI_BLUE + systemClass + ANSI_RESET + ": [" + getDateTime() + "] " + colorizeMessage(message, messageStatus) + "\n");
    }

    private static String getDateTime(){
        Date date = new Date(System.currentTimeMillis());
        DateFormat formatter = new SimpleDateFormat("HH:mm:ss.SSS");
        return formatter.format(date);
    }

    private static String colorizeMessage(String message, Enums.MessageStatus messageStatus) {
        switch (messageStatus) {
            case SUCCESS:
                return ANSI_GREEN + message + ANSI_RESET; // Green
            case FAILED:
                return ANSI_RED + message + ANSI_RED; // Red
            default:
                return message;
        }
    }
}
