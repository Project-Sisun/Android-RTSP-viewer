package sisun.kokkiri.sisun.CameraList;

import android.content.Context;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;


public class OnItemLongClickCameraList implements AdapterView.OnItemLongClickListener {

    private CameraListAdapter adapter;
    private Context context;
    private Handler handler;

    public OnItemLongClickCameraList(Context context, CameraListAdapter adapter, Handler handler) {
        this.adapter = adapter;
        this.context = context;
        this.handler = handler;
    }

    // ----------------------------------------------------------------- methods

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        String cameraName = adapter.getCameraName(position);
        MyCameraManager cameraManager = new MyCameraManager(context);

        if (cameraManager.removeCameraInfo(cameraName) == 1) {
            Toast.makeText(context, cameraName + "(을)를 삭제했습니다.", Toast.LENGTH_SHORT).show();
            handler.sendEmptyMessage(0);
        }

        return true;
    }
}
