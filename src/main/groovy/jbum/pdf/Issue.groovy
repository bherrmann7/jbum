package jbum.pdf;

import java.awt.Color;
import java.io.FileOutputStream;
import jbum.core.ImageInfo;

import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfTable;
import com.lowagie.text.pdf.PdfWriter;

public class Issue {

 
    
    
    public static void main2(String[] args) throws Exception, DocumentException {
		Document doc = new Document();
		PdfWriter pdfWriter = PdfWriter.getInstance(doc, new FileOutputStream(
				"issue.pdf"));
		pdfWriter.setPDFXConformance(PdfWriter.PDFX1A2001);
        
		BaseFont basefont = BaseFont.createFont(
				"/usr/lib/cinelerra/fonts/times.ttf", BaseFont.CP1252,
				BaseFont.EMBEDDED);
		Font f12 = new Font(basefont, 12);
		Paragraph pa = new Paragraph("Something like text", f12);
		pa.setAlignment(Element.ALIGN_CENTER);
		pa.setSpacingAfter(15);  // <--- comment out to make happy
		doc.open();
		doc.add(pa);
		doc.close();

		Runtime.getRuntime().exec("xpdf issue.pdf");
	}
}
