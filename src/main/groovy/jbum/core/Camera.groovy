package jbum.core

class Camera {

    Camera() {
        // Should cache and default these to last used by name
        setOffsets(0, 0, 0)
    }

    String name
    List<ImageInfo> imageInfos = [];
    long offset;

    private long MIN_MS = 60 * 1000
    private long HOUR_MS = 60 * MIN_MS
    private long DAY_MS = 24 * HOUR_MS

    void setOffsets(int days, int hours, int minutes) {
        offset = 0
        offset += days * DAY_MS
        offset += hours * 60 * 60 * 1000;
        offset += minutes * 60 * 1000;
    }

    def getOffsets() {
        long days = offset / DAY_MS
        long hours = (offset - days * DAY_MS) / HOUR_MS
        long minutes = (offset - days * DAY_MS - hours * HOUR_MS) / MIN_MS
        [days, hours, minutes] as List<Integer>
    }
}
