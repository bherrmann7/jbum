package jbum.pdf;

import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;

public class PdfPTableNOB extends PdfPTable {

    Font font;
    
	public PdfPTableNOB(int i, Font font) {
		super(i);
        this.font = font;
	}

	@Override
	public void addCell(String text) {	
		PdfPCell cell = new PdfPCell(new Phrase(text,font));
		cell.setBorder(Rectangle.NO_BORDER);
		cell.setPadding(10);
		super.addCell(cell);
	}
	
	
	@Override
	public void addCell(Image image) {
		PdfPCell cell = new PdfPCell(image);
		cell.setBorder(Rectangle.NO_BORDER);
		cell.setVerticalAlignment(Element.ALIGN_CENTER);
		cell.setPadding(10);
		super.addCell(cell);
	}
	
	public void addCell(PdfPTable table) {
		PdfPCell cell = new PdfPCell(table);
		cell.setBorder(Rectangle.NO_BORDER);
		cell.setPadding(10);
		super.addCell(cell);
	}	
}
