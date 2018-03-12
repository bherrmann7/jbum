package jbum.core;

public class Version {
     static String ver = "1.99";
     static String date = "9-Jan-2015";

     static String VERSION = "version "+ver+" "+date;

    String[] changes = new String[]{"0.1 initial poop", "1.14 fix version number", "1.15 added Export to pdf, made create button grey", "1.16 added Feedback dialog to make giving feedback even easier", "1.17 doing 'Save' causes browser to show page.  Polaroid Flow now has image date on tooltip.", "1.18 bug fixes for Internet Explorer (added in hidden code for emailing photos)", "1.19 now uses a lightbox for popping up images (for polaroidFlow layout)", "1.20 fixed hideous memory problem (leak) with image rotation.  Added groovy for tinkering.", "1.21 fixed 3 bugs (spaces in path to jar, filename of restored images, no avi files.)", "1.22 Updated groovy to 1.5", "1.23 Do not delete .mov or .avi", "1.24 Fixed bugs found by Carolyn Lee", "1.25 Added publish to scp to a site. ", "1.26 split doesnt destroy album at destination (if one is there.)", "1.27 fix 'publish' so it created directories correctly", "1.28 Added detection of cameras and time sliding on 'Order by Exif date'", "1.29 removed lowercase hack which shouldn't have been checked in", "1.99 restarting work, need to catch up on many photos...."};

    /** by the build/publish process to grab the version for the web site  */
    public static void main(String[] args) {
        if (args.length == 0)
            System.out.println(ver);
        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("full")) {
                System.out.println(VERSION);
            }
            if (args[0].equalsIgnoreCase("date")) {
                System.out.println(date);
            }
        }

    }
}
