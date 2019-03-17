package jbum.core

import java.awt.*
import java.text.SimpleDateFormat

class ImageInfo2 {
    static SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy hh:mm:ss a EEE")

    String fileName;
    Dimension imgSize;
    Dimension mediumSize;
    Dimension smallSize;
    String comment;

    ImageInfo2(String fileName, Dimension imgSize, Dimension mediumSize,
               Dimension smallSize, String comment) {
        super();
        this.fileName = fileName;
        this.imgSize = imgSize;
        this.mediumSize = mediumSize;
        this.smallSize = smallSize;
        this.comment = comment.replace('\n', ' ')
    }

    // for json
    ImageInfo2() {}

    ImageInfo toImageInfo() {
        ImageInfo ii = new ImageInfo(new File(fileName), imgSize, mediumSize, smallSize)
        ii.setText(comment)
        return ii
    }

}
