
package jbum.pdf;

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
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import jbum.core.DPage;
import jbum.core.ImageInfo;

File dir = new File("/home/bob/Desktop/db-scale");
dir.mkdir()

int desiredWidth = 1080;

def deeAlbum = new File("/home/bob/Desktop/db/jbum.ser");

if ( true) {
	println  "turned off"
	return
}

def run = { array ->
	println array.join(" ");
	array.execute().waitFor();
}
	

DPage meta = new DPage(deeAlbum, false);
meta.vii.vec.each { ImageInfo vii ->
	def src = vii.getOriginalFile(deeAlbum.parentFile)	
	if (vii.imgSize.width > desiredWidth ) {
		run( [ "convert", "-geometry", "$desiredWidth", "$src", "$dir/$src.name"] )
	} else {
		run( ["cp", "$src", "$dir"] )
	}
}
