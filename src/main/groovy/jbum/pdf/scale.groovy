package jbum.pdf

import jbum.core.DPage
import jbum.core.ImageInfo

File dir = new File("/home/bob/Desktop/db-scale");
dir.mkdir()

int desiredWidth = 1080;

def deeAlbum = new File("/home/bob/Desktop/db/jbum.ser");

if (true) {
    println "turned off"
    return
}

def run = { array ->
    println array.join(" ");
    array.execute().waitFor();
}


DPage meta = new DPage(deeAlbum, false);
meta.vii.vec.each { ImageInfo vii ->
    def src = vii.getOriginalFile(deeAlbum.parentFile)
    if (vii.imgSize.width > desiredWidth) {
        run(["convert", "-geometry", "$desiredWidth", "$src", "$dir/$src.name"])
    } else {
        run(["cp", "$src", "$dir"])
    }
}
