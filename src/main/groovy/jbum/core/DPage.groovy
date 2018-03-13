package jbum.core

import com.thoughtworks.xstream.XStream
import jbum.layouts.Page
import jbum.layouts.TemplateFactory
import jbum.ui.BlogInfo
import jbum.ui.Main

import java.awt.*

/**
 * Data Page.. a model of the data used to construct the page.
 */
public class DPage {
    Color background = ColorSet.getBackground(ColorSet.DEFAULT);

    Color panelEven = ColorSet.getPanelEven(ColorSet.DEFAULT);

    Color panelOdd = ColorSet.getPanelOdd(ColorSet.DEFAULT);

    Color text = ColorSet.getText(ColorSet.DEFAULT);

    File where;

    String intro;

    String prolog = Prefs.getInitialPrologText();

    String template;

    String title;

    VecImageInfo vii;

    int picsPerRow = 2;

    BlogInfo blogInfo;

    public DPage(File where, String title, String intro, VecImageInfo vecii,
                 Color bg, Color text, Color oddPanel, Color evenPanel,
                 int picsPerRow, String template, String prolog, BlogInfo blogInfo) {
        super();
        this.where = where;
        this.title = title;
        this.intro = intro;
        this.vii = vecii;
        this.background = bg;
        this.text = text;
        this.panelOdd = oddPanel;
        this.panelEven = evenPanel;
        this.picsPerRow = picsPerRow;
        this.template = template;
        this.prolog = prolog;
        this.blogInfo = blogInfo;
    }

    public DPage(File jbumFile, boolean newPage) {
        this.where = jbumFile.getParentFile();
        if (newPage) {
            template = TemplateFactory.POLAROIDS_FLOW;
            picsPerRow = 3;
            panelEven = Color.WHITE;
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
            Main.error(e, "loading image information");
        }

        try {
            // newer settings
            background = (Color) oos.readObject();
            text = (Color) oos.readObject();
            panelOdd = (Color) oos.readObject();
            panelEven = (Color) oos.readObject();
            picsPerRow = oos.readInt();
            template = oos.readUTF();
            prolog = (String) oos.readObject();
            blogInfo = (BlogInfo) oos.readObject();
        } catch (Exception e) {
            // Here when the saved version was older than our newer format
            // values not read are "reset-to-factory"
            // e.printStackTrace();
        }

    }

    public void setBackgroundColor(Color backgroundColor) {
        this.background = backgroundColor;
    }

    public Color getBackgroundColor() {
        return background;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public String getIntro() {
        return intro;
    }

    public void setPanelEvenColor(Color panelEvenColor) {
        this.panelEven = panelEvenColor;
    }

    public Color getPanelEvenColor() {
        return panelEven;
    }

    public void setPanelOddColor(Color panelOddColor) {
        this.panelOdd = panelOddColor;
    }

    public Color getPanelOddColor() {
        return panelOdd;
    }

    public void setPicsPerRow(int picsPerRow) {
        this.picsPerRow = picsPerRow;
    }

    public int getPicsPerRow() {
        return picsPerRow;
    }

    public void setProlog(String prolog) {
        this.prolog = prolog;
    }

    public String getProlog() {
        return prolog;
    }

    public void setTemplate(String templates) {
        this.template = templates;
    }

    public String getTemplate() {
        return template;
    }

    public void setTextColor(Color textColor) {
        this.text = textColor;
    }

    public Color getTextColor() {
        return text;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setVii(VecImageInfo vii) {
        this.vii = vii;
    }

    public VecImageInfo getVii() {
        return vii;
    }

    public static void main(String[] args) throws FileNotFoundException {

        if (args[0].endsWith(".ser")) {
            DPage p = new DPage(new File(args[0]), false);
            XStream xstream = new XStream();
            System.out.println(xstream.toXML(p));
        }
        if (args[0].endsWith(".xml")) {
            XStream xstream = new XStream();
            DPage np = (DPage) xstream.fromXML(new FileReader(args[0]));
            np.save();
        }
    }

    boolean exists() {
        return new File(where, "jbum.ser").exists();
    }

    public void save() {
        try {
            ObjectOutputStream oos = new ObjectOutputStream(
                    new FileOutputStream(new File(where, "jbum.ser")));
            oos.writeObject(title);
            oos.writeObject(intro);
            oos.writeObject(vii);
            oos.writeObject(background);
            oos.writeObject(text);
            oos.writeObject(panelOdd);
            oos.writeObject(panelEven);
            oos.writeInt(picsPerRow);
            oos.writeUTF(template);
            oos.writeObject(prolog);
            oos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public File getWhere() {
        return where;
    }

    public void setWhere(File where) {
        this.where = where;
    }

    public Page toPage() {
        return new Page(title, intro, vii.toModern(), picsPerRow, Main
                .prettyColor(background), Main.prettyColor(text), Main
                .prettyColor(panelOdd), Main.prettyColor(panelEven), prolog, where.toString());
    }
}
