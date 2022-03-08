package jbum.core

import jbum.layouts.Page
import jbum.ui.App
import com.fasterxml.jackson.databind.ObjectMapper

import java.awt.*

/** Data Page.. a model of the data used to construct the page. */
public class DPage {
    File where;
    VecImageInfo vii;
    Color background = ColorSet.getBackground(ColorSet.DEFAULT);
    Color panel = ColorSet.getPanel(ColorSet.DEFAULT);
    Color text = ColorSet.getText(ColorSet.DEFAULT);
    String intro = Prefs.getInitialIntroText();
    String prolog = Prefs.getInitialPrologText();
    String title = Prefs.getInitialTitleText()
    int picsPerRow = 3;

    DPage(File where, VecImageInfo vecii) {
        this.where = where
        this.vii = vecii
    }

    DPage(File where, String title, String intro, VecImageInfo vecii,
          Color bg, Color text, Color panel,
          int picsPerRow, String prolog) {
        super();
        this.where = where;
        this.title = title;
        this.intro = intro;
        this.vii = vecii;
        this.background = bg;
        this.text = text;
        this.panel = panel;
        this.picsPerRow = picsPerRow;
        this.prolog = prolog;
    }

    DPage(File jbumDir) {
        if (!jbumDir.isDirectory()) {
            throw new RuntimeException("Not a directory: " + jbumDir)
        }
        File f = new File(jbumDir, "jbum.json")
        if (!f.exists()) {
            f = new File(jbumDir, "jbum.ser")
            if (!f.exists()) {
                throw new RuntimeException("missing jbum.json/jbum.ser: " + jbumDir)
            }
        }
        loadIt(f)
    }

    DPage(File jbumFile, boolean newPage) {
        loadIt(jbumFile)
    }

    void loadIt(File jbumFile) {
        this.where = jbumFile.getParentFile();

        if (jbumFile.name.endsWith(".json")) {
            ObjectMapper om = new ObjectMapper()
            FileReader fr = new FileReader(jbumFile)
            Page page = om.readValue(fr, Page.class)
            fr.close()
            title = page.title
            intro = page.intro
            vii = new VecImageInfo()
            page.photos.forEach({
                vii.add(it.toImageInfo())
            })
            background = Color.decode(page.bgColor)
            text = Color.decode(page.textColor)
            panel = Color.decode(page.panelColor)
            picsPerRow = page.picsPerRow
            prolog = page.prolog
            return
        }

        ObjectInputStream oos = null;

        try {
            oos = new JbumObjectInputStream(new FileInputStream(jbumFile));
            title = (String) oos.readObject();
            intro = (String) oos.readObject();
            vii = (VecImageInfo) oos.readObject();
        } catch (Exception e) {
            // should always be able to read these
            e.printStackTrace();
            App.error(e, "loading image information");
        }

        try {
            // newer settings... might be missing... expect EOF
            background = (Color) oos.readObject();
            text = (Color) oos.readObject();
            panel = (Color) oos.readObject();
            /*was (Color) */ oos.readObject() // not used - was odd panel color
            picsPerRow = oos.readInt();
            oos.readUTF(); // not used - was template
            prolog = (String) oos.readObject();
        } catch (EOFException e) {
            System.out.println("NOTE: Probably old format.  EOF: ${jbumFile}")
        } catch (Exception e) {
            // Here when the saved version was older than our newer format
            // values not read are "reset-to-factory"
            // e.printStackTrace();
            System.out.println("NOTE: ---- Really old jbum.ser or error unserializing: ${jbumFile}")
        }

    }

    void setBackgroundColor(Color backgroundColor) {
        this.background = backgroundColor;
    }

    Color getBackgroundColor() {
        return background;
    }

    void setIntro(String intro) {
        this.intro = intro;
    }

    String getIntro() {
        return intro;
    }

    void setPanelColor(Color panel) {
        this.panel = panel;
    }

    Color getPanelColor() {
        return panel;
    }

    void setPicsPerRow(int picsPerRow) {
        this.picsPerRow = picsPerRow;
    }

    int getPicsPerRow() {
        return picsPerRow;
    }

    void setProlog(String prolog) {
        this.prolog = prolog;
    }

    String getProlog() {
        return prolog;
    }

    void setTemplate(String templates) {
        this.template = templates;
    }

    void setTextColor(Color textColor) {
        this.text = textColor;
    }

    Color getTextColor() {
        return text
    }

    void setTitle(String title) {
        this.title = title;
    }

    String getTitle() {
        return title;
    }

    void setVii(VecImageInfo vii) {
        this.vii = vii;
    }

    VecImageInfo getVii() {
        return vii;
    }

    boolean exists() {
        return new File(where, "jbum.ser").exists() || new File(where, "jbum.json").exists()
    }

    void save() {
        try {
            ObjectOutputStream oos = new ObjectOutputStream(
                    new FileOutputStream(new File(where, "jbum.ser")));
            oos.writeObject(title);
            oos.writeObject(intro);
            oos.writeObject(vii);
            oos.writeObject(background);
            oos.writeObject(text);
            oos.writeObject(panel);
            oos.writeObject(Color.RED) //"Not Used - was panel odd color")
            oos.writeInt(picsPerRow);
            oos.writeUTF("Not Used - was Template Name");
            oos.writeObject(prolog);
            oos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        def fos = new FileOutputStream(new File(where, "jbum.json"))
        ObjectMapper om = new ObjectMapper()
        def pp = om.writerWithDefaultPrettyPrinter()
        fos.write(pp.writeValueAsBytes(toPage()))
        fos.close()
    }

    File getWhere() {
        return where;
    }

    void setWhere(File where) {
        this.where = where;
    }

    Page toPage() {
        return new Page(title, intro, vii.toModern(), picsPerRow,
                WebColor.getHex(background), WebColor.getHex(text), WebColor.getHex(panel), prolog, where.toString());
    }
}
