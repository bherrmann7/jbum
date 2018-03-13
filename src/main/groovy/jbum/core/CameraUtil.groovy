package jbum.core

import com.drew.imaging.jpeg.JpegMetadataReader
import com.drew.metadata.Directory
import com.drew.metadata.Metadata
import com.drew.metadata.exif.ExifDirectory
import jbum.ui.Main

class CameraUtil {
    static def findCameras(VecImageInfo vecImageInfo) {
        Map<String, Camera> map = [:]

        vecImageInfo.vec.each { ImageInfo ii ->
            // BOBH toLowerCase ...
            String name = getCameraName(new File(Main.getCurrentDir(), ii.fileName.name))
            if (!map[name])
                map[name] = new Camera(name: name, imageInfos: [ii])
            else
                map[name].imageInfos += ii
        }
        return map.values()
    }

    public static String getCameraName(File jpegFile) {
        Metadata metadata = JpegMetadataReader.readMetadata(jpegFile);
        Directory dir = metadata.getDirectory(ExifDirectory.class);
        return dir.getString(271);
    }

    static Date adjustTime(Date d, List<Camera> cameraList, String cameraName) {
        Camera camera = cameraList.find { it.name == cameraName }
        d.setTime(d.getTime() + camera.offset)
        return d;
    }

}
