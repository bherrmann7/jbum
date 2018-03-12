package jbum.ui;

import java.io.DataInputStream;
import java.net.URL;

import javax.swing.JFrame;

import jbum.core.Version;

public class Update extends Thread {

	public static void check(JFrame frame){
//		Update update = new Update(frame);
//		update.start();
	}

	@SuppressWarnings("deprecation")
	public void run() {
		try {
			URL url = new URL("http://jadn.com/jbum/update.dat");
			DataInputStream dis = new DataInputStream(url.openStream());
			String version = dis.readLine();
			if ( !Version.VERSION.equals( version ) ){
				Main.warning("Version mismatch", "Your version of jbum doenst match the lastest released version.\nYours = "+Version.VERSION+"\nHosted at jadn.com/jbum/ = "+version);
			}
		} catch (Throwable t){
			//t.printStackTrace();
		}
		
		
	}
}
