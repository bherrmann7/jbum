package jbum.ui

import com.jcraft.jsch.*;
import java.awt.*;
import javax.swing.*;


class FetchUserInfo implements UserInfo, UIKeyboardInteractive {
  String password

  boolean promptYesNo(String str) { 1 }
  public String getPassphrase() { return null; }
  public boolean promptPassphrase(String message) { return true; }
  public boolean promptPassword(String message) { password }

  public void showMessage(String message) {
    JOptionPane.showMessageDialog(null, message);
  }

  public String[] promptKeyboardInteractive(String destination,
                                            String name,
                                            String instruction,
                                            String[] prompt,
                                            boolean[] echo) {
    return null;
  }  
}

class fetch {

  static void main(String[] args) {
    JSch jsch = new JSch();
    Session session = jsch.getSession("bob", "jadn.com", 22);

    // username and password will be given via UserInfo interface.
    UserInfo ui = new FetchUserInfo();
    session.setUserInfo(ui);

    session.connect();

    Channel channel = session.openChannel("sftp");
    channel.connect();
    ChannelSftp c = (ChannelSftp) channel;

    def files = []
    def recurse
    recurse = {path ->
      //println "recurse on $path"
      c.ls(path).each {
        if (it.attrs.dir) {
          if (it.filename != '.' && it.filename != '..')
            recurse(path + '/' + it.filename)
        } else {
          files += path + '/' + it.filename
        }
      }
    }

    def base = '/home/bob/peaprocess/beach and soccer'
    recurse(base)
    //def vv = c.ls("/home/bob/peaprocess/beach and soccer")
    files.each {
      //println it
    }

    File f = new File("beachAndSoccer");
    f.mkdir();

    files.each {
      println "Getting $it"
      def remain = it.substring(base.size() + 1);
      File dest = new File(f, remain)
      dest.parentFile.mkdirs()
      c.get(it, dest.toString())
    }

//    println "All files are"
//    files.each {
//      println it
//    }
    //c.get("/jadn/index.html", "index.html")

    c.close();

    println "All finished."

    session.disconnect();


  }


}