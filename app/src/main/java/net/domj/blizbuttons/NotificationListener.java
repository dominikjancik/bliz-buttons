package net.domj.blizbuttons;

import android.content.Intent;
import android.net.Uri;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import java.io.IOException;

public class NotificationListener extends NotificationListenerService {
    private static final String TAG = NotificationListener.class.getSimpleName();

    public void startNewActivity(String packageName) {
        Intent intent = getPackageManager().getLaunchIntentForPackage(packageName);
        if (intent == null) {
            // Bring user to the market or let them choose an app?
            intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("market://details?id=" + packageName));
        }
        //intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.v(TAG, "Created");
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        //super.onNotificationPosted(sbn);
        Log.v(TAG, "Posted");

        Log.v(TAG, sbn.getPackageName());
        Log.v(TAG, sbn.getNotification().toString());

        if (sbn.getPackageName().contains("com.skype.m2")) {
            Log.v(TAG, "is skype");
            if (sbn.getNotification().category.contains("call")) {
                Log.v(TAG, sbn.isClearable() ? "clearable" : "not");
//                cancelNotification(sbn.getKey());
//                startNewActivity("com.skype.m2");
            }
        }
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        Log.v(TAG, "Removed");

//        super.onNotificationRemoved(sbn);
    }

    @Override
    public void onListenerConnected() {
        super.onListenerConnected();

        Log.v(TAG, "On listener connected");
    }
}
