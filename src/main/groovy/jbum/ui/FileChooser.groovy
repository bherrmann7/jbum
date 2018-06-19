package jbum.ui

import javax.swing.*
import java.awt.*
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent

class FileChooser {
    File myFile;
    ActionListener myActionListener;

    FileChooser(String reasonForChoosing) {
        final JFrame frame = new JFrame("jbum");

        //frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            void windowClosing(WindowEvent e) {
                myFile = null;
                myActionListener.actionPerformed(null);
                frame.dispose();
            }
        });

        Container contentPane = frame.getContentPane();
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BorderLayout());
        titlePanel.setBackground(new Color(255, 255, 204));

        ImageIcon icon = new ImageIcon(App.class.getClassLoader().getResource("jbum.gif"));

        titlePanel.add(new JLabel(icon), BorderLayout.WEST);
        titlePanel.add(new JLabel(reasonForChoosing), BorderLayout.CENTER);

        final JTextField text = new JTextField();
        text.setEditable(false);
        contentPane.add(titlePanel, BorderLayout.NORTH);

        final JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        ActionListener actionListener = new ActionListener() {
            void actionPerformed(ActionEvent e) {
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

    void addActionListener(ActionListener list) {
        myActionListener = list;
    }

    File getSelectedFile() {
        return myFile;
    }
}
