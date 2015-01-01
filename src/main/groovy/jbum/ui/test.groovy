//package jbum.ui;
//
//import java.awt.BorderLayout;
//import java.awt.Color;
//import java.awt.event.WindowAdapter;
//import java.awt.event.WindowEvent;
//import java.io.File;
//
//import javax.swing.BorderFactory;
//import javax.swing.Box;
//import javax.swing.BoxLayout;
//import javax.swing.JFrame;
//import javax.swing.JLabel;
//import javax.swing.JOptionPane;
//import javax.swing.JPanel;
//import javax.swing.JScrollPane;
//import javax.swing.JTextArea;
//import javax.swing.JTextField;
//
//
//class CP extends JScrollPane {
//
//    JTextField titleTF = new JTextField("Title", 60);
//
//    JTextArea introTA = new JTextArea("intro", 5, 60);
//
//    JTextArea prologTA = new JTextArea("", 5, 60);
//
//   
//
//    DeletionManager deletionManager;
//
//    File currentDir;
//
//    File jbumSer;
//
//    private String iconPath;
//
//    String htmlPath;
//
//    JPanel titleP = new JPanel();
//
//    JPanel introP = new JPanel();
//
//    //JPanel prologP = new JPanel();
//
//    Color light = new Color(0xD0D0FF);
//
//    Color dark = new Color(0xE0E0E0);
//
//    Color[] rotateColor = { light, dark, dark, light };
//
//    int lastColor = 0;
//
//    CP() {
//        super(new Box(BoxLayout.Y_AXIS));
//  
//        
//    }
//
//   
//
//    
//
//
//    void scanDir() {
//       
//        
//        titleTF.setText("meta.getTitle()");
//        introTA.setText("meta.getIntro()");
//
//        prologTA.setText("meta.getProlog()");
//
//
//        doComponents();
//        //rebuildComponents();
//        
//        
//    }
//
//   
//    public void rebuildComponents() {
//        Box box = (Box) getViewport().getView();
//
//        box.removeAll();
//
//        
//        setVisible(false);
//        doComponents();
//        setVisible(true);
//    }
//
//
//    JPanel prologP = new JPanel();
//
//    void doComponents() {
//        Box box = (Box) getViewport().getView();
//
//        box.removeAll();
//
//        for (int i = 0; i < 150; i++)
//            box.add(new JLabel("Hi " + i));
//        
//        JTextArea p = new JTextArea("\ndistance", 5, 60);
//        //p.setText(prologTA.getText());
//        p.setText("\nCreated with <a href=http://jbum.sf.net>jbum</a>.");
//        //p.setText("");
//        prologP.add(p);
//        box.add(prologP);
//        //prologP.setBackground(getBackground());
//        //prologTA.setBackground(getBackground().darker());
//    }
//    
//  
//}
//
//public class test {
//
//
//    public test() {
//        final JFrame frame = new JFrame();
//        frame.setTitle("jbum");
//        frame.addWindowListener(new WindowAdapter() {
//
//            public void windowClosing(WindowEvent e) {
//                System.exit(0);
//            }
//        });
//        frame.setSize(1050, 800);
//        //frame.setSize(500,500);
//        frame.setLocation(30, 100);
//        //frame.pack();
//        frame.setVisible(true);
//
//        /*
//         * Color newColor = JColorChooser.showDialog( ColorChooserDemo2.this,
//         * "Choose Background Color", banner.getBackground());
//         */
//
//        CP cp = new CP();
//        frame.getContentPane().add(cp, BorderLayout.CENTER);
//        {
//            JPanel statusP = new JPanel();
//            statusP.setLayout(new BorderLayout());
//            statusP.setBorder(BorderFactory.createLoweredBevelBorder());
//            statusP.add(new JLabel("statusL"), BorderLayout.CENTER);
//            statusP.add(new JLabel("memStatusL"), BorderLayout.EAST);
//            frame.getContentPane().add(statusP, BorderLayout.SOUTH);
//        }
//   
//    
//        
//       
//        cp.scanDir();
//
//        frame.setVisible(true);
//        
//
//    }
//
//    
//    public static void main(String[] args) {
//     
//                new test();
//      
//    }
//
//    public static void error(Exception e, String whywhere) {
//        JOptionPane.showMessageDialog(null, whywhere, e.getMessage(),
//                JOptionPane.ERROR_MESSAGE);
//    }
//
// 
//
//
//}