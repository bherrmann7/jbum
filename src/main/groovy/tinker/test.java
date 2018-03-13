

package tinker;

import com.drew.imaging.jpeg.JpegMetadataReader;
import com.drew.imaging.jpeg.JpegProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifDirectory;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class test {
    public static void main(String[] args)
            throws JpegProcessingException, ParseException {
        SimpleDateFormat sd = new SimpleDateFormat("yyyy:MM:dd kk:mm:ss");
        File jpegFile = new File("/jadn/keys/100_0721.JPG");
        jpegFile = new File("/jadn/keys/CIMG1373.JPG");

        Metadata metadata = JpegMetadataReader.readMetadata(jpegFile);
        Directory dir = metadata.getDirectory(ExifDirectory.class);
        Date d = sd.parse(dir.getString(36867));

        System.out.println(d);
    }
}
