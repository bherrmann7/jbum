package jbum.ui;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import se.datadosen.component.RiverLayout;

public class Prefs extends jbum.core.Prefs {
	
	public static void main(String[] args) {
		new Prefs();
	}
	
	JFrame f;
	JTextField title;
	JTextField intro;
	JTextField firstImage;
	JTextArea  prolog;
	JTextField pdfviewer;
	JTextField imageEditorUI;
	JTextField webbrowser;
	
	public Prefs() {
		f = new JFrame();
		f.setTitle("Jbum Preferences");
		JPanel p = new JPanel();
		f.setContentPane(p);
		
		//p.setLayout(new GridLayout(0,2));
		p.setLayout(new RiverLayout());
		p.add("center", new JLabel("Jbum Preferences are used when creating a new page"));
		
		p.add("p right", new JLabel("Initial Title"));
		p.add("tab hfill", title=new JTextField(getInitialTitleText(),30));
		p.add("p right", new JLabel("Initial Introduction"));
		p.add("tab hfill",intro=new JTextField(getInitialIntroText(),30));
		p.add("p right",new JLabel("First Image"));
		p.add("tab hfill",firstImage=new JTextField(getInitialFirstImageText(),30));
		p.add("p right",new JLabel("Prolog"));
		prolog=new JTextArea(getInitialPrologText(),6,40);
		JScrollPane jsp = new JScrollPane(prolog, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		p.add("tab hfill vfill", jsp);
		p.add("p right", new JLabel("Pdf viewer"));
		p.add("tab hfill", pdfviewer=new JTextField(getPDFViewer(),30));
		p.add("p right", new JLabel("Image editor"));
		p.add("tab hfill", imageEditorUI=new JTextField(getImageEditor(),30));
		p.add("p right", new JLabel("web browser"));
		p.add("tab hfill", webbrowser=new JTextField(getWebBrowser(),30));
		p.add(new JPanel());
		
		JPanel okcancel = new JPanel();
		p.add("p center",okcancel);
		JButton ok = new JButton("OK");
		okcancel.add(ok);
		ok.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				setInitialTitleText(title.getText());
				setInitialIntroText(intro.getText());
				setInitialFirstImageText(firstImage.getText());
				setInitialPrologText(prolog.getText());
				setPDFViewer(pdfviewer.getText());
				setImageEditor(imageEditorUI.getText());
				setWebBrowser(webbrowser.getText());
				f.dispose();		
			}}
		);
		
		JButton cancel = new JButton("Cancel");
		okcancel.add(cancel);
		cancel.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				f.dispose();				
			}}
		);
		
		setFramePlace(f, 100);
//		f.setSize(f.getPreferredSize());
//		f.setSize(600, 400);
//		f.setVisible(true);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	
	}


}
