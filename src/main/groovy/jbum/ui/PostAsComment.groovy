package jbum.ui;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JFrame;

public class PostAsComment {

	@SuppressWarnings("deprecation")
	public PostAsComment(JFrame frame, String from, String subject,
			String message) {
		boolean found = false;
		try {
			Main.status("Feedback: connecting to send feedback...");
			URL url = new URL("http://jadn.com/comments/comment/save?"
					+ "name=" + URLEncoder.encode(from) + "&"
					+ "key=jbumFeedback&" + "email=me@jadn.com&" + "comment="
					+ URLEncoder.encode(message));
			URLConnection connection = url.openConnection();
			Main.status("Feedback: connected...");
			BufferedReader inx = new BufferedReader(new InputStreamReader(
					connection.getInputStream()));
			Main.status("Feedback: reading result....");
			String line;
			Pattern pattern = Pattern.compile("Comment \\d+ created");
			while ((line = inx.readLine()) != null) {
				if (found)
					continue;
				Matcher matcher = pattern.matcher(line);
				if (matcher.find())
					found = true;
			}
			inx.close();
		} catch (Throwable t) {
		}
		if (found) {
			Main.status("Feedback: successfully delivered.");
			Main.info("Feedback was delivered",
			"Your feedback was delivered.  THANKS!!!");
		} else {
			Main.status("Feedback: error sending feedback..");
			Main.error("Feedback not sent",
			"Sorry your feedback could not be delivered. \n Please send an email to jbum@jadn.com, THANKS!");
		}

	}

}
