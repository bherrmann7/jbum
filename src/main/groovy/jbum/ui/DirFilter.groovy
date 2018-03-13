package jbum.ui

import javax.swing.filechooser.FileFilter

public class DirFilter extends FileFilter {
    public boolean accept(File f) {
        if (f.isDirectory()) {
            return true;
        }

        return false;
    }

    // The description of this filter
    public String getDescription() {
        return "Just Directories";
    }
}
