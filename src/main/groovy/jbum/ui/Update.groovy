package jbum.ui

import jbum.core.Version

class Update extends Thread {

    @SuppressWarnings("deprecation")
    void run() {
        try {
            URL url = new URL("http://jadn.com/jbum/update.dat");
            DataInputStream dis = new DataInputStream(url.openStream());
            String version = dis.readLine();
            if (!Version.VERSION.equals(version)) {
                App.warning("Version mismatch", "Your version of jbum does not match the latest released version.\nYours = " + Version.VERSION + "\nHosted at jadn.com/jbum/ = " + version);
            }
        } catch (Throwable t) {
            //t.printStackTrace();
        }

    }
}
