package PC_part.SACK_pc_client.Configurable;

public class Labels {


    /*
    Connection
     */
    public static String updatePortsList = ConfigsReader.getStringValue("updatePortsList");
    public static String connectToSelected = ConfigsReader.getStringValue("connectToSelected");
    public static String disconnect = ConfigsReader.getStringValue("disconnect");
    public static String network = ConfigsReader.getStringValue("network");
    public static String usablePorts = ConfigsReader.getStringValue("usablePorts");
    public static String noUsablePorts = ConfigsReader.getStringValue("noUsablePorts");

    /*
    Connection results
     */
    public static String noConnections = ConfigsReader.getStringValue("noConnections");
    public static String disconnection = ConfigsReader.getStringValue("disconnection");
    public static String connectionNow = ConfigsReader.getStringValue("connectionNow");
    public static String connectedTo = ConfigsReader.getStringValue("connectedTo");
    public static String cannotConnectTo = ConfigsReader.getStringValue("cannotConnectTo");

    /*
    Common
     */
    public static String cannotPerformOperationCuzNoConnection =
            ConfigsReader.getStringValue("cannotPerformOperationCuzNoConnection");

    /*
    Time
     */
    public static String getTimeFromDuino = ConfigsReader.getStringValue("getTimeFromDuino");
    public static String writeLocalTime = ConfigsReader.getStringValue("writeLocalTime");
    public static String duinoTime = ConfigsReader.getStringValue("duinoTime");
    public static String localTime = ConfigsReader.getStringValue("localTime");

    /*
    Rings
     */
    public static String addOneMoreRing = ConfigsReader.getStringValue("addOneMoreRing");
    public static String removeSelected = ConfigsReader.getStringValue("removeSelected");
    public static String copySelected = ConfigsReader.getStringValue("copySelected");
    public static String insert = ConfigsReader.getStringValue("insert");
    public static String dropSelection = ConfigsReader.getStringValue("dropSelection");
    public static String getRingsFromDuino = ConfigsReader.getStringValue("getRingsFromDuino");
    public static String writeRingsToDuino = ConfigsReader.getStringValue("writeRingsToDuino");

    /*
    Main menu
     */
    public static String chart = ConfigsReader.getStringValue("chart");
    public static String table = ConfigsReader.getStringValue("table");
    public static String timeSync = ConfigsReader.getStringValue("timeSync");
    public static String connection = ConfigsReader.getStringValue("connection");
    public static String exit = ConfigsReader.getStringValue("exit");

    /*
    Weekdays
     */
    public static String monday = ConfigsReader.getStringValue("monday");
    public static String tuesday = ConfigsReader.getStringValue("tuesday");
    public static String wednesday = ConfigsReader.getStringValue("wednesday");
    public static String thursday = ConfigsReader.getStringValue("thursday");
    public static String friday = ConfigsReader.getStringValue("friday");
    public static String saturday = ConfigsReader.getStringValue("saturday");
    public static String sunday = ConfigsReader.getStringValue("sunday");


    public static String hours = ConfigsReader.getStringValue("hours");
    public static String minutes = ConfigsReader.getStringValue("minutes");
    public static String seconds = ConfigsReader.getStringValue("seconds");

    /*
    Time dialogue
     */
    public static String empty = ConfigsReader.getStringValue("empty");
    public static String cannotParseTime = ConfigsReader.getStringValue("cannotParseTime");

    /*
    IP dialogue
     */
    public static String wrongIP =          ConfigsReader.getStringValue("wrongIP");
    public static String niceIP =           ConfigsReader.getStringValue("niceIP");
    public static String emptyIP =          ConfigsReader.getStringValue("emptyIP");
    public static String wrongCharactersInLine = ConfigsReader.getStringValue("wrongCharactersInLine");
    public static String wrongBytesNumber_6_or_8 = ConfigsReader.getStringValue("wrongBytesNumber_6_or_8");
    public static String tooBigByte =       ConfigsReader.getStringValue("tooBigByte");
    public static String bigByte =          ConfigsReader.getStringValue("bigByte");
    public static String wrongBytesNumber = ConfigsReader.getStringValue("wrongBytesNumber");

}
