package jbum.pdf

import com.lowagie.text.*
import com.lowagie.text.pdf.PdfPCell
import com.lowagie.text.pdf.PdfPTable
import com.lowagie.text.pdf.PdfWriter

public class Issue2 {

    public static void main2(String[] args) throws Exception, DocumentException {
        Document doc = new Document();
        PdfWriter pdfWriter = PdfWriter.getInstance(doc, new FileOutputStream("issue2.pdf"));
        pdfWriter.setPDFXConformance(PdfWriter.PDFX1A2001);
        doc.open();

        doc.add(Image.getInstance("/home/bherrmann/loopy.jpg"));

        doc.close();

        Runtime.getRuntime().exec("xpdf issue2.pdf");
    }

    public static void main(String[] args) throws Exception, DocumentException {
        Document doc = new Document();
        PdfWriter pdfWriter = PdfWriter.getInstance(doc, new FileOutputStream("issue2.pdf"));
        pdfWriter.setPDFXConformance(PdfWriter.PDFX1A2001);
        doc.open();

        PdfPTable table = new PdfPTableNOB(2, null);
        table.setWidthPercentage(100);

        Image img = Image.getInstance("/home/bherrmann/loopy.gif");
        img.scalePercent(100 * 72 / 300);
        PdfPCell p = new PdfPCell(img);
        p.setPadding(5);
        p.setHorizontalAlignment(Element.ALIGN_CENTER);
        p.setBorder(Rectangle.NO_BORDER);
        table.addCell(p);

        Image img2 = Image.getInstance("/home/bherrmann/loopy.gif");
        img2.scalePercent(100 * 72 / 300);
        p = new PdfPCell(img2);
        p.setHorizontalAlignment(Element.ALIGN_CENTER);
        p.setBorder(Rectangle.NO_BORDER);
        table.addCell(p);

        doc.add(table);

        doc.close();

        Runtime.getRuntime().exec("xpdf issue2.pdf");
    }

}