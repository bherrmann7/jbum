package jbum.layouts

public class TemplateFactory {

	public static final String STANDARD = "Standard";

	public static final String POLAROIDS = "Polaroids";

	public static final String POLAROIDS_FLOW = "Polaroids Flow";

	public static final String WOODEN_FLOW = "Wooden Flow";

	public static final String CHAMELEON_FLOW = "Chameleon Flow";

	static private String[] templates = [ "standard", "IntroOnSide",
			"polaroids", "polaroidsFlow", "woodenFlow", "chameleon" ] as String[];

	public static String[] getNames() {
		return [ STANDARD, "Intro on left side", POLAROIDS,
				POLAROIDS_FLOW, WOODEN_FLOW, CHAMELEON_FLOW ] as String[];
	}

	public static String getResourceName(String templateName) {
		int dex = 0;
		for (int i = 0; i < getNames().length; i++) {
			if (templateName.equals(getNames()[i])) {
				dex = i;
				break;
			}
		}
		return "jbum/layouts/" + templates[dex] + "/page.html";
	}

	public static String getResourceBase(String templateName) {
		int dex = 0;
		for (int i = 0; i < getNames().length; i++) {
			if (templateName.equals(getNames()[i])) {
				dex = i;
				break;
			}
		}
		return "jbum/layouts/" + templates[dex] + "/";
	}

	public static boolean isJavaScriptTemplate(String templateName) {
		return templateName.equals(POLAROIDS_FLOW) ||
				 templateName.equals(WOODEN_FLOW) ||
				 templateName.equals(CHAMELEON_FLOW);
	}

	public static String[] getOtherResources(String templateName) {
		if (!isJavaScriptTemplate(templateName))
			return new String[0];
		if (templateName.equals(POLAROIDS_FLOW))
			return computeResources(POLAROIDS_FLOW);

		return [
				getResourceBase(templateName) + "prototype-1.4.0.js",
				getResourceBase(templateName) + "layout.js",
				getResourceBase(templateName) + "dl.png",
				getResourceBase(templateName) + "down.png",
				getResourceBase(templateName) + "dr.png",
				getResourceBase(templateName) + "left.png",
				getResourceBase(templateName) + "right.png",
				getResourceBase(templateName) + "ul.png",
				getResourceBase(templateName) + "up.png",
				getResourceBase(templateName) + "ur.png" ] as String[]

	}

	public static boolean isCommentOnBottom(String templateName) {
		return templateName.equals(POLAROIDS_FLOW) ||
				templateName.equals(POLAROIDS) ||
				templateName.equals(WOODEN_FLOW) ||
				templateName.equals(CHAMELEON_FLOW);
	}

	public static void main(String[] args) {
		computeResources(POLAROIDS_FLOW);

	}

	private static String[] computeResources(String templateName) {
		try {
			BufferedReader d = new BufferedReader(new InputStreamReader(
					TemplateFactory.class.getResourceAsStream( "/"
							+ getResourceBase(templateName)+"resources.txt")));
			ArrayList<String> resources = new ArrayList<String>();
			String line = null;
			while ((line = d.readLine()) != null) {
				resources.add(getResourceBase(templateName)+line);
			}
			return (String[]) resources.toArray(new String[resources.size()]);
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}

}