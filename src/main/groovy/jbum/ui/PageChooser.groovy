package jbum.ui

import jbum.core.Prefs
import jbum.core.Version

import javax.swing.*
import javax.swing.border.EmptyBorder
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener
import javax.swing.table.AbstractTableModel
import java.awt.*
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.text.SimpleDateFormat

/**
 * @author bob
 */
public class PageChooser {
    static JButton openButton = new JButton("Open" /* "Modify" */);
    static SimpleDateFormat sd = new SimpleDateFormat("MM/dd/yyyy");

    public static void main(String[] args) {

        System.setSecurityManager(null);

        if (args.length != 0) {
            new Main(new File(args[0]));
            return;
        }

        final JFrame frame = new JFrame("Jbum - Page Chooser");
        final PagesDataModel pagesDataModel = new PagesDataModel();
        pagesDataModel.validate();

        final JTable table = new JTable(pagesDataModel);

        Color yellow = new Color(255, 255, 204);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        /*
         * frame.addWindowListener(new WindowAdapter() { public void
         * windowClosing(WindowEvent e) { //myFile = null;
         * //myActionListener.actionPerformed(null); frame.dispose(); } });
         */
        Container contentPane = frame.getContentPane();
        frame.setBackground(yellow);
        contentPane.setBackground(yellow);

        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BorderLayout());
        titlePanel.setBackground(yellow);

        ImageIcon icon = new ImageIcon(Main.class.getClassLoader().getResource("jbum.gif"));

        titlePanel.add(new JLabel(Version.VERSION, icon, SwingConstants.RIGHT), BorderLayout.WEST);

        contentPane.add(titlePanel, BorderLayout.NORTH);

        Box stack = Box.createVerticalBox();
        stack.setBorder(new EmptyBorder(10, 10, 10, 10));
        contentPane.add(stack, BorderLayout.CENTER);

        boolean welcome = false;

        if (pagesDataModel.getRowCount() == 0) {
            welcome = true;
        }

        boxstuff:
        {
            Box y = Box.createVerticalBox();
            y.setBorder(BorderFactory.createTitledBorder(
                    "Create new page from folder of images"));

            stack.add(y);

            final JTextField jtf = new JTextField(20);

            innerstuff:
            {
                Box x = Box.createHorizontalBox();
                x.setBorder(new EmptyBorder(10, 10, 10, 10));
                y.add(x);
                x.add(new JLabel("Folder: "));
                x.add(jtf);

                // this feels unclean, but it works
                Dimension d = jtf.getPreferredSize();
                d.width = 1000;
                d.height += 5;
                jtf.setMaximumSize(d);

                JButton browseButton = new JButton("Browse...");
                browseButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        final FileChooser chooser = new FileChooser(
                                "Choose a directory with images to create a page with.");
                        chooser.addActionListener(new ActionListener() {
                            public void actionPerformed(ActionEvent axe) {
                                File file = chooser.getSelectedFile();

                                if (file == null) {
                                    return;
                                }

                                if (!file.isDirectory()) {
                                    file = file.getParentFile();
                                }

                                jtf.setText(file.toString());
                            }
                        });
                    }
                });
                x.add(browseButton);
            }


            uipanel:
            {
                JPanel p = new JPanel();
                p.setBackground(yellow);
                p.setBorder(new EmptyBorder(10, 10, 10, 10));
                p.setLayout(new BorderLayout());

                JPanel buttonHolder = new JPanel();
                buttonHolder.setBackground(yellow);
                final JButton createButton = new JButton("Create/Open");
                createButton.setEnabled(false);
                buttonHolder.add(createButton);

                jtf.getDocument().addDocumentListener(new DocumentListener() {

                    public void changedUpdate(DocumentEvent e) {
                        check();
                    }

                    private void check() {
                        try {
                            File file = new File(jtf.getText());
                            createButton.setEnabled(file.isDirectory());
                        } catch (Throwable t) {
                        }
                    }

                    public void insertUpdate(DocumentEvent e) {
                        check();
                    }

                    public void removeUpdate(DocumentEvent e) {
                        check();
                    }
                });

                p.add(buttonHolder, BorderLayout.EAST);
                createButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        File f = new File(jtf.getText());

                        if (!f.exists()) {
                            Main.error("Choosing a folder to work with...",
                                    "Can't create/open a folder that doesn't exist");

                            return;
                        }

                        new Main(f);
                        frame.dispose();
                    }
                });
                y.add(p);
            }
        }

        if (!welcome) {
            block:
            {
                JPanel p = new JPanel();
                p.setBackground(yellow);
                p.setLayout(new BorderLayout());
                stack.add(p);
                JLabel or = new JLabel("  Or");
                p.add(or, BorderLayout.WEST);
                or.setFont(new Font("Sans", Font.BOLD, 25));
            }
            Box y = Box.createVerticalBox();
            stack.add(y);
            y.setBorder(BorderFactory.createTitledBorder(
                    "Modify a previously created page"));

            block:
            {
                Box x = Box.createVerticalBox();
                JScrollPane scrollpane = new JScrollPane(table);
                scrollpane.setBackground(yellow);

                x.add(scrollpane);
                y.add(x);
            }

            block:
            {
                JPanel p = new JPanel();
                p.setBackground(yellow);
                p.setBorder(new EmptyBorder(10, 10, 10, 10));
                p.setLayout(new BorderLayout());

                JPanel first = new JPanel();
                p.add(first, BorderLayout.WEST);
                first.setBackground(yellow);

                JButton findButton = new JButton("Find...");
                first.add(findButton);

                findButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        final FileChooser chooser = new FileChooser(
                                "Where to start finding from?");
                        chooser.addActionListener(new ActionListener() {
                            public void actionPerformed(ActionEvent ae) {
                                File file = chooser.getSelectedFile();

                                if (file == null) {
                                    return;
                                }

                                if (!file.isDirectory()) {
                                    file = file.getParentFile();
                                }

                                doFind(file);
                                pagesDataModel.update();

                                if (pagesDataModel.getRowCount() == 0) {
                                    openButton.setEnabled(false);
                                } else {
                                    openButton.setEnabled(true);
                                }
                            }
                        });
                    }
                });

                JButton clearButton = new JButton("Clear");
                first.add(clearButton);

                clearButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        pagesDataModel.clear();
                        openButton.setEnabled(false);
                        Prefs.setLastModified(new ArrayList<String[]>());
                    }
                });

                JPanel last = new JPanel();
                last.setBackground(yellow);
                p.add(last, BorderLayout.EAST);
                last.add(openButton);
                openButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        int sel = table.getSelectedRow();

                        if (sel == -1) {
                            return;
                        }

                        String value = (String) table.getModel().getValueAt(sel,
                                2);
                        new Main(new File(value));
                        frame.dispose();
                    }
                });

                if (pagesDataModel.getRowCount() == 0) {
                    openButton.setEnabled(false);
                }

                y.add(p);
            }
        }

        Prefs.setFramePlace(frame, welcome);
        frame.setVisible(true);

        Update.check(frame);
    }

    private static void doFind(File root) {
        ArrayList<String[]> al = new ArrayList<String[]>();

        // find all jbum.ser files
        File[] jbf = getAll(root);

        for (int i = 0; i < jbf.length; i++) {
            String dateMod = sd.format(new Date(jbf[i].lastModified()));
            String title = jbf[i].getParentFile().getName();
            String path = jbf[i].getParent().toString();
            al.add([dateMod, title, path]);
        }

        //Collections.sort(al);
        //Collections.reverse(al);
        Prefs.setLastModified(al);
    }

    static File[] getAll(File dir) {
        //System.out.println("Checking: "+dir);
        ArrayList<File> al = new ArrayList<File>();
        File[] list = dir.listFiles();

        for (int i = 0; i < list.length; i++) {
            if (list[i].isDirectory()) {
                File[] sub = getAll(list[i]);

                for (int j = 0; j < sub.length; j++) {
                    al.add(sub[j]);
                }
            }

            if (list[i].getName().equals("jbum.ser")) {
                al.add(list[i]);

                //status("Found "+list[i]);
            }
        }

        return al.toArray(new File[0]);
    }

    @SuppressWarnings("serial")
    private static final class PagesDataModel extends AbstractTableModel {

        ArrayList data;

        PagesDataModel() {
            data = Prefs.getLastModified();
        }

        public void update() {
            data = Prefs.getLastModified();
            fireTableDataChanged();
        }

        public int getColumnCount() {
            return 3;
        }

        public int getRowCount() {
            return data.size();
        }

        public Object getValueAt(int row, int col) {
            return ((String[]) data.get(row))[col];
        }

        public String getColumnName(int col) {
            if (col == 0) {
                return "Last Modified";
            }

            if (col == 1) {
                return "Title";
            }

            return "Folder Path";
        }

        public void validate() {
            for (int i = data.size() - 1; i >= 0; i--) {
                if (!new File(getValueAt(i, 2).toString()).exists()) {
                    data.remove(i);
                }
            }
        }

        public void clear() {
            data.clear();
            fireTableDataChanged();
        }
    }
}
