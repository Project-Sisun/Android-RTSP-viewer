package sisun.kokkiri.sisun.UI;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import sisun.kokkiri.sisun.CameraList.CameraListAdapter;
import sisun.kokkiri.sisun.DebugTool.DebugTool;
import sisun.kokkiri.sisun.R;


public class Camera_View extends AppCompatActivity implements ListView.OnItemClickListener {

    private ListView cameraListView;
    private CameraListAdapter cameraListViewAdapter;

    // ----------------------------------------------------------------- methods

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera_view);

        Intent intent = getIntent();
        String title = intent.getStringExtra("Title");
        android.support.v7.app.ActionBar actionBar1 = getSupportActionBar();
        if (actionBar1 != null) {
            actionBar1.setTitle(title);
        }

        //-------------------------------------------------------  Camera ListView setting

        cameraListView = (ListView) findViewById(R.id.additional_carmera_list);
        cameraListViewAdapter = new CameraListAdapter(this);
        cameraListView.setAdapter(cameraListViewAdapter);
        cameraListView.setOnItemClickListener(this);

        //------------------------------------------------------- get Camera Info

        String uri = intent.getStringExtra("Address");

        //uri = "rtsp://mpv.cdn3.bigCDN.com:554/bigCDN/mp4:bigbuckbunnyiphone_400.mp4";
        //uri = "rtsp://admin:1234@192.168.0.200:554/video1";          // <<- B117 Camera1

        RelativeLayout viewer_layout = (RelativeLayout) findViewById(R.id.viewer_layout);
        viewer_layout.addView(new RtspPlayView(getApplicationContext(), uri));

    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String cameraName = cameraListViewAdapter.getCameraName(position);
        String cameraAddress = cameraListViewAdapter.getCameraAddress(position);

        if (DebugTool.DEBUG) {
            Toast.makeText(this, cameraAddress, Toast.LENGTH_SHORT).show();
        }

        android.support.v7.app.ActionBar actionBar1 = getSupportActionBar();
        if (actionBar1 != null) {
            actionBar1.setTitle(cameraName);
        }

        RelativeLayout viewer_layout = (RelativeLayout) findViewById(R.id.viewer_layout);
        viewer_layout.removeAllViews();
        viewer_layout.addView(new RtspPlayView(getApplicationContext(), cameraAddress));

    }
}
