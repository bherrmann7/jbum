/*
 * Created on Apr 21, 2005
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package jbum.other

import com.sun.image.codec.jpeg.JPEGCodec
import com.sun.image.codec.jpeg.JPEGImageEncoder

import javax.swing.*
import java.awt.*
import java.awt.geom.AffineTransform
import java.awt.image.BufferedImage

/**
 * lifted from,
 * http://java.sun.com/developer/TechTips/txtarchive/1999/Oct99_PatrickC.txt
 * @author bob
 */

 class ThumbnailMaker {

     static void main(String[] args) {
        Date start = new Date();
        createThumbnail("/tmp/cimg0173.jpg", "/tmp/thumb.jpg", 200);
        Date end = new Date();
        System.out.println("Took " + (end.getTime() - start.getTime()) / 1000 + " seconds.");
        System.exit(0);
    }

    /**
     * Reads an image in a file and creates a thumbnail in another file.
     *
     * @param orig
     *            The name of image file.
     * @param thumb
     *            The name of thumbnail file. Will be created if necessary.
     * @param maxDim
     *            The width and height of the thumbnail must be maxDim pixels or
     *            less.
     */
     static void createThumbnail(String orig, String thumb, int maxDim) {
        try {
            // Get the image from a file.
            Image inImage = new ImageIcon(orig).getImage();

            // Determine the scale.
            double scale = (double) maxDim / (double) inImage.getHeight(null);
            if (inImage.getWidth(null) > inImage.getHeight(null)) {
                scale = (double) maxDim / (double) inImage.getWidth(null);
            }

            // Determine size of new image. One of them
            // should equal maxDim.
            int scaledW = (int) (scale * inImage.getWidth(null));
            int scaledH = (int) (scale * inImage.getHeight(null));

            // Create an image buffer in which to paint on.
            BufferedImage outImage = new BufferedImage(scaledW, scaledH,
                    BufferedImage.TYPE_INT_RGB);

            // Set the scale.
            AffineTransform tx = new AffineTransform();

            // If the image is smaller than the desired image size,
            // don't bother scaling.
            if (scale < 1.0d) {
                tx.scale(scale, scale);
            }

            // Paint image.
            Graphics2D g2d = outImage.createGraphics();
            g2d.drawImage(inImage, tx, null);
            g2d.dispose();

            // JPEG-encode the image and write to file.
            OutputStream os = new FileOutputStream(thumb);
            JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(os);
            encoder.encode(outImage);
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
