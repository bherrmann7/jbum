package jbum.core

import jbum.ui.App

import javax.swing.*
import java.awt.*
import java.awt.image.BufferedImage

class ImageProcessor implements Runnable {

    static final String CLOCKWISE = "rr";
    static final String COUNTER_CLOCKWISE = "rl";
    static final String SMALLER = "sm";
    static ImageProcessor ip;
    Vector vii = new Vector();
    Vector vtask = new Vector();

    static queueSize() {
        if (ip == null || ip.vii == null)
            return 0
        return ip.vii.size()
    }

    @SuppressWarnings("unchecked")
    static void enqueue(ImageInfo ii, String task) {
        synchronized (ImageProcessor.class) {
            if (ip == null) {
                ip = new ImageProcessor();
                new Thread(ip, "ImageProcessor").start();
            }
            ip.vii.add(ii);
            ip.vtask.add(task);
        }
    }

    void run() {
        try {
            ImageInfo ii
            String task

            while (true) {
                synchronized (ImageProcessor.class) {
                    if (vii.size() != 0) {
                        ii = (ImageInfo) vii.elementAt(0)
                        task = (String) vtask.elementAt(0)
                    } else {
                        // no work.
                        ip = null;
                        App.status("");
                        return
                    }
                }

                App.status("Processing " + ii.getName() + ", " + vii.size()
                        + " items in awaiting processing.")

                Image img = makeThumb(ii, task)

                // Problem with image processing returns null.
                if (img != null) {
                    JButton button = App.myself.centerP.imageName2button[ii.name]
                    button.setIcon(ImageCache.set(ii.getSmallFile(App.getCurrentDir()), img));
                    System.gc()
                    App.save()
                    // Should we throw here?  should we have error message?
                }

                synchronized (getClass()) {
                    // remove work item
                    vii.remove(0);
                    vtask.remove(0);
                }
            }
        } catch (Throwable t) {
            t.printStackTrace()
            App.error(t, "Error occurred during image processing");
        }
    }

    Image makeThumb(ImageInfo ii, String task) {
        // not sure where to really put this block. ImageDB class perhaps?
        // and get rid of vecii?

        File smallerdir = ii.getSmallFile(App.getCurrentDir()).getParentFile();
        if (!smallerdir.isDirectory()) {
            smallerdir.mkdir();
        }

        if (!smallerdir.isDirectory()) {
            throw new RuntimeException("Unable to create directory named 'smaller'");
        }

        Image img = new ImageIcon(ii.getOriginalFile(App.getCurrentDir()).toString()).getImage();
        int width = img.getWidth(null);
        int height = img.getHeight(null);

        // Dont know why, but this causes memory to sucked up like crazy
        if (!task.equals(SMALLER)) {
            int nwidth = height;
            int nheight = width;

            BufferedImage bimg = new BufferedImage(nwidth, nheight, BufferedImage.TYPE_INT_RGB);
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
                        .getOriginalFile(App.getCurrentDir()));
                com.sun.image.codec.jpeg.JPEGImageEncoder en = com.sun.image.codec.jpeg.JPEGCodec
                        .createJPEGEncoder(fileOut);

                en.encode(bimg);
            } catch (Exception e) {
                App.error(e, "Unable to write out image (permissions?) ");
                return null;
            }

            img.flush();
            bimg = null;

            // now reload newly rotated image.
            img = new ImageIcon(ii.getOriginalFile(App.getCurrentDir()).toString()).getImage();

        }

        // done w rotation.
        width = img.getWidth(null);
        height = img.getHeight(null);
        ii.imgSize = new Dimension(width, height);

        int mediumWidth = 1000;
        int mediumHeight = -1;
        if (height > 800) {
            mediumHeight = 800;
            mediumWidth = (width * mediumHeight) / height;
        }

        Image me = makeSmall(ii.getName(), img, ii.getMediumFile(App.getCurrentDir()), mediumWidth, mediumHeight);
        Image sm = makeSmall(ii.getName(), img, ii.getSmallFile(App.getCurrentDir()), 300, -1);

        ii.mediumSize = new Dimension(me.getWidth(null), me.getHeight(null));
        ii.smallSize = new Dimension(sm.getWidth(null), sm.getHeight(null));

        me.flush();
        img.flush();

        return sm;
    }

    Image makeSmall(String name, Image img, File outFile, int width,
                    int height) {
        try {
            Image smaller = img.getScaledInstance(width, height,
                    Image.SCALE_SMOOTH);

            ImageLoader ab = new ImageLoader(smaller, "getting smaller of : " + name);
            ab.waitForBits();

            // we are lazy, let scaledInstance figure out height
            height = smaller.getHeight(null);

            // copy smaller image into buffer
            BufferedImage bimg = new BufferedImage(width, height,
                    BufferedImage.TYPE_INT_RGB);
            Graphics2D g2 = bimg.createGraphics();
            g2.drawImage(smaller, 0, 0, width, height, null);
            g2.dispose();

            def fileOut = new java.io.FileOutputStream(outFile);
            def en = com.sun.image.codec.jpeg.JPEGCodec.createJPEGEncoder(fileOut);

            en.encode(bimg);
            bimg.flush();
            fileOut.close();
            return smaller;
        } catch (Exception e) {
            App.error(e, "Unable to write out image (permissions?) ");
            return null;
        }
    }

}