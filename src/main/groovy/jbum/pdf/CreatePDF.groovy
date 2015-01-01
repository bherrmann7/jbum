
package jbum.pdf;

import static jbum.pdf.IntroPDF.safeEmbed;
import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import java.awt.Color;
import jbum.core.DPage;
import jbum.core.ImageInfo;

public class CreatePDF {

    static Font f12;

    static void write(Document doc, DPage meta, Font f12, Font f24) throws DocumentException {
        CreatePDF.f12 = f12;

        Chunk c = new Chunk(meta.getTitle(), f24);
        Paragraph pa = new Paragraph(c);
        pa.setAlignment(Element.ALIGN_CENTER);
        pa.setSpacingAfter(5);
        doc.add(pa);

        pa = new Paragraph(new Chunk(meta.getIntro(), f12));
        pa.setAlignment(Element.ALIGN_CENTER);
        pa.setSpacingAfter(15);
        doc.add(pa);

        PdfPTable table = new PdfPTable(2);

        table.setWidthPercentage(100.0f);

        for (int i = 0; i < meta.getVii().size(); i++) {
            ImageInfo ii = meta.getVii().get(i);

            PdfPTable it = new PdfPTable(1);
            PdfPCell pt = new PdfPCell(it);
            pt.setBorder(Rectangle.NO_BORDER);
            pt.setVerticalAlignment(Element.ALIGN_MIDDLE);
            pt.setHorizontalAlignment(Element.ALIGN_CENTER);
            pt.setPadding(5f);
            table.addCell(pt);

            Image img = null;
            try {
                img = safeEmbed(ii.getMediumFile(meta.getWhere()).toString());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            img.scalePercent(100 * 72 / 300);
            img.setAlignment(Image.MIDDLE);
            //img.setAlignment(Image.ALIGN_CENTER);
            PdfPCell cell = new PdfPCell(img);
            cell.setBorder(Rectangle.NO_BORDER);
            cell.setPadding(3f);
            if (!BigPDF.embedTest) 
                cell.setBackgroundColor(Color.WHITE);
            it.addCell(cell);
            PdfPCell p = new PdfPCell(scrub(ii.commentTA.getText()));
            p.setHorizontalAlignment(Element.ALIGN_MIDDLE);
            //p.setHorizontalAlignment(Element.ALIGN_CENTER);
            if (!BigPDF.embedTest)
                p.setBackgroundColor(Color.WHITE);
            p.setBorder(Rectangle.NO_BORDER);
            it.addCell(p);
        }

        int remaining = meta.getVii().size() % 2;
        PdfPCell cell = new PdfPCell(new Paragraph(new Chunk(" ", f12)));
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setPadding(3f);
        if (remaining == 1) {
            table.addCell(cell);
            table.addCell(cell);
        }
        if (remaining == 2) {
            table.addCell(cell);
        }
        doc.add(table);

        String prolog = meta.getProlog();

        String chatter = "<p>Created with <a href=http://jbum.sf.net/>jbum</a>";
        int cstart = prolog.indexOf(chatter);
        if (cstart != -1) {
            prolog = prolog.substring(0, cstart - 1) + prolog.substring(cstart + chatter.length());
        }
        chatter = "<p>\nMade with <a href=http://jbum.sf.net>jbum</a>";
        cstart = prolog.indexOf(chatter);
        if (cstart != -1) {
            prolog = prolog.substring(0, cstart - 1) + prolog.substring(cstart + chatter.length());
        }

        pa = new Paragraph(prolog, f12);
        pa.setAlignment(Element.ALIGN_CENTER);
        pa.setSpacingAfter(15);
        if (prolog.trim().length() != 0)
            doc.add(pa);

    }

    private static Phrase scrub(String str) {
        StringBuffer sb = new StringBuffer();
        boolean lastWasSpace = false;
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) == 10) {
                if (!lastWasSpace)
                    sb.append(' ');
            } else if (str.charAt(i) == 32 && lastWasSpace) {} else
                sb.append(str.charAt(i));
            lastWasSpace = (str.charAt(i) == 32);
        }
        return new Phrase(new Chunk(sb.toString(), f12));

    }
}
