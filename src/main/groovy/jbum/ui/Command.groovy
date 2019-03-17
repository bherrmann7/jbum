package jbum.ui

import com.fasterxml.jackson.databind.ObjectMapper
import jbum.core.DPage
import jbum.core.ImageProcessor
import jbum.layouts.ExportToTemplate

import javax.swing.*

/**
 *  Handles command line processing.
 */
class Command {

    static void usage() {
        println("usage: jbum director-of-images");
        println("usage: jbum  # will open page chooser");
        println("usage: jbum make {directory-name}");
        println("usage: jbum rebuild {jbum.json | jbum.ser} # rebuilds images");
    }

    static void die(String msg) {
        System.err.println("jbum Die: " + msg)
        System.exit(1)
    }

    static void main(String[] args) {
        if (args.length == 0) {
            PageChooser.start();
        } else if (args[0] == "rebuild") {
            File file = new File(args[1])
            if (file.getName() != "jbum.ser" && file.getName() != "jbum.json")
                die("loadSave requires path to jbum.json or jbum.ser file.")
            DPage dp = new DPage(file, false)

            File outFile
            if (file.getParentFile() == null)
                outFile = new File("jbum.json")
            else
                outFile = new File(file.getParentFile(), "jbum.json")
            def fos = new FileOutputStream(outFile)
            ObjectMapper om = new ObjectMapper()
            def pp = om.writerWithDefaultPrettyPrinter()
            fos.write(pp.writeValueAsBytes(dp.toPage()))
            fos.close()

            ExportToTemplate e2p = new ExportToTemplate();
            e2p.export(dp);

        } else if (args[0] == "make") {
            File file = new File(args[1])
            if (!file.isDirectory()) die("make command requires a directory.  This is not a directory " + args[1])
            File jbSer = new File(file, "jbum.ser")
            if (jbSer.exists()) die("Directory already has a jbum.ser file!")
            File jbJson = new File(file, "jbum.json")
            if (jbJson.exists()) die("Directory already has a jbum.json file!")
            App.myself = new App()
            CenterP centerP = new CenterP(new DeletionManager(new JMenuBar()))
            App.myself.centerP = centerP
            centerP.setDir(file);
            centerP.scanDir()
            println("Waiting for enqueued images to be finished.")
            while (ImageProcessor.queueSize() > 0) {
                println("Queue count: " + ImageProcessor.queueSize())
                Thread.sleep(1000)
            }
            System.exit(0)
        } else if (args.length == 1) {
            File file = new File(args[0]);
            if (file.exists() && file.isDirectory()) {
                new App(new File(args[0]));
            } else {
                System.err.println("Unknown command or invalid directory: " + args[0])
                usage();
            }
        } else {
            System.err.println("Unknown command")
            usage();
        }
    }

}
