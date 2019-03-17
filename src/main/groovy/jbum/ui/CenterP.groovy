package jbum.ui

import com.drew.imaging.jpeg.JpegMetadataReader
import com.drew.metadata.Directory
import com.drew.metadata.Metadata
import com.drew.metadata.exif.ExifDirectory
import com.swabunga.spell.engine.SpellDictionary
import com.swabunga.spell.engine.SpellDictionaryHashMap
import com.swabunga.spell.swing.JTextComponentSpellChecker
import jbum.core.*
import jbum.ui.Prefs

import javax.swing.*
import javax.swing.border.EmptyBorder
import javax.swing.text.JTextComponent
import java.awt.*
import java.awt.event.ActionEvent
import java.text.SimpleDateFormat
import java.util.List

@SuppressWarnings("serial")
class CenterP extends JScrollPane {

    static String[] buttonInfo = [ //

                                   "X", "delete image", "trash",

                                   "<-", "move image back in list", "back",

                                   "->", "move image forwared in list", "forward",

                                   "C", "Rotate Clockwise", "clockwise",

                                   "CC", "Rotate Counter Clockwise", "counterclockwise",

                                   "info", "display image info", "information",

//                                          "+", "view larger version of imageoom ", "zoom",

                                   "R", "reload image", "reload",

                                   "sp", "Spell check", "spellcheck",

                                   "tool", "external tool", "hammer",

                                   "-",
                                   "split - takes this image and all before it and moves them into another folder",
                                   "split",

                                   "I",
                                   "Insert deleted images after this image",
                                   "insert",

    ];

    static SimpleDateFormat sd = new SimpleDateFormat("yyyy:MM:dd kk:mm:ss");

    SpellDictionary dictionary;

    VecImageInfo vecii;

    DeletionManager deletionManager;

    File currentDir;

    private JPanel introP = new JPanel();

    private JPanel titleP = new JPanel();

    JTextArea introTA = new JTextArea("intro", 5, 60);

    JTextArea prologTA = new JTextArea("", 5, 60);

    JTextField titleTF = new JTextField("Title", 60);

    String htmlPath

    private String iconPath

    private Color panelColor

    CenterP(DeletionManager deletionManager) {
        super(new Box(BoxLayout.Y_AXIS));

        this.deletionManager = deletionManager;
        deletionManager.centerP = this;

        introTA.setWrapStyleWord(true);
        introTA.setLineWrap(true);
        prologTA.setWrapStyleWord(true);
        prologTA.setLineWrap(true);
    }

    static Date getDate(File jpegFile, List<Camera> cameraList) {
        if (cameraList == null) {
            throw new RuntimeException("Humm...");
        }
        try {
            Metadata metadata = JpegMetadataReader.readMetadata(jpegFile);
            Directory dir = metadata.getDirectory(ExifDirectory.class);
            Date d = sd.parse(dir.getString(36867));
            String cameraName = dir.getString(271);
            d = CameraUtil.adjustTime(d, cameraList, cameraName);
            return d;
        } catch (Exception e) {
            return null;
        }
    }

    class IDate implements Comparable {
        ImageInfo ii;

        Date date;

        int compareTo(Object o) {
            return date.compareTo(((IDate) o).date);
        }
    };

    @SuppressWarnings("unchecked")
    void orderByExifDate(List<Camera> cameras) {

        // get dates
        ArrayList<IDate> idates = new ArrayList<IDate>();

        for (int i = 0; i < vecii.size(); i++) {
            IDate id = new IDate();
            id.ii = vecii.get(i);
            id.date = getDate(id.ii.getOriginalFile(App.getCurrentDir()), cameras);

            if (id.date == null) {
                if (i != 0) {
                    id.date = idates.get(i - 1).date;
                } else {
                    for (int s = i + 1; (s < vecii.size()) && (id.date == null); s++) {
                        id.date = getDate(vecii.get(s).getOriginalFile(
                                App.getCurrentDir()), cameras);
                    }

                    if (id.date == null) {
                        // nothing must have a date.
                        id.date = new Date();
                    }
                }
            }

            idates.add(id);
        }

        // sort
        Collections.sort(idates);

        // re-populate
        vecii.clear();

        for (Iterator<IDate> iter = idates.iterator(); iter.hasNext();) {
            IDate idate = iter.next();
            vecii.add(idate.ii);
        }

        rebuildComponents();
    }

    void rebuildImages() {
        for (int i = 0; i < vecii.size(); i++) {
            vecii.get(i).getMediumFile(App.getCurrentDir()).delete()
            vecii.get(i).getSmallFile(App.getCurrentDir()).delete()
        }
        ImageCache.clear()
        rebuildComponents()
    }


    void rebuildComponents() {
        setVisible(false);
        Box box = (Box) getViewport().getView();
        box.removeAll();
        doComponents();
        setVisible(true);
    }

    void setColor(String what, Color c) {
        if ("Text".equals(what)) {
            titleTF.setForeground(c);
            introTA.setForeground(c);
            prologTA.setForeground(c);
        }

        if ("Panel".equals(what)) {
            panelColor = c;
        }

        if ("Background".equals(what)) {
            // Box box = (Box) getViewport().getView();
            setBackground(c);
        }
    }

    Color getColor(String what) {
        if ("Text".equals(what)) {
            return titleTF.getForeground();
        }

        if ("Panel".equals(what)) {
            return panelColor
        }

        if ("Background".equals(what)) {
            return getBackground();
        }

        return Color.black;
    }

    void setColorSet(String setName) {
        setColor("Background", ColorSet.getBackground(setName));
        setColor("Text", ColorSet.getText(setName));
        setColor("Panel", ColorSet.getPanel(setName));
    }

    void setDir(File dir) {
        currentDir = dir;
        iconPath = '' + currentDir + File.separator + "smaller" + File.separator;

        File outF = new File(iconPath);

        File jbumSer = new File(dir, "jbum.ser");
        if (jbumSer.exists() && !outF.exists()) {
            iconPath = outF.getParent() + File.separator;
            JOptionPane
                    .showMessageDialog(
                    null,
                    "This jbum page uses the older style of putting thumbnails\n"
                            + "in the main directory (and not the 'smaller' sub directory). Resaving\n"
                            + "this page may result in a page w/o images.   You can regenerate thumbnails from the menu.",
                    "Missing 'smaller' directory",
                    JOptionPane.WARNING_MESSAGE);
        }

        htmlPath = '' + currentDir + File.separator + "html" + File.separator;
    }

    DPage getCurrentPage() {
        DPage p = new DPage(currentDir, titleTF.getText(), introTA.getText(),
                vecii, getColor("Background"), getColor("Text"),
                getColor("Panel"), App.getPicsPerRow(),
                prologTA.getText());

        return p;
    }

    static void sort(VecImageInfo vecii) {
        boolean flipped = true;

        while (flipped) {
            flipped = false;

            for (int i = 0; i < (vecii.size() - 1); i++) {
                String s1 = vecii.get(i).getName();
                String s2 = vecii.get(i + 1).getName();

                if (s1.compareTo(s2) > 0) {
                    ImageInfo tmp = vecii.get(i);
                    vecii.set(vecii.get(i + 1), i);
                    vecii.set(tmp, i + 1);
                    flipped = true;
                }
            }
        }
    }

    void scanDir() {
        boolean newPage = false
        File jbumFile = new File(currentDir, "jbum.json")
        if (!jbumFile.exists())
            jbumFile = new File(currentDir, "jbum.ser");

        DPage meta = null
        if (!jbumFile.exists()) {
            newPage = true;
            vecii = new VecImageInfo();

            File[] list = currentDir.listFiles();

            if (list) {
                for (int i = 0; i < list.length; i++) {
                    if (list[i].toString().toLowerCase().endsWith(".jpg")) {
                        ImageInfo ii = new ImageInfo(list[i], null, null, null);
                        vecii.add(ii);
                    }
                }
            }

            // listFiles comes back random, so we sort it.
            sort(vecii);

            if (vecii.size() > 0) {
                vecii.get(0).commentTA
                        .setText(Prefs.getInitialFirstImageText());
            }

            meta = new DPage(currentDir, vecii)
        } else {
            meta = new DPage(jbumFile, newPage);
        }
        titleTF.setText(meta.getTitle());
        introTA.setText(meta.getIntro());
        vecii = meta.getVii();

        setColor("Background", meta.getBackgroundColor());
        setColor("Text", meta.getTextColor());
        setColor("Panel", meta.getPanelColor());

        App.setPicsPerRow(meta.getPicsPerRow());
        prologTA.setText(meta.getProlog());

        doComponents();
    }

    void spellcheck() {
        try {
            if (dictionary == null) {
                dictionary = new SpellDictionaryHashMap(new InputStreamReader(
                        CenterP.class.getClassLoader().getResourceAsStream(
                                "english.0")));
            }

            JTextComponentSpellChecker sc = new JTextComponentSpellChecker(
                    dictionary);
            spellcheck(this, sc);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void spellcheck(Component component, JTextComponentSpellChecker sc) {
        if (component instanceof Container) {
            Container x = (Container) component;

            for (int i = 0; i < x.getComponentCount(); i++) {
                spellcheck(x.getComponent(i), sc);
            }
        }

        if (component instanceof JTextComponent) {
            sc.spellCheck((JTextComponent) component);
        }
    }

    def imageName2button = [:]

    private void doComponents() {
        Box box = (Box) getViewport().getView();

        titleP.add(titleTF);
        box.add(titleP);
        titleP.setBackground(getBackground());
        titleTF.setBackground(slightlyDarker(getBackground()));

        introP.add(introTA);
        box.add(introP);
        introP.setBackground(getBackground());
        introTA.setBackground(slightlyDarker(getBackground()));

        def rowP = new Box(BoxLayout.X_AXIS);
        rowP.setBorder(new EmptyBorder(5, 0, 0, 0))
        box.add(rowP);
        box.getParent().setBackground(getBackground());
        setBackground(getBackground());

        for (int i = 0; i < vecii.size(); i++) {
            ImageInfo ii = vecii.get(i);

            JPanel littleP = new JPanel() {
                @Override
                Dimension getMaximumSize() {
                    return super.getPreferredSize()
                }
            }
            littleP.setBackground(panelColor);
            littleP.setBorder(new EmptyBorder(5, 5, 5, 5))
            littleP.setLayout(new BorderLayout());
            rowP.add(littleP);
            littleP.setAlignmentY(0)
            if (rowP instanceof Box) {
                rowP.add(Box.createRigidArea(new Dimension(5, 0)))
            }

            String cameraName = CameraUtil.getCameraName(ii.getOriginalFile(App.getCurrentDir()))
            String name = ii.name + (cameraName == null ? "" : " : " + cameraName)

            JButton button = new JButton(/*name ,*/ ImageCache.get(ii.getSmallFile(App.getCurrentDir())));
            imageName2button[ii.name] = button
            button.setBackground(littleP.getBackground());
            button.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0))
            button.setMargin(new Insets(0, 0, 0, 0))
            if (ii.imgSize == null || (!ii.getSmallFile(App.getCurrentDir()).exists())) {
                ImageProcessor.enqueue(ii, ImageProcessor.SMALLER);
            }

            littleP.add(button, BorderLayout.NORTH);

            button.setBackground(panelColor);
            button.setVerticalTextPosition(JButton.BOTTOM);
            button.setHorizontalTextPosition(JButton.CENTER);

            JTextArea jta = ii.commentTA;
            jta.setBackground(slightlyDarker(panelColor));
            jta.setForeground(introTA.getForeground());
            jta.setLineWrap(true);
            jta.setWrapStyleWord(true);

            littleP.add(jta, BorderLayout.CENTER);

            button.addActionListener({ ActionEvent ae ->
                if (ae.modifiers & Event.CTRL_MASK)
                    return
                if (ae.modifiers & Event.SHIFT_MASK) {
                    Runtime.getRuntime().exec([
                            jbum.core.Prefs.getWebBrowser(),
                            "file://" + ii.getOriginalFile(App.getCurrentDir()).toString()]
                            as String[])
                } else
                    new Zoom(ii.getMediumFile(App.getCurrentDir()));
            });

            button.addMouseListener(new PopClickListener(ii.name))

            int x = App.getPicsPerRow();

            if ((i + 1) % x == 0) {
                rowP = new Box(BoxLayout.X_AXIS);
                rowP.setBorder(new EmptyBorder(5, 0, 0, 0))
                box.add(rowP);
                //rowP.setBackground(getBackground());
            }

        }

        JPanel prologP = new JPanel();
        prologP.add(prologTA);
        box.add(prologP);
        prologP.setBackground(getBackground());
        prologTA.setBackground(slightlyDarker(getBackground()));
    }

    Color slightlyDarker(Color c) {
        def FACTOR = 0.9
        return new Color(Math.max((int) (c.getRed() * FACTOR), 0),
                Math.max((int) (c.getGreen() * FACTOR), 0),
                Math.max((int) (c.getBlue() * FACTOR), 0),
                c.getAlpha());
    }
}
