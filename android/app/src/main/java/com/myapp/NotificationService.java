package com.myapp;

import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.ReactApplication;
import android.os.Bundle;
import android.app.Notification;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
import android.util.Base64;
import java.io.ByteArrayOutputStream;


import com.facebook.react.bridge.WritableNativeMap;

public class NotificationService extends NotificationListenerService {

    private static final String TAG = "NotificationService";

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        Log.d(TAG,
                "Notification received Noti module: " + sbn.getPackageName() + ":" + sbn.getNotification().tickerText);

        String ticker = "";
        Bundle extras = sbn.getNotification().extras;

        if (sbn.getNotification().tickerText == null) {
            ticker = extras.getString(Notification.EXTRA_TEXT);
        } else {
            ticker = sbn.getNotification().tickerText.toString();
        }
        WritableNativeMap params = new WritableNativeMap();
        PackageManager packageManager = getPackageManager();
        String appName = "";
        try {
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(sbn.getPackageName(), 0);
            appName = packageManager.getApplicationLabel(applicationInfo).toString();
            Drawable icon = packageManager.getApplicationIcon(sbn.getPackageName());
            Bitmap bitmap = drawableToBitmap(icon);

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream.toByteArray();
            String encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);

            params.putString("logo", encoded);
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "App not found", e);
        }

        params.putString("text", ticker);
        String app = sbn.getPackageName();
        params.putString("app", app);
        String title = extras.getString(Notification.EXTRA_TITLE);
        params.putString("title", title);
        params.putString("appName", appName);
        NotificationModule.sendEvent("notificationReceived", params);
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        Log.d(TAG, "Notification Removed: " + sbn.getNotification().tickerText);
    }

    private Bitmap drawableToBitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }
}