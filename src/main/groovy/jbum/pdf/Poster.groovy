package jbum.pdf;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;

import jbum.core.DPage;
import jbum.core.IndexScanner;
import jbum.core.IndexScanner.DirEntry;

import com.lowagie.text.Document;
import com.lowagie.text.Rectangle;
import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGImageEncoder;

public class Poster {

	public static void main(String[] args) throws Exception {
		if (args.length==0){
			args = [ "20", "30", "/tmp/samples" ] as String[];
		}
		// 20x30
		// 20 * 300 = 6000
		// 30 * 300 = 9000

		// 16 x 20
		// 16*300 = 4800
		// 20*300 = 6000

		// 9600 x 12000 too big

		// 20 * 450 = 9000
		// 30 * 450 = 13500

		int width = Integer.parseInt(args[0]);
		int height = Integer.parseInt(args[1]);
		File imagesDir = new File(args[2]);
		
		BufferedImage outImage = new BufferedImage(width*300, height*300,
				BufferedImage.TYPE_INT_RGB);

		Graphics2D g2d = outImage.createGraphics();
		g2d.setColor(Color.YELLOW);
		g2d.fillRect(0, 0, outImage.getWidth(), outImage.getHeight());

		Random r = new Random();
		File[] files = imagesDir.listFiles(); 
		for (int i = 0; i < files.length; i++) {
			if (!files[i].toString().endsWith(".JPG"))
				continue;

				Image img = SoftImage.get(files[i].toString());
				int x = r.nextInt(outImage.getWidth()) - 50;
				int y = r.nextInt(outImage.getHeight()) - 50;

				g2d.drawImage(img, x, y, null);
				img.flush();
				img = null;
				System.gc();
				System.out.print(".");
				System.out.flush();
		
		}

		// JPEG-encode the image and write to file.
		OutputStream os = new FileOutputStream("collage.jpg");
		JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(os);
		encoder.encode(outImage);
		os.close();

	}

}
