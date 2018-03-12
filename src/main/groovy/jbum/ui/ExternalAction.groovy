package jbum.ui;

import javax.swing.JButton;

import jbum.core.ImageInfo;
import jbum.core.ImageProcessor;
import jbum.core.Prefs;


public class ExternalAction implements Runnable {
    ImageInfo ii;


    public ExternalAction(ImageInfo ii) {
        this.ii = ii;
        new Thread(this, "ExternalAction").start();
    }

    public void run() {
        try {
            String target = ii.getOriginalFile(Main.getCurrentDir()).toString();

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
            ImageProcessor.enqueue(ii, ImageProcessor.SMALLER);
        } catch (Exception e) {
        	Main.error("running "+Prefs.getImageEditor(), "Unable to execute program: "+Prefs.getImageEditor()+
        			"\nYou may want to modify your program preferences in File/Preferences"+        			
        			"\n\nError:"+e.getMessage());
            e.printStackTrace();
        }
    }
}
