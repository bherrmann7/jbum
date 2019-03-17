package jbum.ui

import jbum.core.ImageInfo
import jbum.core.VecImageInfo

import javax.swing.*
import javax.swing.event.MenuEvent
import javax.swing.event.MenuListener
import java.awt.event.ActionEvent
import java.awt.event.ActionListener

class DeletionManager implements ActionListener, MenuListener {
    CenterP centerP;

    JMenu menu = new JMenu("Deleted");

    // TODO: having these everywhere is a kludge
    final String outPathSm = new File("smaller").toString() + File.separator + "sm_";

    final String outPathMe = new File("smaller").toString() + File.separator + "me_";

    final String htmlPathPre = new File("html").toString() + File.separator + "me_";

    JCheckBoxMenuItem cb = new JCheckBoxMenuItem("Restore to end of images");

    DeletionManager(JMenuBar menuBar) {
        menuBar.add(menu);
        menu.addMenuListener(this);
        cb.setSelected(true);
    }

    void menuCanceled(MenuEvent e) {
    }

    void menuDeselected(MenuEvent e) {
    }

    void menuSelected(MenuEvent e) {
        menu.removeAll();

        File[] files = purgeList();

        if (files.length == 0) {
            menu.add(new JMenuItem("No deleted files"));

            return;
        }

        menu.add(cb);

        JMenuItem menuItem = new JMenuItem("Purge deleted files from disk");
        menu.add(menuItem);
        menuItem.addActionListener(this);

        menuItem = new JMenuItem("Restore All");
        menu.add(menuItem);
        menuItem.addActionListener(this);
        menu.addSeparator();

        for (int i = 0; i < files.length; i++) {
            menu.add(menuItem = new JMenuItem("restore " + files[i].getName()));
            menuItem.addActionListener(this);
        }
    }

    void actionPerformed(ActionEvent ae) {
        if (ae.getActionCommand().startsWith("Purge")) {
            Object[] options = ["OK", "CANCEL"]
            int c = JOptionPane.showOptionDialog(null,
                    "Really delete the images from the filesystem?", "Warning",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE,
                    null, options, options[0]);

            if (c == 0) {
                purge();
            }

            return;
        }

        if ("Restore All".equals(ae.getActionCommand())) {
            File[] files = purgeList();

            VecImageInfo vecii = new VecImageInfo();

            for (int i = 0; i < files.length; i++) {
                ImageInfo ii = new ImageInfo(new File(files[i].getName()),
                        null, null, null);
                vecii.add(ii);
            }

            CenterP.sort(vecii);

            if (cb.isSelected()) {
                for (int i = 0; i < vecii.size(); i++)
                    centerP.vecii.add(vecii.get(i));
            } else {
                for (int i = vecii.size() - 1; i >= 0; i--)
                    centerP.vecii.addFirst(vecii.get(i));
            }

            centerP.rebuildComponents();

            return;
        }

        File fname = new File(centerP.currentDir, ae.getActionCommand()
                .substring("restore ".length()));

        ImageInfo ii = new ImageInfo(fname, null, null, null);

        if (cb.isSelected()) {
            centerP.vecii.add(ii);
        } else {
            centerP.vecii.addFirst(ii);
        }

        centerP.rebuildComponents();
    }

    File[] purgeList() {
        File[] list = centerP.currentDir.listFiles();
        Vector<File> v = new Vector<File>();
        outer:
        for (int i = 0; i < list.length; i++) {
            if (list[i].getName().toLowerCase().endsWith(".jpg")
            // dont yet handl movies right
//					|| list[i].getName().toLowerCase().endsWith(".mov")
//					|| list[i].getName().toLowerCase().endsWith(".avi")) {
            ) {
                // icon is used by me personally for my weblog.
                if (list[i].getName().startsWith("icon")) {
                    continue;
                }

                for (int j = 0; j < centerP.vecii.size(); j++) {
                    if (centerP.vecii.get(j).getName()
                            .equals(list[i].getName())) {
                        continue outer;
                    }
                }

                v.add(list[i]);
            }
        }

        return v.toArray(new File[0]);
    }

    void purge() {
        File[] list = purgeList();

        for (int i = 0; i < list.length; i++) {
            list[i].delete();
            new File(outPathSm + list[i]).delete();
            new File(outPathMe + list[i]).delete();
            new File(htmlPathPre + list[i] + ".html").delete();
        }
    }
}
