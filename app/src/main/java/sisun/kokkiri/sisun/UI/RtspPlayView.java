package sisun.kokkiri.sisun.UI;

import android.content.Context;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import sisun.kokkiri.sisun.JNIAdapter.FFmpegAdapter;


public class RtspPlayView extends SurfaceView implements SurfaceHolder.Callback {

    private SurfaceHolder mHolder;
    private FFmpegAdapter Adapter;
    public Thread ffmpeg_thread;

    public RtspPlayView(Context context, String uri) {
        super(context);
        mHolder = getHolder();
        mHolder.addCallback(this); // JNI에 있는 라이브러리를 통해서 작업한다.
        Adapter = new FFmpegAdapter();
        Adapter.setDataSource(uri);
    }

    /**
     * -----------------------------
     * SurfaceHolder.Callback Implementation
     */
    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {

        // 비디오 플레이는 시간이 많이 걸리는 작업이여서 작업도중 메인 UI 쓰레드를 차단하지 않기 위해서, 별도의 쓰레드를 통해서 재생한다.
        (ffmpeg_thread = new Thread(new Runnable() {
            @Override
            public void run() {
                Log.d("FFmpeg_DEBUGING", "FFmpeg Thread ID : "+ffmpeg_thread.getId() + " start RTSP!!");
                Adapter.play(mHolder.getSurface());
            }

        })).start();

    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

    }

}

