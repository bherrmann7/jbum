package jbum.ui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import se.datadosen.component.RiverLayout;

public class Feedback extends JDialog {
	private static final long serialVersionUID = 1L;
	
	private JTextField from;
	private JTextField subject;
	private JTextArea message;

	public Feedback(final JFrame frame) {
		super(frame);
		setTitle("Jbum Feedback");
		
		setLayout(new RiverLayout());
		add("center", new JLabel("Sending feedback email..."));
		
		add("br left", new JLabel("From:"));
		add("tab", from=new JTextField("Enter your email",30));

		add("br left", new JLabel("Subject:"));
		add("tab ",subject=new JTextField("Jbum feedback",30));

		add("br left", new JLabel("Message:"));
		add("tab", message=new JTextArea(20,60));
		
		message.setWrapStyleWord(true);
		
		JPanel okcancel = new JPanel();
		add("p left",okcancel);
		JButton send = new JButton("Send");
		okcancel.add(send);
		send.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {				
				dispose();				
				try {
					new PostAsComment(frame, from.getText(), subject.getText(), message.getText());
				} catch (Exception e1) {
					Main.error(e1, "unable to send feedback email");
				}
			}}
		);
		JButton cancel = new JButton("Cancel");
		okcancel.add(cancel);
		cancel.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {				
				dispose();		
			}}
		);
		
		
		setLocation(frame.getLocation().x+100,frame.getLocation().y+100);
		Dimension d = getPreferredSize();
		d.height += 25;
		setSize(d);
		//setAlwaysOnTop(true);
		setVisible(true);
		message.requestFocus();
	}

}
