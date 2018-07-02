package jbum.ui

import com.drew.imaging.jpeg.JpegMetadataReader
import com.drew.metadata.Directory
import com.drew.metadata.Metadata
import com.drew.metadata.exif.ExifDirectory
import jbum.core.DPage
import jbum.core.Prefs
import jbum.core.Version

import javax.swing.*
import javax.swing.border.EmptyBorder
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener
import javax.swing.table.TableCellRenderer
import javax.swing.table.TableColumn
import javax.swing.table.TableModel
import javax.swing.table.TableRowSorter
import java.awt.*
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.text.SimpleDateFormat


class DoFind extends SwingWorker<String, Object> {
    def findComplete
    def backgroundWork

    DoFind(def findComplete, def backgroundWork) {
        this.findComplete = findComplete
        this.backgroundWork = backgroundWork
        findComplete(false)
    }

    @Override
    public String doInBackground() {
        backgroundWork()
        return "cow";
    }

    @Override
    protected void done() {
        try {
            get();
            findComplete(true)
        } catch (Exception ignore) {
            ignore.printStackTrace();
        }
    }
}

class PageChooser {
    static JButton openButton = new JButton("Open" /* "Modify" */);
    static SimpleDateFormat mmddyyyy = new SimpleDateFormat("MM/dd/yyyy")
    static SimpleDateFormat stockImageFormat = new SimpleDateFormat("yyyy:MM:dd kk:mm:ss")

    static void recomputeTableWidths(JTable table) {
        for (int column = 0; column < table.getColumnCount(); column++) {
            TableColumn tableColumn = table.getColumnModel().getColumn(column);
            int preferredWidth = Math.max(60, tableColumn.getMinWidth() + 20)
            int maxWidth = tableColumn.getMaxWidth();

            for (int row = 0; row < table.getRowCount(); row++) {
                TableCellRenderer cellRenderer = table.getCellRenderer(row, column);
                Component c = table.prepareRenderer(cellRenderer, row, column);
                int width = c.getPreferredSize().width + table.getIntercellSpacing().width+10;
                preferredWidth = Math.max(preferredWidth, width);

                //  We've exceeded the maximum width, no need to check other rows

                if (preferredWidth >= maxWidth) {
                    preferredWidth = maxWidth;
                    break;
                }
            }

            tableColumn.setPreferredWidth(preferredWidth);
        }
    }

    static void start() {
        final JFrame frame = new JFrame("Jbum - Page Chooser")
        final PageDataModel pageDataModel = new PageDataModel();
        pageDataModel.validate();

        final JTable table = new JTable(pageDataModel);
        table.setAutoCreateRowSorter(true);

        TableRowSorter<TableModel> sorter = new TableRowSorter<>(table.getModel());
        table.setRowSorter(sorter);
        def sortKeys = [] //new ArrayList<RowSorter.SortKey>();

        int columnIndexToSort = 0;
        sortKeys.add(new RowSorter.SortKey(columnIndexToSort, SortOrder.DESCENDING));

        sorter.setSortKeys(sortKeys);

        def dateCompare = new Comparator<String>() {
            @Override
            public int compare(String name1, String name2) {
                if (name1 == "Unknown") name1 = "1900/01/01"
                if (name2 == "Unknown") name2 = "1900/01/01"
                return mmddyyyy.parse(name1).compareTo(mmddyyyy.parse(name2));
            }
        }
        sorter.setComparator(0, dateCompare);
        sorter.setComparator(1, dateCompare);

        sorter.sort();

        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        recomputeTableWidths(table)

        Color yellow = new Color(255, 255, 204);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Container contentPane = frame.getContentPane();
        frame.setBackground(yellow);
        contentPane.setBackground(yellow);

        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BorderLayout());
        titlePanel.setBackground(yellow);
        JLabel titleLabel = new JLabel("Jbum " + Version.VERSION, SwingConstants.RIGHT);
        titleLabel.setFont(new Font("Sans", Font.BOLD, 25));
        titlePanel.add(titleLabel, BorderLayout.WEST);
        titlePanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        contentPane.add(titlePanel, BorderLayout.NORTH);

        Box stack = Box.createVerticalBox();
        stack.setBorder(new EmptyBorder(10, 10, 10, 10));
        contentPane.add(stack, BorderLayout.CENTER);

        boolean welcome = false;

        if (pageDataModel.getRowCount() == 0) {
            //welcome = true;
        }
        final JTextField jtf = new JTextField(20);
        JButton browseButton = new JButton("Browse...");
        final JButton createButton = new JButton("Create/Open");
        JButton findButton = new JButton("Find...");
        JButton clearButton = new JButton("Clear");
        def uiComponents = [jtf, browseButton, createButton, findButton, clearButton]

        def startingFind = { enabled ->
            uiComponents.each {
                it.setEnabled(enabled)
            }
            if (enabled) {
                pageDataModel.update();
                PageChooser.recomputeTableWidths(table)
                if (pageDataModel.getRowCount() == 0) {
                    PageChooser.openButton.setEnabled(false);
                } else {
                    PageChooser.openButton.setEnabled(true);
                }
            }

        }

        boxstuff:
        {
            Box y = Box.createVerticalBox();
            y.setBorder(BorderFactory.createTitledBorder(
                    "Create new page from folder of images"));

            stack.add(y);

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

                browseButton.addActionListener(new ActionListener() {
                    void actionPerformed(ActionEvent e) {
                        final FileChooser chooser = new FileChooser(
                                "Choose a directory with images to create a page with.");
                        chooser.addActionListener(new ActionListener() {
                            void actionPerformed(ActionEvent axe) {
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
                createButton.setEnabled(false);
                buttonHolder.add(createButton);

                jtf.getDocument().addDocumentListener(new DocumentListener() {

                    void changedUpdate(DocumentEvent e) {
                        check();
                    }

                    void insertUpdate(DocumentEvent e) {
                        check();
                    }

                    void removeUpdate(DocumentEvent e) {
                        check();
                    }

                    private void check() {
                        try {
                            File file = new File(jtf.getText());
                            createButton.setEnabled(file.isDirectory());
                        } catch (Throwable t) {
                        }
                    }

                });

                p.add(buttonHolder, BorderLayout.EAST);
                createButton.addActionListener(new ActionListener() {
                    void actionPerformed(ActionEvent e) {
                        File f = new File(jtf.getText());

                        if (!f.exists()) {
                            App.error("Choosing a folder to work with...",
                                    "Can't create/open a folder that doesn't exist");

                            return;
                        }

                        new App(f);
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

                first.add(findButton);

                findButton.addActionListener(new ActionListener() {
                    void actionPerformed(ActionEvent e) {
                        final FileChooser chooser = new FileChooser(
                                "Where to start finding from?");
                        chooser.addActionListener(new ActionListener() {
                            void actionPerformed(ActionEvent ae) {
                                File file = chooser.getSelectedFile();

                                if (file == null) {
                                    return;
                                }

                                if (!file.isDirectory()) {
                                    file = file.getParentFile();
                                }

                                (new DoFind(startingFind, { PageChooser.doFind(file) })).execute()

                            }
                        });
                    }
                });

                first.add(clearButton);

                clearButton.addActionListener(new ActionListener() {
                    void actionPerformed(ActionEvent e) {
                        pageDataModel.clear();
                        openButton.setEnabled(false);
                        Prefs.setLastModified(new ArrayList<String[]>());
                    }
                });

                JPanel last = new JPanel();
                last.setBackground(yellow);
                p.add(last, BorderLayout.EAST);
                last.add(openButton);
                openButton.addActionListener(new ActionListener() {
                    void actionPerformed(ActionEvent e) {
                        int sel = table.getSelectedRow();
                        if (sel == -1) {
                            return;
                        }
                        def x = table.convertRowIndexToModel(sel)
                        String value = (String) table.getModel().getValueAt(x, 4);
                        new App(new File(value));
                        frame.dispose();
                    }
                });

                if (pageDataModel.getRowCount() == 0) {
                    openButton.setEnabled(false);
                }

                y.add(p);
            }
        }

        Prefs.setFramePlace(frame, welcome);
        frame.setVisible(true);

//        Update.check(frame);
    }

    private static void doFind(File root) {
        ArrayList<String[]> al = new ArrayList<String[]>();

        // find all jbum.ser/jbum.json files
        File[] jbf = getAll(root);

        for (int i = 0; i < jbf.length; i++) {
            String dateMod = mmddyyyy.format(new Date(jbf[i].lastModified()));
            def dateSizeTitle = findOldestImageDateAndSizeAndTitle(jbf[i])
            def (oldestImageDate,size, title) = dateSizeTitle
            String path = jbf[i].getParent().toString();
            al.add([dateMod, oldestImageDate, size, title, path] as String[]);
        }

        //Collections.sort(al);
        //Collections.reverse(al);
        Prefs.setLastModified(al);
    }

    private static def findOldestImageDateAndSizeAndTitle(File file) {
        if (!file.exists()) {
            return ["", "", ""]
        }
        DPage dpage = new DPage(file, false)
        def size = dpage.getVii().size()

        String oldestImageDate = "Unknown"
        for (int i = size - 1; i > 0; i--) {
            def lastImageFile = dpage.getVii().get(size - 1).getOriginalFile(file.getParentFile())
            if (!lastImageFile.exists())
                continue
            Metadata metadata = JpegMetadataReader.readMetadata(lastImageFile);
            Directory dir = metadata.getDirectory(ExifDirectory.class);
            if (dir == null)
                continue
            String dateCreated = dir.getString(36867)
            if (dateCreated == null)
                continue
            Date d = stockImageFormat.parse(dateCreated);
            oldestImageDate = mmddyyyy.format(d)
        }

        return [oldestImageDate, size, dpage.title]
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

            // can have either jbum.ser both/or jbum.json
            // we prefer jbum.json as newer data
            if (list[i].getName().equals("jbum.ser")) {
                if (!new File(dir, "jbum.json").exists()) {
                    al.add(list[i]);
                }
                //status("Found "+list[i]);
            }
            if (list[i].getName().equals("jbum.json")) {
                al.add(list[i]);
            }
        }

        return al.toArray(new File[0]);
    }

}
