package jbum.ui

import java.awt.Component
import java.awt.Dimension
import se.datadosen.component.RiverLayout
import javax.swing.*
import jbum.core.DPage
import java.awt.event.ActionListener

class MyCellRenderer extends JLabel implements ListCellRenderer {

  final static URL url = MyCellRenderer.class.getClassLoader().getResource("jbum/ui/icons/back.gif")
  final static ImageIcon longIcon = new ImageIcon(url);

  // This is the only method defined by ListCellRenderer.
  // We just reconfigure the JLabel each time we're called.

  public Component getListCellRendererComponent(
  JList list,
  Object value,            // value to display
  int index,               // cell index
  boolean isSelected,      // is the cell selected
  boolean cellHasFocus) {
    setText(new File(value).name);
    //setIcon(longIcon);
    setIcon(new ImageIcon(value))
    setVerticalTextPosition(JLabel.CENTER) 
    if (isSelected) {
      setBackground(list.getSelectionBackground());
      setForeground(list.getSelectionForeground());
    }
    else {
      setBackground(list.getBackground());
      setForeground(list.getForeground());
    }
    setEnabled(list.isEnabled());
    setFont(list.getFont());
    setOpaque(true);
    return this;
  }
}


class Blog {

  void openDialog(DPage dpage) {
    Main.myself.saveAction.actionPerformed(null)
    BlogInfo blogInfo = dpage.blogInfo

    if(!blogInfo.title)
        blogInfo.title = dpage.title
    if(!blogInfo.text)
        blogInfo.text = dpage.intro
    
    blogInfo.init()

    JFrame jf = new JFrame();

    JPanel jp = new JPanel()
    jp.setLayout(new RiverLayout());
    jf.getContentPane().add(jp)

    def blogId =  new JTextField(blogInfo.id);
    blogId.setEditable(false);

    jp.add("p right", new JLabel("Blog Entry Id"))
    jp.add("tab hfill", blogId )

    jp.add("p right", new JLabel("Blog Posting URL"));
    jp.add("tab hfill", new JTextField(blogInfo.postURL))

    jp.add("p right", new JLabel("Title"))
    jp.add("tab hfill", new JTextField(blogInfo.title))

    jp.add("p right", new JLabel("Image"))

    String[] data = new String[ dpage.vii.vec.size() ]
    dpage.vii.vec.eachWithIndex { jbum.core.ImageInfo ii, dex ->
        data[dex] = ii.getSmallFile(dpage.where).toString()
    }

    JList dataList = new JList(data);
    dataList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
    dataList.setVisibleRowCount(-1)
    dataList.setLayoutOrientation(dataList.HORIZONTAL_WRAP)
    dataList.setCellRenderer(new MyCellRenderer())
    JScrollPane listScroller = new JScrollPane(dataList);
    listScroller.setPreferredSize(new Dimension(500, 250));

    jp.add("tab hfill", listScroller)

    jp.add("p right", new JLabel("Blog Entry"))
    jp.add("tab hfill", new JTextArea(10, 10))

    JPanel okcancel = new JPanel();
    jp.add("p center", okcancel);
    JButton ok = new JButton("Publish");
    ok.addActionListener([actionPerformed: {
      jf.dispose()
      println "do blogging."

    }] as ActionListener)
    okcancel.add(ok);


    JButton cancel = new JButton("Cancel");
    okcancel.add(cancel);
    cancel.addActionListener([actionPerformed: { f.dispose(); }] as ActionListener)

//		f.setSize(f.getPreferredSize());
//		f.setSize(600, 400);
//		f.setVisible(true);
    jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    jf.setSize(800, 400)
    jf.setVisible(true)
  }

}