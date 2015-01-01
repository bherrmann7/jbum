
package jbum.pdf;

import com.lowagie.text.Document;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import com.lowagie.text.Image;
import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import java.awt.Color;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import com.lowagie.text.pdf.BaseFont;


import jbum.core.DPage;
import jbum.core.ImageInfo;

class GwiltBook {
		
	// 8.25 x 10.75 (72points/inch)  592F x 774F
	// 8.75" x 11.25" (72points/inch) = 630F x 810F 
		
	static boolean testLayout //= true;
	
	static void main(String[] args) {
		println "Timing."
		long start = System.currentTimeMillis();
		
	    Rectangle r = new Rectangle(630F, 810F);
	    //r = PageSize.LETTER;
	    //    marginLeft,marginRight,marginTop,marginBottom,
        Document doc = new Document(r, 18, 18, 27, 27);
        FileOutputStream fos = new FileOutputStream("gwiltBook.pdf");
        PdfWriter pdfWriter = PdfWriter.getInstance(doc, fos);

        //pdfWriter.setPDFXConformance(PdfWriter.PDFX1A2001);
        //BigPDF.embedTest = true;
        
        doc.open();
               
        if (!testLayout) { 
	        (1..6).each {
	        	Image img = Image.getInstance("/home/bob/book/p${it}.png")
	        	img.setAlignment(Image.MIDDLE);
	        	img.scalePercent((float)(100 * 72 / 300));
	         	doc.add( img )
	        }
        }
        
    	DPage meta = new DPage(new File("/home/bob/Desktop/gwilt-book/db/jbum.ser"), false);
		String times = "/home/bob/times.ttf";
		BaseFont	basefont = BaseFont.createFont(times, BaseFont.CP1252,
				BaseFont.EMBEDDED);
		Font	f12 = new Font(basefont, 12);
		Font	f24 = new Font(basefont, 24);
		

		CreatePDF2.write(doc, meta, f12, f24, testLayout);
		
		doc.close();
		
		long end = System.currentTimeMillis();
		println "Took ${(end-start)/1000} seconds"
		
        Runtime.getRuntime().exec("xpdf gwiltBook.pdf");
	}

}

