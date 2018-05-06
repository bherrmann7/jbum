package jbum.ui

import jbum.core.ImageInfo
import jbum.core.Prefs

class ExternalAction implements Runnable {
    ImageInfo ii;


     ExternalAction(ImageInfo ii) {
        this.ii = ii;
        new Thread(this, "ExternalAction").start();
    }

     void run() {
        try {
            String target = ii.getOriginalFile(App.getCurrentDir()).toString();

            println("Target is $target")

            //			StringBuffer tt = new StringBuffer();
            //			for (int i = 0; i < target.length(); i++) {
            //				if (target.charAt(i)==' ')
            //					tt.append('\\');
            //				tt.append(target.charAt(i));
            //			}
            Process p = Runtime.getRuntime().exec([
                    Prefs.getImageEditor(), target
            ] as String[]);
            p.waitFor();
            //ImageProcessor.enqueue(ii, ImageProcessor.SMALLER);
        } catch (Exception e) {
            App.error("running " + Prefs.getImageEditor(), "Unable to execute program: " + Prefs.getImageEditor() +
                    "\nYou may want to modify your program preferences in File/Preferences" +
                    "\n\nError:" + e.getMessage());
            e.printStackTrace();
        }
    }
}
