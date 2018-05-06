package jbum.core

import com.drew.imaging.jpeg.JpegMetadataReader
import com.drew.metadata.Directory
import com.drew.metadata.Metadata

import java.text.SimpleDateFormat

 class IndexScanner {
     static SimpleDateFormat sd = new SimpleDateFormat("yyyy:MM:dd kk:mm:ss");

     interface StatusCallback {
         void progress(int atNum, int ofTotal, String name);
    }

    String[] topLevelDirs;

     IndexScanner(String[] topLevelDirs) {
        this.topLevelDirs = topLevelDirs;
    }

     static Date getEarliestDate(File jbfile) {
        File usedate = new File(jbfile.getParentFile(), "usedate");

        if (usedate.exists()) {
            try {
                BufferedReader d = new BufferedReader(new InputStreamReader(
                        new FileInputStream(usedate)));
                String date = d.readLine();

                return sd.parse(date);
            } catch (Exception e) {
                System.out.println("Couldnt parse the useDate file. " + usedate
                        + ", probably not in fmt: " + sd.toPattern());
            }
        }

        File[] list = jbfile.getParentFile().listFiles();
        Date d = new Date();

        for (int i = 0; i < list.length; i++) {
            if (!list[i].toString().endsWith(".jpg")
                    && !list[i].toString().endsWith(".JPG")) {
                continue;
            }

            Date ck = getDate(list[i]);

            if (ck == null) {
                continue;
            }

            if (ck.before(d)) {
                d = ck;

                // get first date, call that earliest.
                return ck;
            }
        }

        // System.out.println(efile);
        return d;
    }

     static Date getDate(File jpegFile) {
        try {
            Metadata metadata = JpegMetadataReader.readMetadata(jpegFile);

            // iterate through metadata directories
            Iterator directories = metadata.getDirectoryIterator();

            while (directories.hasNext()) {
                Directory directory = (Directory) directories.next();

                // iterate through tags and print to System.out
                String date = directory.getDescription(306);
                if (date == null)
                    date = directory.getDescription(36867);

                if (date != null) {
                    try {
                        return sd.parse(date);
                    } catch (java.text.ParseException pe) {
                    }

                    try {
                        SimpleDateFormat sd = new SimpleDateFormat(

                                // Mon Apr 16 22:54:37 2001;
                                "EEE MMM dd kk:mm:ss yyyy;");

                        return sd.parse(date);
                    } catch (java.text.ParseException pe) {
                    }

                    System.out.println("===========>  COULDN'T PARSE DATE: "
                            + date);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    StatusCallback callback;

    @SuppressWarnings("unchecked")
     ArrayList<DirEntry> scan(StatusCallback callback) {
        if (callback == null)
            callback = new StatusCallback() {
                 void progress(int atNum, int ofTotal, String name) {
                    System.out.println(atNum + " " + ofTotal + " " + name);
                }
            };
        this.callback = callback;

        // find all jbum.ser files
        ArrayList<File> alx = new ArrayList<File>();

        for (int i = 0; i < topLevelDirs.length; i++) {
            getAll(new File(topLevelDirs[i]), alx);
        }

        File[] jbf = alx.toArray(new File[0]);

        ArrayList<DirEntry> al = new ArrayList<DirEntry>();

        for (int i = 0; i < jbf.length; i++) {
            callback.progress(i, jbf.length, jbf[i].toString());
            if (new File(jbf[i].getParent(), "private").exists())
                continue;

            Date d = getEarliestDate(jbf[i]);

            // System.out.println("MMM yy"+" "+jbf[i]);
            al.add(new DirEntry(d, jbf[i]));
        }

        Collections.sort(al);
        //Collections.reverse(al);

        return al;
    }


    void getAll(File dir, ArrayList<File> al) {
        // System.out.println("Checking: "+dir);
        File[] list = dir.listFiles();

        for (int i = 0; i < list.length; i++) {
            if (list[i].isDirectory()) {
                getAll(list[i], al);
            }

            if (list[i].getName().equals("jbum.ser")) {
                al.add(list[i]);
                callback.progress(-1, -1, "Found " + list[i]);
            }
        }
    }

     class DirEntry implements Comparable {
         Date d;

         File jbf;

        DirEntry(Date d, File jbf) {
            this.d = d;
            this.jbf = jbf;
        }

        /*
         * (non-Javadoc)
         *
         * @see java.lang.Comparable#compareTo(java.lang.Object)
         */

         int compareTo(Object o) {
            return d.compareTo(((DirEntry) o).d);
        }
    }

}
