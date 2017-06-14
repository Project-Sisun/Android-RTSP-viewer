package sisun.kokkiri.sisun.UI;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessaging;

import sisun.kokkiri.sisun.CameraList.CameraListAdapter;
import sisun.kokkiri.sisun.CameraList.OnItemClickCameraList;
import sisun.kokkiri.sisun.CameraList.OnItemLongClickCameraList;
import sisun.kokkiri.sisun.DebugTool.DebugTool;
import sisun.kokkiri.sisun.R;


public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private ListView cameraListView;
    private CameraListAdapter cameraListViewAdapter;
    private Handler myHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            initListView();
        }
    };

    // ----------------------------------------------------------------- methods

    private void initListView() {

        cameraListViewAdapter = new CameraListAdapter(this);
        cameraListView.setAdapter(cameraListViewAdapter);
        cameraListView.setOnItemClickListener(new OnItemClickCameraList(this, cameraListViewAdapter));
        cameraListView.setOnItemLongClickListener(new OnItemLongClickCameraList(this, cameraListViewAdapter, myHandler));

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setLogo(R.drawable.sisun_logo);

        android.support.v7.app.ActionBar actionBar1 = getSupportActionBar();
        if (actionBar1 != null)
            actionBar1.setTitle("카메라 리스트");

        //actionBar1.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        //getSupportActionBar().setCustomView(R.layout.sisun_bar);

        //-------------------------------------------------------  Camera ListView setting

        cameraListView = (ListView) findViewById(R.id.camera_list);
        initListView();

        //-------------------------------------------------------  UI setting

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Add_Camera.class);
                startActivity(intent);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();


        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //-------------------------------------------------------  Firebase setting

        FirebaseMessaging.getInstance().subscribeToTopic("news");

        //-------------------------------------------------------  FireBase Reply

        Intent intent = getIntent();
        String fireAddress = intent.getStringExtra("Address");

        if (fireAddress != null) {
            String fireTitles = intent.getStringExtra("Title");
            Intent fireIntent = new Intent(getApplicationContext(), Camera_View.class);
            fireIntent.putExtra("Title", fireTitles);
            fireIntent.putExtra("Address", fireAddress);

            if (DebugTool.DEBUG) {
                Toast.makeText(this, fireTitles, Toast.LENGTH_SHORT).show();
                Toast.makeText(this, fireAddress, Toast.LENGTH_SHORT).show();
            }

            startActivity(fireIntent);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        initListView();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.filter1) {
            return true;
        } else if (id == R.id.filter2) {
            return true;
        } else if (id == R.id.filter3) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.event_list) {

        } else if (id == R.id.demo) {
            Intent intent = new Intent(getApplicationContext(), Camera_View.class);
            intent.putExtra("Title", "Demo Camera");
            intent.putExtra("Address", "rtsp://admin:1234@192.168.0.200:554/video1");
            startActivity(intent);

        } else if (id == R.id.board) {

        } else if (id == R.id.setting) {

        } else if (id == R.id.add_new_camera) {
            Intent intent = new Intent(getApplicationContext(), Add_Camera.class);
            startActivity(intent);
        } else if (id == R.id.setting_camera) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
