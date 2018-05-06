package jbum.ui

import javax.swing.filechooser.FileFilter

 class DirFilter extends FileFilter {
     boolean accept(File f) {
        if (f.isDirectory()) {
            return true;
        }

        return false;
    }

    // The description of this filter
     String getDescription() {
        return "Just Directories";
    }
}
