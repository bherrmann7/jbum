package jbum.pdf;

import java.awt.Color;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfWriter;

public class coverPDF {
	

	public static void main(String[] args) throws Exception {

		Rectangle r = PageSize.LETTER;
		
		// 17.25" (+ spine) x 11.25"
		// spine for 306 pages is 0.77 inches
		
		// spine for 64 pages is 0.16 inches
		
		float width =  (float)((17.25+0.16)*72);
		float height = (float)(11.25*72);
        
        
        //1250.11x810
        //width = 1250.11f;
		
		r = new Rectangle( width, height); 
		r.setBackgroundColor(Color.BLACK);
		Document doc = new Document(r, 0, 0, 0, 0);

        System.out.println("Document is "+ width+"x"+height);

		System.out.println("Image should be size "+ width*300/72+"x"+height*300/72);
		
		FileOutputStream fos = new FileOutputStream("cover.pdf");
		PdfWriter.getInstance(doc, fos);
		doc.open();

		writeCover(doc);

		doc.close();

		Runtime.getRuntime().exec("xpdf cover.pdf");
	}
	
	public static void writeCover(Document doc) throws DocumentException,
			MalformedURLException, IOException {


		Image img = Image.getInstance("collage.jpg");
		img.scalePercent(100*72/300);
		doc.add(img);
	}

		

}
