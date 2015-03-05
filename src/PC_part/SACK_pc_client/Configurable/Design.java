package PC_part.SACK_pc_client.Configurable;

import java.awt.*;

public class Design {

    public static final Color lightBackgroundColor =
            new Color(ConfigsReader.getIntValue("lightBackgroundColor"));

    public static final Color lightForegroundColor =
            new Color(ConfigsReader.getIntValue("lightForegroundColor"));

    public static final Color darkForegroundColor =
            new Color(ConfigsReader.getIntValue("darkForegroundColor"));

    public static final Color lightFontColor =
            new Color(ConfigsReader.getIntValue("lightFontColor"));

    public static final Color darkFontColor =
            new Color(ConfigsReader.getIntValue("darkFontColor"));

    public static final Font font = new Font(ConfigsReader.getStringValue("bigFontName"),
            Font.PLAIN, ConfigsReader.getIntValue("bigFontSize"));
    public static final Font fontSmall = new Font(ConfigsReader.getStringValue("smallFontName"),
            Font.PLAIN, ConfigsReader.getIntValue("smallFontSize"));

    public static final Stroke fat = new BasicStroke(3);

    public static final int shadowRadius =
            ConfigsReader.getIntValue("shadowRadius");

    public static final float shadowOpacity =
            ConfigsReader.getFloatValue("shadowOpacity");

    public static final boolean drawShadows =
            ConfigsReader.getIntValue("drawShadows") == 1;

    public static final float activityChangeTime =
            ConfigsReader.getFloatValue("activityChangeTime");

    public static final boolean windowBoarders =
            ConfigsReader.getIntValue("windowBoarders")==1;

    public static final boolean dragWindow =
            ConfigsReader.getIntValue("dragWindow")==1;

    public static final boolean resizableWindow =
            ConfigsReader.getIntValue("resizableWindow")==1;
}