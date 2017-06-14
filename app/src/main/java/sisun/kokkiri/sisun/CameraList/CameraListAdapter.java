package sisun.kokkiri.sisun.CameraList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import sisun.kokkiri.sisun.R;


public class CameraListAdapter extends BaseAdapter {

    private Context context;
    private MyCameraManager cameraManager;
    private ArrayList<String> cameraNameList;
    private ArrayList<String> cameraAddressList;

    public CameraListAdapter(Context context) {
        this.context = context;
        this.cameraManager = new MyCameraManager(context);
        this.cameraNameList = cameraManager.getCameraName();
        this.cameraAddressList = cameraManager.getCameraAddress();
    }

    // ----------------------------------------------------------------- methods

    @Override
    public int getCount() {
        return cameraNameList.size();
    }

    @Override
    public Object getItem(int position) {
        return cameraNameList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.item_layout, parent, false);
        }

        // 리스트뷰 내 아이콘 처리
        ImageView imageView = (ImageView) convertView.findViewById(R.id.imageView);
        imageView.setImageResource(R.drawable.camera_icon);

        String str;
        if ((str = cameraNameList.get(position)) != null)
            ((TextView) convertView.findViewById(R.id.textView)).setText(str);

        return convertView;
    }

    public String getCameraName(int position) {
        return cameraNameList.get(position);
    }

    public String getCameraAddress(int position) {
        return cameraAddressList.get(position);
    }

}
