package sisun.kokkiri.sisun.CameraList;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import sisun.kokkiri.sisun.DebugTool.DebugTool;
import sisun.kokkiri.sisun.UI.Camera_View;


public class OnItemClickCameraList implements ListView.OnItemClickListener {

    private CameraListAdapter adapter;
    private Context context;

    public OnItemClickCameraList(Context context, CameraListAdapter adapter) {
        this.context = context;
        this.adapter = adapter;
    }

    // ----------------------------------------------------------------- methods

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String cameraName = adapter.getCameraName(position);
        String cameraAddress = adapter.getCameraAddress(position);

        if (DebugTool.DEBUG) {
            Toast.makeText(context, cameraAddress, Toast.LENGTH_SHORT).show();
        }

        Intent intent = new Intent(context, Camera_View.class);
        intent.putExtra("Title", cameraName);
        intent.putExtra("Address", cameraAddress);
        context.startActivity(intent);

    }

}
