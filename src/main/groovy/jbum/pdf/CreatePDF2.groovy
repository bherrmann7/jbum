package jbum.pdf;

import com.lowagie.text.BadElementException;
import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;

import jbum.core.DPage;
import jbum.core.ImageInfo;

public class CreatePDF2 {

	static Font f12;
	static Font f24;

	static DPage meta;

	static void write(Document doc, DPage meta, Font f12, Font f24,
			boolean testLayout) throws DocumentException {
		CreatePDF2.f12 = f12;
		CreatePDF2.f24 = f24;
		CreatePDF2.meta = meta;

		boolean comments = false;
		// Chunk c = new Chunk(meta.getTitle(), f24);
		// Paragraph pa = new Paragraph(c);
		// pa.setAlignment(Element.ALIGN_CENTER);
		// pa.setSpacingAfter(5);
		// doc.add(pa);
		//
		// pa = new Paragraph(new Chunk(meta.getIntro(), f12));
		// pa.setAlignment(Element.ALIGN_CENTER);
		// pa.setSpacingAfter(15);
		// doc.add(pa);

		PdfPTable table = new PdfPTable(2);

		table.setWidthPercentage(100.0f);

		// // cuddle same sizes images, if we can...
		// for (int i = 0; i < meta.getVii().size(); i++) {
		// ImageInfo ii = meta.getVii().get(i);
		//
		// if (i % 2 == 1) {
		// // see if previous image is same size
		// Dimension prev = meta.getVii().get(i - 1).imgSize;
		// if (!ii.imgSize.equals(prev)) {
		// int limit = i + 8;
		// if (limit >= meta.getVii().size())
		// limit = meta.getVii().size();
		// boolean flipped = false;
		// for (int x = i + 1; x < limit; x++) {
		// if (meta.getVii().get(x).imgSize.equals(prev)) {
		// meta.getVii().set(meta.getVii().get(x), i);
		// meta.getVii().set(ii, x);
		// flipped = true;
		// break;
		// }
		// }
		// }
		// }
		// }

		// lets drop all the nonstandard size images

		// for (int i = 0; i < meta.getVii().size(); i++) {
		// ImageInfo ii = meta.getVii().get(i);
		//			
		//			
		//
		// if (i % 2 == 1) {
		// // see if previous image is same size
		// Dimension prev = meta.getVii().get(i - 1).imgSize;
		// if (!ii.imgSize.equals(prev)) {
		// int limit = i + 8;
		// if (limit >= meta.getVii().size())
		// limit = meta.getVii().size();
		// boolean flipped = false;
		// for (int x = i + 1; x < limit; x++) {
		// if (meta.getVii().get(x).imgSize.equals(prev)) {
		// meta.getVii().set(meta.getVii().get(x), i);
		// meta.getVii().set(ii, x);
		// flipped = true;
		// break;
		// }
		// }
		// }
		// }
		// }

		boolean even = true;
		int j = 0;
		
		for (int i = 0; i < meta.getVii().size(); i++) {
			ImageInfo ii = meta.getVii().get(i);

			PdfPTable it = new PdfPTable(1);
			PdfPCell pt = new PdfPCell(it);
			pt.setBorder(Rectangle.NO_BORDER);
			pt.setVerticalAlignment(Element.ALIGN_TOP);
			pt.setHorizontalAlignment(Element.ALIGN_CENTER);
			pt.setPadding(0f);
			table.addCell(pt);

			if (ii.commentTA.getText().trim().startsWith(":")) {
				comments = true;
				ii.commentTA.setText(ii.commentTA.getText().substring(1));

				add(ii, it, testLayout, comments);

				pt = new PdfPCell(new Phrase(new Chunk("")));
				pt.setBorder(Rectangle.NO_BORDER);
				pt.setVerticalAlignment(Element.ALIGN_TOP);
				pt.setHorizontalAlignment(Element.ALIGN_CENTER);
				pt.setPadding(0f);
				table.addCell(pt);
				doc.add(table);
				doc.newPage();
				table = new PdfPTable(2);
				table.setWidthPercentage(100.0f);
				continue;
			}			
			String comment = ii.commentTA.getText().trim();
			if ( comment.startsWith(":") || comment.startsWith("*")) {
				//comments = true;
				ii.commentTA.setText(comment.substring(1));

				add(ii, it, testLayout, true/*comments*/);

				pt = new PdfPCell(new Phrase(new Chunk("")));
				pt.setBorder(Rectangle.NO_BORDER);
				pt.setVerticalAlignment(Element.ALIGN_TOP);
				pt.setHorizontalAlignment(Element.ALIGN_CENTER);
				pt.setPadding(0f);
				table.addCell(pt);
				doc.add(table);
				doc.newPage();
				if ( comment.startsWith("*")) {
					table = new PdfPTable(3);
					PdfPCell p = new PdfPCell(scrub(""));
					p.setHorizontalAlignment(Element.ALIGN_CENTER);
					p.setBackgroundColor(Color.WHITE);
					p.setBorder(Rectangle.NO_BORDER);
					table.addCell(p);
					p = new PdfPCell(scrub24("CANDIDS"));
					p.setHorizontalAlignment(Element.ALIGN_CENTER);
					p.setBackgroundColor(Color.WHITE);
					p.setBorder(Rectangle.NO_BORDER);
					table.addCell(p);
					p = new PdfPCell(scrub(""));
					p.setHorizontalAlignment(Element.ALIGN_CENTER);
					p.setBackgroundColor(Color.WHITE);
					p.setBorder(Rectangle.NO_BORDER);
					table.addCell(p);
				}
				else {
					table = new PdfPTable(2);
				}
				table.setWidthPercentage(100.0f);
				continue;
			}
			add(ii, it, testLayout, comments);

			// if last was tall, and we have two wides
			if (!even && tall(i - 1) && wide(i) && wide(i + 1)) {
				i++;
				ii = meta.getVii().get(i);
				add(ii, it, testLayout, comments);
			}

			// if little first, then tall, stack two littles.
			if (even && !last(i) && wide(i) && tall(i + 1)) {
				// findwide()
				for (int x = i + 2; x < i + 10; x++) {
					if (wide(x)) {
						swap(i + 1, x);
						i++;
						ii = meta.getVii().get(i);
						add(ii, it, testLayout, comments);
						break;
					}
				}
			}

			if (ii.commentTA.getText().trim().startsWith(":")) {
				System.out.println("thought needed");
			}

			even = !even;
			j++;
			if (j % 2 != 0 && even)
				System.out.println("huh");
		}
		

		// int added = 0;
		//
		// if ((added % 2) != 0) {
		// PdfPCell cell = new PdfPCell(new Paragraph(new Chunk(" ", f12)));
		// cell.setBorder(Rectangle.NO_BORDER);
		// cell.setPadding(3f);
		// table.addCell(cell);
		// doc.add(table);
		// }
		doc.add(table);
		
		doc.newPage();
		
		Image img;
		try {
			img = Image.getInstance("/home/bob/Desktop/gwilt-book/db/theEnd.jpg");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		img.scalePercent(100 * 72 / 300);
//		PdfPCell pt = new PdfPCell(new Phrase(new Chunk("")));
//		pt.setBorder(Rectangle.NO_BORDER);
//		pt.setVerticalAlignment(Element.ALIGN_TOP);
//		pt.setHorizontalAlignment(Element.ALIGN_CENTER);
//		pt.setPadding(0f);
//		table.addCell(pt);
		
		table = new PdfPTable(1);
		
		// img.setAlignment(Element.ALIGN_TOP);
		// img.setAlignment(Image.ALIGN_CENTER);
		PdfPCell cell = new PdfPCell(img);
		cell.setBorder(Rectangle.NO_BORDER);
		cell.setPadding(3f);
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setVerticalAlignment(Element.ALIGN_TOP);
		if (!BigPDF.embedTest)
			cell.setBackgroundColor(Color.WHITE);
		table.addCell(cell);
		
		String poem="Here's wishing you the top o' life without a single tumble.,Here's wishing you the smiles o' life and not a single grumble.,Here's wishing you the best o' life and not a claw about it.,Here's wishing you the joy in life and not a day without it.,-Irish Blessing";

		String comment = "And they lived happily ever after...";
		
		String[] lines = poem.split(",");
		for (int i = 0; i < lines.length; i++) {
			
		// if ( comment.length()==0)
		// comment = ii.getName();
		if (comment.length() != 0) {
			PdfPCell p = new PdfPCell(scrub(lines[i]));
			// p.setHorizontalAlignment(Element.ALIGN_MIDDLE);
			p.setHorizontalAlignment(Element.ALIGN_CENTER);
			if (!BigPDF.embedTest)
				p.setBackgroundColor(Color.WHITE);
			p.setBorder(Rectangle.NO_BORDER);
			table.addCell(p);
		}
		
		}
		
		
		doc.newPage();

		doc.add(table);
		
		doc.newPage();

		doc.add(scrub(""));

	}

	private static boolean last(int i) {
		return i == meta.getVii().size() - 1;
	}

	private static ImageInfo get(int i) {
		return meta.getVii().get(i);
	}

	private static void swap(int i, int x) {
		ImageInfo tmp_i = meta.getVii().get(i);
		meta.getVii().set(get(x), i);
		meta.getVii().set(tmp_i, x);
	}

	private static boolean wide(int i) {
		return !tall(i);
	}

	private static boolean tall(int i) {
		Dimension d = meta.getVii().get(i).imgSize;
		return d.height > d.width;
	}

	private static void add(ImageInfo ii, PdfPTable it, boolean testLayout,
			boolean comments) {

		File scaled = new File("/home/bob/Desktop/gwilt-book/db-scale");

		Image img = null;
		try {
			img = safeEmbed(ii.getOriginalFile(scaled).toString(), testLayout,
					ii.imgSize);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		img.scalePercent(100 * 72 / 300);
		// img.setAlignment(Element.ALIGN_TOP);
		// img.setAlignment(Image.ALIGN_CENTER);
		PdfPCell cell = new PdfPCell(img);
		cell.setBorder(Rectangle.NO_BORDER);
		cell.setPadding(3f);
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setVerticalAlignment(Element.ALIGN_TOP);
		if (!BigPDF.embedTest)
			cell.setBackgroundColor(Color.WHITE);
		it.addCell(cell);
		String comment = ii.commentTA.getText().trim();
		if (!comments)
			comment = "";
		// if ( comment.length()==0)
		// comment = ii.getName();
		if (comment.length() != 0) {
			PdfPCell p = new PdfPCell(scrub24(comment));
			// p.setHorizontalAlignment(Element.ALIGN_MIDDLE);
			p.setHorizontalAlignment(Element.ALIGN_CENTER);
			if (!BigPDF.embedTest)
				p.setBackgroundColor(Color.WHITE);
			p.setBorder(Rectangle.NO_BORDER);
			it.addCell(p);
		}
	}

	private static Phrase scrub24(String str) {
		StringBuffer sb = new StringBuffer();
		boolean lastWasSpace = false;
		for (int i = 0; i < str.length(); i++) {
			if (str.charAt(i) == 10) {
				if (!lastWasSpace)
					sb.append(' ');
			} else if (str.charAt(i) == 32 && lastWasSpace) {
			} else
				sb.append(str.charAt(i));
			lastWasSpace = (str.charAt(i) == 32);
		}
		return new Phrase(new Chunk(sb.toString(), f24));

	}

	private static Phrase scrub(String str) {
		StringBuffer sb = new StringBuffer();
		boolean lastWasSpace = false;
		for (int i = 0; i < str.length(); i++) {
			if (str.charAt(i) == 10) {
				if (!lastWasSpace)
					sb.append(' ');
			} else if (str.charAt(i) == 32 && lastWasSpace) {
			} else
				sb.append(str.charAt(i));
			lastWasSpace = (str.charAt(i) == 32);
		}
		return new Phrase(new Chunk(sb.toString(), f12));

	}

	public static void main(String[] args) throws Exception {
		DPage meta = new DPage(new File("/home/bob/Desktop/db/jbum.ser"), false);
		File deeOut = new File("/home/bob/Desktop/dbpage.pdf");

		Rectangle r = PageSize.LETTER;
		// r = new Rectangle(630F, 810F);

		Document doc = new Document(r, 18, 18, 9, 9);
		PdfWriter.getInstance(doc, new FileOutputStream(deeOut));
		doc.open();

		Font f12 = new Font(12);
		Font f24 = new Font(24);

		CreatePDF2.write(doc, meta, f12, f24, false);

		doc.close();

		Runtime.getRuntime().exec("xpdf " + deeOut);
	}

	static HashMap<String, BufferedImage> pretend = new HashMap<String, BufferedImage>();

	static int count;

	public static Image safeEmbed(String string, boolean testLayout,
			Dimension dim) throws Exception {
		int desiredWidth = 1080;

		if (testLayout) {
			if (dim.width > desiredWidth) {
				dim.height = dim.height * desiredWidth / dim.width;
				dim.width = desiredWidth;
			}
			String pair = dim.width + "x" + dim.height;

			BufferedImage memImage = pretend.get(pair);
			if (memImage == null)
				memImage = new BufferedImage(dim.width, dim.height,
						BufferedImage.TYPE_INT_RGB);

			// pretend.put(pair, memImage);

			Graphics2D g2d = memImage.createGraphics();
			g2d.setColor(Color.YELLOW);
			g2d.fillRect(0, 0, memImage.getWidth(), memImage.getHeight());
			g2d.drawString(string.substring(string.lastIndexOf('/')), 10, 150);
			g2d.dispose();

			return Image.getInstance(memImage, null);
		}
		if (BigPDF.embedTest)
			return Image.getInstance("/home/bob/loopy.gif");
		return Image.getInstance(string);
	}

}
