package jbum.pdf;

import java.text.SimpleDateFormat;

import jbum.core.DPage;

import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.ColumnText;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfPageEvent;
import com.lowagie.text.pdf.PdfWriter;

public class BigPageEvent implements PdfPageEvent {
	
	
	public static SimpleDateFormat humanFormat = new SimpleDateFormat(
			"MMM d, ''yy");

	protected Phrase header;

	protected Phrase footer;

	public BigPageEvent() {
	}

	protected Document toc;

	public void onChapter(PdfWriter pdfwriter, Document document, float f,
			Paragraph title) {
	}

	public void onChapterEnd(PdfWriter pdfwriter, Document document, float f) {
	}

	public void onCloseDocument(PdfWriter pdfwriter, Document document) {
	}

	public static String shortWhere(DPage dpage) {
		String sname = BigPDF.dpage.getWhere().toString();
		// /jadn/babypea/2006/xxxx/jbum.ser
		if (sname.startsWith(BigPDF.base.toString()))
			sname = sname.substring(BigPDF.base.toString().length()+1, sname
					.length()
					- "/jbum.ser".length());
		else
			throw new RuntimeException("WHAT -> " + sname);
		return sname;
	}
	
	public boolean needFooter = true;

	public void onEndPage(PdfWriter writer, Document document) {
		PdfContentByte cb = writer.getDirectContent();
		
		if ( !needFooter )
			return;
		footer = new Phrase(new Chunk("" + document.getPageNumber(), BigPDF.f12Font));
		ColumnText.showTextAligned(cb, Element.ALIGN_CENTER, footer, (document
				.right() - document.left())
				/ 2 + document.leftMargin(), document.bottom() - 10, 0);
		Phrase name = new Phrase(new Chunk(shortWhere(BigPDF.dpage) + "["
				+ BigPDF.dpage.getVii().size() + "]",BigPDF.f12Font));
		ColumnText.showTextAligned(cb, Element.ALIGN_LEFT, name, document
				.left()
				+ document.leftMargin(), document.bottom() - 10, 0);
		Phrase pics = new Phrase(new Chunk(humanFormat.format(BigPDF.date),BigPDF.f12Font));
		ColumnText.showTextAligned(cb, Element.ALIGN_RIGHT, pics, document
				.right()
				- document.rightMargin(), document.bottom() - 10, 0);
		// }
	}

	public void onGenericTag(PdfWriter pdfwriter, Document document,
			Rectangle rectangle, String s) {
	}

	public void onOpenDocument(PdfWriter pdfwriter, Document document) {
	}

	public void onParagraph(PdfWriter pdfwriter, Document document, float f) {
	}

	public void onParagraphEnd(PdfWriter pdfwriter, Document document, float f) {
	}

	public void onSection(PdfWriter pdfwriter, Document document, float f,
			int i, Paragraph paragraph) {
	}

	public void onSectionEnd(PdfWriter pdfwriter, Document document, float f) {
	}

	public void onStartPage(PdfWriter pdfwriter, Document document) {
	}

}
