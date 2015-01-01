package jbum.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.Insets;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.text.JTextComponent;

import jbum.core.*;
import jbum.layouts.TemplateFactory;

import com.drew.imaging.jpeg.JpegMetadataReader;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifDirectory;
import com.swabunga.spell.engine.SpellDictionary;
import com.swabunga.spell.engine.SpellDictionaryHashMap;
import com.swabunga.spell.swing.JTextComponentSpellChecker;

@SuppressWarnings("serial")
public class CenterP extends JScrollPane {

	public static String[] buttonInfo = [ //

			"-",
			"split - takes this image and all before it and moves them into another folder",
			"split", //
			"I", "Insert deleted images after this image",
			"insert", //
			"C", "Rotate Clockwise",
			"clockwise", //
			"CC", "Rotate Counter Clockwise",
			"counterclockwise", //
			"+", "view larger version of imageoom ",
			"zoom", //
			"X", "delete image",
			"trash", //
			"<-", "move image back in list",
			"back", //
			"->", "move image forwared in list",
			"forward", //
			"R", "reload image",
			"reload", //
			"info", "display image info",
			"information", //
			"sp", "Spell check", "spellcheck", "tool", "external tool",
			"hammer" ];

	static SimpleDateFormat sd = new SimpleDateFormat("yyyy:MM:dd kk:mm:ss");

	public SpellDictionary dictionary;

	public VecImageInfo vecii;

	Color dark = new Color(0xE0E0E0);

	// JPanel prologP = new JPanel();
	Color light = new Color(0xD0D0FF);

	DeletionManager deletionManager;

	File currentDir;

	File jbumSer;

	private JPanel introP = new JPanel();

	private JPanel titleP = new JPanel();

	JTextArea introTA = new JTextArea("intro", 5, 60);

	JTextArea prologTA = new JTextArea("", 5, 60);

	JTextField titleTF = new JTextField("Title", 60);

	String htmlPath;

	Color[] rotateColor = [ light, dark, dark, light ];

	int lastColor = 0;

	private String iconPath;

	CenterP(DeletionManager deletionManager) {
		super(new Box(BoxLayout.Y_AXIS));

		this.deletionManager = deletionManager;
		deletionManager.centerP = this;

		introTA.setWrapStyleWord(true);
		introTA.setLineWrap(true);
		prologTA.setWrapStyleWord(true);
		prologTA.setLineWrap(true);
	}

	public static Date getDate(File jpegFile, List<Camera> cameraList) {
        if(cameraList==null){
            throw new RuntimeException("Humm...");
        }
		try {
			Metadata metadata = JpegMetadataReader.readMetadata(jpegFile);
			Directory dir = metadata.getDirectory(ExifDirectory.class);
			Date d = sd.parse(dir.getString(36867));
            String cameraName = dir.getString(271);
            d = CameraUtil.adjustTime(d, cameraList, cameraName);
			return d;
		} catch (Exception e) {
			return null;
		}
	}

    class IDate implements Comparable {
        ImageInfo ii;

        Date date;

        public int compareTo(Object o) {
            return date.compareTo(((IDate) o).date);
        }
    };

	/**
	 * 
	 */
	@SuppressWarnings("unchecked")
	public void orderByExifDate(List<Camera> cameras) {

		// get dates
		ArrayList<IDate> idates = new ArrayList<IDate>();

		for (int i = 0; i < vecii.size(); i++) {
			IDate id = new IDate();
			id.ii = vecii.get(i);
			id.date = getDate(id.ii.getOriginalFile(Main.getCurrentDir()),cameras);

            if(id.ii.getName().endsWith("_2157.jpg")){
                System.out.println("hit it");
            }

			if (id.date == null) {
				if (i != 0) {
					id.date = idates.get(i - 1).date;
				} else {
					for (int s = i + 1; (s < vecii.size()) && (id.date == null); s++) {
						id.date = getDate(vecii.get(s).getOriginalFile(
								Main.getCurrentDir()), cameras);
					}

					if (id.date == null) {
						// nothing must have a date.
						id.date = new Date();
					}
				}
			}

			idates.add(id);
		}

		// sort
		Collections.sort(idates);

		// re-populate
		vecii.clear();

		for (Iterator<IDate> iter = idates.iterator(); iter.hasNext();) {
			IDate idate = iter.next();
			vecii.add(idate.ii);
		}

		rebuildComponents();
	}

	public void rebuildComponents() {
		Box box = (Box) getViewport().getView();

		box.removeAll();

		deletionManager.imageInfoIsNow(vecii);
		setVisible(false);
		doComponents();
		setVisible(true);
	}

	/*
	 * private void newColor( Component comp, Color c ){ if (comp instanceof
	 * Container) { Container x = (Container) comp;
	 * 
	 * for (int i = 0; i < x.getComponentCount(); i++) { newColor(
	 * x.getComponent(i), c); } } comp.setBackground(c);
	 */
	void setColor(String what, Color c) {
		if ("Text".equals(what)) {
			titleTF.setForeground(c);
			introTA.setForeground(c);
			prologTA.setForeground(c);
		}

		if ("Panel Odd".equals(what)) {
			rotateColor[1] = c;
			rotateColor[2] = c;
		}

		if ("Panel Even".equals(what)) {
			rotateColor[0] = c;
			rotateColor[3] = c;
		}

		if ("Background".equals(what)) {
			// Box box = (Box) getViewport().getView();
			setBackground(c);
		}
	}

	Color getColor(String what) {
		if ("Text".equals(what)) {
			return titleTF.getForeground();
		}

		if ("Panel Odd".equals(what)) {
			return rotateColor[1];
		}

		if ("Panel Even".equals(what)) {
			return rotateColor[0];
		}

		if ("Background".equals(what)) {
			return getBackground();
		}

		return Color.black;
	}

	void setColorSet(String setName) {
		setColor("Background", ColorSet.getBackground(setName));
		setColor("Text", ColorSet.getText(setName));
		setColor("Panel Odd", ColorSet.getPanelOdd(setName));
		setColor("Panel Even", ColorSet.getPanelEven(setName));
	}

	void setDir(File dir) {
		currentDir = dir;
		jbumSer = new File(dir, "jbum.ser");
		iconPath = ''+currentDir + File.separator + "smaller" + File.separator;

		File outF = new File(iconPath);

		if (jbumSer.exists() && !outF.exists()) {
			iconPath = outF.getParent() + File.separator;
			JOptionPane
					.showMessageDialog(
							null,
							"This jbum page uses the older style of putting thumbnails\n"
									+ "in the main directory (and not the 'smaller' sub directory). Resaving\n"
									+ "this page may result in a page w/o images.",
							"Missing 'smaller' directory",
							JOptionPane.WARNING_MESSAGE);
		}

		htmlPath = ''+currentDir + File.separator + "html" + File.separator;
	}

	/*
	 * void setBackgroundChildren(Component component, Color color) { if
	 * (component instanceof JTextArea) {
	 * component.setBackground(color.darker());
	 * 
	 * return; }
	 * 
	 * component.setBackground(color);
	 * 
	 * if (component instanceof Container) { Container x = (Container)
	 * component;
	 * 
	 * for (int i = 0; i < x.getComponentCount(); i++) {
	 * setBackgroundChildren(x.getComponent(i), color); } } }
	 */
	DPage getCurrentPage() {
		DPage p = new DPage(currentDir, titleTF.getText(), introTA.getText(),
				vecii, getColor("Background"), getColor("Text"),
				getColor("Panel Odd"), getColor("Panel Even"), Main
						.getPicsPerRow(), Main.getTemplate(), prologTA
						.getText(), blogInfo);

		return p;
	}

    BlogInfo blogInfo = new BlogInfo(this);

	public static void sort(VecImageInfo vecii) {
		boolean flipped = true;

		while (flipped) {
			flipped = false;

			for (int i = 0; i < (vecii.size() - 1); i++) {
				String s1 = vecii.get(i).getName();
				String s2 = vecii.get(i + 1).getName();

				if (s1.compareTo(s2) > 0) {
					ImageInfo tmp = vecii.get(i);
					vecii.set(vecii.get(i + 1), i);
					vecii.set(tmp, i + 1);
					flipped = true;
				}
			}
		}
	}

	void scanDir() {
		boolean newPage = false;

		if (!jbumSer.exists()) {
			newPage = true;
			vecii = new VecImageInfo();

			File[] list = currentDir.listFiles();

            if(list) {
                for (int i = 0; i < list.length; i++) {
                    if (list[i].toString().toLowerCase().endsWith(".jpg")) {
                        ImageInfo ii = new ImageInfo(list[i], null, null, null);
                        vecii.add(ii);
                    }
                    // not ready for this yet.
                    if (false) {
                        if (list[i].toString().toLowerCase().endsWith(".mov")
                                || list[i].toString().toLowerCase()
                                .endsWith(".avi")) {
                            ImageInfo ii = new ImageInfo(list[i], null, null, null);
                            vecii.add(ii);
                        }
                    }
                }
            }

			// listFiles comes back random, so we sort it.
			sort(vecii);

			if (vecii.size() > 0) {
				vecii.get(0).commentTA
						.setText(Prefs.getInitialFirstImageText());
			}

			try {
				ObjectOutputStream oos = new ObjectOutputStream(
						new FileOutputStream(jbumSer));
				oos.writeObject(Prefs.getInitialTitleText());
				oos.writeObject(Prefs.getInitialIntroText());
				oos.writeObject(vecii);
				oos.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		// load()
		DPage meta = new DPage(jbumSer, newPage);
		titleTF.setText(meta.getTitle());
		introTA.setText(meta.getIntro());
		vecii = meta.getVii();

		setColor("Background", meta.getBackgroundColor());
		setColor("Text", meta.getTextColor());
		setColor("Panel Odd", meta.getPanelOddColor());
		setColor("Panel Even", meta.getPanelEvenColor());

		Main.setPicsPerRow(meta.getPicsPerRow());
		Main.setTemplate(meta.getTemplate());
		prologTA.setText(meta.getProlog());

		deletionManager.imageInfoIsNow(vecii);

		doComponents();

		// rebuildComponents();
	}

	void spellcheck() {
		try {
			if (dictionary == null) {
				dictionary = new SpellDictionaryHashMap(new InputStreamReader(
						CenterP.class.getClassLoader().getResourceAsStream(
								"english.0")));
			}

			JTextComponentSpellChecker sc = new JTextComponentSpellChecker(
					dictionary);
			spellcheck(this, sc);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	void spellcheck(Component component, JTextComponentSpellChecker sc) {
		if (component instanceof Container) {
			Container x = (Container) component;

			for (int i = 0; i < x.getComponentCount(); i++) {
				spellcheck(x.getComponent(i), sc);
			}
		}

		if (component instanceof JTextComponent) {
			sc.spellCheck((JTextComponent) component);
		}
	}

	private void doComponents() {
		Box box = (Box) getViewport().getView();

		titleP.add(titleTF);
		box.add(titleP);
		titleP.setBackground(getBackground());
		titleTF.setBackground(getBackground().darker());

		introP.add(introTA);
		box.add(introP);
		introP.setBackground(getBackground());
		introTA.setBackground(getBackground().darker());

		JPanel rowP = new JPanel();
		box.add(rowP);
		rowP.setBackground(getBackground());

		lastColor = 0;
		/*
		 * for (int i = vecii.size() - 1; i >= 0; i--) { ImageInfo ii =
		 * vecii.get(i); if (ii.getName().toLowerCase().endsWith(".mov") ||
		 * ii.getName().toLowerCase().endsWith(".avi")) { vecii.remove(ii); } }
		 */

		for (int i = 0; i < vecii.size(); i++) {
			ImageInfo ii = vecii.get(i);
			Color useColor = rotateColor[lastColor];

			if (Main.getTemplate().equals("Polaroids")) {
				useColor = rotateColor[0];
			}
			if (Main.getTemplate().equals("Wooden Flow")) {
				useColor = rotateColor[0];
			}
			if (Main.getTemplate().equals("Polaroids Flow")) {
				useColor = rotateColor[0];
			}

			JPanel littleP = new JPanel();
			littleP.setBackground(useColor);
			littleP.setLayout(new BorderLayout());
			rowP.add(littleP);

			String name = ii.getName();

			JButton button = new JButton(name, ImageCache.get(ii
					.getSmallFile(Main.getCurrentDir())));
			button.setBackground(littleP.getBackground());

			if (ii.imgSize == null ||
			// Not sure why this was turned off ... Apr 17,2007
					(!ii.getSmallFile(Main.getCurrentDir()).exists())) {
				ImageProcessor.enqueue(ii, button, ImageProcessor.SMALLER);
			}

			if (TemplateFactory.isCommentOnBottom(Main.getTemplate())) {
				littleP.add(button, BorderLayout.NORTH);
			} else {
				littleP.add(button, BorderLayout.CENTER);
			}

			button.setBackground(useColor);
			button.setVerticalTextPosition(JButton.BOTTOM);
			button.setHorizontalTextPosition(JButton.CENTER);

			JTextArea jta = ii.commentTA;
			jta.setBackground(useColor.darker());
			jta.setForeground(introTA.getForeground());
			jta.setLineWrap(true);
			jta.setWrapStyleWord(true);

			if (Main.getTemplate().equals("Polaroids")
					|| Main.getTemplate().equals("Polaroids Flow")) {
				littleP.add(jta, BorderLayout.CENTER);
			} else {
				littleP.add(jta, BorderLayout.EAST);
			}

			JPanel tools = new JPanel();

			// if ( Main.getTemplate().equals("Polaroids") )
			tools.setLayout(new GridLayout(0, 6));
			// tools.setLayout(new GridLayout(1,0));
			tools.setBackground(useColor);
			tools.setBorder(new EmptyBorder(0, 0, 0, 0));

			ImageAction ia = new ImageAction(this, ii, button, littleP);
			button.addActionListener(ia);

			for (int bDex = 0; bDex < (buttonInfo.length / 3); bDex++) {
				URL url = getClass().getClassLoader().getResource(
						"jbum/ui/icons/" + buttonInfo[(bDex * 3) + 2] + ".gif");
				JButton b = new JButton(new ImageIcon(url));
				url = getClass().getClassLoader().getResource(
						"jbum/ui/icons/" + buttonInfo[(bDex * 3) + 2]
								+ "Dark.gif");
				b.setRolloverIcon(new ImageIcon(url));
				b.addActionListener(ia);
				b.setBackground(useColor);
				b.setToolTipText(buttonInfo[(bDex * 3) + 1]);
				b.setMargin(new Insets(0, 0, 0, 0));
				b.setBorderPainted(false);
				b.setBackground(useColor);
				tools.add(b);
			}

			littleP.add(tools, BorderLayout.SOUTH);

			int x = Main.getPicsPerRow();

			if (rowP.getComponentCount() == x) {
				rowP = new JPanel();
				box.add(rowP);
				rowP.setBackground(getBackground());
			}

			if (++lastColor == rotateColor.length) {
				lastColor = 0;
			}
		}

		JPanel prologP = new JPanel();
		prologP.add(prologTA);
		box.add(prologP);
		prologP.setBackground(getBackground());
		prologTA.setBackground(getBackground().darker());
	}
}
