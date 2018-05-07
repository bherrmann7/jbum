package jbum.ui

import com.thoughtworks.xstream.XStream
import com.thoughtworks.xstream.io.json.JsonHierarchicalStreamDriver
import jbum.core.DPage
import jbum.core.ImageProcessor
import jbum.layouts.Page

import javax.swing.JLabel
import javax.swing.JMenuBar

/**
 *  Handles command line processing.
 */
class Command {

    static void die(String msg){
        System.err.println("jbum Die: "+msg)
        System.exit(1)
    }

    static void main(String[] args){
        if(args.length == 0){
            PageChooser.start();
        } else if(args[0] == "dump"){
            File file = new File(args[1])
            DPage dp = new DPage(file, false)
            XStream xstream = new XStream(new JsonHierarchicalStreamDriver());
            xstream.alias("page", jbum.layouts.Page.class);
            println(xstream.toXML(dp.toPage()))
        } else if(args[0] == "saveAsJson"){
            File file = new File(args[1])
            DPage dp = new DPage(file, false)
            XStream xstream = new XStream(new JsonHierarchicalStreamDriver());
            xstream.alias("page", jbum.layouts.Page.class);
            FileOutputStream fos = new File(file.getParentFile(), "jbum.json")
            fos.write(xstream.toXML(dp.toPage()).getBytes())
            fos.write()
        } else if(args[0] == "saveAsJser"){
            File file = new File(args[1])
            if(!file.getName().equals("jbum.json")){
                die("Expected jbum.json file")
            }
            XStream xstream = new XStream(new JsonHierarchicalStreamDriver());
            xstream.alias("page", jbum.layouts.Page.class);
            Object obj = xstream.fromXML(new FileInputStream(file))
            println(obj)
        } else if (args[0] == "make") {
            File file = new File(args[1])
            if(!file.isDirectory()) die("make command requires a directory.  This is not a directory "+args[1])
            File jbSer = new File(file,"jbum.ser")
            if(jbSer.exists()) die("Directory already has a jbum.ser file!")
            App.myself = new App()
            CenterP centerP = new CenterP(new DeletionManager(new JMenuBar()))
            App.myself.centerP = centerP
            centerP.setDir(file);
            centerP.scanDir()
            println("Waiting for enqueued images to be finished.")
            while(ImageProcessor.queueSize()>0){
                println("Queue count: "+ImageProcessor.queueSize())
                Thread.sleep(1000)
            }
            System.exit(0)
        } else if (args.length==1){
            new App(new File(args[0]));
        }
    }

}
