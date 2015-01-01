package jbum.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;


public class FileChooser {
    File myFile;
    ActionListener myActionListener;

    public FileChooser(String reasonForChoosing) {
        final JFrame frame = new JFrame("jbum");

        //frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent e) {
                    myFile = null;
                    myActionListener.actionPerformed(null);
                    frame.dispose();
                }
            });

        Container contentPane = frame.getContentPane();
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BorderLayout());
        titlePanel.setBackground(new Color(255, 255, 204));

        ImageIcon icon = new ImageIcon(Main.class.getClassLoader().getResource("jbum.gif"));

        titlePanel.add(new JLabel(icon), BorderLayout.WEST);
        titlePanel.add(new JLabel(reasonForChoosing), BorderLayout.CENTER);

        final JTextField text = new JTextField();
        text.setEditable(false);
        contentPane.add(titlePanel, BorderLayout.NORTH);

        final JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        ActionListener actionListener = new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    String command = e.getActionCommand();

                    if (command.equals(JFileChooser.APPROVE_SELECTION)) {
                        myFile = chooser.getSelectedFile();
                    }

                    frame.dispose();
                    myActionListener.actionPerformed(e);
                }
            };

        chooser.addActionListener(actionListener);
        contentPane.add(chooser, BorderLayout.CENTER);
        frame.pack();
        frame.setSize(800, 800);

        Dimension d = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        int x = (d.width - frame.getWidth()) / 2;
        int y = (d.height - frame.getHeight()) / 2;
        frame.setLocation(x, y);
        frame.setVisible(true);
    }

    public void addActionListener(ActionListener list) {
        myActionListener = list;
    }

    public File getSelectedFile() {
        return myFile;
    }
}
