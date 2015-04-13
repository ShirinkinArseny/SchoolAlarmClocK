package PC_part.Common;

import java.text.SimpleDateFormat;

public class Logger {

    private static final SimpleDateFormat sdf = new SimpleDateFormat("HH.mm.ss yyyy-MM-dd");

    private static String getDateText() {
        return sdf.format(java.util.Calendar.getInstance().getTime().getTime());
    }

    public static void logWarning(Class cl, String s) {
        System.err.println("[Warning] " + getDateText() + " (" + cl.getName() + ") " + s);
    }

    public static void logError(Class cl, String s) {
        System.err.println("[ERROR] " + getDateText() + " (" + cl.getName() + ") " + s);
    }

    public static void logInfo(Class cl, String s) {
        System.out.println("[Info] " + getDateText() + " (" + cl.getName() + ") " + s);
    }

}
