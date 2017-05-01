package com.iamchill.chill;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

//import com.getpebble.android.kit.PebbleKit;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.facebook.FacebookSdk;
import com.parse.Parse;
import com.parse.ParseInstallation;

import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.internal.TwitterApi;

import io.fabric.sdk.android.Fabric;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Qbold on 29.11.2015.
 */
public class Chill extends MultiDexApplication {

    public static GoogleAnalytics analytics;
    public static Tracker tracker;

    public static boolean initialized;

    public static Chill chill;

//    public void viewError(String s) {
//        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
//    }

    @Override
    public void onCreate() {
        if (!Fabric.isInitialized()) {
            TwitterAuthConfig auth = new TwitterAuthConfig("pXol8UkljduKxe5wTGv998AlY",
                    "ItAGXsPZAOgA46TbRsNJyAZDaDMVNkR06LYecbwI0cQmKLswju");
            Fabric.with(this, new Twitter(auth));
        }
        chill = this;

        if (!initialized) {
            Parse.enableLocalDatastore(this);
            Parse.initialize(this, "vlSSbINvhblgGlipWpUWR6iJum3Q2xd7GthrDVUI", "ZR93BdaHDWTzjIvfDur3X02D3tNs0gATKwY1srh8");
            ParseInstallation.getCurrentInstallation().saveInBackground();
            initialized = true;
        }

        analytics = GoogleAnalytics.getInstance(this);
        analytics.setLocalDispatchPeriod(1800);

        tracker = analytics.newTracker("UA-59826573-3"); // Replace with actual tracker/property Id
        tracker.enableExceptionReporting(true);
        tracker.enableAdvertisingIdCollection(true);
        tracker.enableAutoActivityTracking(true);

//        viewError("Connected: " + PebbleKit.isWatchConnected(getApplicationContext()));
//        if (ChillService.connected_receiver == null)
//            ChillService.connected_receiver = new BroadcastReceiver() {
//
//                @Override
//                public void onReceive(Context context, Intent intent) {
////                Log.i(getLocalClassName(), "Pebble connected!");
////                System.out.println("Pebble connected");
////                    MainActivity.main.viewError("Pebble connected");
////                Toast.makeText(getApplicationContext(), "Pebble connected", Toast.LENGTH_LONG);
//                }
//
//            };
//        PebbleKit.registerPebbleConnectedReceiver(getApplicationContext(), ChillService.connected_receiver);
//        System.out.println("sasa");

//        if (ChillService.disconnected_receiver == null)
//            ChillService.disconnected_receiver = new BroadcastReceiver() {
//
//                @Override
//                public void onReceive(Context context, Intent intent) {
////                Log.i(getLocalClassName(), "Pebble disconnected!");
////                System.out.println("Pebble disconnected");
////                    MainActivity.main.viewError("Pebble disconnected");
////                Toast.makeText(getApplicationContext(), "Pebble disconnected", Toast.LENGTH_LONG);
//                }
//
//            };
//        PebbleKit.registerPebbleDisconnectedReceiver(getApplicationContext(), ChillService.disconnected_receiver);
//        System.out.println("sasa");
    }

    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

}
