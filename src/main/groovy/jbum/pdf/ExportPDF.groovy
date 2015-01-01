package jbum.pdf;

import java.io.File;
import java.io.FileOutputStream;

import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.PageSize;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfWriter;

import jbum.core.DPage;
import jbum.core.Prefs;
import jbum.ui.Main;

public class ExportPDF {

	public ExportPDF(DPage page) throws Exception {
		System.out.println(page.getWhere());
		File outfile = new File(page.getWhere(), "jbum.pdf");
		makePDF(page, outfile);
		
		if ( new File(Prefs.getPDFViewer()).exists() )
			Runtime.getRuntime().exec(Prefs.getPDFViewer()+" " + outfile);
		else
			Main.error("PDF Viewer not found", "The PDF View program cannot be found.  Please correct the PDF Viewer preference in File/Preference.\n\nThe Viewer program is currently configured as: "+Prefs.getPDFViewer());
	}

	public static void make2(DPage page) throws Exception {
		System.out.println(page.getWhere());
		File outfile = new File(page.getWhere(), "jbum2.pdf");
		makePDF(page, outfile);
		
		if ( new File(Prefs.getPDFViewer()).exists() )
			Runtime.getRuntime().exec(Prefs.getPDFViewer()+" " + outfile);
		else
			Main.warning("PDF Viewer not found", "Please correct the PDF Viewer preference.\nFile not found: "+Prefs.getPDFViewer());
	}

	public static void makePDF(DPage meta, File outfile) throws Exception {

		Rectangle r = PageSize.LETTER;
		// r = new Rectangle(630F, 810F);

		Document doc = new Document(r, 53, 53, 45, 45);
		PdfWriter.getInstance(doc, new FileOutputStream(outfile));
		doc.open();

		Font f12 = new Font(12);
		Font f24 = new Font(24);

		CreatePDF.write(doc, meta, f12, f24);

		doc.close();

	}

}
