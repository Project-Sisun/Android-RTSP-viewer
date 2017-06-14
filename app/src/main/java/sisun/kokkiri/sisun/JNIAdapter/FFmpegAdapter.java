package sisun.kokkiri.sisun.JNIAdapter;


public class FFmpegAdapter {

    static {
        System.loadLibrary("VideoPlayer");
    }

    public static native void setDataSource(String uri);

    public static native int play(Object surface);

}
