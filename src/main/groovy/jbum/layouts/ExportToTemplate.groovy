package jbum.layouts

import com.thoughtworks.xstream.XStream
import com.thoughtworks.xstream.io.json.JsonHierarchicalStreamDriver
import jbum.core.DPage

class ExportToTemplate {

    void export(DPage dp) throws IOException {
        prepare(dp.getWhere());

        Page page = dp.toPage();

        FileOutputStream fos = new FileOutputStream(new File(dp.getWhere(), "index.html"));
        if (false) {
            XStream xstream = new XStream(new JsonHierarchicalStreamDriver());
            xstream.alias("page", jbum.layouts.Page.class);

            fos.write("<script>data = ".getBytes());
            fos.write(xstream.toXML(page).getBytes());
            fos.write("\np = data.page".getBytes());
            fos.write("</script>\n".getBytes());

        }
        fos.write(("<div id=ctx></div>" +
                "<script src='layout.js'></script>\n").getBytes());
        fos.close();
    }

    void prepare(File currentDir) throws FileNotFoundException {

        File index = new File(currentDir, "index.html");
        File htmldir = new File(currentDir, "html");
        if (!htmldir.isDirectory()) {
            htmldir.mkdir();
        }
        if (!htmldir.isDirectory()) {
            throw new RuntimeException(
                    "Unable to create directory named 'html'");
        }
        if (index.exists()) {
            File nIndex = new File(currentDir, index.toString() + "."
                    + System.currentTimeMillis());
            index.renameTo(nIndex);
        }
        String[] x = TemplateFactory.getOtherResources();
        for (int i = 0; i < x.length; i++) {
            copyIfDoesNotExist(currentDir, x[i]);
        }
        // copyIfDoesNotExist(currentDir, "template.html");
        copyIfDoesNotExist(currentDir, "photo.png");
    }

    InputStream getTemplate(File currentDir, String templateName) throws FileNotFoundException {
        File template = new File(currentDir, "template.html");
        InputStream is = null;
        String trytoLoad = null;
        if (template.exists()) {
            trytoLoad = "file:" + template;
            is = new FileInputStream(template);
        } else {
            trytoLoad = "classLoaderOf" + getClass().getName() + ":" + TemplateFactory.getResourceName(templateName);
            is = getClass().getClassLoader().getResourceAsStream(
                    TemplateFactory.getResourceName(templateName));
        }
        if (is == null)
            throw new RuntimeException("Template not found, " + trytoLoad);
        return is;
    }

    private String handleAdd(String panel, Double width, Double height) {
        handleAdd(panel, width.toInteger(), height.toInteger())
    }

    private String handleAdd(String panel, int width, int height) {
        int dex = 0;
        while ((dex = panel.indexOf('{$ add ')) != -1) {
            int end = panel.indexOf(' $}', dex);
            String[] args = panel.substring(dex + '{$ add '.length(), end)
                    .split(" ");
            int contents = height
            if (args[0].equals("mediumWidth"))
                contents = width
            contents += Integer.parseInt(args[1]);
            panel = panel.substring(0, dex) + contents + panel.substring(end + 3);
        }
        return panel;
    }

    void copyIfDoesNotExist(File dir, String resourceName) {

        String dname = resourceName;
        int ldex = resourceName.indexOf('/', "jbum/layouts/".length());
        if (ldex != -1)
            dname = resourceName.substring(ldex + 1);
        File destination = new File(dir, dname);
//		if (destination.exists())
//			return;		
        destination.getParentFile().mkdirs();
        try {
            FileOutputStream fos = new FileOutputStream(destination);
            InputStream is = getClass().getClassLoader().getResourceAsStream(resourceName);
            while (true) {
                int b = is.read();
                if (b == -1)
                    break;
                fos.write((byte) b);
            }
            fos.close();
        } catch (Throwable ioe) {
            System.out.println("problem copying the file, " + destination);
            System.out.println("ex: " + ioe.getMessage());
        }
    }
}