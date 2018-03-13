/*
 * Created on Aug 27, 2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package jbum.core
/**
 * @author bob
 */
public class JbumObjectInputStream extends ObjectInputStream {

    /**
     * @throws IOException
     * @throws SecurityException
     */
    protected JbumObjectInputStream(InputStream is) throws IOException,
            SecurityException {
        super(is);
    }

    @SuppressWarnings("unchecked")
    protected Class resolveClass(ObjectStreamClass desc) throws IOException,
            ClassNotFoundException {
        if (desc.getName().equals("VecImageInfo"))
            return VecImageInfo.class;
        if (desc.getName().equals("ImageInfo"))
            return ImageInfo.class;
        return super.resolveClass(desc);
    }

}