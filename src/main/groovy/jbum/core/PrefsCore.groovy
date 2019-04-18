/*
 * Created on Aug 12, 2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */

package jbum.core

import jbum.ui.Main

/**
 * @author bob
 */
public class PrefsCore {

    public static void setApp(String appName) {
        PrefsCore.appName = appName;
    }

    final static boolean debug = false;

    private static String appName = "jbum";

    private static File file;

    private static Properties prop;

    private static void initProp() {
        if (prop != null)
            return;

        file = new File(System.getProperty("user.home") + File.separator +
                "." + appName + ".prop");

        if (file.exists()) {
            try {
                java.io.ObjectInputStream ois = new java.io.ObjectInputStream(
                        new java.io.FileInputStream(file));
                prop = (Properties) ois.readObject();
                ois.close();
            } catch (Exception e) {
                Main.error(e, "fff");
            }
        } else {
            prop = new Properties();
        }
    }

    static int getInt(String name, int defaulta) {
        initProp();
        if (prop.get(name) == null)
            return defaulta;
        int x = Integer.parseInt(prop.get(name).toString());
        if (debug)
            System.out.println("AppProp: getInt(name=" + name + ",default="
                    + defaulta + ") returns " + x);
        return x;
    }

    static File getFile(String name, File defaulta) {
        initProp();
        File r = (File) prop.get(name);
        if (r == null)
            r = defaulta;
        if (debug)
            System.out.println("AppProp: getFile(name=" + name + ",default="
                    + defaulta + ") returns " + r);
        return r;
    }

    static File getFile(String name) {
        initProp();
        File f = (File) prop.get(name);
        if (debug)
            System.out.println("AppProp: getFile(name=" + name + ") returns "
                    + f);
        return f;
    }

    public static Object get(String name, Object defaultValue) {
        initProp();

        Object o = prop.get(name);
        if (o == null)
            o = defaultValue;

        if (debug)
            System.out.println("AppProp: get(name=" + name + ",default=" + defaultValue
                    + ") returns " + o);
        return o;
    }

    public static String getStr(String name, Object defaultValue) {
        initProp();

        Object o = prop.get(name);
        if (o == null)
            o = defaultValue;

        if (debug)
            System.out.println("AppProp: get(name=" + name + ",default=" + defaultValue
                    + ") returns " + o);

        if (o == null)
            return null;
        return o.toString();
    }

    static void setInt(String name, int x) {
        initProp();
        if (debug)
            System.out.println("AppProp: set(name=" + name + ", x=" + x + ");");

        prop.put(name, Integer.toString(x));
        syncProp();
    }

    public static void set(String name, java.io.Serializable x) {
        initProp();
        if (debug)
            System.out.println("AppProp: set(name=" + name + ", x=" + x + ");");

        prop.put(name, x);
        syncProp();
    }

    static void list(java.io.PrintWriter out) {
        initProp();
        out.println("-- AppProp --");
        for (java.util.Enumeration e = prop.keys(); e.hasMoreElements();) {
            String key = e.nextElement().toString();
            out.println(key + "=" + prop.get(key).toString());
        }
    }

    private static void syncProp() {
        try {
            java.io.ObjectOutputStream oos = new java.io.ObjectOutputStream(
                    new java.io.FileOutputStream(file));
            oos.writeObject(prop);
            oos.close();
        } catch (Exception e) {
            Main.error(e, "saving prop");
        }
    }

}