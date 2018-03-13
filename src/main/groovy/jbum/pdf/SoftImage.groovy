package jbum.pdf

import com.sun.image.codec.jpeg.JPEGCodec
import com.sun.image.codec.jpeg.JPEGImageEncoder
import jbum.core.ImageLoader

import java.awt.*
import java.awt.image.BufferedImage
import java.awt.image.FilteredImageSource
import java.awt.image.RGBImageFilter

class Soften extends RGBImageFilter {

    Soften() {
        canFilterIndexColorModel = true;
    }

    public int filterRGB(int x, int y, int rgb) {
        int h = original.getHeight(null);
        int w = original.getWidth(null);
        int tx = 0xFF;
        int ty = 0xFF;
        if (x < w / 10) {
            tx = x * 255 / (w / 10);
        }
        if (x > w - w / 10) {
            tx = (w - x) * 255 / (w / 10);
        }
        if (y < h / 10) {
            ty = y * 255 / (h / 10);
        }
        if (y > (h - h / 10)) {
            ty = (h - y) * 255 / (h / 10);
        }
        int t = 0xFF;
        if (tx != 0xFF || ty != 0xFF) {
            if (tx != 0xFF && ty != 0xFF) {
                t = Math.min(tx, ty);// /2;
                // //(int)Math.sqrt(tx*tx
                // + ty*ty);
            } else if (tx != 0xff)
                t = tx;
            else
                t = ty;
        }
        return rgb & (t << 24 | 0xFFFFFF);
    }
}


public class SoftImage {

    // Test method
    public static void main(String[] args) throws Exception {

        BufferedImage outImage = new BufferedImage(800, 800,
                BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = outImage.createGraphics();
        g2d.setColor(Color.BLUE);
        g2d.fillRect(0, 0, 800, 800);

        Image img = SoftImage
                .get("/jadn/babypea/2006/canon-xti/smaller/sm_img_0088.jpg");
        g2d.drawImage(img, 0, 0, null);
        g2d.dispose();

        // JPEG-encode the image and write to file.
        OutputStream os = new FileOutputStream("collage.jpg");
        JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(os);
        encoder.encode(outImage);
        os.close();
    }


    public static Image get(String imageFile) {

        final Image original = Toolkit.getDefaultToolkit().createImage(
                imageFile);
        ImageLoader watcher = new ImageLoader(original, "original " + imageFile);
        watcher.waitForBits();

        return get(original);
    }


    public static Image get(Image image) {
        final Image original = image;
        Image soft = Toolkit.getDefaultToolkit().createImage(
                new FilteredImageSource(original.getSource()), new Soften());
        ImageLoader watcher = new ImageLoader(soft, "softened");
        watcher.waitForBits();

        image.flush();
        return soft;
    }

}
