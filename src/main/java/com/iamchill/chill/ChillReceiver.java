package com.iamchill.chill;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.Toast;

import com.parse.ParsePushBroadcastReceiver;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

public class ChillReceiver extends ParsePushBroadcastReceiver
//        BroadcastReceiver
{

    public ChillReceiver() {
    }

    @Override
    public void onPushReceive(Context c, Intent intent) {
        super.onPushReceive(c, intent);
        new Thread(new Runnable() {
            @Override
            public void run() {
                ChillService.chill_service.refresh();
            }
        }).start();
//        try {
//            String action = intent.getAction();
//            String channel = intent.getExtras().getString("com.parse.Channel");
//            JSONObject json = new JSONObject(intent.getExtras().getString(
//                    "com.parse.Data"));
//
//            Iterator itr = json.keys();
//            while (itr.hasNext()) {
//                String key = (String) itr.next();
//                Log.d("d", "..." + key + " => " + json.getString(key));
//            }
//        } catch (JSONException e) {
//            Log.d("d", "JSONException: " + e.getMessage());
//        }
//        System.out.println("Receive " + super.toString());
    }

    @Override
    public void onPushOpen(Context c, Intent oi) {
        try {
            JSONObject json = new JSONObject(oi.getExtras().getString(
                    "com.parse.Data"));
            String nm = json.getString("alert").trim().split("[ :]")[0];
            JSONObject message = null;
            if (ChillService.chill_service.map != null && ChillService.chill_service.map.containsKey(nm))
                message = ChillService.chill_service.map.get(nm);

            Intent result = new Intent(c, MainActivity.class);
            result.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            if (message != null)
                result = result.putExtra("id_contact", message.getString("id_contact"))
                        .putExtra("from_notification", true);
            c.startActivity(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    public Bitmap getLargeIcon(Context c, Intent oi) {
//        return MainActivity.getIconResource(content);
//    }

//    @Override
//    public void onReceive(Context context, Intent intent) {
//        if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
//            context.startService(new Intent(context, ChillService.class));
//        }
//    }
}
