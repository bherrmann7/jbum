package jbum.core;

import java.io.*;
import java.net.URLEncoder;
import java.net.URL;
import java.net.URLConnection;

public class MailThread extends Thread {
	String message;
	
	String from = "bob@jadn.com";
	String to = "bob@jadn.com";
	String subject = "jbum problem";
	String host = "jadn.com";


	public MailThread(String message) {
		this.message = message;
		start();
	}
	
	public MailThread(String host, String to, String from, String subject, String message) throws Exception {
		this.host = host;
		this.to=to;
		this.from=from;
		this.subject=subject;
		this.message=message;
		start();
	}
	

	public MailThread(String message, Throwable e) {
		
		StringWriter sw = new StringWriter();
		e.printStackTrace(new PrintWriter(sw));
		
		this.message = message + "\n\nStack\n"+sw.getBuffer();
		start();
	}

	private static void say(PrintWriter pw, String string) {
		pw.print(string);
		pw.flush();
	}

	private static void outit(String string) {
		System.out.println(string);

		if (string.startsWith("3") || string.startsWith("2")) {
			return;
		}

		System.err.println("Response looks bad.");
		throw new RuntimeException("Response looks bad: "+string);
	}

	public void run() {
		try {
			StringBuilder sb = new StringBuilder();
			sb.append("\nHostname = ");
			try {
				sb.append(java.net.Inet4Address.getLocalHost().getHostName());
			} catch (Throwable e) {
				sb.append( e.getMessage());
			}

			sb.append( "\n\n" );

			sb.append( message );
			
			sb.append( getSysProperties() );
			
//			send( host,  to,  from,  subject,  sb.toString());
		} catch (Throwable t) {
			t.printStackTrace();
			// all well...
		}

	}
	
	// jnlp throws security exception if you invoke getProperties(), so we just
	// ask for the stock ones. here.
	
	String[] wellKnownProperties = [
			"java.version",
			"java.vendor",
			"java.vendor.url",
			"java.home",
			"java.vm.specification.version",
			"java.vm.specification.vendor",
			"java.vm.specification.name",
			"java.vm.version",
			"java.vm.vendor",
			"java.vm.name",
			"java.specification.version",
			"java.specification.vendor",
			"java.specification.name",
			"java.class.version",
			"java.class.path",
			"java.library.path",
			"java.io.tmpdir",
			"java.compiler",
			"java.ext.dirs",
			"os.name",
			"os.arch",
			"os.version",
			"file.separator",
			"path.separator",
			"line.separator",
			"user.name",
			"user.home",
			"user.dir"
	];
	
	private String getSysProperties() {
		StringBuilder sb = new StringBuilder();
		for (int i=0;i<wellKnownProperties.length;i++) {
			sb.append("\n");
			sb.append(wellKnownProperties[i]);
			sb.append("=");
			sb.append(System.getProperty(wellKnownProperties[i]));
		}
		
		sb.append("\n");
		
		return sb.toString();
	}

    private static void send(String host, String to, String from, String subject, String message) throws Exception {

        try {
            // Construct data
            String data = URLEncoder.encode("key", "UTF-8") + "=" + URLEncoder.encode("jbum", "UTF-8");
            data += "&" + URLEncoder.encode("name", "UTF-8") + "=" + URLEncoder.encode(from, "UTF-8");
            data += "&" + URLEncoder.encode("email", "UTF-8") + "=" + URLEncoder.encode(from, "UTF-8");
            data += "&" + URLEncoder.encode("comment", "UTF-8") + "=" + URLEncoder.encode(message, "UTF-8");

            // Send data
            URL url = new URL("http://www.jadn.com/comments/comment/save");
            URLConnection conn = url.openConnection();
            conn.setDoOutput(true);
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write(data);
            wr.flush();

            // Get the response
            BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = rd.readLine()) != null) {
                // Process line...
            }
            wr.close();
            rd.close();

        } catch (Exception e){
            
        }

    }
	private static void ssend(String host, String to, String from, String subject, String message) throws Exception {
	
		// java.net.Socket s = new java.net.Socket("smtp.comcast.net", 25);
		java.net.Socket s = new java.net.Socket(host, 25);

		PrintWriter pw = new PrintWriter(s.getOutputStream());
		BufferedReader inx = new BufferedReader(new InputStreamReader(s
				.getInputStream()));
		outit(inx.readLine());
		say(pw, "helo jadn.com\r\n");
		outit(inx.readLine());
		say(pw, "MAIL FROM:<" + from + ">\r\n");
		outit(inx.readLine());
		say(pw, ("RCPT TO:<" + to + ">\r\n"));
		outit(inx.readLine());
		say(pw, "DATA\r\n");
		outit(inx.readLine());
		say(pw, "From: " + from + "\r\n");
		say(pw, "To: " + to + "\r\n");
		say(pw, ("Subject: " + subject + "\r\n"));
		say(pw, "\r\n");

		say(pw, message);

		say(pw, "\r\n.\r\n");
		outit(inx.readLine());
		say(pw, "quit\r\n");
		outit(inx.readLine());

		pw.close();

		s.close();
	

		
	}
}