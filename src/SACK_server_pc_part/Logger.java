package SACK_server_pc_part;

import java.text.SimpleDateFormat;

public class Logger {


    private static final SimpleDateFormat sdf = new SimpleDateFormat("HH.mm.ss yyyy-MM-dd");
    private static final java.util.Calendar cal = java.util.Calendar.getInstance();

    private static String getDateText() {
        return sdf.format(cal.getTime().getTime());
    }

    public static void logWarning(String category, String s) {
        System.err.println("[Warning] " + getDateText() + " (" + category + ") " + s);
    }

    public static void logError(String category, String s) {
        System.err.println("[ERROR] " + getDateText() + " (" + category + ") " + s);
    }

    public static void logInfo(String category, String s) {
        System.out.println("[Info] " + getDateText() + " (" + category + ") " + s);
    }

}
