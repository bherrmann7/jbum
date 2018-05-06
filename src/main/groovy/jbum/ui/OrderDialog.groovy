package jbum.ui

import jbum.core.Camera
import jbum.core.CameraUtil
import jbum.core.VecImageInfo
import se.datadosen.component.RiverLayout

import javax.swing.*
import java.awt.*
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.util.List

 class OrderDialog extends JDialog {

    def mapUI = [:]

     OrderDialog(final JFrame frame, VecImageInfo vecImageInfo, CenterP centerP) {
        super(frame);
        setTitle("Order by exif date");

        setLayout(new RiverLayout());
        def cameras = CameraUtil.findCameras(vecImageInfo);
        add("br left", new JLabel("Camera"));
        add("tab", new JLabel("Images"));
        add("tab", new JLabel("Days"));
        add("tab", new JLabel("Hours"));
        add("tab", new JLabel("Minutes"));

        cameras.each { Camera camera ->
            List<Integer> DHMOffsets = camera.getOffsets()
            def dayTF = new JTextField('' + DHMOffsets.get(0), 5);
            def hourTF = new JTextField('' + DHMOffsets.get(1), 5)
            def minTF = new JTextField('' + DHMOffsets.get(2), 5)
            add("br left", new JLabel(camera.name));
            add("tab", new JLabel('' + camera.imageInfos.size()));
            add("tab", dayTF);
            add("tab", hourTF);
            add("tab", minTF);
            mapUI[camera.name] = [dayTF, hourTF, minTF]
        }

        JPanel okcancel = new JPanel();
        add("p left", okcancel);
        JButton send = new JButton("Reorder Images");
        okcancel.add(send);
        send.addActionListener(new ActionListener() {
             void actionPerformed(ActionEvent e) {
                cameras.each { Camera camera ->
                    def uiSet = mapUI[camera.name]
                    camera.setOffsets(Integer.parseInt(uiSet[0].text),
                            Integer.parseInt(uiSet[1].text),
                            Integer.parseInt(uiSet[2].text))

                }
                okcancel.setEnabled(false)
                centerP.orderByExifDate(cameras as List<Camera>)
                dispose();
            }
        }
        );
        JButton cancel = new JButton("Cancel");
        okcancel.add(cancel);
        cancel.addActionListener(new ActionListener() {
             void actionPerformed(ActionEvent e) {
                dispose();
            }
        }
        )

        setLocation((int) (frame.getLocation().x + 100), (int) (frame.getLocation().y + 100));
        Dimension d = getPreferredSize();
        d.height += 25;
        setSize(d);
        //setAlwaysOnTop(true);
        setVisible(true);
    }

}
