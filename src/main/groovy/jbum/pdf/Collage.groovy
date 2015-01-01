package jbum.pdf;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
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

public class Collage {

	public static void main(String[] args) throws Exception {

		// 20x30
		// 20 * 300 = 6000
		// 30 * 300 = 9000

		// 16 x 20
		// 16*300 = 4800
		// 20*300 = 6000

		// 9600 x 12000 too big

		// 20 * 450 = 9000
		// 30 * 450 = 13500
		
		
		// 8x11 book cover => 17.25" (+ spine) x 11.25"
		// spine for 64 pages is 0.16
		
//		float spine = (float)0.16;
//		float width =  (float)((17.25+spine)*300);
//		float height = (float)(11.25*300);
		
		
		int widthFace = 20 * 300 ; //(int)17.25*300;		
		int heightPixels = 30 * 300 ; //(int)11.25*300;		
		int spinePixels = 0 ; //(int)(0.16*300);
		
		
		// photo cling
		//widthFace=17*300;
		//heightPixels=11*300;
		//spinePixels = 0;
		
		//widthF
		    
		System.out.println("Image should be size "+(widthFace+spinePixels)+"x"+heightPixels);
		
		BufferedImage outImage = new BufferedImage(widthFace+spinePixels, heightPixels,
				BufferedImage.TYPE_INT_RGB);

		Graphics2D g2d = outImage.createGraphics();
		g2d.setColor(Color.BLACK);
		g2d.fillRect(0, 0, outImage.getWidth(), outImage.getHeight());

		IndexScanner is = null;
		ArrayList<DirEntry> dirs = null;

		Random r = new Random();
		HashMap<String, Image> h = new HashMap<String, Image>();

		int repeat = 0;
		while (repeat++ < 20 ) {
			//
			DPage dp = new DPage(new File("/home/bob/Desktop/gwilt-book/db/jbum.ser"), false);
			System.out.println(dp.getTitle());
			System.out.println(dp.getWhere());

			for (int i = 0; i < dp.getVii().size(); i++) {
				

				if ( dp.getVii().get(i).imgSize.height < 1500 ||
						dp.getVii().get(i).imgSize.width < 1500 )
					continue;

				File f = dp.getVii().get(i).getMediumFile(
						dp.getWhere());
				int x = r.nextInt(outImage.getWidth()) - 50;
				int y = r.nextInt(outImage.getHeight()) - 50;

				Image img = h.get(f.toString());
				if (img == null) {
					img = SoftImage.get(f.toString());
					// h.put(f.toString(), img);
				}

				g2d.drawImage(img, x, y, null);
				img.flush();
				img = null;
				System.gc();
				System.out.print(".");
				System.out.flush();
				
			}

			System.out.println("\n repeat " + repeat);
		}

		int tWidth = 2000;
		int tHeight = 500;
		tWidth = tWidth / 3;
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
		tg2d.drawString("Scott and Andrea", 300 / 3, 230 / 3);
		useme = fonts[17].deriveFont(font2);
		tg2d.setFont(useme);
		tg2d.drawString("Feb 24, 2007", 600 / 3, 400 / 3);
		tg2d.dispose();

		Image softTitle = SoftImage.get(titleImage);

		int sw = softTitle.getWidth(null);
		int xpos = (outImage.getWidth() - sw) / 2;

		xpos = outImage.getWidth() - spinePixels;

		/*
		 * s = 231; <-- width ---------> | |s| | | | | title |
		 */
		int nospine = outImage.getWidth() - spinePixels;
		int half = nospine / 2;
		int centerCover = outImage.getWidth() - half / 2;
		xpos = centerCover - sw / 2;
		
		xpos = outImage.getWidth() / 2 - sw /2 ;

		g2d.drawImage(softTitle, xpos, (int) (outImage.getHeight() * 0.90),
				null);
		
		// for debugging title placement
		/*
		g2d.setColor(Color.yellow);
		g2d.draw3DRect(widthFace/2, 0, spinePixels, heightPixels, true);
		 */
		
		// JPEG-encode the image and write to file.
		OutputStream os = new FileOutputStream("collage.jpg");
		JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(os);
		encoder.encode(outImage);
		os.close();

	}

}
