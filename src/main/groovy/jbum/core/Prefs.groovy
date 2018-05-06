package jbum.core

import javax.swing.*
import java.awt.*
import java.text.SimpleDateFormat

 class Prefs {

     static void setFramePlace(JFrame frame, boolean welcome) {
        int fudge = 0;
        if (welcome)
            fudge = 25;
        setFramePlace(frame, fudge);
    }

     static void setFramePlace(JFrame frame, int heightFudge) {

        Dimension pref = frame.getPreferredSize();
        // this fudgery is annoying
        pref.height += heightFudge;
        frame.setSize(pref);

        // frame.setSize(new Dimension(675, h));
        Dimension d = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        int x = (d.width - frame.getWidth()) / 2;
        int y = (d.height - frame.getHeight()) / 2;
        frame.setLocation(100, 100);
        frame.setSize((d.width as Integer)-200, (d.height as Integer)-200)
        frame.setVisible(true);

        // f.setLocation(Pref.getChooserLocation(Pref.getMiddle(f.getSize())));
        // f.setSize(Pref.getChooserDimension(new Dimension(800,800)));
        // f.setLocation(Pref.getChooserLocation(Pref.getMiddle(f.getSize())));
    }

    static SimpleDateFormat sd = new SimpleDateFormat("MM/dd/yyyy");

     static void justLoaded(File file) {
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
     static ArrayList<String[]> getLastModified() {
        ArrayList<String[]> arrayList = (ArrayList<String[]>) PrefsCore.get("LASTMOD", new ArrayList());
        return arrayList;
    }

     static void setLastModified(ArrayList<String[]> al) {
        PrefsCore.set("LASTMOD", al);
    }

     static String getInitialFirstImageText() {
        return PrefsCore.getStr("InitialFirstImageText",
                "[Enter comment about this image here.]");
    }

     static void setInitialFirstImageText(String initialFirstImageText) {
        PrefsCore.set("InitialFirstImageText", initialFirstImageText);
    }

     static String getInitialIntroText() {
        return PrefsCore
                .getStr("InitialIntroText", "[Enter introduction here]");
    }

     static void setInitialIntroText(String initialIntroText) {
        PrefsCore.set("InitialIntroText", initialIntroText);
    }

     static String getInitialPrologText() {
        return PrefsCore.getStr("InitialPrologText", "[Enter the prolog here]\n\n\n<p>Created with <a href=http://jbum.sf.net/>jbum</a>");
    }

     static void setInitialPrologText(String initialPrologText) {
        PrefsCore.set("InitialPrologText", initialPrologText);
    }

     static String getInitialTitleText() {
        return PrefsCore.getStr("InitialTitleText", "[Enter title here]");
    }

     static void setInitialTitleText(String initialTitleText) {
        PrefsCore.set("InitialTitleText", initialTitleText);
    }


     static String getPDFViewer() {
        return PrefsCore.getStr("pdfViewer", "/usr/bin/xpdf");
    }

     static void setPDFViewer(String pdfviewer) {
        PrefsCore.set("pdfViewer", pdfviewer);
    }

     static String getImageEditor() {
        String defaultBrowser = "C:\\Windows\\System32\\mspaint.exe";
        if (File.separatorChar == '/')
            defaultBrowser = "/usr/bin/gimp";
        if (System.getProperty("java.vendor").toString().indexOf("Apple") != -1)
            defaultBrowser = "open";
        return PrefsCore.getStr("imageEditor", defaultBrowser);
    }

     static void setImageEditor(String pdfviewer) {
        PrefsCore.set("imageEditor", pdfviewer);
    }

     static String getWebBrowser() {
        String defaultBrowser = "C:\\Program Files\\Internet Explorer\\iexplore.exe";
        if (File.separatorChar == '/')
            defaultBrowser = "htmlview";
        if (System.getProperty("java.vendor").toString().indexOf("Apple") != -1)
            defaultBrowser = "open";
        return PrefsCore.getStr("webbrowser", defaultBrowser);
    }

     static void setWebBrowser(String webbrowser) {
        PrefsCore.set("webbrowser", webbrowser);
    }
}
