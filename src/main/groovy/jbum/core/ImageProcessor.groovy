package jbum.core;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JButton;

import jbum.ui.Main;

import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGImageEncoder;

public class ImageProcessor implements Runnable {

    public static final String CLOCKWISE = "rr";

    public static final String COUNTER_CLOCKWISE = "rl";

    public static final String SMALLER = "sm";

    static ImageProcessor ip;

    Vector vii = new Vector();

    //Vector vbutton = new Vector();

    Vector vtask = new Vector();

    //Vector vcolor = new Vector();

    @SuppressWarnings("unchecked")
	public static void enqueue(ImageInfo ii, String task) {
        synchronized (ImageProcessor.class) {
            if (ip == null) {
                ip = new ImageProcessor();
                new Thread(ip, "ImageProcessor").start();
            }

            ip.vii.add(ii);
            //ip.vbutton.add(button);
            ip.vtask.add(task);
            //ip.vcolor.add(button.getBackground());
            //button.setBackground(Color.yellow);
        }
    }

    public void run() {
    	try {
        ImageInfo ii = null;
        String task = null;

        while (true) {
            synchronized (ImageProcessor.class) {
                if (vii.size() != 0) {
                    ii = (ImageInfo) vii.elementAt(0);
                    task = (String) vtask.elementAt(0);
                } else {
                    // no work.
                    ip = null;
                    Main.status("");

                    return;
                }
            }

            Main.status("Processing " + ii.getName() + ", " + vii.size()
                    + " items in awaiting processing.");
            
            Image img = null;
            
            // handle movie
            if ( ii.getName().toLowerCase().endsWith(".avi") ||
            		ii.getName().toLowerCase().endsWith(".mov") 
            		){
            	
            	//makeMovieThumb(ii,task);
            	
            } else {

            	// do real work.
            	img = makeThumb(ii, task);
            
            }
            // Problem with image processing returns null.
            if (img != null) {
                System.gc();
                Main.save();
            }

            synchronized (getClass()) {
                // remove work item
                vii.remove(0);
                vtask.remove(0);
            }
        }
    	} catch (Throwable t){
    		Main.error(t,"Error occured during image processing");
    	}
    }

    Image makeThumb(ImageInfo ii, String task) {
        // not sure where to really put this block. ImageDB class perhaps?
        // and get rid of vecii?

        File smallerdir = ii.getSmallFile(Main.getCurrentDir()).getParentFile();
        if (!smallerdir.isDirectory()) {
            smallerdir.mkdir();
        }

        if (!smallerdir.isDirectory()) {
            throw new RuntimeException(
                    "Unable to create directory named 'smaller'");
        }

        Image img = new ImageIcon(ii.getOriginalFile(Main.getCurrentDir()).toString()).getImage();
//         Image img = java.awt.Toolkit.getDefaultToolkit().createImage(
//        		 ii.getOriginalFile(Main.getCurrentDir()).toString());
//        
//         {
//         ImageLoader ab = new ImageLoader(img, ii.getName());
//         ab.waitForBits();
//         }

        int width = img.getWidth(null);
        int height = img.getHeight(null);

        // Dont know why, but this causes memory to sucked up like crazy
        if (!task.equals(SMALLER)) {
            int nwidth = height;
            int nheight = width;

            BufferedImage bimg = new BufferedImage(nwidth, nheight,
                    BufferedImage.TYPE_INT_RGB);

            Graphics2D g2 = bimg.createGraphics();

            double theta = 0;

            if (task.equals(CLOCKWISE)) {
                // Set rotation to 90 degrees clockwise and reposition
                theta = 1.5707963267948966192313216916398;
                g2.translate(height, 0);
            } else {
                theta = -1.5707963267948966192313216916398;
                g2.translate(0, width);
            }

            // Rotate the image
            g2.rotate(theta);

            // Draw the image
            g2.drawImage(img, 0, 0, width, height, null);
            g2.dispose();
            g2 = null;

            try {
                FileOutputStream fileOut = new FileOutputStream(ii
                        .getOriginalFile(Main.getCurrentDir()));
                com.sun.image.codec.jpeg.JPEGImageEncoder en = com.sun.image.codec.jpeg.JPEGCodec
                        .createJPEGEncoder(fileOut);

                en.encode(bimg);
            } catch (Exception e) {
                Main.error(e, "Unable to write out image (permissions?) ");
                return null;
            }

            img.flush();
            bimg = null;

            // now reload newly rotated image.
            img = new ImageIcon(ii.getOriginalFile(Main.getCurrentDir()).toString()).getImage();

        }

        // done w rotation.
        width = img.getWidth(null);
        height = img.getHeight(null);
        ii.imgSize = new Dimension(width, height);
        
        int mediumWidth = 700;
        int mediumHeight = -1;
        if ( height > 600 ) {
        	mediumHeight = 600;
        	mediumWidth =  (width * mediumHeight)/height ;
        }
        	

        Image me = makeSmall(ii.getName(), img, ii.getMediumFile(Main.getCurrentDir()), mediumWidth, mediumHeight);
        Image sm = makeSmall(ii.getName(), img, ii.getSmallFile(Main.getCurrentDir()), 180, -1);

        ii.mediumSize = new Dimension(me.getWidth(null), me.getHeight(null));
        ii.smallSize = new Dimension(sm.getWidth(null), sm.getHeight(null));

        me.flush();
        me = null;

        img.flush();
        img = null;

        return sm;
    }

    /**
     * This version creates jaggy small images
     */
    Image makeSmall2(String name, Image img, File outFile, int width, int height) {
        try {

            double scale = (double) width / (double) img.getWidth(null);

            int scaledW = (int) (scale * img.getWidth(null));
            int scaledH = (int) (scale * img.getHeight(null));

            // Create an image buffer in which to paint on.
            BufferedImage outImage = new BufferedImage(scaledW, scaledH,
                    BufferedImage.TYPE_INT_RGB);

            // Set the scale.
            AffineTransform tx = new AffineTransform();
            tx.scale(scale, scale);

            // Paint image.
            Graphics2D g2d = outImage.createGraphics();
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2d.drawImage(img, tx, null);
            g2d.dispose();

            // JPEG-encode the image and write to file.
            OutputStream os = new FileOutputStream(outFile);
            JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(os);
            encoder.encode(outImage);
            os.close();
            return outImage;
        } catch (Exception e) {
            Main.error(e, "Unable to write out image (permissions?) ");
            return null;
        }
    }

    Image makeSmall(String name, Image img, File outFile, int width,
            int height) {
        try {
            Image smaller = img.getScaledInstance(width, height,
                    Image.SCALE_SMOOTH);

            ImageLoader ab = new ImageLoader(smaller, "getting smaller of : "
                    + name);
            ab.waitForBits();

            // we are lazy, let scaledInstance figure out height
            height = smaller.getHeight(null);

            // copy smaller image into buffer
            BufferedImage bimg = new BufferedImage(width, height,
                    BufferedImage.TYPE_INT_RGB);
            Graphics2D g2 = bimg.createGraphics();
            g2.drawImage(smaller, 0, 0, width, height, null);
            g2.dispose();

            java.io.FileOutputStream fileOut = new java.io.FileOutputStream(
                    outFile);
            com.sun.image.codec.jpeg.JPEGImageEncoder en = com.sun.image.codec.jpeg.JPEGCodec
                    .createJPEGEncoder(fileOut);

            en.encode(bimg);

            bimg.flush();

            fileOut.close();

            return smaller;
        } catch (Exception e) {
            Main.error(e, "Unable to write out image (permissions?) ");
            return null;
        }
    }

}