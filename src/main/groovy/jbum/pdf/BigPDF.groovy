package jbum.pdf;

import java.awt.Color;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import jbum.core.DPage;
import jbum.core.IndexScanner;
import jbum.core.IndexScanner.DirEntry;

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
import com.lowagie.text.pdf.PdfWriter;

import static jbum.pdf.IntroPDF.safeEmbed;

public class BigPDF {

	public static boolean embedTest = false;

	public static DPage dpage;

	public static Date date;

	public static String jadnBase = "/6/jadn.com/jadn.com";
	static {
		if (!new File(jadnBase).exists())
			jadnBase = "/jadn";

// This class really needs to move to jphotobook
//		if (!new File(jadnBase).exists())
//			throw new RuntimeException("Unable to locate pictures");
	}

	public static File base = new File(jadnBase + "/babypea/2006");

	public static class TOCE {

		String title;

		int pageNum;

		public String where;

		public String date;

		public Color color;

		public File picture;

		public File picture2;
	};

	public static Font f12Font;

	public static Font f24Font;

	static {
		BaseFont basefont;
		try {
			String times = "/home/bherrmann/times.ttf";
			if (!new File(times).exists())
				times = "/usr/lib/cinelerra/fonts/times.ttf";
			basefont = BaseFont.createFont(times, BaseFont.CP1252,
					BaseFont.EMBEDDED);
			f12Font = new Font(basefont, 12);
			f24Font = new Font(basefont, 24);

		} catch (DocumentException e) {
//			e.printStackTrace();
		} catch (IOException e) {
//			e.printStackTrace();
		}

	}

	public static void main(String[] args) throws Exception {

		File bigpdf = new File("/tmp/kids.pdf");

		IndexScanner is = new IndexScanner([ base.toString() ] as String[]);
		ArrayList<DirEntry> dirs = is.scan(null);

		BigPageEvent event = new BigPageEvent();

		Rectangle r = new Rectangle(630F, 810F);
		Document doc = new Document(r, 53, 53, 45, 45);
		PdfWriter pdfWriter = PdfWriter.getInstance(doc, new FileOutputStream(
				bigpdf));
		if (embedTest)
			pdfWriter.setPDFXConformance(PdfWriter.PDFX1A2001);
		pdfWriter.setLinearPageMode();
		pdfWriter.setPageEvent(event);

		ArrayList<TOCE> toces = new ArrayList<TOCE>();

		int total = 0;

		for (Iterator iter = dirs.iterator(); iter.hasNext();) {
			DirEntry de = (DirEntry) iter.next();

			DPage localdpage = new DPage(de.jbf, false);

			if (!doc.isOpen()) {
				if (!embedTest)
					r.setBackgroundColor(localdpage.getBackgroundColor());
				doc.open();
			} else {
				r = new Rectangle(630F, 810F);
				if (!embedTest)
					r.setBackgroundColor(localdpage.getBackgroundColor());
				doc.setPageSize(r);
				doc.newPage();
			}

			dpage = localdpage;
			date = de.d;
			total += dpage.getVii().size();

			String title = dpage.getTitle().trim();
			if (title.equals(""))
				title = dpage.getIntro().trim();

			TOCE toce = new TOCE();
			toce.title = title;
			toce.where = BigPageEvent.shortWhere(dpage) + "["
					+ dpage.getVii().size() + "]";
			toce.date = BigPageEvent.humanFormat.format(date);
			toce.pageNum = pdfWriter.getPageNumber();
			toce.color = dpage.getBackgroundColor();
			toce.picture = dpage.getVii().get(0).getSmallFile(
					dpage.getWhere().getParentFile());
			toce.picture2 = dpage.getVii().get(1).getSmallFile(
					dpage.getWhere().getParentFile());
			toces.add(toce);

			CreatePDF.write(doc, dpage, f12Font, f24Font);

		}

		// not even number of pages
//		if ( pdfWriter.getPageNumber() % 2 == 0 ){
			doc.newPage();
			doc.add( new Paragraph("\n\n\n      Thats all folks.", f12Font));
	//	}


		System.out.println("writting intro/toc...");

		// reset color
		r = new Rectangle(630F, 810F);
		doc.setPageSize(r);

		int beforeTOC = pdfWriter.getPageNumber();

		doc.newPage();
		event.needFooter = false;

		IntroPDF.writeIntro(doc, total);

		doc.newPage();

		Chunk c = new Chunk("Table of Contents", f24Font);
		Paragraph pa = new Paragraph(c);
		pa.setAlignment(Element.ALIGN_CENTER);
		pa.setSpacingAfter(10);
		doc.add(pa);

		doc.add(new Chunk("\n", f12Font));

		PdfPTable table = new PdfPTable(6);
		table.setHeaderRows(1);

		// 8
		float[] widths = [ 3 * 72f - 36, 36f + 18f, 36f + 18f, 72f + 72f, 72f,
				36f ] as float[];
		table.setTotalWidth(widths);
		table.setLockedWidth(true);

		PdfPCell pt = new PdfPCell(Phrase("Title"));
		table.addCell(pt);

		pt = new PdfPCell(Phrase("sample pictures"));
		table.addCell(pt);

		pt = new PdfPCell(Phrase("sample pictures"));
		table.addCell(pt);

		pt = new PdfPCell(Phrase("site location"));
		table.addCell(pt);

		pt = new PdfPCell(Phrase("date"));
		table.addCell(pt);

		pt = new PdfPCell(Phrase("Page"));
		table.addCell(pt);

		for (TOCE toce : toces) {

			pt = new PdfPCell(Phrase(toce.title));
			if (!embedTest)
				pt.setBackgroundColor(toce.color);
			pt.setBorder(Rectangle.NO_BORDER);
			table.addCell(pt);

			Image img = safeEmbed(toce.picture.toString());
			img.scalePercent(100 * 72 / 300);
			pt = new PdfPCell(img);
			if (!embedTest)
				pt.setBackgroundColor(toce.color);
			pt.setBorder(Rectangle.NO_BORDER);
			pt.setPadding(3);
			table.addCell(pt);

			img = safeEmbed(toce.picture2.toString());
			img.scalePercent(100 * 72 / 300);
			pt = new PdfPCell(img);
			if (!embedTest)
				pt.setBackgroundColor(toce.color);
			pt.setBorder(Rectangle.NO_BORDER);
			pt.setPadding(3);
			table.addCell(pt);

			pt = new PdfPCell(Phrase(toce.where));
			if (!embedTest)
				pt.setBackgroundColor(toce.color);
			pt.setBorder(Rectangle.NO_BORDER);
			table.addCell(pt);

			pt = new PdfPCell(Phrase(toce.date));
			if (!embedTest)
				pt.setBackgroundColor(toce.color);
			pt.setBorder(Rectangle.NO_BORDER);
			table.addCell(pt);

			pt = new PdfPCell(Phrase("" + toce.pageNum));
			if (!embedTest)
				pt.setBackgroundColor(toce.color);
			pt.setBorder(Rectangle.NO_BORDER);
			table.addCell(pt);

		}
		doc.add(table);

		doc.newPage();
		
				
		// do reorder
		int totalPages = pdfWriter.getPageNumber();	

		int[] reorder = new int[totalPages - 1];

		for (int i = 0; i < reorder.length; i++) {
			// reorder[i]=i+1;
			if (i < totalPages - beforeTOC - 1)
				reorder[i] = beforeTOC + i + 1;
			else
				reorder[i] = i - (totalPages - beforeTOC) + 1 + 1;

			// System.out.println(i+" = "+reorder[i]);
		}

		pdfWriter.reorderPages(reorder);

		doc.close();

		// Runtime.getRuntime().exec("xpdf toc.pdf");
		Runtime.getRuntime().exec("xpdf " + bigpdf);
	}

	private static Phrase Phrase(String string) {
		return new Phrase(new Chunk(string, f12Font));
	}

}
