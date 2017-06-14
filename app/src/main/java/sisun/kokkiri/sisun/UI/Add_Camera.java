package sisun.kokkiri.sisun.UI;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import sisun.kokkiri.sisun.R;


public class Add_Camera extends AppCompatActivity {

    private RadioButton useInfo;
    private RadioButton useUri;
    private EditText uriInfo;
    private EditText cameraName;
    private EditText ipAddress;
    private EditText portNumber;
    private EditText user;
    private EditText password;

    private int enableColor = Color.parseColor("#cccccc");
    private int disableColor = Color.rgb(255, 88, 88);

    // ----------------------------------------------------------------- methods

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_camera);

        android.support.v7.app.ActionBar actionBar1 = getSupportActionBar();
        if (actionBar1 != null) {
            actionBar1.setTitle("카메라 추가");
        }

        useInfo = (RadioButton) findViewById(R.id.UseINFO);
        useUri = (RadioButton) findViewById(R.id.UseURI);
        uriInfo = (EditText) findViewById(R.id.URI_INFO);
        cameraName = (EditText) findViewById(R.id.camera_name_field);
        ipAddress = (EditText) findViewById(R.id.ip_address_field);
        portNumber = (EditText) findViewById(R.id.port_num_field);
        user = (EditText) findViewById(R.id.id_field);
        password = (EditText) findViewById(R.id.password_field);

        RadioButtonOnClicked listener = new RadioButtonOnClicked();
        useInfo.setOnClickListener(listener);
        useUri.setOnClickListener(listener);

        ((Button) findViewById(R.id.camera_info_save)).setOnClickListener(new SaveButtonOnClicked());
        ((Button) findViewById(R.id.back_btn_in_camera_add)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        if (useInfo.isChecked())
            setUseInfo(true);
        else
            setUseInfo(false);

    }

    private void setUseInfo(boolean bool) {

        int color = disableColor;

        if (bool)
            color = enableColor;

        ipAddress.setEnabled(bool);
        ipAddress.setBackgroundColor(color);
        ipAddress.setText("");
        portNumber.setEnabled(bool);
        portNumber.setBackgroundColor(color);
        portNumber.setText("");
        user.setEnabled(bool);
        user.setBackgroundColor(color);
        user.setText("");
        password.setEnabled(bool);
        password.setBackgroundColor(color);
        password.setText("");

        if (color == enableColor)
            color = disableColor;
        else
            color = enableColor;

        uriInfo.setEnabled(!bool);
        uriInfo.setBackgroundColor(color);
        uriInfo.setText("rtsp://");
    }

    class RadioButtonOnClicked implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            if (v.equals(useInfo)) {
                useUri.setChecked(false);
                setUseInfo(true);
            } else {
                useInfo.setChecked(false);
                setUseInfo(false);
            }
        }
    }


    class SaveButtonOnClicked implements View.OnClickListener {
        @Override
        public void onClick(View v) {

            Context context = getApplicationContext();
            String camera_name = cameraName.getText().toString();
            String rtsp_address = null;

            if (useInfo.isChecked()) {

                String ip_address = ipAddress.getText().toString();
                String port_number = portNumber.getText().toString();
                String id = user.getText().toString();
                String pass = password.getText().toString();

                if (camera_name.length() == 0
                        || ip_address.length() == 0
                        || port_number.length() == 0
                        || id.length() == 0
                        || pass.length() == 0) {
                    Toast.makeText(context, "기입을 완료 해 주세요", Toast.LENGTH_SHORT).show();
                    return;
                }

                rtsp_address = "rtsp://";
                rtsp_address += id;
                rtsp_address += ":";
                rtsp_address += pass;
                rtsp_address += "@";
                rtsp_address += ip_address;
                rtsp_address += ":";
                rtsp_address += port_number;
                rtsp_address += "/video1";

            } else if (useUri.isChecked()) {
                rtsp_address = uriInfo.getText().toString();

                if (camera_name.length() == 0 || rtsp_address.length() == 0) {
                    Toast.makeText(context, "기입을 완료 해 주세요", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            SharedPreferences pref = context.getSharedPreferences("Camera_Info", Context.MODE_PRIVATE);
            String cameraName = pref.getString("CameraName", "");
            String rtspAddress = pref.getString("RtspAddress", "");

            cameraName += "," + camera_name;
            rtspAddress += "," + rtsp_address;

            SharedPreferences.Editor editor = pref.edit();
            editor.clear();
            editor.putString("CameraName", cameraName);
            editor.putString("RtspAddress", rtspAddress);
            editor.commit();

            Toast.makeText(context, "카메라 정보 추가 완료", Toast.LENGTH_SHORT).show();
            Toast.makeText(context, camera_name + " & " + rtsp_address, Toast.LENGTH_SHORT).show();
            finish();

        }
    }
}
