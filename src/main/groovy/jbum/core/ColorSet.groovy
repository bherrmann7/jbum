package jbum.core;

import java.awt.Color;

/*
 * Created on Mar 19, 2004 @author bob
 */

public class ColorSet {

    public static String[] getNames() {
        String[] names = new String[sets.length / 5];
        for (int i = 0; i < sets.length / 5; i++) {
            names[i] = (String) sets[i * 5];
        }
        return names;
    }

    private static int getDex(String x) {
        for (int i = 0; i < sets.length / 5; i++) {
            if (x.equals(sets[i * 5]))
                return i * 5;
        }
        return 0;
    }

    static String DEFAULT = "Default Colors";

    private static Object[] sets = [
    // Name, Background, Text, Panel Even, PanelOdd
            DEFAULT, new Color(0xD0D0B0), Color.black, new Color(0xD0D0FF),
            new Color(0xE0E0E0),

            "Christmas", Color.WHITE, Color.black, new Color(0xFAB8B8),
            new Color(0xccffcc),

            "Halloween", new Color(0xFFDD77), Color.black, new Color(0xFFCC99),
            new Color(0xFFFFCC) ]

    public static Color getBackground(String name) {
        return (Color) sets[getDex(name) + 1];
    }

    public static Color getText(String name) {
        return (Color) sets[getDex(name) + 2];
    }

    public static Color getPanelEven(String name) {
        return (Color) sets[getDex(name) + 3];
    }

    public static Color getPanelOdd(String name) {
        return (Color) sets[getDex(name) + 4];
    }

}