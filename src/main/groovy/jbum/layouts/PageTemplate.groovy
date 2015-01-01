package jbum.layouts;

import java.io.InputStream;

class PageTemplate {

    String foreach = '{$ foreach $}';

    String next = '{$ next $}';

    String header;

    String footer;

    String template;

    PageTemplate(InputStream is) {
        try {
            StringBuffer sb = new StringBuffer();
            int b = 0;
            while ((b = is.read()) >= 0)
                sb.append((char) b);
            String x = sb.toString();

            int foreachLoc = x.indexOf(foreach);
            if (foreachLoc == -1) {
                error(foreach + " tag not found");
            }

            int nextLoc = x.indexOf(next);
            if (nextLoc == -1) {
                error(next + " tag not found");
            }

            header = x.substring(0, foreachLoc);
            template = x.substring(foreachLoc + foreach.length(), nextLoc);
            footer = x.substring(nextLoc + next.length());

        } catch (Exception e) {
            error("Exception: " + e);
            e.printStackTrace();
        }
    }

    String replaceAll(String src, String match, String replace) {
        int loc = -1;
        while ((loc = src.indexOf(match)) != -1) {
            src = src.substring(0, loc) + replace
                    + src.substring(loc + match.length());
        }
        return src;
    }

    void error(String msg) {
        System.err.println(msg);
        throw new RuntimeException(msg);
    }

}