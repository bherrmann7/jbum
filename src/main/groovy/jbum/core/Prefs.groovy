package jbum.core

import javax.swing.*
import java.awt.*
import java.text.SimpleDateFormat

/*
 * Created on Aug 12, 2004
 */

/**
 * @author bob
 */
public class Prefs {

    public static void setFramePlace(JFrame frame, boolean welcome) {
        int fudge = 0;
        if (welcome)
            fudge = 25;
        setFramePlace(frame, fudge);
    }

    public static void setFramePlace(JFrame frame, int heightFudge) {

        Dimension pref = frame.getPreferredSize();
        // this fudgery is annoying
        pref.height += heightFudge;
        frame.setSize(pref);

        // frame.setSize(new Dimension(675, h));
        Dimension d = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        int x = (d.width - frame.getWidth()) / 2;
        int y = (d.height - frame.getHeight()) / 2;
        frame.setLocation(x, y);
        frame.setVisible(true);

        // f.setLocation(Pref.getChooserLocation(Pref.getMiddle(f.getSize())));
        // f.setSize(Pref.getChooserDimension(new Dimension(800,800)));
        // f.setLocation(Pref.getChooserLocation(Pref.getMiddle(f.getSize())));

    }

    static SimpleDateFormat sd = new SimpleDateFormat("MM/dd/yyyy");

    public static void justLoaded(File file) {
        ArrayList<String[]> al = Prefs.getLastModified();
        String dateMod = sd.format(new Date(file.lastModified()));
        String title = file.getName();
        String path = file.toString();

        // Remove it if it is already in list
        for (Iterator<String[]> iter = al.iterator(); iter.hasNext();) {
            String[] element = iter.next();
            if (element[2].equals(path)) {
                al.remove(element);
                break;
            }
        }

        // add to top.
        al.add(0, [dateMod, title, path] as String[]);

        setLastModified(al);
    }

    @SuppressWarnings("unchecked")
    public static ArrayList<String[]> getLastModified() {
        ArrayList<String[]> arrayList = (ArrayList<String[]>) PrefsCore.get("LASTMOD", new ArrayList());
        return arrayList;
    }

    public static void setLastModified(ArrayList<String[]> al) {
        PrefsCore.set("LASTMOD", al);
    }

    public static String getInitialFirstImageText() {
        return PrefsCore.getStr("InitialFirstImageText",
                "[Enter comment about this image here.]");
    }

    public static void setInitialFirstImageText(String initialFirstImageText) {
        PrefsCore.set("InitialFirstImageText", initialFirstImageText);
    }

    public static String getInitialIntroText() {
        return PrefsCore
                .getStr("InitialIntroText", "[Enter introduction here]");
    }

    public static void setInitialIntroText(String initialIntroText) {
        PrefsCore.set("InitialIntroText", initialIntroText);
    }

    public static String getInitialPrologText() {
        return PrefsCore.getStr("InitialPrologText", "[Enter the prolog here]\n\n\n<p>Created with <a href=http://jbum.sf.net/>jbum</a>");
    }

    public static void setInitialPrologText(String initialPrologText) {
        PrefsCore.set("InitialPrologText", initialPrologText);
    }

    public static String getInitialTitleText() {
        return PrefsCore.getStr("InitialTitleText", "[Enter title here]");
    }

    public static void setInitialTitleText(String initialTitleText) {
        PrefsCore.set("InitialTitleText", initialTitleText);
    }


    public static String getPDFViewer() {
        return PrefsCore.getStr("pdfViewer", "/usr/bin/xpdf");
    }

    public static void setPDFViewer(String pdfviewer) {
        PrefsCore.set("pdfViewer", pdfviewer);
    }

    public static String getImageEditor() {
        String defaultBrowser = "C:\\Windows\\System32\\mspaint.exe";
        if (File.separatorChar == '/')
            defaultBrowser = "/usr/bin/gimp";
        if (System.getProperty("java.vendor").toString().indexOf("Apple") != -1)
            defaultBrowser = "open";
        return PrefsCore.getStr("imageEditor", defaultBrowser);
    }

    public static void setImageEditor(String pdfviewer) {
        PrefsCore.set("imageEditor", pdfviewer);
    }

    public static String getWebBrowser() {
        String defaultBrowser = "C:\\Program Files\\Internet Explorer\\iexplore.exe";
        if (File.separatorChar == '/')
            defaultBrowser = "htmlview";
        if (System.getProperty("java.vendor").toString().indexOf("Apple") != -1)
            defaultBrowser = "open";
        return PrefsCore.getStr("webbrowser", defaultBrowser);
    }

    public static void setWebBrowser(String webbrowser) {
        PrefsCore.set("webbrowser", webbrowser);
    }
}
