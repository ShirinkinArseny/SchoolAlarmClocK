package PC_part.SACK_pc_client.Configurable;

import PC_part.Common.Logger;
import PC_part.SACK_pc_client.Resources.Images;

import java.awt.*;
import java.io.FileInputStream;
import java.io.IOException;

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

    public static Font font;
    public static Font fontSmall;

    static {

        try {
            font = Font.createFont(Font.TRUETYPE_FONT,
                    Images.class.getResourceAsStream("FiraSans-Thin.ttf"));

            font=font.deriveFont(ConfigsReader.getFloatValue("bigFontSize"));

            Logger.logInfo(Design.class, "Font had been read successfully");
        } catch (Exception e) {
            Logger.logError(Design.class, "Cannot read font");
            new Exception().printStackTrace();
            System.exit(1);
        }

        try {
            fontSmall = Font.createFont(Font.TRUETYPE_FONT,
                    Images.class.getResourceAsStream("FiraSans-Thin.ttf"));

            fontSmall=fontSmall.deriveFont(ConfigsReader.getFloatValue("smallFontSize"));

            Logger.logInfo(Design.class, "Font had been read successfully");
        } catch (Exception e) {
            Logger.logError(Design.class, "Cannot read font");
            new Exception().printStackTrace();
            System.exit(1);
        }

    }

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