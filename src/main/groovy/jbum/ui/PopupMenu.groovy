package jbum.ui

import javax.swing.*
import java.awt.event.ActionEvent
import java.awt.event.ActionListener

class PopupMenu extends JPopupMenu {

    static buttonImageIcons = [:]

    ImageIcon getImageIcon(String resourceName) {
        ImageIcon imageIcon = buttonImageIcons[resourceName]
        if (imageIcon != null) {
            return imageIcon
        }
        URL url = getClass().getClassLoader().getResource(
                "jbum/ui/icons/" + resourceName + ".gif");
        imageIcon = new ImageIcon(url)
        buttonImageIcons[resourceName] = imageIcon
        return imageIcon
    }

     PopupMenu(String imageName) {
        (0..<CenterP.buttonInfo.size() / 3).forEach {
            //String buttonCmd = CenterP.buttonInfo[it * 3]
            String buttonName = CenterP.buttonInfo[it * 3 + 2]
            String buttonToolTip = CenterP.buttonInfo[it * 3 + 1]

            JMenuItem jMenuItem = new JMenuItem()
            jMenuItem.setIcon(getImageIcon(buttonName))
            jMenuItem.setText(buttonToolTip)
            jMenuItem.addActionListener({ ActionEvent ae ->
                String buttonText = ((JMenuItem) ae.getSource()).getText()
                ImageAction.performAction(imageName, buttonText)
            } as ActionListener)
            add(jMenuItem)

        }

    }
}
