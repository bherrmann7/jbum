package jbum.core.test

import com.drew.imaging.jpeg.JpegMetadataReader
import com.drew.imaging.jpeg.JpegProcessingException
import com.drew.metadata.Directory
import com.drew.metadata.Metadata
import junit.framework.TestCase

import java.text.DateFormat
import java.text.SimpleDateFormat

public class SanityCheckMeta extends TestCase {

    public static void testSanity() throws JpegProcessingException {

        File jpegFile = new File("src/jbum/core/test/cimg0173.jpg");

        Date d = getDate(jpegFile);

        DateFormat df = DateFormat.getDateTimeInstance(DateFormat.MEDIUM,
                DateFormat.MEDIUM);

        df.format(d);
        //System.out.println("Date is " + df.format(d));
    }


    public static Date getDate(File jpegFile) {
        try {
            Metadata metadata = JpegMetadataReader.readMetadata(jpegFile);
            // iterate through metadata directories
            Iterator directories = metadata.getDirectoryIterator();
            while (directories.hasNext()) {
                Directory directory = (Directory) directories.next();
                // iterate through tags and print to System.out
                String date = directory.getDescription(306);
                if (date != null) {
                    SimpleDateFormat sd = new SimpleDateFormat(
                            "yyyy:MM:dd kk:mm:ss");
                    return sd.parse(date);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}

