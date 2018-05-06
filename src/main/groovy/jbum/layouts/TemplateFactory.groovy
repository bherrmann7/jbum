package jbum.layouts

class TemplateFactory {

     static final String STANDARD = "Standard";

     static final String POLAROIDS = "Polaroids";

     static final String POLAROIDS_FLOW = "Polaroids Flow";

     static final String WOODEN_FLOW = "Wooden Flow";

     static final String CHAMELEON_FLOW = "Chameleon Flow";

    static private String[] templates = ["standard", "IntroOnSide",
                                         "polaroids", "polaroidsFlow", "woodenFlow", "chameleon"] as String[];

     static String[] getNames() {
        return [STANDARD, "Intro on left side", POLAROIDS,
                POLAROIDS_FLOW, WOODEN_FLOW, CHAMELEON_FLOW] as String[];
    }

     static String getResourceName(String templateName) {
        int dex = 0;
        for (int i = 0; i < getNames().length; i++) {
            if (templateName.equals(getNames()[i])) {
                dex = i;
                break;
            }
        }
        return "jbum/layouts/" + templates[dex] + "/page.html";
    }

     static String getResourceBase(String templateName) {
        int dex = 0;
        for (int i = 0; i < getNames().length; i++) {
            if (templateName.equals(getNames()[i])) {
                dex = i;
                break;
            }
        }
        return "jbum/layouts/" + templates[dex] + "/";
    }
    

     static String[] getOtherResources() {
            return computeResources(POLAROIDS_FLOW);
    }

    private static String[] computeResources(String templateName) {
        try {
            BufferedReader d = new BufferedReader(new InputStreamReader(
                    TemplateFactory.class.getResourceAsStream("/"
                            + getResourceBase(templateName) + "resources.txt")));
            ArrayList<String> resources = new ArrayList<String>();
            String line = null;
            while ((line = d.readLine()) != null) {
                resources.add(getResourceBase(templateName) + line);
            }
            return (String[]) resources.toArray(new String[resources.size()]);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

}