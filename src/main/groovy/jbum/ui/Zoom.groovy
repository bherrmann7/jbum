package jbum.ui

import javax.swing.BorderFactory;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;


public class Zoom {
    public Zoom(File imgFile) {
        final JFrame frame = new JFrame();
        frame.setTitle("jbum zoom");

        Image img = Toolkit.getDefaultToolkit().createImage(imgFile.toString());
        ImageIcon icon = new ImageIcon(img);
        JButton button = new JButton(icon);
        button.setBorder(BorderFactory.createEmptyBorder())

        button.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent ae) {
                    frame.setVisible(false);
                    frame.dispose();
                }
            });

        frame.getContentPane().add(button, BorderLayout.CENTER);
        frame.setSize(10 + icon.getIconWidth(), 40 + icon.getIconHeight());

        Dimension d = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        int x = (d.width - frame.getWidth()) / 2;
        int y = (d.height - frame.getHeight()) / 2;
        frame.setLocation(x, y);
        frame.setVisible(true);
    }
}
