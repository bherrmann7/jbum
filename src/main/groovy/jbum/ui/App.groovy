package jbum.ui

import jbum.core.ColorSet
import jbum.core.DPage
import jbum.core.Prefs
import jbum.core.Version
import jbum.layouts.ExportToTemplate

import javax.swing.*
import java.awt.*
import java.awt.event.*

class App {
    private static App myself;
    CenterP centerP;
    JLabel memStatusL = new JLabel();
    JLabel statusL = new JLabel("");
    ButtonGroup imagesPerRow;
    final JFrame frame = new JFrame();

    App(){

    }

    ActionListener saveAction = new ActionListener() {
        @SuppressWarnings("deprecation")
        void actionPerformed(ActionEvent ae) {
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

    App(File file) {
        myself = this;
        frame.setTitle("jbum - " + file);
        frame.addWindowListener(new WindowAdapter() {
            void windowClosing(WindowEvent e) {
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
            void actionPerformed(ActionEvent ae) {
                try {
                    DPage dpage = centerP.getCurrentPage();
                    dpage.save();
                    ExportToTemplate e2p = new ExportToTemplate();
                    e2p.export(dpage);

                    try {
                        if (Prefs.getWebBrowser().trim().length() != 0)
                            Runtime.getRuntime().exec([
                                    Prefs.getWebBrowser(),
                                    "file:///"
                                            + fixSpaces(dpage.getWhere().toString())
                                            + "/index.html"] as String[])
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
            void actionPerformed(ActionEvent ae) {
                centerP.spellcheck();
            }
        });

        menuItem = new JMenuItem("Rename/Move", KeyEvent.VK_R);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R,
                ActionEvent.ALT_MASK));
        menu.add(menuItem);
        menuItem.addActionListener(new ActionListener() {
            void actionPerformed(ActionEvent ae) {
                new RenameMoveChooser();
            }
        });

        menu.add(menuItem = new JMenuItem("Order images by exif date..."));
        menuItem.addActionListener(new ActionListener() {
            void actionPerformed(ActionEvent ae) {
                //centerP.orderByExifDate();
                OrderDialog orderDialog = new OrderDialog(frame, centerP.vecii, centerP);
                orderDialog.show();

            }
        });

        menu.add(menuItem = new JMenuItem("Preferences..."));
        menuItem.addActionListener(new ActionListener() {
            void actionPerformed(ActionEvent ae) {
                new jbum.ui.Prefs();
            }
        });


        menu.add(menuItem = new JMenuItem("Rebuild Images"));
        menuItem.addActionListener(new ActionListener() {
            void actionPerformed(ActionEvent ae) {
                centerP.rebuildImages()
            }
        });

        menuItem = new JMenuItem("Exit");
        menu.add(menuItem);
        menuItem.addActionListener(new ActionListener() {
            void actionPerformed(ActionEvent ae) {
                System.exit(0);
            }
        }
        )

        // / -- Deleted
        DeletionManager deletionManager = new DeletionManager(menuBar);

        // Layout
        menuBar.add(menu = new JMenu("Layout"));

        ActionListener layoutChange = new ActionListener() {
            void actionPerformed(ActionEvent e) {
                centerP.rebuildComponents();
            }
        };

        picsPerRow:
        {

            imagesPerRow = new ButtonGroup();
            (2..6).forEach {
                def rb = new JRadioButtonMenuItem("$it images per row");
                imagesPerRow.add(rb)
                menu.add(rb)
                rb.addActionListener(layoutChange)
                // default to 4 per row
                if (it == 4) rb.setSelected(true)

            }

        }

        // Color
        menuBar.add(menu = new JMenu("Color"));

        ActionListener ae = new ActionListener() {
            void actionPerformed(ActionEvent ae) {
                Color c = centerP.getColor(ae.getActionCommand());
                Color newColor = JColorChooser.showDialog(frame,
                        "Choose Background Color", c);

                if (newColor != null) {
                    centerP.setColor(ae.getActionCommand(), newColor);
                    centerP.rebuildComponents();
                }
            }
        };

        menuItem = new JMenuItem("Background");

        menuItem.addActionListener(ae);
        menu.add(menuItem);
        menuItem = new JMenuItem("Text");

        menuItem.addActionListener(ae);
        menu.add(menuItem);
        menuItem = new JMenuItem("Panel Odd");

        menuItem.addActionListener(ae);
        menu.add(menuItem);
        menuItem = new JMenuItem("Panel Even");

        menuItem.addActionListener(ae);
        menu.add(menuItem);

        menu.addSeparator();
        ae = new ActionListener() {
            void actionPerformed(ActionEvent axe) {
                centerP.setColorSet(axe.getActionCommand());
                centerP.rebuildComponents();
            }
        }


        for (
                int i = 0;
                i < ColorSet.getNames().length; i++)

        {
            menuItem = new JMenuItem(ColorSet.getNames()[i]);
            menuItem.addActionListener(ae);
            menu.add(menuItem);
        }

        menu.addSeparator();
        menuItem = new JMenuItem("Show current colors");

        menu.add(menuItem);
        menuItem.addActionListener(new ActionListener() {
            void actionPerformed(ActionEvent axey) {
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

        // / -- Help
        final ImageIcon icon = new ImageIcon(App.class.getClassLoader().getResource("author.jpg"));

        // makes help on the right
        menuBar.add(Box.createHorizontalGlue())
        menuBar.add(menu = new JMenu("Help"))

        menu.add(menuItem = new JMenuItem("About"))
        menuItem.addActionListener(new

                ActionListener() {
                    void actionPerformed(ActionEvent axe) {
                        JOptionPane.showMessageDialog(frame,
                                "Written by Robert Herrmann,\nbob@jadn.com.\n"
                                        + "http://jadn.com\n\n" + Version.VERSION,
                                "About jbum", JOptionPane.INFORMATION_MESSAGE, icon);
                    }
                }

        );

        frame.setSize(1050, 800);

        // frame.setSize(500,500);
        frame.setLocation(30, 100);

        // frame.pack();
        frame.setVisible(true);

        centerP = new CenterP(deletionManager);

        // frame.getContentPane().add(centerP, BorderLayout.CENTER);
        frame.getContentPane().add(centerP, BorderLayout.CENTER)

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
            void run() {
                while (true) {
                    String msg = "";
                    if (myself != null && myself.centerP != null && myself.centerP.vecii != null)
                        msg += "images=" + App.myself.centerP.vecii.size();
                    msg += ' mem=' + ((long) (Runtime.getRuntime().totalMemory() / 1_000_000)) + "MB";
                    memStatusL.setText(msg);

                    try {
                        Thread.sleep(2000);
                    } catch (Exception e) {
                    }
                }
            }
        }

        ).start();

        centerP.setDir(file);
        Prefs.justLoaded(file);
        centerP.scanDir();

        frame.setVisible(true);
    }

    static int getPicsPerRow() {
        // bobh hack for dumping via command line
        if(myself.imagesPerRow == null)
            return 4;
        return Integer.parseInt(myself.imagesPerRow.elements.find { it.selected }.text[0])
    }

    static void setPicsPerRow(int x) {
        myself.imagesPerRow.elements.find { it.text.startsWith(x.toString()) }.setSelected(true)
    }

    static void error(Throwable e, String whywhere) {
        JOptionPane.showMessageDialog(null, e.getMessage(), whywhere,
                JOptionPane.ERROR_MESSAGE);
    }

    static void error(String title, String msg) {
        JOptionPane.showMessageDialog(null, msg, title,
                JOptionPane.ERROR_MESSAGE);
    }

    static void warning(String title, String msg) {
        JOptionPane.showMessageDialog(null, msg, title,
                JOptionPane.WARNING_MESSAGE);
    }

    static void info(String title, String msg) {
        JOptionPane.showMessageDialog(null, msg, title,
                JOptionPane.INFORMATION_MESSAGE);
    }

    static void save() {
        myself.centerP.getCurrentPage().save();
    }

    static void status(String status) {
        myself.statusL.setText(status);
    }

    static File getCurrentDir() {
        return myself.centerP.currentDir;
    }

    static String prettyColor(Color c) {
        return "#" + get2Hex(c.getRed()) + get2Hex(c.getGreen()) + get2Hex(c.getBlue());
    }

    static String get2Hex(int x) {
        String v = Integer.toHexString(x);

        if (v.length() == 1) {
            v = "0" + v;
        }

        return v;
    }

    static void movedTo(File dst) {
        myself.centerP.currentDir = dst;
        myself.frame.setTitle("jbum - " + dst);
    }

    private String fixSpaces(String string) {
        return string.replaceAll(" ", "%20");
    }
}
