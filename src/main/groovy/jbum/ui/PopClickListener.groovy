package jbum.ui

import javax.swing.*
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent

class PopClickListener extends MouseAdapter {
    String imageName

    PopClickListener(String imageName){
        this.imageName = imageName
    }

     void mousePressed(MouseEvent e) {
        if (e.isPopupTrigger())
            doPop(e);
    }

     void mouseReleased(MouseEvent e) {
        if (e.isPopupTrigger())
            doPop(e);
    }

    private void doPop(MouseEvent e) {
        JButton jb = e.getComponent()
        // convert button text to image name
        //String imageName = jb.text
        def dexOf = imageName.indexOf(" : ")
        if (dexOf != -1) {
            imageName = imageName.substring(0, dexOf)
        }
        PopupMenu menu = new PopupMenu(imageName);
        menu.show(e.getComponent(), e.getX(), e.getY());
    }
}
