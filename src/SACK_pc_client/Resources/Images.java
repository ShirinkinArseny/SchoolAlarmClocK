package SACK_pc_client.Resources;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Images {

    public static final BufferedImage bigBackArrow=loadImage("BigBackArrow.png");
    public static final BufferedImage backArrow=loadImage("BackArrow.png");
    public static final BufferedImage darkDoubleArrow = loadImage("DarkDoubleArrow.png");
    public static final BufferedImage lightDoubleArrow = loadImage("LightDoubleArrow.png");
    public static final BufferedImage darkArrow = loadImage("DarkArrow.png");
    public static final BufferedImage markBoards = loadImage("MarkBoarders.png");
    public static final BufferedImage markFilled = loadImage("MarkFilled.png");

    private static BufferedImage loadImage(String s) {
        try {
            return ImageIO.read(new File("/home/nameless/IdeaProjects/SoundTest/src/SACK_pc_client/Resources/"+s));
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
            return null;
        }
    }

}
