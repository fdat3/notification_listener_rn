package com.myapp;

import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import java.util.Map;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import java.util.HashMap;

import android.util.Log;

public class CalendarModule extends ReactContextBaseJavaModule {

    CalendarModule(ReactApplicationContext context) {
        super(context);
    }

    @Override
    public String getName() {
        return "CalendarModule";
    }

    @ReactMethod(isBlockingSynchronousMethod = true)
    public String createCalendarEvent(String name, String location) {
        String txt = ("Create event called with name: " + name
                + " and location: " + location);
        return txt;
    }
}