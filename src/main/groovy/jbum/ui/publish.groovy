package jbum.ui

import jbum.core.*

import com.jcraft.jsch.*;
import java.awt.*;
import javax.swing.*
import javax.swing.event.*
import se.datadosen.component.RiverLayout
import java.awt.event.ActionListener
import java.beans.PropertyChangeListener

public class Publish {

    void openDialog() {

        // save current work
        Main.myself.saveAction.actionPerformed(null)
        DPage dpage = Main.myself.centerP.getCurrentPage();
        def start = dpage.where

        JFrame jf = new JFrame();

        JPanel jp = new JPanel()
        jp.setLayout(new RiverLayout());
        jf.getContentPane().add(jp)
        jp.add("p center", new JLabel("Publish photos to site using scp (sftp)"))

        JTextField host, user, password, destDir
        JCheckBox textOnly
        JLabel pubLabel

        jp.add("p right", new JLabel("Host"))
        jp.add("tab hfill", host = new JTextField(30))

        jp.add("p right", new JLabel("User"))
        jp.add("tab hfill", user = new JTextField(30))

        jp.add("p right", new JLabel("Password"))
        jp.add("tab hfill", password = new JTextField(30))

        jp.add("p right", new JLabel("Destination directory"))
        jp.add("tab hfill", destDir = new JTextField(30))
        def rebuildLabel = {
            pubLabel.text = "( Will publish to: ${destDir.text}/${start.name} )"
        }
        destDir.getDocument().addDocumentListener([changedUpdate: rebuildLabel, insertUpdate: rebuildLabel, removeUpdate: rebuildLabel] as DocumentListener)

        jp.add("p center", pubLabel = new JLabel(""))

        jp.add("p right", new JLabel("Only update text"))
        jp.add("tab hfill", textOnly = new JCheckBox())

        JPanel okcancel = new JPanel();
        jp.add("p center", okcancel);
        JButton ok = new JButton("OK");
        ok.addActionListener([actionPerformed: {
            jf.dispose()
            PrefsCore.set("host", host.text)
            PrefsCore.set("user", user.text)
            PrefsCore.set("password", password.text)
            PrefsCore.set("destDir", destDir.text)
            PrefsCore.set("textOnly", textOnly.isSelected())
            startPublish(host.text, user.text, password.text, destDir.text, textOnly.isSelected())
        }] as ActionListener)

        okcancel.add(ok);

        host.text = PrefsCore.get("host", '')
        user.text = PrefsCore.get("user", System.properties["user.name"])
        password.text = PrefsCore.get("password", '')
        destDir.text = PrefsCore.get("destDir", '')
        textOnly.setSelected(PrefsCore.get("textOnly", false))

        JButton cancel = new JButton("Cancel");
        okcancel.add(cancel);
        cancel.addActionListener([actionPerformed: { jf.dispose(); }] as ActionListener)

        def loc = Main.myself.frame.location
        jf.setLocation((int) (loc.x + 150), (int) (loc.y + 150))
        jf.setSize(600, 400)
        jf.setVisible(true)

    }

    void startPublish(host, user, password, destDir, textOnly) {
        Thread.start {
            def putting            
            try {
                Main.status("Starting to transfer files")

                // save current work
                Main.myself.saveAction.actionPerformed(null)
                DPage dpage = Main.myself.centerP.getCurrentPage();
                def start = dpage.where

                def startTime = System.currentTimeMillis()

                JSch jsch = new JSch();
                Session session = jsch.getSession(user, host, 22);

                // username and password will be given via UserInfo interface.
                UserInfo ui = new FetchUserInfo();
                ui.password = password
                session.setUserInfo(ui);

                Main.status("Connecting to host...")

                session.connect();

                Channel channel = session.openChannel("sftp");

                Main.status("Opening sftp channel...")
                channel.connect();
                ChannelSftp c = (ChannelSftp) channel;

                File srcDir = start
                File dstDir = new File(destDir, start.name)
                def sFile = {File file ->
                    file.toString().replace('\\', '/')
                }

                def mkdir = {File dir ->
                    try {
                        c.mkdir(sFile(dir))
                    } catch (SftpException ex) {
                        // ok to ignore
                    }
                }

                Main.status("Making needed directories...")

                mkdir(dstDir)
                srcDir.eachDirRecurse {
                    def file = new File(dstDir, it.toString().substring(srcDir.toString().size()+1))
                    mkdir(file)
                    //println "Ensureed $file"
                }

                def monitor = [
                        init: {int i, java.lang.String s, java.lang.String s1, long l ->
                        },
                        count: {long l -> return true },
                        end: {-> }
                ] as SftpProgressMonitor

                def files = []
                srcDir.eachFileRecurse {
                    if (it.directory)
                        return
                    if (textOnly && !it.toString().endsWith('.html'))
                        return
                    files << it
                }

                files.eachWithIndex {file, index ->
                    String relativePath = file.toString().substring(srcDir.toString().length()+1)
                    Main.status("Transfering $index/$files.size $relativePath")
                    putting = [file.toString(), sFile(new File(dstDir, relativePath))]
                    c.put(file.toString(), sFile(new File(dstDir, relativePath)), null, ChannelSftp.OVERWRITE)
                }

                Main.status("all done.  Tranfered ${files.size()} in ${(System.currentTimeMillis() - startTime) / 1000} seconds")

            } catch (Throwable t) {
                t.printStackTrace()
                println("Transfer Aborted, files=${putting}, error=" + t.message)
                Main.status("Transfer Aborted, files=${putting}, error=" + t.message)
            }

        }
    }


}