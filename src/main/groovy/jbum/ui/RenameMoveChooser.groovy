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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import jbum.core.Prefs;


public class RenameMoveChooser {
    File myFile;
    ActionListener myActionListener;

    public RenameMoveChooser() {
        final JFrame frame = new JFrame("jbum - Rename/Move Chooser");

        // frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent e) {
                    frame.dispose();
                }
            });

        Container contentPane = frame.getContentPane();
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BorderLayout());
        titlePanel.setBackground(new Color(255, 255, 204));

        ImageIcon icon = new ImageIcon(Main.class.getClassLoader().getResource("jbum.gif"));

        titlePanel.add(new JLabel(icon), BorderLayout.WEST);
        titlePanel.add(new JLabel(
                "<html>Rename or Move images, current location: <p>&nbsp;&nbsp;&nbsp;&nbsp;<b>" +
                Main.getCurrentDir() +
                "</b><p>&nbsp;<p>Choose new location...</html>"),
            BorderLayout.CENTER);

        final JTextField text = new JTextField();
        text.setEditable(false);
        contentPane.add(titlePanel, BorderLayout.NORTH);

        final JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        ActionListener actionListener = new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    String command = e.getActionCommand();

                    if (command.equals(JFileChooser.APPROVE_SELECTION)) {
                        moveAllTo(Main.getCurrentDir(),
                            chooser.getSelectedFile());
                    }

                    frame.dispose();
                }

                private void moveAllTo(File src, File dst) {
                    if (!dst.exists()) {
                        if (!dst.mkdirs()) {
                            Main.error("Unable to create directory: " + dst,
                                "Rename Move Chooser");

                            return;
                        }
                    }

                    recursive(src, dst);
                    src.delete();
                    Main.movedTo(dst);

                    ArrayList<String[]> al = Prefs.getLastModified();

                    for (int i = 0; i < al.size(); i++) {
                        String[] entry = al.get(i);

                        if (entry[2].equals(src.toString())) {
                            entry[2] = dst.toString();
                            entry[1] = dst.getName();
                            Prefs.setLastModified(al);
                        }
                    }
                }

                private void recursive(File src, File dst) {
                    File[] files = src.listFiles();

                    for (int i = 0; i < files.length; i++) {
                        File dstItem = new File(dst + File.separator +
                                files[i].getName());

                        if (files[i].isDirectory()) {
                            if (!files[i].renameTo(dstItem)) {
                                // move failed, so we have to do copy
                                if (!dstItem.isDirectory() && !dstItem.mkdir()) {
                                    Main.error("Unable to create needed destination directory.",
                                        "Rename/Move");

                                    // / ideally we should *unwind*
                                    return;
                                }

                                recursive(files[i], dstItem);
                            }
                        } else {
                            // normal file
                            if (!files[i].renameTo(dstItem)) {
                                // copy file..
                                copy(files[i], dstItem);
                            }
                        }
                    }
                }

                private boolean copy(File srcFile, File dstFile) {
                    try {
                        FileInputStream fis = new FileInputStream(srcFile);
                        FileOutputStream fos = new FileOutputStream(dstFile);
                        byte[] b = new byte[(int) srcFile.length()];
                        fis.read(b);
                        fos.write(b);

                        return true;
                    } catch (Throwable e) {
                        // e.printStackTrace();
                        return false;
                    }
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