package sisun.kokkiri.sisun.FireBase;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.RemoteMessage;

import java.util.ArrayList;
import java.util.StringTokenizer;

import sisun.kokkiri.sisun.CameraList.MyCameraManager;
import sisun.kokkiri.sisun.DebugTool.DebugTool;
import sisun.kokkiri.sisun.UI.HomeActivity;
import sisun.kokkiri.sisun.R;


public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        sendPushNotification(remoteMessage.getData().get("message"));
    }

    private String parseMessage(String message) {

        StringTokenizer tokenizer = new StringTokenizer(message, "\"");
        ArrayList<String> words = new ArrayList<>();

        while (tokenizer.hasMoreTokens())
            words.add(tokenizer.nextToken());

        return words.get(13);

    }

    private Intent isRelevantIP(String ipAddress) {
        MyCameraManager cameraManager = new MyCameraManager(this);

        ArrayList<String> cameraAddressList = cameraManager.getCameraAddress();
        if (cameraAddressList == null)
            return null;

        for (int i = 0; i < cameraAddressList.size(); i++) {

            if (cameraAddressList.get(i).contains(ipAddress)) {

                Intent intent = new Intent(this, HomeActivity.class);
                intent.putExtra("Title", cameraManager.getCameraName().get(i));
                intent.putExtra("Address", cameraAddressList.get(i));
                if (DebugTool.DEBUG) {
                    Log.i(DebugTool.DEBUGTAG, "receive address >> " + ipAddress);
                    Log.i(DebugTool.DEBUGTAG, "Relevant Camera >> "+cameraManager.getCameraName().get(i) + " & " + cameraAddressList.get(i));
                }
                return intent;

            }

        }

        return null;
    }

    private void sendPushNotification(String message) {

        String ipAddress = parseMessage(message);

        Intent intent = isRelevantIP(ipAddress);
        if (intent == null)
            return;

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.common_ic_googleplayservices)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                .setContentTitle("화재 감지!!")
                .setContentText("앱을 열어 화재를 확인하세요!")
                .setAutoCancel(true)
                .setSound(defaultSoundUri).setLights(000000255, 500, 2000)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        PowerManager pm = (PowerManager) this.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wakelock = pm.newWakeLock(
                PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.ON_AFTER_RELEASE, "TAG");
        wakelock.acquire(5000);

        if (wakelock != null) {
            wakelock.release();
            wakelock = null;
        }

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }
}
