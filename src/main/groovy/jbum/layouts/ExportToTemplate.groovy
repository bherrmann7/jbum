package jbum.layouts;

import static jbum.ui.Main.prettyColor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;

import jbum.core.DPage;
import jbum.core.ImageInfo;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.json.JsonHierarchicalStreamDriver;

public class ExportToTemplate {
	
//	public void export(File currentDir, String templateName, String title, String intro,
//			VecImageInfo vecii, int picsPerRow, String bgColor,
//			String textColor, String panelOddColor, String panelEvenColor,
//			String prolog) throws IOException {
	
	public void export(DPage dp) throws IOException  {
			prepare(dp.getWhere(), dp.getTemplate());
			
			if ( TemplateFactory.isJavaScriptTemplate(dp.getTemplate()) ) {
				Page page = dp.toPage();
				
				XStream xstream = new XStream(new JsonHierarchicalStreamDriver());
				xstream.alias("page", jbum.layouts.Page.class);
				
				FileOutputStream fos = new FileOutputStream( new File(dp.getWhere(),"index.html") );
				fos.write ("<script>data = ".getBytes());
				fos.write(xstream.toXML(page).getBytes());		
				fos.write ("\np = data.page".getBytes());
				fos.write ("</script>\n".getBytes());
				fos.write("<script src='layout.js'></script>\n".getBytes());
				fos.close();
				return;
			}
			
			InputStream is = getTemplate(dp.getWhere(), dp.getTemplate());

			PageTemplate page = new PageTemplate(is);
			String newSection = page.replaceAll(page.header, '{$ title $}',
					dp.getTitle());
			String titleNoHtml = dp.getTitle();
			int d = titleNoHtml.indexOf('<');
			if (d != -1) {
				titleNoHtml = titleNoHtml.substring(0, d);
			}
			newSection = page.replaceAll(newSection, '{$ title-nohtml $}',
					titleNoHtml);
			newSection = page.replaceAll(newSection, '{$ intro $}', dp.getIntro());
			newSection = page.replaceAll(newSection, '{$ bgColor $}', prettyColor(dp.getBackgroundColor()));
			newSection = page.replaceAll(newSection, '{$ textColor $}',
					prettyColor(dp.getTextColor()));
			PrintWriter pw = new PrintWriter(new FileOutputStream(new File(
					dp.getWhere(), "index.html")));
			pw.write(newSection);
			String BGCOLOR1 = prettyColor(dp.getPanelEvenColor());
			String BGCOLOR2 = prettyColor(dp.getPanelOddColor());
			String BGCOLOR = BGCOLOR1;
			String FirstInRowColor = BGCOLOR2;
			for (int i = 0; i < dp.getVii().size(); i++) {
				if (i % dp.getPicsPerRow() == 0) {					
					pw.println("<TR>");
					if (FirstInRowColor == BGCOLOR1)
						BGCOLOR = BGCOLOR2;
					else
						BGCOLOR = BGCOLOR1;
					FirstInRowColor = BGCOLOR;
				}
				if (BGCOLOR == BGCOLOR1)
					BGCOLOR = BGCOLOR2;
				else
					BGCOLOR = BGCOLOR1;

				ImageInfo ii = dp.getVii().get(i);
				String name = ii.getName().replaceAll(" ", "%20");
				// Do medium image.
				m:{
					PrintWriter ww = new PrintWriter(new FileOutputStream(
							new File(dp.getWhere(), "html/me_" + ii.getName()
									+ ".html")));
					ww
							.println("<center><a href=../"
									+ name
									+ " onclick='window.moveTo(0,0);window.resizeTo(screen.availWidth,screen.availHeight);'>");
					ww.println("<img src=../smaller/me_" + name + "></a>");
					ww.close();
				}
				String panel = page.replaceAll(page.template,
						'{$ imageName $}', name);
				panel = handleAdd(panel, ii.mediumSize.width,
						ii.mediumSize.height);
				panel = page.replaceAll(panel, '{$ mediumWidth $}', ""
						+ ii.mediumSize.width);
				panel = page.replaceAll(panel, '{$ mediumHeight $}', ""
						+ ii.mediumSize.height);
				panel = page.replaceAll(panel, '{$ imageWidth $}', ""
						+ ii.smallSize.width);
				panel = page.replaceAll(panel, '{$ imageHeight $}', ""
						+ ii.smallSize.height);
				panel = page.replaceAll(panel, '{$ comment $}',
						dp.getVii().get(i).commentTA.getText());		
				panel = page.replaceAll(panel, '{$ originalWidth $}', ""
						+ ii.imgSize.width);
				panel = page.replaceAll(panel, '{$ originalHeight $}', ""
						+ ii.imgSize.height);
				panel = page.replaceAll(panel, '{$ textColor $}', prettyColor(dp.getTextColor()));
				panel = page.replaceAll(panel, '{$ color $}', BGCOLOR);
				pw.write(panel.toCharArray());
			}
			String prologHtml = page.replaceAll(page.footer, '{$ prolog $}',
					dp.getProlog());
			pw.write(prologHtml);
			pw.close();
	
	}

	void prepare(File currentDir, String templateName) throws FileNotFoundException {

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
		String[] x = TemplateFactory.getOtherResources(templateName);
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
			panel = panel.substring(0, dex) + contents
					+ panel.substring(end + 3);
		}
		return panel;
	}

	void copyIfDoesNotExist(File dir, String resourceName) {

		String dname = resourceName;
		int ldex = resourceName.indexOf('/',"jbum/layouts/".length());
		if (ldex != -1)
			dname = resourceName.substring(ldex + 1);
		File destination = new File(dir, dname);
//		if (destination.exists())
//			return;		
		destination.getParentFile().mkdirs();
		try {
			FileOutputStream fos = new FileOutputStream(destination);
			InputStream is = getClass().getClassLoader().getResourceAsStream(
					resourceName);
			while (true) {
				int b = is.read();
				if (b == -1)
					break;
				fos.write((byte) b);
			}
			fos.close();
		} catch (Throwable ioe) {
			System.out.println("problem copying the file, " + destination);
			System.out.println("ex: "+ioe.getMessage());
		}
	}
}