package sisun.kokkiri.sisun.CameraList;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.StringTokenizer;


public class MyCameraManager {

    private Context context;

    public MyCameraManager(Context context) {
        this.context = context;
    }

    // ----------------------------------------------------------------- methods

    public ArrayList<String> getCameraName() {

        SharedPreferences pref = context.getSharedPreferences("Camera_Info", Context.MODE_PRIVATE);
        String cameraNames = pref.getString("CameraName", "");

        ArrayList<String> cameraNameList = new ArrayList<>();

        StringTokenizer tokenizer = new StringTokenizer(cameraNames, ",");
        while (tokenizer.hasMoreElements()) {
            cameraNameList.add(tokenizer.nextToken());
        }

        return cameraNameList;
    }

    public ArrayList<String> getCameraAddress() {

        SharedPreferences pref = context.getSharedPreferences("Camera_Info", Context.MODE_PRIVATE);
        String rtspAddresses = pref.getString("RtspAddress", "");

        ArrayList<String> cameraAddressList = new ArrayList<>();

        StringTokenizer tokenizer = new StringTokenizer(rtspAddresses, ",");
        while (tokenizer.hasMoreElements()) {
            cameraAddressList.add(tokenizer.nextToken().trim());
        }

        return cameraAddressList;
    }

    public int removeCameraInfo(String cameraName) {

        ArrayList<String> cameraNameList = getCameraName();
        ArrayList<String> cameraAddressList = getCameraAddress();

        for (int i = 0; i < cameraNameList.size(); i++) {
            if (cameraNameList.get(i).equals(cameraName)) {
                cameraNameList.remove(i);
                cameraAddressList.remove(i);

                String newCameraNames = "";
                for (int j = 0; j < cameraNameList.size(); j++)
                    newCameraNames += "," + cameraNameList.get(j);

                String newRtspAddresses = "";
                for (int j = 0; j < cameraAddressList.size(); j++)
                    newRtspAddresses += "," + cameraAddressList.get(j);

                SharedPreferences pref = context.getSharedPreferences("Camera_Info", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();
                editor.clear();
                editor.putString("CameraName", newCameraNames);
                editor.putString("RtspAddress", newRtspAddresses.trim());
                editor.commit();

                return 1;
            }
        }

        return 0;
    }
}
