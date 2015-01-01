package jbum.core.test

import jbum.core.*;
import jbum.ui.*;
import javax.swing.JMenuBar;
import javax.swing.JButton;

class ImageTest extends junit.framework.TestCase {
	
	void testImageRotate(){
		DeletionManager dm = new DeletionManager(new JMenuBar());
		Main.myself = new Main();
		Main.myself.centerP = new CenterP(dm);
		Main.myself.centerP.currentDir = new File("/tmp");
		
		// copy test file
		(0..2).each {
			def fos = new FileOutputStream("/tmp/img_100"+it+".jpg");
			fos.write(new File("src/jbum/core/test/img_1000.jpg").readBytes())
			fos.close();
		}
		
		System.gc();
		System.gc();
		int start = Runtime.getRuntime().totalMemory();
		
		JButton jlabel = new JButton();
	
		(0..2).each {
			ImageInfo ii = new ImageInfo(new File("img_100"+it+".jpg"),null,null,null);
			//ImageProcessor.enqueue(ii, jlabel, ImageProcessor.SMALLER);
			ImageProcessor.enqueue(ii, jlabel, ImageProcessor.CLOCKWISE);
			//ImageProcessor.enqueue(ii, jlabel, ImageProcessor.COUNTER_CLOCKWISE);
			
			//ip.makeThumb(ii, ip.CLOCKWISE);
			//ip.makeThumb(ii, ip.COUNTER_CLOCKWISE);
		}
		
		// snapshot of ip
		ImageProcessor sip = ImageProcessor.ip;
		
		while (sip!=null && sip.vii.size()!=0) {
			Thread.sleep(1000);			
			println jlabel.background.toString() + " "+ sip.vii.size();
			System.gc();
			System.gc();
			int end = Runtime.getRuntime().totalMemory();
			
			println "memory used ${(end-start)/1000000} MB"
			sip = ImageProcessor.ip;
		}
		
	}
}