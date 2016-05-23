package com.matescorp.parkinggo.gcm;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.matescorp.parkinggo.R;
import com.matescorp.parkinggo.activity.IntroActivity;
import com.matescorp.parkinggo.activity.LoginActivity;
import com.matescorp.parkinggo.activity.MainActivity;
import com.matescorp.parkinggo.util.Config;
import com.matescorp.parkinggo.util.DataPreference;

import org.apache.log4j.chainsaw.Main;

/**
 * Created by sjkim on 16. 5. 10.
 */
public class GcmIntentService extends IntentService {
    public static final String TAG = "icelancer";
    public static final String TITLE = "title";
    public static final String MESSAGE = "message";
    public static final String ENTRY_CAR = "50";
    public static final String EXIT_CAR = "45";
    public static final int NOTIFICATION_ID = 1;
    private NotificationManager mNotificationManager;
    NotificationCompat.Builder builder;

    public GcmIntentService() {
        super("GcmIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        String messageType = gcm.getMessageType(intent);

        String title = extras.getString(TITLE);
        String message = extras.getString(MESSAGE);


        if (!extras.isEmpty()) {
            if (GoogleCloudMessaging.
                    MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                Log.i(TAG, "Received: " + extras.toString());

                if (DataPreference.getPush()) {
                    if (extras.get(TITLE).equals(ENTRY_CAR)) {
                        sendNotification(getResources().getString(R.string.entry_information), message + " " + getResources().getString(R.string.in_the_lot));
                    } else if (extras.get(TITLE).equals(EXIT_CAR)) {
                        sendNotification(getResources().getString(R.string.exit_information), message + " " + getResources().getString(R.string.out_of_the_lot));
                    } else {
                        sendNotification(title, message);
                    }
                }
                Intent i = new Intent(Config.GCM_SEND_KEY);
                i.putExtra("", "");
                sendBroadcast(i);
            }
        }
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

    private void sendNotification(String title, String message) {


        mNotificationManager = (NotificationManager)
                getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, MainActivity.class), 0);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.icon_noti_in)
                        .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.icon_noti_in))
                        .setContentTitle(title)
                        .setAutoCancel(true)
                        .setColor(Color.parseColor("#ff7f66"))
                        .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_LIGHTS)
                        .setContentText(message)
                        .setPriority(Notification.PRIORITY_MAX);
//        NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//        nm.notify(0, mBuilder.build());

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }
}
