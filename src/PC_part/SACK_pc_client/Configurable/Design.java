package PC_part.SACK_pc_client.Configurable;

import java.awt.*;

public class Design {

    public static Color lightBackgroundColor =
            new Color(ConfigsReader.getIntValue("lightBackgroundColor"));

    public static Color lightForegroundColor =
            new Color(ConfigsReader.getIntValue("lightForegroundColor"));

    public static Color darkForegroundColor =
            new Color(ConfigsReader.getIntValue("darkForegroundColor"));

    public static Color lightFontColor =
            new Color(ConfigsReader.getIntValue("lightFontColor"));

    public static Color darkFontColor =
            new Color(ConfigsReader.getIntValue("darkFontColor"));

    public static Font font = new Font("Fira Sans Thin", Font.PLAIN, 35);
    public static Font fontSmall = new Font("Fira Sans Thin", Font.PLAIN, 20);

    public static Stroke fat = new BasicStroke(3);

    public static final int shadowRadius =
            ConfigsReader.getIntValue("shadowRadius");

    public static final float shadowOpacity =
            ConfigsReader.getFloatValue("shadowOpacity");

    public static final boolean drawShadows =
            ConfigsReader.getIntValue("drawShadows") == 1;

    public static float activityChangeTime =
            ConfigsReader.getFloatValue("activityChangeTime");
}