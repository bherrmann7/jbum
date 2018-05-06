package jbum.other

 class Scan {

     static void main(String[] args) {
        File f = new File(System.getProperty("user.home"));
        if (args.length == 1)
            f = new File(args[0]);

        System.out.println(f);

        findImgs(f);
        System.out.println("Possibles: " + possibles.size());
    }

    static HashMap<String, File> possibles = new HashMap<String, File>();

    private static void findImgs(File f) {
        File[] l = f.listFiles();
        for (int i = 0; i < l.length; i++) {
            if (l[i].getName().toLowerCase().endsWith(".jpg") && l[i].length() > 10000) {
                if (l[i].toString().indexOf("archive") != -1 || l[i].toString().indexOf("meta") != -1)
                    return;
                update(l[i].getParent(), l[i]);
                System.out.println(l[i].getParent());
                return;
            } else if (l[i].isDirectory()) {
                if (!new File(l[i], "jbum.ser").exists() && !new File(l[i], "index.html").exists())
                    findImgs(l[i]);
            }
        }

    }

    private static void update(String parent, File file) {

        possibles.put(parent, file);
    }

}
