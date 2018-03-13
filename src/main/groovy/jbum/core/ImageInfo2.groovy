package jbum.core

import java.awt.*
import java.text.SimpleDateFormat

public class ImageInfo2 {

    public String fileName;
    public Dimension imgSize;
    public Dimension mediumSize;
    public Dimension smallSize;
    public String comment;

    static SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy hh:mm:ss a EEE")

    public ImageInfo2(String fileName, Dimension imgSize, Dimension mediumSize,
                      Dimension smallSize, String comment) {
        super();
        this.fileName = fileName;
        this.imgSize = imgSize;
        this.mediumSize = mediumSize;
        this.smallSize = smallSize;
        this.comment = comment.replace('\n', ' ')
    }

}
