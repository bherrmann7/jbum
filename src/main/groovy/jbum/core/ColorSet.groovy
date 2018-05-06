package jbum.core

import java.awt.*

 class ColorSet {


    private static Object[] sets = [
            // Name, Background, Text, Panel
            [ DEFAULT, new Color(0xD0D0B0), Color.black, new Color(0xD0D0FF) ],

            ["Christmas", Color.WHITE, Color.black, new Color(0xFAB8B8)],

            ["Halloween", new Color(0xFFDD77), Color.black, new Color(0xFFCC99) ]]

     static String[] getNames() {
        def names = []
        sets.each {
            names.add( it[0] )
        }
        return names;
    }

    private static int getDex(String x) {
        return sets.findIndexOf { it[0] == x }
    }

    static String DEFAULT = "Default Colors";

     static Color getBackground(String name) {
        return (Color) sets[getDex(name)][1];
    }

     static Color getText(String name) {
        return (Color) sets[getDex(name)][2];
    }

     static Color getPanel(String name) {
        return (Color) sets[getDex(name)][3];
    }

}