
package jbum.pdf;

import com.lowagie.text.Document;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;


public class TestDocument {

    public static void main(String[] args) throws Exception {
        Rectangle r = PageSize.LETTER;
        r = new Rectangle(630F, 810F);

        Document doc = new Document(r, 53, 53, 45, 45);
        FileOutputStream fos = new FileOutputStream("testdoc.pdf");
        PdfWriter pdfWriter = PdfWriter.getInstance(doc, fos);

        //pdfWriter.setPDFXConformance(PdfWriter.PDFX1A2001);

        doc.open();

        IntroPDF.writeIntro(doc, 33);
        
        for (int i = 0; i < 50; i++) {
            doc.add(new Paragraph("This is a test document.  Page "+i,BigPDF.f12Font));
            doc.newPage();
        }

        doc.close();

        Runtime.getRuntime().exec("xpdf testdoc.pdf");
    }
}

