
package jbum.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Iterator;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTextArea;

import jbum.core.ImageCache;

import com.drew.imaging.jpeg.JpegMetadataReader;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;


/**
 * @author bob
 */
public class JpgInfo {
	
	public static void main(String[] args) {
			File orig = new File("/jadn/babypea/2006/parlee farms/100_2134.JPG");
			File small = new File( orig.getParent(), "smaller/sm_"+orig.getName());
    		new JpgInfo(orig,small,null);   	
	}
	
    public JpgInfo(File jpegFile, File smallFile, Component c) {
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
                        info.append("  " + element.getTagName() + "("+element.getTagType()+"): " +
                            element.getDescription());
                        info.append("\n");
                    }
                }
            }
        } catch (Exception e) {
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
                public void actionPerformed(ActionEvent ae) {
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
