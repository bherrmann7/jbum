/*
 * Created on Sep 20, 2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package jbum.core

import javax.swing.*
import java.awt.*

/**
 * @author bob
 */
public class ImageCache {
    private static HashMap<File, ImageIcon> images = new HashMap<File, ImageIcon>();

    public static ImageIcon set(File name, Image img) {
        images.put(name, new ImageIcon(img));
        return get(name);
    }

    public static ImageIcon get(File smallFile) {
        ImageIcon icon = images.get(smallFile);
        if (icon != null)
            return icon;
        Image img = Toolkit.getDefaultToolkit().createImage(
                smallFile.toString());
        return set(smallFile, img);
    }

}