package jbum.core;

import java.awt.Dimension;
import java.io.File;

import javax.swing.JTextArea;

public class ImageInfo implements java.io.Serializable {
    final static long serialVersionUID = 2366333334630035894L;

    private File fileName;

    public Dimension imgSize;

    public Dimension mediumSize;

    public Dimension smallSize;

    public transient JTextArea commentTA = mkTA()

    public ImageInfo(File x, Dimension imgSize, Dimension mediumSize,
            Dimension smallSize) {
        this.fileName = x;
        this.imgSize = imgSize;
        this.mediumSize = mediumSize;
        this.smallSize = smallSize;

        commentTA.setLineWrap(true);
        commentTA.setWrapStyleWord(true);
    }

    private void writeObject(java.io.ObjectOutputStream out)
            throws java.io.IOException {
        out.defaultWriteObject();
        out.writeObject(commentTA.getText());
    }

    private void readObject(java.io.ObjectInputStream inx)
            throws java.io.IOException, ClassNotFoundException {
        inx.defaultReadObject();
        commentTA = mkTA();
        commentTA.setText((String) inx.readObject());

        // remove all slashes... (Here to fixed older versions.)
        String fname = fileName.toString();
        int last = fname.lastIndexOf(File.separator);
        if (last != -1) {
            fileName = new File(fname.substring(last + File.separator.length()));
        }
    }

    private JTextArea mkTA() {
        return new JTextArea(3, 20);
    }

    public String getName() {
        return fileName.getName();
    }

    public File getOriginalFile(File baseDir) {
        return new File(baseDir, fileName.getName());
    }

    public File getMediumFile(File baseDir) {
        return new File(baseDir, "smaller"
                + File.separator + "me_" + fileName.getName());
    }

    public File getSmallFile(File baseDir) {
        return new File(baseDir, "smaller"
                + File.separator + "sm_" + fileName.getName());
    }

	public ImageInfo2 toImageInfo2() {
		return new ImageInfo2(fileName.toString(), imgSize, mediumSize, smallSize, commentTA.getText());
	}
}