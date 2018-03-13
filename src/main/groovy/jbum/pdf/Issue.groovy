package jbum.pdf

import com.lowagie.text.*
import com.lowagie.text.pdf.BaseFont
import com.lowagie.text.pdf.PdfWriter

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
