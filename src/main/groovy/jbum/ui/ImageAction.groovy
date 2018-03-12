package jbum.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.InputStreamReader;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import jbum.core.DPage;
import jbum.core.ImageInfo;
import jbum.core.ImageProcessor;
import jbum.core.Splitter;

import com.swabunga.spell.engine.SpellDictionaryHashMap;
import com.swabunga.spell.swing.JTextComponentSpellChecker;


public class ImageAction {

    static void performAction(String imageName, String tooltext) {
        String actionName = "zoom";

        for (int i = 0; i < CenterP.buttonInfo.length; i += 3) {
            if (CenterP.buttonInfo[i + 1].equals(tooltext)){
                actionName = CenterP.buttonInfo[i];
            }
        }

        CenterP centerP = Main.myself.centerP

        ImageInfo ii = centerP.vecii.vec.find { ImageInfo ii ->
            ii.getOriginalFile(Main.currentDir).name == imageName
        }
        if(ii == null){
            throw new RuntimeException("Unable to find: "+imageName)
        }

        if ("X".equals(actionName)) {
            // data list
            centerP.vecii.remove(ii);

            // reorder UI
            centerP.rebuildComponents();

            ii.getMediumFile(Main.getCurrentDir()).delete();
            ii.getSmallFile(Main.getCurrentDir()).delete();

            // TODO: purge all html?
            return;
        }

        if ("I".equals(actionName)) {
            int pos = centerP.vecii.indexOf(ii);
            File[] files = centerP.deletionManager.purgeList();

            for (int i = 0; i < files.length; i++) {
                ImageInfo jii = new ImageInfo(files[i], null, null, null);
                centerP.vecii.add(pos + 1 + i, jii);
            }

            centerP.rebuildComponents();

            return;
        }

        if ("R".equals(actionName)) {
            ImageProcessor.enqueue(ii, ImageProcessor.SMALLER);

            return;
        }

        if ("C".equals(actionName)) {
            ImageProcessor.enqueue(ii, ImageProcessor.CLOCKWISE);

            return;
        }

        if ("CC".equals(actionName)) {
            ImageProcessor.enqueue(ii, ImageProcessor.COUNTER_CLOCKWISE);

            return;
        }

        if ("<-".equals(actionName)) {
            int pos = centerP.vecii.indexOf(ii);

            if (pos == 0) {
                return;
            }

            // reorder data list
            centerP.vecii.set(centerP.vecii.get(pos - 1), pos);
            centerP.vecii.set(ii, pos - 1);

            // reorder UI
            centerP.rebuildComponents();

            return;
        }

        if ("->".equals(actionName)) {
            int pos = centerP.vecii.indexOf(ii);

            if (pos == (centerP.vecii.size() - 1)) {
                return;
            }

            // reorder data list
            centerP.vecii.set(centerP.vecii.get(pos + 1), pos);
            centerP.vecii.set(ii, pos + 1);

            // reorder UI
            centerP.rebuildComponents();

            return;
        }

        if ("-".equals(actionName)) {
            //JOptionPane.showMessageDialog(centerP,
            //                    "Warning: Split drops entered comments for all images"
            //                          + "\nbefore the one selected as the split point.");
            //JOptionPane.INFORMATION_MESSAGE);
            // Do split.
            final FileChooser chooser = new FileChooser(
                    "Select directory to move all images before this one to");
            ActionListener actionListener = new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        File moveTo = chooser.getSelectedFile();

                        if (moveTo == null) {
                            return;
                        }

                        if (!moveTo.exists()) {
                            JOptionPane.showMessageDialog(null,
                                "The chosen directory does not exist.",
                                "Error splitting up photos",
                                JOptionPane.ERROR_MESSAGE);

                            return;
                        }

                        if (!moveTo.isDirectory()) {
                            JOptionPane.showMessageDialog(null,
                                "The chosen file is not a directory.\nI need a directory to move the images to.",
                                "Error splitting up photos",
                                JOptionPane.ERROR_MESSAGE);

                            return;
                        }

                        int loc = centerP.vecii.indexOf(ii);
                        DPage page = centerP.getCurrentPage();
                        Splitter.split(page, loc, moveTo);
                        page.save();
                        centerP.rebuildComponents();
                    }
                };

            chooser.addActionListener(actionListener);

            return;
        }

        if ("sp".equals(actionName)) {
            try {
                if (centerP.dictionary == null) {
                    centerP.dictionary = new SpellDictionaryHashMap(new InputStreamReader(
                                CenterP.class.getClassLoader()
                                             .getResourceAsStream("english.0")));
                }

                JTextComponentSpellChecker sc = new JTextComponentSpellChecker(centerP.dictionary);
                sc.spellCheck(ii.commentTA);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return;
        }

        if ("info".equals(actionName)) {
            new JpgInfo(ii.getOriginalFile(Main.getCurrentDir()),
                ii.getSmallFile(Main.getCurrentDir()), centerP);

            return;
        }

        if ("tool".equals(actionName)) {
            new ExternalAction(ii);
            return;
        }

        // so If we get to here, must be the "+" button        
        new Zoom(ii.getMediumFile(Main.getCurrentDir()));
    }
}

