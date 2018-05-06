package jbum.core

import java.awt.*
import java.awt.image.ImageObserver

 class ImageLoader implements ImageObserver {

    boolean ok;
    String imageName;

     ImageLoader(String imageName) {
        this.imageName = imageName;
    }

     ImageLoader(Image img, String imageName) {
        this.imageName = imageName;
        if (img.getHeight(this) != -1) {
            ok = true;
        }
    }

     boolean imageUpdate(Image img, int infoflags, int x, int y,
                               int width, int height) {

        // get rid of warnings
        if (false) {
            System.out.println(img.toString() + width + height + x + y);
        }

        if ((infoflags & ERROR) != 0) {
            System.err.println("ERROR: error bit set to on, " + imageName);
            //System.exit(1);
        }
        if ((infoflags & ABORT) != 0) {
            System.err.println("ERROR: abort bit set to on, " + imageName);
            //System.exit(1);
        }
        if ((infoflags & ALLBITS) != 0) {
            ok = true;
            return false;
        }

        return true; // indicates I want more info
    }

     void waitForBits() {
        while (!ok)
            try {
                Thread.sleep(100);
            } catch (Exception e) {
            }
    }
}