package jbum.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.lang.reflect.Method;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JColorChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.KeyStroke;

import jbum.core.ColorSet;
import jbum.core.DPage;
import jbum.core.ImageInfo;
import jbum.core.MailThread;
import jbum.core.Prefs;
import jbum.core.Version;
import jbum.layouts.ExportToTemplate;
import jbum.layouts.TemplateFactory;

public class Main {
    private static Main myself;

    CenterP centerP;

    JLabel memStatusL = new JLabel();

    JLabel statusL = new JLabel("");

    JRadioButtonMenuItem rbTwo = new JRadioButtonMenuItem("2");

    JRadioButtonMenuItem rbThree = new JRadioButtonMenuItem("3");

    JRadioButtonMenuItem rbFour = new JRadioButtonMenuItem("4");

    JRadioButtonMenuItem[] templateRbs;

    final JFrame frame = new JFrame();

    // for testing
    public Main() {
    }

    public ActionListener saveAction = new ActionListener() {
        @SuppressWarnings("deprecation")
        public void actionPerformed(ActionEvent ae) {
            try {
                DPage dpage = centerP.getCurrentPage();
                dpage.save();
                ExportToTemplate e2p = new ExportToTemplate();
                e2p.export(dpage);
            } catch (Throwable t) {
                t.printStackTrace();
                error(t, "saving page");
            }

        }

    };

    public Main(File file) {
        myself = this;
        frame.setTitle("jbum - " + file);
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });

        JMenuBar menuBar = new JMenuBar();
        frame.setJMenuBar(menuBar);

        // Build the first menu.
        JMenu menu = new JMenu("File");
        menu.setMnemonic(KeyEvent.VK_F);
        menuBar.add(menu);

        // a group of JMenuItems
        JMenuItem menuItem = null;

        /*
           * menuItem = new JMenuItem("Open directory...", KeyEvent.VK_O);
           * menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O,
           * ActionEvent.ALT_MASK)); menu.add(menuItem);
           * menuItem.addActionListener(new ActionListener() {...
           */
        menuItem = new JMenuItem("Save", KeyEvent.VK_S);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,
                ActionEvent.ALT_MASK));
        menu.add(menuItem);
        menuItem.addActionListener(saveAction);

        menuItem = new JMenuItem("Save and view in browser", KeyEvent.VK_B);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_B,
                ActionEvent.ALT_MASK));
        menu.add(menuItem);
        menuItem.addActionListener(new ActionListener() {
            @SuppressWarnings("deprecation")
            public void actionPerformed(ActionEvent ae) {
                try {
                    DPage dpage = centerP.getCurrentPage();
                    dpage.save();
                    ExportToTemplate e2p = new ExportToTemplate();
                    e2p.export(dpage);

                    try {
                        if (Prefs.getWebBrowser().trim().length() != 0)
                            Runtime.getRuntime().exec(
                                    Prefs.getWebBrowser()
                                            + " file:///"
                                            + fixSpaces(dpage.getWhere()
                                            .toString())
                                            + "/index.html");
                    } catch (Throwable t) {
                        t.printStackTrace();
                        error(t,
                                "Unable to preview in browser.\nCheck your browser perference.\n");
                    }
                } catch (Throwable t) {
                    t.printStackTrace();
                    error(t, "saving page");
                }

            }

        });

        menuItem = new JMenuItem("Spellcheck all", KeyEvent.VK_C);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C,
                ActionEvent.ALT_MASK));
        menu.add(menuItem);
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                centerP.spellcheck();
            }
        });

        menuItem = new JMenuItem("Rename/Move", KeyEvent.VK_R);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R,
                ActionEvent.ALT_MASK));
        menu.add(menuItem);
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                new RenameMoveChooser();
            }
        });

        menu.add(menuItem = new JMenuItem("Order images by exif date..."));
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                //centerP.orderByExifDate();
                OrderDialog orderDialog = new OrderDialog(frame, centerP.vecii, centerP);
                orderDialog.show();

            }
        });

        menu.add(menuItem = new JMenuItem("Preferences..."));
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                new jbum.ui.Prefs();
            }
        });

        menu.add(menuItem = new JMenuItem("Export PDF"));
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                try {
                    new jbum.pdf.ExportPDF(centerP.getCurrentPage());
                } catch (Exception e) {
                    error("Generating PDF", e.getMessage());
                }
            }
        });

        menu.add(menuItem = new JMenuItem("Export PDF 2"));
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                try {
                    jbum.pdf.ExportPDF.make2(centerP.getCurrentPage());
                } catch (Exception e) {
                    error("Generating PDF", e.getMessage());
                }
            }
        });

        menu.add(menuItem = new JMenuItem(
                "Publish"));
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                new Thread(new Runnable() {
                    public void run() {
                        try {
                            Object o = Class.forName("jbum.ui.Publish").newInstance();
                            Method m = o.getClass().getMethod("openDialog");
                            m.invoke(o, []);
                        } catch (Exception e) {
                            error(e, "trying to publish");
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });

        menu.add(menuItem = new JMenuItem(
                "Blog"));
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                new Thread(new Runnable() {
                    public void run() {
                        try {
                            Blog blog = new Blog();
                            blog.openDialog(centerP.getCurrentPage());
                        } catch (Exception e) {
                            error(e, "trying to blog");
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });

        if (false/* one off for my buddy Tom */)

        {
            menu
                    .add(menuItem = new JMenuItem(
                    "Load comments from comments.csv"));
            menuItem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent ae) {
                    try {
                        BufferedReader br = new BufferedReader(new FileReader(
                                new File(getCurrentDir(), "comments.csv")));
                        String line = null;
                        while ((line = br.readLine()) != null) {
                            String filename = line.split(",")[0];
                            String comment = line.split(",")[1];

                            boolean match = false;
                            for (int i = 0; i < centerP.vecii.size(); i++) {
                                ImageInfo ii = centerP.vecii.get(i);
                                if (filename.equals(ii.getName())) {
                                    ii.commentTA.setText(comment);
                                    match = true;
                                }
                            }
                            if (!match)
                                if (!comment.endsWith(".avi"))
                                    System.out.println("No match for: "
                                            + filename + " comment:" + comment);
                        }
                    } catch (Exception e) {
                        error(e, "trying to process comments.csv");
                    }
                }
            });
        }

        menuItem = new

                JMenuItem("Exit");

        menu.add(menuItem);
        menuItem.addActionListener(new

                ActionListener() {
                    public void actionPerformed(ActionEvent ae) {
                        System.exit(0);
                    }
                }

        );

        // / -- Deleted
        DeletionManager deletionManager = new DeletionManager(menuBar);

        // Layout
        menuBar.add(menu = new

                JMenu("Layout")

        );

        ButtonGroup group = new ButtonGroup();

        ActionListener layoutChange = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (Main.getTemplate().equals(TemplateFactory.WOODEN_FLOW)) {
                    centerP.setColor("Background", Color.decode("#c97726"));
                    centerP.setColor("Panel Even", Color.decode("#A35200"));
                }
                if (Main.getTemplate().equals(TemplateFactory.POLAROIDS_FLOW)) {
                    centerP.setColor("Panel Even", Color.WHITE);
                }
                if (Main.getTemplate().equals(TemplateFactory.POLAROIDS)) {
                    centerP.setColor("Panel Even", Color.WHITE);
                }
                if (Main.getTemplate().equals(TemplateFactory.CHAMELEON_FLOW)) {
                    centerP.setColor("Background", Color.decode("#e0e0e0"));
                    centerP.setColor("Panel Even", Color.WHITE);
                    centerP.setColor("Panel Odd", Color.WHITE);
                }
                centerP.rebuildComponents();
            }
        };

        rbTwo = new

                JRadioButtonMenuItem("2 images per row");

        rbTwo.setSelected(true);
        group.add(rbTwo);
        menu.add(rbTwo);

        rbThree = new

                JRadioButtonMenuItem("3 images per row");

        rbThree.setSelected(true);
        group.add(rbThree);
        menu.add(rbThree);

        rbFour = new

                JRadioButtonMenuItem("4 images per row");

        rbFour.setSelected(true);
        group.add(rbFour);
        menu.add(rbFour);

        rbTwo.addActionListener(layoutChange);
        rbThree.addActionListener(layoutChange);
        rbFour.addActionListener(layoutChange);

        // Templates....
        group = new

                ButtonGroup();

        menu.addSeparator();

        String[] tnames = TemplateFactory.getNames();
        templateRbs = new JRadioButtonMenuItem[tnames.length];

        for (
                int i = 0;
                i < tnames.length; i++)

        {
            templateRbs[i] = new JRadioButtonMenuItem(tnames[i]);
            templateRbs[i].addActionListener(layoutChange);
            group.add(templateRbs[i]);
            menu.add(templateRbs[i]);
        }

        // Color
        menuBar.add(menu = new

                JMenu("Color")

        );

        ActionListener ae = new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                Color c = centerP.getColor(ae.getActionCommand());
                Color newColor = JColorChooser.showDialog(frame,
                        "Choose Background Color", c);

                if (newColor != null) {
                    centerP.setColor(ae.getActionCommand(), newColor);
                    centerP.rebuildComponents();
                }
            }
        };

        menuItem = new

                JMenuItem("Background");

        menuItem.addActionListener(ae);
        menu.add(menuItem);
        menuItem = new

                JMenuItem("Text");

        menuItem.addActionListener(ae);
        menu.add(menuItem);
        menuItem = new

                JMenuItem("Panel Odd");

        menuItem.addActionListener(ae);
        menu.add(menuItem);
        menuItem = new

                JMenuItem("Panel Even");

        menuItem.addActionListener(ae);
        menu.add(menuItem);

        menu.addSeparator();
        ae = new ActionListener() {
            public void actionPerformed(ActionEvent axe) {
                centerP.setColorSet(axe.getActionCommand());
                centerP.rebuildComponents();
            }
        }

        ;

        for (
                int i = 0;
                i < ColorSet.getNames().length; i++)

        {
            menuItem = new JMenuItem(ColorSet.getNames()[i]);
            menuItem.addActionListener(ae);
            menu.add(menuItem);
        }

        menu.addSeparator();
        menuItem = new

                JMenuItem("Show current colors");

        menu.add(menuItem);
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent axey) {
                JOptionPane.showMessageDialog(frame, "Background: "
                        + prettyColor(centerP.getColor("Background"))
                        + "\nText: " + prettyColor(centerP.getColor("Text"))
                        + "\nPanel Odd: "
                        + prettyColor(centerP.getColor("Panel Odd"))
                        + "\nPanel Even: "
                        + prettyColor(centerP.getColor("Panel Even")),
                        "Active colors", JOptionPane.INFORMATION_MESSAGE);
            }
        }

        );

        menuBar.add(menuItem = new

                JMenuItem("Feedback")

        );
        menuItem.addActionListener(new

                ActionListener() {
                    public void actionPerformed(ActionEvent axe) {
                        new Feedback(frame);
                    }
                }

        );

        // / -- Help
        final ImageIcon icon = new ImageIcon(Main.class.getClassLoader().getResource("author.jpg"));

        // makes help on the right
        menuBar.add(Box.createHorizontalGlue());
        menuBar.add(menu = new

                JMenu("Help")

        );
        menu.add(menuItem = new

                JMenuItem("About Groovy")

        );
        menuItem.addActionListener(new

                ActionListener() {
                    public void actionPerformed(ActionEvent axe) {

                        try {
                            Object o = Class.forName("jbum.ui.GroovyVersion").newInstance();
                            String about = o.getClass().getField("about").get(o).toString();

                            JOptionPane.showMessageDialog(frame, about, "About Groovy",
                                    JOptionPane.INFORMATION_MESSAGE, icon);
                        } catch (Throwable e1) {
                            new MailThread("About Groovy", e1);
                            e1.printStackTrace();
                        }
                    }
                }

        );
        menu.add(menuItem = new

                JMenuItem("Groovy Console")

        );
        menuItem.addActionListener(new

                ActionListener() {
                    public void actionPerformed(ActionEvent axe) {
                        try {
                            Object o = Class.forName("jbum.ui.GroovyVersion").newInstance();
                            o.getClass().getMethod("startConsole").invoke(o,
                                    (Object[]) null);
                        } catch (Throwable e1) {
                            new MailThread("startConsole", e1);
                            e1.printStackTrace();
                        }
                    }
                }

        );
        menu.add(menuItem = new

                JMenuItem("About")

        );
        menuItem.addActionListener(new

                ActionListener() {
                    public void actionPerformed(ActionEvent axe) {
                        JOptionPane.showMessageDialog(frame,
                                "Written by Robert Herrmann,\nbob@jadn.com.\n"
                                        + "http://jadn.com\n\n" + Version.VERSION,
                                "About jbum", JOptionPane.INFORMATION_MESSAGE, icon);
                    }
                }

        );

        try

        {
            Object groovyUI = Class.forName("jbum.ui.GroovyUI").newInstance();
            Method m = groovyUI.getClass().getMethod("init",
                    [javax.swing.JFrame.class] as Class[])
            m.invoke(groovyUI, [frame] as Object[]);
        }

        catch (
                Throwable e1
                )

        {
            new MailThread("Creating GroovyUI", e1);
            e1.printStackTrace();
        }

        frame.setSize(1050, 800);

        // frame.setSize(500,500);
        frame.setLocation(30, 100);

        // frame.pack();
        frame.setVisible(true);

        /*
        * Color newColor = JColorChooser.showDialog( ColorChooserDemo2.this,
        * "Choose Background Color", banner.getBackground());
        */
        centerP = new

                CenterP(deletionManager);

        // frame.getContentPane().add(centerP, BorderLayout.CENTER);
        frame.getContentPane().

                add(centerP, BorderLayout.CENTER);

        b:
        {
            JPanel statusP = new JPanel();
            statusP.setLayout(new BorderLayout());
            statusP.setBorder(BorderFactory.createLoweredBevelBorder());
            statusP.add(statusL, BorderLayout.CENTER);
            statusP.add(memStatusL, BorderLayout.EAST);
            frame.getContentPane().add(statusP, BorderLayout.SOUTH);
        }

        new Thread(new Runnable() {
            public void run() {
                while (true) {
                    String msg = "";
                    if (myself != null && myself.centerP != null
                            && myself.centerP.vecii != null)
                        msg += "images=" + Main.myself.centerP.vecii.size();
                    msg += ' mem=' +((long)(Runtime.getRuntime().totalMemory() / 1_000_000)) + "MB";
                    memStatusL.setText(msg);

                    try {
                        Thread.sleep(2000);
                    } catch (Exception e) {
                    }
                }
            }
        }

        ).

                start();

        centerP.setDir(file);
        Prefs.justLoaded(file);
        centerP.scanDir();

        frame.setVisible(true);
    }

    static int getPicsPerRow() {
        int picsPerRow = 2;

        if (myself.rbThree.isSelected()) {
            picsPerRow = 3;
        }

        if (myself.rbFour.isSelected()) {
            picsPerRow = 4;
        }

        return picsPerRow;
    }

    static void setPicsPerRow(int x) {
        myself.rbTwo.setSelected(x == 2);
        myself.rbThree.setSelected(x == 3);
        myself.rbFour.setSelected(x == 4);
    }

    public static void main(String[] args) {
        // What directory do we start in?
        final FileChooser chooser = new FileChooser(
                "Choose a directory with images to work with.");
        chooser.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                File file = chooser.getSelectedFile();

                if (file == null) {
                    System.exit(0);
                }

                if (!file.isDirectory()) {
                    file = file.getParentFile();
                }

                new Main(file);
            }
        });
    }

    public static void error(Throwable e, String whywhere) {
        new MailThread(whywhere, e);

        JOptionPane.showMessageDialog(null, e.getMessage(), whywhere,
                JOptionPane.ERROR_MESSAGE);

        // Need dialog to ask user permission, and show progres... and allow
        // abort

        // ByteArrayOutputStream baos = new ByteArrayOutputStream();
        // e.printStackTrace(new PrintStream(baos));
        // String msg = "\nException trace\n";
        // msg += new String(baos.toByteArray());
        // try {
        // sendEmail( msg );
        // } catch (MessagingException e1) {
        // // TODO Auto-generated catch block
        // e1.printStackTrace();
        // }
    }

    public static void error(String title, String msg) {
        new MailThread(title + " " + msg);

        JOptionPane.showMessageDialog(null, msg, title,
                JOptionPane.ERROR_MESSAGE);
    }

    public static void warning(String title, String msg) {
        JOptionPane.showMessageDialog(null, msg, title,
                JOptionPane.WARNING_MESSAGE);
    }

    public static void info(String title, String msg) {
        JOptionPane.showMessageDialog(null, msg, title,
                JOptionPane.INFORMATION_MESSAGE);
    }

    public static void save() {
        myself.centerP.getCurrentPage().save();
    }

    public static void status(String status) {
        myself.statusL.setText(status);
    }

    static String getTemplate() {
        if (myself.templateRbs != null) {
            for (int i = 0; i < myself.templateRbs.length; i++) {
                if (myself.templateRbs[i].isSelected()) {
                    return myself.templateRbs[i].getText();
                }
            }
        }

        return TemplateFactory.STANDARD;
    }

    static void setTemplate(String name) {
        for (int i = 0; i < myself.templateRbs.length; i++) {
            myself.templateRbs[i].setSelected(myself.templateRbs[i].getText()
                    .equals(name));
        }
    }

    public static File getCurrentDir() {
        return myself.centerP.currentDir;
    }

    public static String prettyColor(Color c) {
        return "#" + get2Hex(c.getRed()) + get2Hex(c.getGreen())
        +get2Hex(c.getBlue());
    }

    static String get2Hex(int x) {
        String v = Integer.toHexString(x);

        if (v.length() == 1) {
            v = "0" + v;
        }

        return v;
    }

    public static void movedTo(File dst) {
        myself.centerP.currentDir = dst;
        myself.frame.setTitle("jbum - " + dst);
    }

    private String fixSpaces(String string) {
        return string.replaceAll(" ", "%20");
    }
}
