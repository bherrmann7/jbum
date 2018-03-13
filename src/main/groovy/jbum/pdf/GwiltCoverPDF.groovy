package jbum.pdf

import com.lowagie.text.Document
import com.lowagie.text.Image
import com.lowagie.text.Rectangle
import com.lowagie.text.pdf.PdfWriter

import java.awt.*

//case wrap cover for book size of 8.25" x 10.75, 
// 8x11 book cover => 17.25" (+ spine) x 11.25"
// spine for 64 pages is 0.16


float width = (17.25 + 0.16) * 72;
float height = 11.25 * 72;

r = new Rectangle(width, height);
r.setBackgroundColor(Color.YELLOW);
Document doc = new Document(r, 0, 0, 0, 0);

FileOutputStream fos = new FileOutputStream("cover.pdf");
PdfWriter.getInstance(doc, fos);
doc.open();

Image img = Image.getInstance("collage.jpg");
// the 3 in 103 is pure fudge factor to get the image to fill the cover
img.scalePercent((float) (103 * 72 / 300));
doc.add(img);

doc.close();

"xpdf cover.pdf".execute()


