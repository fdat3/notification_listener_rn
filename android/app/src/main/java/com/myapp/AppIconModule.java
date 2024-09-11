package com.myapp;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
import android.util.Base64;
import android.util.Log;

import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.Arguments;

import java.io.ByteArrayOutputStream;

public class AppIconModule extends ReactContextBaseJavaModule {

    public AppIconModule(ReactApplicationContext reactContext) {
        super(reactContext);
    }

    @Override
    public String getName() {
        return "AppIconModule";
    }

    @ReactMethod
    public void getAppIcons(ReadableArray packageNamesArray, Callback callback) {
        PackageManager pm = getReactApplicationContext().getPackageManager();
        WritableMap iconsMap = Arguments.createMap();

        for (int i = 0; i < packageNamesArray.size(); i++) {
            String packageName = packageNamesArray.getString(i);
            try {
                ApplicationInfo appInfo = pm.getApplicationInfo(packageName, 0);
                Drawable icon = pm.getApplicationIcon(appInfo);
                Bitmap bitmap = drawableToBitmap(icon);

                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                byte[] byteArray = byteArrayOutputStream.toByteArray();
                String encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);

                iconsMap.putString(packageName, encoded);
            } catch (PackageManager.NameNotFoundException e) {
                Log.e("AppIconModule", "Package not found: " + packageName, e);
                iconsMap.putString(packageName, null);
            }
        }

        callback.invoke(iconsMap);
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
