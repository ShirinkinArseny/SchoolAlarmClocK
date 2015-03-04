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

    public static final Font font = new Font("Fira Sans Thin", Font.PLAIN, 35);
    public static final Font fontSmall = new Font("Fira Sans Thin", Font.PLAIN, 20);

    public static final Stroke fat = new BasicStroke(3);

    public static final int shadowRadius =
            ConfigsReader.getIntValue("shadowRadius");

    public static final float shadowOpacity =
            ConfigsReader.getFloatValue("shadowOpacity");

    public static final boolean drawShadows =
            ConfigsReader.getIntValue("drawShadows") == 1;

    public static final float activityChangeTime =
            ConfigsReader.getFloatValue("activityChangeTime");
}