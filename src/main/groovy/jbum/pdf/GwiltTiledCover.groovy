package jbum.pdf

import com.sun.image.codec.jpeg.JPEGCodec
import com.sun.image.codec.jpeg.JPEGImageEncoder
import jbum.core.DPage
import jbum.core.ImageInfo

import java.awt.*
import java.awt.image.BufferedImage

DPage dp = new DPage(new File("/home/bob/Desktop/gwilt-book/db/jbum.ser"), false);

int spine = 117;
int coverWidth = 5475 + spine
int coverHeight = 3750;

BufferedImage outImage = new BufferedImage(coverWidth, coverHeight,
        BufferedImage.TYPE_INT_RGB);

Graphics2D g2d = outImage.createGraphics();
g2d.setColor(Color.WHITE);
g2d.fillRect(0, 0, outImage.getWidth(), outImage.getHeight());

int cx = coverWidth - 180
int cy = 0

while (cx > -170) {
    dp.getVii().vec.each { ImageInfo ii ->
        if (cx < -170)
            return;

        File f = ii.getSmallFile(dp.getWhere());

        Image img = SoftImage.get(f.toString());
        g2d.drawImage(img, cx, cy, null);

        cy += img.getHeight(null) - 10;
        if (cy > coverHeight) {
            cx -= 170;
            cy = 0;
        }
    }
}

int tWidth = 2800;
int tHeight = 450;
tWidth = 2800 / 3;
tHeight = tHeight / 3;

BufferedImage titleImage = new BufferedImage(tWidth, tHeight,
        BufferedImage.TYPE_INT_RGB);

Graphics2D tg2d = titleImage.createGraphics();

float font = 160f;
float font2 = 100f;
font = font / 3;
font2 = font2 / 3;

Font[] fonts = java.awt.GraphicsEnvironment
        .getLocalGraphicsEnvironment().getAllFonts();
Font useme = fonts[17].deriveFont(font);
tg2d.setFont(useme);
tg2d.drawString("Scott and Andrea's Wedding", (int) (360 / 3), (int) (200 / 3));
useme = fonts[17].deriveFont(font2);
tg2d.setFont(useme);
tg2d.drawString("February 24, 2007", (int) (900 / 3), (int) (350 / 3));
tg2d.dispose();

Image softTitle = SoftImage.get(titleImage);

int sw = softTitle.getWidth(null);
int xpos = (outImage.getWidth() - sw) / 2;

xpos = outImage.getWidth() - 231/* spine */;

/*
 * s = 231; <-- width ---------> | |s| | | | | title |
 */
int nospine = outImage.getWidth() - 231;
int half = nospine / 2;
int centerCover = outImage.getWidth() - half / 2;
xpos = centerCover - sw / 2;

g2d.drawImage(softTitle, xpos, (int) (outImage.getHeight() * 0.15),
        null);

// JPEG-encode the image and write to file.
OutputStream os = new FileOutputStream("gwiltCover.jpg");
JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(os);
encoder.encode(outImage);
os.close();
