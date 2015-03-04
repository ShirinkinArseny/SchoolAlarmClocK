package PC_part.SACK_pc_client.Configurable;

public class Labels {


    /*
    Connection
     */
    public static final String updatePortsList = ConfigsReader.getStringValue("updatePortsList");
    public static final String connectToSelected = ConfigsReader.getStringValue("connectToSelected");
    public static final String disconnect = ConfigsReader.getStringValue("disconnect");
    public static final String network = ConfigsReader.getStringValue("network");
    public static final String usablePorts = ConfigsReader.getStringValue("usablePorts");
    public static final String noUsablePorts = ConfigsReader.getStringValue("noUsablePorts");

    /*
    Connection results
     */
    public static final String noConnections = ConfigsReader.getStringValue("noConnections");
    public static final String disconnection = ConfigsReader.getStringValue("disconnection");
    public static final String connectionNow = ConfigsReader.getStringValue("connectionNow");
    public static final String connectedTo = ConfigsReader.getStringValue("connectedTo");
    public static final String cannotConnectTo = ConfigsReader.getStringValue("cannotConnectTo");

    /*
    Common
     */
    public static final String cannotPerformOperationCuzNoConnection =
            ConfigsReader.getStringValue("cannotPerformOperationCuzNoConnection");

    /*
    Time
     */
    public static final String getTimeFromDuino = ConfigsReader.getStringValue("getTimeFromDuino");
    public static final String writeLocalTime = ConfigsReader.getStringValue("writeLocalTime");
    public static final String duinoTime = ConfigsReader.getStringValue("duinoTime");
    public static final String localTime = ConfigsReader.getStringValue("localTime");

    /*
    Rings
     */
    public static final String addOneMoreRing = ConfigsReader.getStringValue("addOneMoreRing");
    public static final String removeSelected = ConfigsReader.getStringValue("removeSelected");
    public static final String copySelected = ConfigsReader.getStringValue("copySelected");
    public static final String insert = ConfigsReader.getStringValue("insert");
    public static final String dropSelection = ConfigsReader.getStringValue("dropSelection");
    public static final String getRingsFromDuino = ConfigsReader.getStringValue("getRingsFromDuino");
    public static final String writeRingsToDuino = ConfigsReader.getStringValue("writeRingsToDuino");

    /*
    Main menu
     */
    public static final String chart = ConfigsReader.getStringValue("chart");
    public static final String table = ConfigsReader.getStringValue("table");
    public static final String timeSync = ConfigsReader.getStringValue("timeSync");
    public static final String connection = ConfigsReader.getStringValue("connection");
    public static final String exit = ConfigsReader.getStringValue("exit");

    /*
    Weekdays
     */
    public static final String monday = ConfigsReader.getStringValue("monday");
    public static final String tuesday = ConfigsReader.getStringValue("tuesday");
    public static final String wednesday = ConfigsReader.getStringValue("wednesday");
    public static final String thursday = ConfigsReader.getStringValue("thursday");
    public static final String friday = ConfigsReader.getStringValue("friday");
    public static final String saturday = ConfigsReader.getStringValue("saturday");
    public static final String sunday = ConfigsReader.getStringValue("sunday");


    public static final String hours = ConfigsReader.getStringValue("hours");
    public static final String minutes = ConfigsReader.getStringValue("minutes");
    public static final String seconds = ConfigsReader.getStringValue("seconds");

    /*
    Time dialogue
     */
    public static final String empty = ConfigsReader.getStringValue("empty");
    public static final String cannotParseTime = ConfigsReader.getStringValue("cannotParseTime");

    /*
    IP dialogue
     */
    public static final String wrongIP =          ConfigsReader.getStringValue("wrongIP");
    public static final String niceIP =           ConfigsReader.getStringValue("niceIP");
    public static final String emptyIP =          ConfigsReader.getStringValue("emptyIP");
    public static final String wrongCharactersInLine = ConfigsReader.getStringValue("wrongCharactersInLine");
    public static final String wrongBytesNumber_6_or_8 = ConfigsReader.getStringValue("wrongBytesNumber_6_or_8");
    public static final String tooBigByte =       ConfigsReader.getStringValue("tooBigByte");
    public static final String bigByte =          ConfigsReader.getStringValue("bigByte");
    public static final String wrongBytesNumber = ConfigsReader.getStringValue("wrongBytesNumber");

    public static final String networkErrors=ConfigsReader.getStringValue("networkErrors");
    public static final String cannotDisconnect=ConfigsReader.getStringValue("cannotDisconnect");

    public static final String cannotReadConfigsValue="Не могу прочитать параметр из конфига: ";
    public static final String cuz=", потому что ";
    public static final String cannotReadConfigsFile="Не могу прочитать конфиги :c";
    public static final String wrongConfigs ="Кривые конфиги: ";
}
