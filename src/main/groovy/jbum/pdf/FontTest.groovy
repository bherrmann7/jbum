package jbum.pdf

import com.lowagie.text.Document
import com.lowagie.text.Font
import com.lowagie.text.Paragraph
import com.lowagie.text.pdf.BaseFont
import com.lowagie.text.pdf.PdfWriter

public class FontTest {

    /**
     * @param args
     * @throws
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        /* chapter08/FileSizeComparison.java */

        Document doc = new Document();

        PdfWriter.getInstance(doc, new FileOutputStream(
                "font.pdf"));

        doc.open();

        File[] files = new File("/usr/lib/cinelerra/fonts/").listFiles();
        for (int i = 0; i < files.length; i++) {
            if (!files[i].toString().endsWith(".ttf"))
                continue;

            BaseFont bf_comic = BaseFont.createFont(
                    files[i].toString(), BaseFont.CP1252,
                    BaseFont.EMBEDDED);

            Font fcomic = new Font(bf_comic, 12);

            doc
                    .add(new Paragraph("quick brown fox jumps over the lazy dog " + files[i].getName(),
                    fcomic));
            //doc.add(Chunk.NEWLINE);
        }

        // step 5: we close the document
        doc.close();

        Runtime.getRuntime().exec("xpdf font.pdf");
    }

}
