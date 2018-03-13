package jbum.core

import javax.swing.*

public class Splitter {
    public static void split(DPage p, int loc, File moveTo) {

        VecImageInfo newVii = new VecImageInfo();
        VecImageInfo oldVii = p.getVii();
        File from = p.getWhere();


        for (int i = 0; i <= loc; i++) {
            ImageInfo iimv = oldVii.get(0);
            try {
                move(iimv.getOriginalFile(from), iimv.getOriginalFile(moveTo));
                if (!iimv.getMediumFile(moveTo).getParentFile().exists()) {
                    iimv.getMediumFile(moveTo).getParentFile().mkdir();
                }
                move(iimv.getMediumFile(from), iimv.getMediumFile(moveTo));
                move(iimv.getSmallFile(from), iimv.getSmallFile(moveTo));
            } catch (Exception e) {
                continue;
            }
            newVii.add(iimv);
            oldVii.remove(iimv);

            // TODO: delete all html?
        }

        // Use page to save new set (should probably clone the Page and use that.)
        p.setVii(newVii);
        p.setWhere(moveTo);
        if (p.exists()) {
            JOptionPane.showMessageDialog(null, "An album is already at that location.  Images copied but will be in that album as 'deleted'. ", "Images added to other album",
                    JOptionPane.WARNING_MESSAGE);
        } else {
            p.save();
        }

        // restore page back for caller.
        p.setVii(oldVii);
        p.setWhere(from);
    }

    private static boolean move(File originalFile, File destFile) {
        boolean mv = originalFile.renameTo(destFile);
        if (!mv) {
            JOptionPane.showMessageDialog(null,
                    "Unable to move image file: " + originalFile.getName() +
                            "\nwill skip this file.", "Error splitting up photos",
                    JOptionPane.ERROR_MESSAGE);
        }

        return mv;
    }
}
