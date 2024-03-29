package jbum.ui

import com.drew.imaging.jpeg.JpegMetadataReader
import com.drew.metadata.Directory
import com.drew.metadata.Metadata
import com.drew.metadata.Tag
import jbum.core.ImageCache

import javax.swing.*
import java.awt.*
import java.awt.event.ActionEvent
import java.awt.event.ActionListener

class JpgInfo {

    JpgInfo(File jpegFile, File smallFile, Component c) {
        StringBuffer info = new StringBuffer();

        try {
            Metadata metadata = JpegMetadataReader.readMetadata(jpegFile);

            // iterate through metadata directories
            Iterator directories = metadata.getDirectoryIterator();

            while (directories.hasNext()) {
                Directory directory = (Directory) directories.next();
                info.append(directory.getName());
                info.append("\n");

                for (Iterator iter = directory.getTagIterator();
                     iter.hasNext();) {
                    Tag element = (Tag) iter.next();

                    if (element.getTagName().indexOf("Unknown tag") == -1) {
                        info.append("  " + element.getTagName() + "(" + element.getTagType() + "): " +
                                element.getDescription());
                        info.append("\n");
                    }
                }
            }
        } catch (Exception e) {
            println("Exception occured with image ${jpegFile}")
            e.printStackTrace();
        }

        final JFrame frame = new JFrame();
        frame.setTitle("Image Info");

        JButton button = new JButton(jpegFile.getName(),
                ImageCache.get(smallFile));
        button.setVerticalTextPosition(JButton.BOTTOM);
        button.setHorizontalTextPosition(JButton.CENTER);
        button.setMargin(new Insets(0, 0, 0, 0));
        button.setBorderPainted(false);

        button.addActionListener(new ActionListener() {
            void actionPerformed(ActionEvent ae) {
                frame.setVisible(false);
                frame.dispose(); // by by ya'all
            }
        });

        frame.getContentPane().add(button, BorderLayout.WEST);

        JTextArea jta = new JTextArea();
        jta.setText(info.toString());
        frame.getContentPane().add(jta, BorderLayout.CENTER);

        frame.setSize(700, 700);
        frame.setVisible(true);
    }
}
