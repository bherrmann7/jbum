package jbum.pdf

import com.sun.image.codec.jpeg.JPEGCodec
import com.sun.image.codec.jpeg.JPEGImageEncoder
import jbum.core.DPage
import jbum.core.ImageInfo

import java.awt.*
import java.awt.image.BufferedImage

public class Tile {

    public static void main(String[] args) throws Exception {

        float width = (float) ((17.25 + 0.77) * 72);
        float height = (float) (11.25 * 72);

        System.out.println("Image should be size " + width * 300 / 72 + "x"
                + height * 300 / 72);

        int iwidth = (int) width * 300 / 72;
        int iheight = (int) height * 300 / 72;

        BufferedImage outImage = new BufferedImage(iwidth, iheight,
                BufferedImage.TYPE_INT_RGB);

        Graphics2D g2d = outImage.createGraphics();
        g2d.setColor(Color.YELLOW);
        g2d.fillRect(0, 0, outImage.getWidth(), outImage.getHeight());

        int startX = 0;
        int currentY = 0;
        int highestImg = 0;

        DPage dp = new DPage(new File("/home/bob/Desktop/db/jbum.ser"), false);
        System.out.println(dp.getTitle());
        System.out.println(dp.getWhere());

        ArrayList<ImageInfo> skipped = new ArrayList<ImageInfo>();
        int skipWidth = 0;

        for (int kk = 0; kk < 2; kk++) {
            for (int i = 0; i < dp.getVii().size(); i++) {
                ImageInfo ii = dp.getVii().get(i);
                if (ii.imgSize.height > ii.imgSize.width) {
                    skipped.add(ii);
                    skipWidth += ii.imgSize.width - 5;
                    continue;
                }

                File f = ii.getSmallFile(dp.getWhere());

                Image img = SoftImage.get(f.toString());

                g2d.drawImage(img, startX, currentY, null);

                if (img.getHeight(null) > highestImg) {
                    highestImg = img.getHeight(null);
                }
                startX += img.getWidth(null) - 5;
                if (startX > iwidth) {
                    currentY += highestImg - 5;
                    highestImg = 0;
                    startX = 0;
                }

                if (currentY > iheight) {
                    System.out.println("early exit.");
                    break;
                }

                img.flush();
                img = null;
                System.gc();
                System.out.println(i + " " + dp.getVii().size());
            }

            for (ImageInfo ii : skipped) {
                File f = ii.getSmallFile(dp.getWhere());

                Image img = SoftImage.get(f.toString());

                g2d.drawImage(img, startX, currentY, null);

                if (img.getHeight(null) > highestImg) {
                    highestImg = img.getHeight(null);
                }
                startX += img.getWidth(null) - 5;

                if (startX > iwidth) {
                    currentY += highestImg - 5;
                    highestImg = 0;
                    startX = 0;
                }

                if (currentY > iheight) {
                    System.out.println("early exit.");
                    break;
                }

                img.flush();
                img = null;
                System.gc();
                System.out.println(ii.getName() + " " + dp.getVii().size());
            }
            skipped.clear();
        }
        /*
         * int tWidth = 2800; int tHeight = 450; tWidth = 2800 / 3; tHeight =
         * tHeight / 3;
         *
         * BufferedImage titleImage = new BufferedImage(tWidth, tHeight,
         * BufferedImage.TYPE_INT_RGB);
         *
         * Graphics2D tg2d = titleImage.createGraphics();
         *
         * float font = 160f; float font2 = 100f; font = font / 3; font2 = font2 /
         * 3;
         *
         * Font[] fonts = java.awt.GraphicsEnvironment
         * .getLocalGraphicsEnvironment().getAllFonts(); Font useme =
         * fonts[17].deriveFont(font); tg2d.setFont(useme);
         * tg2d.drawString("Scott and Andrea's", 300 / 3, 200 / 3); useme =
         * fonts[17].deriveFont(font2); tg2d.setFont(useme);
         * tg2d.drawString("Wedding - Feb 24, 2007", 900 / 3, 350 / 3);
         * tg2d.dispose();
         *
         * Image softTitle = SoftImage.get(titleImage);
         *
         * int sw = softTitle.getWidth(null); int xpos = (outImage.getWidth() -
         * sw) / 2;
         *
         * xpos = outImage.getWidth() - 231;// spine
         *
         *
         * s = 231; <-- width ---------> | |s| | | | | title |
         *
         * int nospine = outImage.getWidth() - 231; int half = nospine / 2; int
         * centerCover = outImage.getWidth() - half / 2; xpos = centerCover - sw /
         * 2;
         *
         * g2d.drawImage(softTitle, xpos, (int) (outImage.getHeight() * 0.15),
         * null);
         *
         */

        // JPEG-encode the image and write to file.
        OutputStream os = new FileOutputStream("tile.jpg");
        JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(os);
        encoder.encode(outImage);
        os.close();

    }

}
