package PC_part.SACK_pc_client.Resources;

import PC_part.SACK_pc_client.Configurable.Design;
import PC_part.SACK_pc_client.Controls.*;
import PC_part.SACK_pc_client.Controls.Menu;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.*;
import java.io.IOException;

public class Images {

    public static final BufferedImage bigBackArrow=
            multiplyToColor(loadImage("BigBackArrow.png"), Design.lightForegroundColor);

    public static final BufferedImage bigBackDoubleArrow=
            multiplyToColor(loadImage("BigDoubleBackArrow.png"), Design.lightForegroundColor);

    public static final BufferedImage backArrow=
            multiplyToColor(loadImage("BackArrow.png"), Design.lightForegroundColor);

    public static final BufferedImage darkDoubleArrow =
            multiplyToColor(loadImage("DarkDoubleArrow.png"), Design.darkForegroundColor);

    public static final BufferedImage lightDoubleArrow =
            multiplyToColor(loadImage("LightDoubleArrow.png"), Design.lightForegroundColor);

    public static final BufferedImage bigArrow =
            multiplyToColor(loadImage("DarkArrow.png"), Design.darkForegroundColor);

    public static final BufferedImage markFilled =
            multiplyToColor(loadImage("MarkFilled.png"), Design.lightForegroundColor);

    public static final BufferedImage markShadow =
            getShadowed(loadImage("MarkFilled.png"));

    public static final BufferedImage shadowMenu =
            getShadowed(getBlackRect(Menu.itemWidth, UICanvas.windowHeight));

    public static final BufferedImage doubleArrowShadowed = getShadowed(darkDoubleArrow);
    public static final BufferedImage bigBackDoubleArrowShadowed = getShadowed(bigBackDoubleArrow);
    public static final BufferedImage bigArrowShadow = getShadowed(bigArrow);

    private static BufferedImage getBlackRect(int w, int h) {
        BufferedImage sized=new BufferedImage(
                w,
                h,
                BufferedImage.TYPE_INT_ARGB
        );
        Graphics g2=sized.getGraphics();

        g2.setColor(new Color(0, 0, 0));
        g2.fillRect(0, 0, w, h);
        g2.dispose();
        return sized;

    }

    private static BufferedImage loadImage(String s) {
        try {
            return ImageIO.read(Images.class.getResourceAsStream(s));
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
            return null;
        }
    }

    private static BufferedImage multiplyToColor(BufferedImage img, Color c) {
        float[] scales = { c.getRed()/255f, c.getGreen()/255f, c.getBlue()/255f, c.getAlpha()/255f };
        float[] offsets = new float[4];
        RescaleOp rop = new RescaleOp(scales, offsets, null);
        return rop.filter(img, img);
    }

    private static BufferedImage getShadowed(BufferedImage img) {

        if (Design.drawShadows) {
            BufferedImage sized = new BufferedImage(
                    img.getWidth() + 2 * Design.shadowRadius,
                    img.getHeight() + 2 * Design.shadowRadius,
                    BufferedImage.TYPE_INT_ARGB
            );
            Graphics g2 = sized.getGraphics();
            g2.drawImage(img, Design.shadowRadius, Design.shadowRadius, null);
            g2.dispose();

            float[] scales = {0, 0, 0, Design.shadowOpacity};
            float[] offsets = new float[4];
            RescaleOp rop = new RescaleOp(scales, offsets, null);
            sized = rop.filter(sized, sized);

            int square = Design.shadowRadius * Design.shadowRadius;
            float sum = 0;
            float[] matrix = new float[square];
            for (int i = 0; i < square; i++) {
                int dx = i % Design.shadowRadius - Design.shadowRadius / 2;
                int dy = i / Design.shadowRadius - Design.shadowRadius / 2;
                matrix[i] = (float) (Design.shadowRadius - Math.sqrt(dx * dx + dy * dy));
                sum += matrix[i];
            }
            for (int i = 0; i < square; i++) {
                matrix[i] /= sum;
            }

            BufferedImageOp op = new ConvolveOp(
                    new Kernel(Design.shadowRadius, Design.shadowRadius, matrix),
                    ConvolveOp.EDGE_NO_OP, null);


            return op.filter(sized, null);
        }
        else return null;


    }

}
