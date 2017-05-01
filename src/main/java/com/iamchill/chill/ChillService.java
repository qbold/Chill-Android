package com.iamchill.chill;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Icon;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.NonNull;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

//import com.getpebble.android.kit.PebbleKit;
//import com.getpebble.android.kit.PebbleKit.PebbleAckReceiver;
//import com.getpebble.android.kit.util.PebbleDictionary;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.data.FreezableUtils;
import com.google.android.gms.wearable.CapabilityInfo;
import com.google.android.gms.wearable.Channel;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class ChillService extends WearableListenerService
//        implements //Runnable,
//        DataApi.DataListener,
//        GoogleApiClient.ConnectionCallbacks,
//        GoogleApiClient.OnConnectionFailedListener
{

    public JSONArray available_icons, fav;
    public ConcurrentHashMap<String, ArrayList<Integer>> sections;
    public static ConcurrentHashMap<String, Bitmap[]> icons;
    public static ArrayList<byte[]> small_icons_data = new ArrayList<>();
    public static ArrayList<String> favorite_icons, favorite_ids;

    public static ChillService chill_service;
    public static ConcurrentHashMap<String, JSONObject> map = new ConcurrentHashMap<>();
    public static ConcurrentHashMap<String, Integer> step = new ConcurrentHashMap<>();
    //    private static boolean firstTime;
//    NotificationManager nm;
//    private static Thread thr;
//    private static int intt;

    private static boolean viewed;
    private static ArrayList<JSONObject> awe = new ArrayList<>();
    public static boolean is_g;
    public static GoogleApiClient google;

//    private static PebbleKit.PebbleDataReceiver pebble_receiver;
//    private static PebbleAckReceiver pebble_ack_receiver;
//    private static PebbleKit.PebbleNackReceiver pebble_nack_receiver;
//    public static BroadcastReceiver connected_receiver, disconnected_receiver;

//    public static ConcurrentHashMap<Integer, PebbleDictionary> story = new ConcurrentHashMap<>();

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        final ArrayList<DataEvent> events = FreezableUtils.freezeIterable(dataEvents);
        try {
            for (DataEvent event : events) {
                if (event.getType() == DataEvent.TYPE_CHANGED) {
                    DataItem item = event.getDataItem();
                    DataMap map = DataMapItem.fromDataItem(item).getDataMap();
//                    if (item.getUri().getPath().contains("message"))
//                        MainActivity.main.viewError("path: " + item.getUri().getPath());
                    if (item.getUri().getPath().compareTo("/get_" + USERS_KEY) == 0) {
//                        MainActivity.main.viewError("users " + map.getBoolean(KEY + "get_" + USERS_KEY + ".int"));
                        if (!map.getBoolean(KEY + "get_" + USERS_KEY + ".int")) {
                            String obj = getJSONString(map, USERS_KEY);
//                            MainActivity.main.viewError("users2 " + obj);
                            if (temp_data.containsKey(USERS_KEY)) {
                                temp_data.replace(USERS_KEY, temp_data.get(USERS_KEY) + obj);
                            } else {
                                temp_data.put(USERS_KEY, obj);
                            }
                            if (isEnd(map, USERS_KEY)) {
                                temp_data.remove(USERS_KEY);
                                sendUsersData();
                            }
                        } else {
                            sendUsersData();
                        }
                    } else if (item.getUri().getPath().compareTo("/get_" + ICONS_KEY) == 0) {
//                        MainActivity.main.viewError("users " + map.getBoolean(KEY + "get_" + USERS_KEY + ".int"));
                        if (!map.getBoolean(KEY + "get_" + ICONS_KEY + ".int")) {
                            String obj = getJSONString(map, ICONS_KEY);
//                            MainActivity.main.viewError("users2 " + obj);
                            if (temp_data.containsKey(ICONS_KEY)) {
                                temp_data.replace(ICONS_KEY, temp_data.get(ICONS_KEY) + obj);
                            } else {
                                temp_data.put(ICONS_KEY, obj);
                            }
                            if (isEnd(map, ICONS_KEY)) {
                                temp_data.remove(ICONS_KEY);
                                sendIconsData();
                            }
                        } else {
                            sendIconsData();
                        }
                    } else if (item.getUri().getPath().compareTo("/get_" + MESSAGE_KEY) == 0) {
                        try {
                            if (!map.getBoolean(KEY + "get_" + MESSAGE_KEY + ".int")) {
                                String obj = getJSONString(map, MESSAGE_KEY);
//                                MainActivity.main.viewError("msg2 " + obj);
//                                if (temp_data.containsKey(MESSAGE_KEY)) {
//                                    temp_data.replace(MESSAGE_KEY, temp_data.get(MESSAGE_KEY) + obj);
//                                } else {
//                                    temp_data.put(MESSAGE_KEY, obj);
//                                }
//                                if (isEnd(map, MESSAGE_KEY)) {
                                JSONObject s = new JSONObject(obj);
//                                    temp_data.remove(MESSAGE_KEY);
//                                    MainActivity.main.viewError("path: " + item.getUri().getPath() + Math.random());
//                                    MainActivity.main.viewError("message " + s.getString("content") + " " + s.getString("id_user"));
                                sendMessage(s.getString("type"), s.getString("content"), s.getString("id_user"), s.getString("time"), s.getBoolean("is_app"));
//                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            MainActivity.main.viewError(e.getMessage());
                        }
                    } else if (item.getUri().getPath().startsWith("/get_" + ICONS_KEY)) {
                        String wq = item.getUri().getPath().substring(1);
                        final String we = item.getUri().getPath().substring(5);
                        if (!map.getBoolean(KEY + wq + ".int")) {
                            final String obj = getJSONString(map, we);
//                            MainActivity.main.viewError("icons2 " + obj);
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    while (icons == null || !icons.containsKey(obj)) {
                                        try {
                                            Thread.sleep(200L);
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    try {
                                        PutDataMapRequest putDataMapReq = PutDataMapRequest.create("/" + we);
                                        putDataMapReq.getDataMap().putBoolean(KEY + we + ".int", false);
                                        putDataMapReq.getDataMap().putString(KEY + we + ".name", obj);
                                        putDataMapReq.getDataMap().putString(KEY + we + "." + obj, getImageString(icons.get(obj)[0]));
                                        putDataMapReq.getDataMap().putLong(KEY + we + ".time", System.currentTimeMillis());
                                        PutDataRequest putDataReq = putDataMapReq.asPutDataRequest();
                                        PendingResult<DataApi.DataItemResult> pendingResult =
                                                Wearable.DataApi.putDataItem(google, putDataReq);
//                                        MainActivity.main.viewError("get_iconsKey " + obj);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        MainActivity.main.viewError(e.getMessage());
                                    }
                                }
                            }).start();
                        }
                    }
                } else if (event.getType() == DataEvent.TYPE_DELETED) {
//                        DataItem item = event.getDataItem();
//                    MainActivity.main.viewError("Deleted: " + item.getUri().getPath());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
//            MainActivity.main.viewError(e.toString());
        }
    }

    private static Random rs = new Random();

//    private static int genKey() {
//        int key = rs.nextInt(256);
//        while (story.containsKey(key)) {
//            key = rs.nextInt(256);
//        }
//        return key;
//    }

    @Override
    public void onPeerConnected(Node n) {
//        makeDefault();
//        Toast.makeText(getApplicationContext(), "peer connected " + n.getDisplayName(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPeerDisconnected(Node n) {
//        Toast.makeText(getApplicationContext(), "peer disconnected " + n.getDisplayName(), Toast.LENGTH_LONG).show();
    }

//    private final static UUID PEBBLE_APP_UUID = UUID.fromString("67c96fdc-c169-4579-b931-17fe81a1b136");
//    private static final int KEY_ICON0 = 1000;
//    private static final int KEY_ICON1 = 1001;
//    private static final int KEY_ICON2 = 1002;
//    private static final int KEY_ICON3 = 1003;
//    private static final int KEY_ICON4 = 1004;
//    private static final int KEY_ICON5 = 1005;
//    private static final int KEY_ICONR0 = 1007;
//    //    private static final int KEY_ICONR1 = 1008;
////    private static final int KEY_ICONR2 = 1009;
////    private static final int KEY_ICONR3 = 1010;
////    private static final int KEY_ICONR4 = 1011;
////    private static final int KEY_ICONR5 = 1012;
//    private static final int KEY_ICONS0 = 1013;
//    //    private static final int KEY_ICONS1 = 1014;
////    private static final int KEY_ICONS2 = 1015;
////    private static final int KEY_ICONS3 = 1016;
////    private static final int KEY_ICONS4 = 1017;
////    private static final int KEY_ICONS5 = 1018;
//    private static final int KEY_RESULT = 1006;
//    private static final int KEY_COUNT_USERS = 999;
//    private static final int KEY_SEND_USERS_QUERY = 998;
//    private static final int KEY_SEND_MESSAGE_QUERY = 996;
//    private static final int MAX_LENGTH_PEBBLE_MESSAGE = 3000;
//    private static final int START_USERS_KEY = 2000;
//    private static int key_users = START_USERS_KEY;

//    private static int generateKeyPebble() {
////        Random r = new Random();
////        int key = r.nextInt(10000);
////        while (key >= KEY_SEND_MESSAGE_QUERY && key <= KEY_ICONR5) {
////            key = r.nextInt(10000);
////        }
//        return key_users++;
//    }

    @Override
    public void onCreate() {
        super.onCreate();
        chill_service = this;
        try {
            iniGoogle(this);
            favorite_icons = new ArrayList<>();
            favorite_ids = new ArrayList<>();
            available_icons = new JSONArray();
            fav = new JSONArray();
            icons = new ConcurrentHashMap<>();
            sections = new ConcurrentHashMap<>();
            step = new ConcurrentHashMap<>();
            step.put(ICONS_KEY, 0);
            step.put(USERS_KEY, 0);
            step.put(MESSAGE_KEY, 0);

            sendUsersData();
            sendIconsData();
//            sendUsersDataPebble();
//            sendIconsDataPebble();
//            MainActivity.main.viewError(PebbleKit.areAppMessagesSupported(getApplicationContext()) + "");

//        DataKeeper.userId = DataKeeper.getDataInt(DataKeeper.UID_KEY);
//        System.out.println("start " + DataKeeper.userId);
//        Toast.makeText(getApplicationContext(), "create", Toast.LENGTH_LONG).show();
        } catch (Exception er) {
            MainActivity.main.viewError(er.getMessage());
        }
    }

//    public static void closePebble() {
////        chill_service.getApplicationContext().unregisterReceiver(pebble_receiver);
////        chill_service.getApplicationContext().unregisterReceiver(connected_receiver);
////        chill_service.getApplicationContext().unregisterReceiver(disconnected_receiver);
//    }
//
//    public static void resumePebble() {
//        if (pebble_receiver == null) {
//            pebble_receiver = new PebbleKit.PebbleDataReceiver(PEBBLE_APP_UUID) {
//                @Override
//                public void receiveData(final Context context, final int transactionId,
//                                        final PebbleDictionary data) {
//                    PebbleKit.sendAckToPebble(Chill.chill.getApplicationContext(), transactionId);
//
//                    try {
//                        if (data.contains(KEY_COUNT_USERS)) {
//                            new Thread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    try {
//                                        while (MainActivity.ad.isEmpty()) {
//                                            Thread.sleep(1000L);
//                                        }
//                                        sendUsersDataPebble();
//                                    } catch (Exception e) {
//                                        MainActivity.main.viewError(e.getMessage());
//                                    }
//                                }
//                            }).start();
//                        }
//                        if (data.contains(KEY_SEND_USERS_QUERY)) {
//                            sendUsersDataPebbleRaw();
//                        }
//                        if (data.contains(KEY_SEND_MESSAGE_QUERY)) {
//                            new Thread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    try {
//                                        String str = data.getString(KEY_SEND_MESSAGE_QUERY);
//                                        String[] aw = str.split(" ");
//                                        String user = aw[0].trim();
//                                        String content = aw[1].trim();
//                                        String type = content.equals("location") ? "location" : "icon";
//                                        if (type.equals("location")) {
//                                            content = MainActivity.x + "," + MainActivity.y;
//                                        }
//                                        JSONObject rs = Networking.sendMessage(type, content, user, "");
//                                        sendMessageToPebble(KEY_RESULT, rs.getString("status").equals("success") ? 1 : 0);
//                                    } catch (Exception e) {
//                                        sendMessageToPebble(KEY_RESULT, 0);
//                                        e.printStackTrace();
//                                    }
//                                }
//                            }).start();
//                        }
//                        if (data.contains(KEY_ICONR0)) {
//                            sendIconsDataPebble();
//                        }
//                    } catch (Exception er) {
//                        MainActivity.main.viewError(er.getMessage());
//                    }
//                }
//
//            };
//        }
//        if (pebble_ack_receiver == null) {
//            pebble_ack_receiver = new PebbleKit.PebbleAckReceiver(PEBBLE_APP_UUID) {
//                @Override
//                public void receiveAck(Context context, int transactionId) {
//                    story.remove(transactionId);
////                    MainActivity.main.viewError("received by pebble " + transactionId);
//                }
//            };
//        }
//        if (pebble_nack_receiver == null) {
//            pebble_nack_receiver = new PebbleKit.PebbleNackReceiver(PEBBLE_APP_UUID) {
//                @Override
//                public void receiveNack(Context context, final int transactionId) {
//                    new Thread(new Runnable() {
//                        @Override
//                        public void run() {
//                            try {
//                                Thread.sleep(3000L);
//                                PebbleDictionary dict = story.get(transactionId);
//                                story.remove(transactionId);
//                                int k = genKey();
//                                story.put(k, dict);
//                                PebbleKit.sendDataToPebbleWithTransactionId(Chill.chill.getApplicationContext(), PEBBLE_APP_UUID, dict, k);
//                            } catch (Exception e) {
//                                MainActivity.main.viewError(e.getMessage());
//                            }
//                        }
//                    }).start();
////                    MainActivity.main.viewError("not received by pebble " + transactionId + " " + story.get(transactionId).toJsonString());
//                }
//            };
//        }
//        PebbleKit.registerReceivedDataHandler(Chill.chill.getApplicationContext(), pebble_receiver);
//        PebbleKit.registerReceivedAckHandler(Chill.chill.getApplicationContext(), pebble_ack_receiver);
//        PebbleKit.registerReceivedNackHandler(Chill.chill.getApplicationContext(), pebble_nack_receiver);
//        sendUsersDataPebble();
//        sendIconsDataPebble();
//    }
//
//    public static void sendUsersDataPebble() {
////        MainActivity.main.finish();
//        try {
////            MainActivity.main.viewError("send KEY_COUNT_USERS " + MainActivity.ad.size());
//            new Thread(new Runnable() {
//                @Override
//                public void run() {
////                    try {
////                        Thread.sleep(3000L);
////                    } catch (InterruptedException e) {
////                        e.printStackTrace();
////                    }
//                    while (MainActivity.ad.isEmpty()) try {
//                        Thread.sleep(3000L);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                    sendMessageToPebble(KEY_COUNT_USERS, MainActivity.ad.size());
////                    sendUsersDataPebbleRaw();
//                }
//            }).start();
//        } catch (Exception e) {
//            MainActivity.main.viewError("Error: " + e.getMessage());
//        }
//    }
//
//    public static void sendUsersDataPebbleRaw() {
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
////                    while (MainActivity.ad.isEmpty()) {
////                        Thread.sleep(1000L);
////                    }
//                    StringBuilder str = new StringBuilder();
//                    for (int i = 0; i < MainActivity.ad.size(); i++) {
//                        try {
//                            String ln = i + " " + MainActivity.ad.get(i) + " " + map.get(MainActivity.ad.get(i)).getString("id_contact");
//                            if (str.length() + ln.length() + 1 > MAX_LENGTH_PEBBLE_MESSAGE) {
//                                sendMessageToPebble(generateKeyPebble(), str.toString());
//                                str = new StringBuilder();
//                            } else {
//                                if (str.length() > 0) str.append(" ");
//                                str.append(ln);
//                            }
//                        } catch (Exception e) {
//                            MainActivity.main.viewError(e.getMessage());
//                            e.printStackTrace();
//                        }
//                    }
//                    if (str.length() > 0) {
//                        sendMessageToPebble(generateKeyPebble(), str.toString());
//                    }
//                } catch (Exception es) {
//                    MainActivity.main.viewError(es.getMessage());
//                }
//            }
//        }).start();
//    }

//    public static void refreshIconsData() {
//        small_icons_data.clear();
//        small_icons_data.add(getImageBytesSmall(icons.get(favorite_icons.get(0))[0]));
//        small_icons_data.add(getImageBytesSmall(icons.get(favorite_icons.get(1))[0]));
//        small_icons_data.add(getImageBytesSmall(icons.get(favorite_icons.get(2))[0]));
//        small_icons_data.add(getImageBytesSmall(icons.get(favorite_icons.get(3))[0]));
//        small_icons_data.add(getImageBytesSmall(icons.get(favorite_icons.get(4))[0]));
//        small_icons_data.add(getImageBytesSmall(icons.get(favorite_icons.get(5))[0]));
//    }

//    public static void sendIconsDataPebble() {
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    while (favorite_icons == null || favorite_icons.isEmpty()) Thread.sleep(3000L);
//                    PebbleDictionary dic = new PebbleDictionary();
//                    dic.addString(KEY_ICONR0, favorite_icons.get(0) + " " + favorite_icons.get(1) + " " + favorite_icons.get(2) + " " + favorite_icons.get(3) + " " + favorite_icons.get(4) + " " + favorite_icons.get(5));
//                    dic.addString(KEY_ICONS0, small_icons_data.get(0).length + " " + small_icons_data.get(1).length + " " + small_icons_data.get(2).length + " " + small_icons_data.get(3).length + " " + small_icons_data.get(4).length + " " + small_icons_data.get(5).length);
//                    dic.addBytes(KEY_ICON0, small_icons_data.get(0));
//                    dic.addBytes(KEY_ICON1, small_icons_data.get(1));
//                    dic.addBytes(KEY_ICON2, small_icons_data.get(2));
//                    dic.addBytes(KEY_ICON3, small_icons_data.get(3));
//                    dic.addBytes(KEY_ICON4, small_icons_data.get(4));
//                    dic.addBytes(KEY_ICON5, small_icons_data.get(5));
//                    int tr = genKey();
//                    PebbleKit.sendDataToPebbleWithTransactionId(Chill.chill.getApplicationContext(), PEBBLE_APP_UUID, dic, tr);
//                    story.put(tr, dic);
//                } catch (Exception e) {
//                    MainActivity.main.viewError("Error: " + e.getMessage());
//                }
//            }
//        }).start();
//    }

//    public static void sendMessageToPebble(int key, String value) {
////        MainActivity.main.viewError("1send string message " + key);
//        try {
//            if (!PebbleKit.isWatchConnected(Chill.chill.getApplicationContext())) {
////                MainActivity.main.viewError("message hasnt been sent, pebble isnt connected");
//                return;
//            }
////            MainActivity.main.viewError("send string message " + key + " " + value);
//            PebbleDictionary dic = new PebbleDictionary();
//            dic.addString(key, value);
////            PebbleKit.sendDataToPebble(Chill.chill.getApplicationContext(), PEBBLE_APP_UUID, dic);
//            int tr = genKey();
//            PebbleKit.sendDataToPebbleWithTransactionId(Chill.chill.getApplicationContext(), PEBBLE_APP_UUID, dic, tr);
//            story.put(tr, dic);
//        } catch (Exception er) {
//            MainActivity.main.viewError(er.getMessage());
//        }
//    }

//    public static void sendMessageToPebble(int key, int value) {
////        MainActivity.main.viewError("1send int message " + key);
//        try {
//            if (!PebbleKit.isWatchConnected(Chill.chill.getApplicationContext())) {
////                MainActivity.main.viewError("message hasnt been sent, pebble isnt connected");
//                return;
//            }
////            MainActivity.main.viewError("send int message " + key);
//            PebbleDictionary dic = new PebbleDictionary();
//            dic.addInt32(key, value);
////            PebbleKit.sendDataToPebble(Chill.chill.getApplicationContext(), PEBBLE_APP_UUID, dic);
//            int tr = genKey();
//            PebbleKit.sendDataToPebbleWithTransactionId(Chill.chill.getApplicationContext(), PEBBLE_APP_UUID, dic, tr);
//            story.put(tr, dic);
//        } catch (Exception er) {
//            MainActivity.main.viewError(er.getMessage());
//        }
//    }

    public void makeDefault() {
        Intent result = new Intent(this, MainActivity.class);
        TaskStackBuilder bl = TaskStackBuilder.create(this);
        bl.addParentStack(MainActivity.class);
        bl.addNextIntent(result);
        PendingIntent pendingIntent = bl.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        Notification.Builder b = new Notification.Builder(this)
                .setContentTitle("Chill")
                .setContentText("")
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.logo))
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.transparent)
                .setPriority(Notification.PRIORITY_MIN);
        if (android.os.Build.VERSION.SDK_INT >= 21)
            b.setVisibility(Notification.VISIBILITY_SECRET);
        Notification notification = b.build();
        startForeground(1222, notification);
    }

//    public static boolean added;

    public void iniGoogle(Context cd) {
        try {
            google = new GoogleApiClient.Builder(cd).addApi(Wearable.API).
//                    addConnectionCallbacks(this).
//        addOnConnectionFailedListener(this).
        build();
            google.connect();
//            Toast.makeText(getApplicationContext(), "addListener " + Math.random(), Toast.LENGTH_LONG);
//            if (!added) {
            Wearable.DataApi.addListener(google, this);
//                added = true;
//            }
            new Thread(new Runnable() {
                @Override
                public void run() {
                    List<Node> connectedNodes =
                            Wearable.NodeApi.getConnectedNodes(google).await().getNodes();
                    if (connectedNodes.size() > 1)
                        makeDefault();
                }
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
//        Toast.makeText(getBaseContext(), "started " + (intent == null) + " " + Math.random(), Toast.LENGTH_LONG).show();
        map = new ConcurrentHashMap<String, JSONObject>();
        awe = new ArrayList<JSONObject>();
        temp_data = new ConcurrentHashMap<>();
        chill_service = this;
        if (is_g) {
            makeDefault();
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            Wearable.DataApi.removeListener(google, this);
            google.disconnect();
//            added = false;
        } catch (Exception e) {
            e.printStackTrace();
        }
//        System.out.println("DESTROYED");
    }

    public void refresh() {
        try {
            final JSONObject object = Networking.getContacts();
            if (Networking.error) {
                if (!viewed) {
                    MainActivity.main.networkError(true);
                    viewed = true;
                }
            } else {
                if (viewed) {
                    MainActivity.main.networkError(false);
                    viewed = false;
//                    MainActivity.main.viewError("Error connecting to server.");
                }
            }
            if (object != null && object.getString("status").equals("success")) {
//                JSONObject w = object.getJSONObject("response");
//                JSONArray ar = w.getJSONArray("users");
//                JSONArray ar2 = w.getJSONArray("apps");
                JSONArray ar = object.getJSONArray("response");
                ConcurrentHashMap<String, JSONObject> old = new ConcurrentHashMap<>();
                for (String s : map.keySet()) {
                    old.put(s, map.get(s));
                }
                map.clear();
                ArrayList<JSONObject> arr = new ArrayList<>(ar.length() /*+ ar2.length()*/);
                for (int i = 0; i < ar.length(); i++) {
                    JSONObject object1 = ar.getJSONObject(i);
                    if (object1.getString("id_contact").equals("1")) continue;
                    if (object1.has("login")) {
                        object1.put("is_app", false);
                    } else {
                        object1.put("is_app", true);
                        object1.put("login", object1.getString("name").trim());
                    }
                    if (object1.getString("login").contains(" ")) {
                        String lg = object1.getString("login").trim().replaceAll(" ", "_");
                        object1.remove("login");
                        object1.put("login", lg);
                    }
                    if (object1.getString("name").contains(" ") && object1.getBoolean("is_app")) {
                        String lg = object1.getString("name").trim().replaceAll(" ", "_");
                        object1.remove("name");
                        object1.put("name", lg);
                    }
                    map.put(object1.getString("login").trim(), object1);
                    arr.add(object1);
                }

                if (old.keySet().size() != map.keySet().size()) {
//                    MainActivity.main.viewError("CHANGE SIZE");
                    sendUsersData();
//                    sendUsersDataPebble();
                }
                awe = arr;
                MainActivity.main.updateReload(arr);

                if (MessagesActivity.view_messages) {
                    MessagesActivity.setData(MessagesActivity.id_contact);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
//        System.out.println("3");
    }

    public static int cnt;
    private static final String KEY = "com.iamchill.chill.key.";
    private static final String USERS_KEY = "users", ICONS_KEY = "icons", MESSAGE_KEY = "message";
    public static ConcurrentHashMap<String, String> temp_data;

    private final static int[] pebble_pallete = {
            0x0000aa, 0x000055, 0x0000ff, 0x005500, 0x005555,
            0x0055ff, 0x0055aa, 0x00aa55, 0x00aa00, 0x00aaaa,
            0x00aaff, 0x00ff55, 0x00ff00, 0x00ffff, 0x00ffaa, 0x550000,
            0xff0000, 0xaa0000, 0xaaffff, 0x55ffff, 0xffffff,
            0xaaffaa, 0xffffaa, 0x55ff55, 0x55ffaa, 0xffff55, 0xaaff55,
            0xaaff00, 0x55ff00, 0xffff00, 0xaaff00, 0x55aaff, 0x55ff00,
            0xffaaff, 0xaaaaff, 0xaaaaaa, 0x55aaaa, 0xffaa55, 0xffaaaa,
            0x55aa55, 0xaaaa55, 0xaaaa00, 0xffaa00, 0x55aa00, 0xaa55ff,
            0x5555ff, 0xff55ff, 0xaa55aa, 0x5555aa, 0xff55aa, 0xaa5555,
            0xff5555, 0x555555, 0xaa5500, 0x555500, 0xff5500, 0xaa00ff,
            0xff00ff, 0x5500ff, 0x5500aa, 0xaa00aa, 0xff00aa, 0x550055,
            0xaa0055, 0xff0055
    };

    public static byte[] getImageBytesSmall(Bitmap b) {
        System.out.println(b.getConfig());
        Bitmap bitmap = Bitmap.createScaledBitmap(b, 30, 30, false).copy(Bitmap.Config.RGB_565, true);
        System.out.println(bitmap.getConfig());
        ByteArrayOutputStream baos = null;
        try {
            baos = new ByteArrayOutputStream();
            int[] pix = new int[bitmap.getWidth() * bitmap.getHeight()];
            bitmap.getPixels(pix, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
            for (int i = 0; i < pix.length; i++) {
                int indx = 0, vl = Integer.MAX_VALUE;
                int red = (pix[i] >> 16) & 0xff;
                int green = (pix[i] >> 8) & 0xff;
                int blue = pix[i] & 0xff;
                for (int j = 0; j < pebble_pallete.length; j++) {
//                    int a_ = (pebble_pallete[j] >> 24) & 0xff;
                    int r_ = (pebble_pallete[j] >> 16) & 0xff;
                    int g_ = (pebble_pallete[j] >> 8) & 0xff;
                    int b_ = pebble_pallete[j] & 0xff;
                    int dif = (red - r_) * (red - r_) + (green - g_) * (green - g_) + (blue - b_) * (blue - b_);
//                    if (a_ < 50)
//                        a_ = 0;
//                    else
//                        a_ = 255;
                    if (vl > dif) {
                        vl = dif;
                        indx = pebble_pallete[j];
                    }
                }
                pix[i] = indx;
            }
            bitmap.setPixels(pix, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);

            ByteArrayOutputStream fos = new ByteArrayOutputStream();
            PngUtils.toIndexed256Colors(new ByteArrayInputStream(baos.toByteArray()), fos);
            fos.close();

            return fos.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                baos.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static String getImageString(Bitmap b) {
        ByteArrayOutputStream baos = null;
        try {
            baos = new ByteArrayOutputStream();
            b.compress(Bitmap.CompressFormat.PNG, 100, baos);
            return Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                baos.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return "";
    }

    public static Semaphore swq = new Semaphore(1);
    public static String old_type = "", old_content = "", old_id = "", old_time = "";

    public static void sendMessage(final String type, String content, final String id, final String time, final boolean is_app) {
        try {
            if (type.equals("location")) {
                content = MainActivity.x + ", " + MainActivity.y;
            }
//            MainActivity.main.viewError("sendMessage1 " + Math.random());
            final String content2 = content;
            new Thread(new Runnable() {

                @Override
                public void run() {
                    try {
                        swq.acquire();
                        if (old_type.equals(type) && old_content.equals(content2) && old_id.equals(id) && old_time.equals(time))
                            return;
                        JSONObject ob = Networking.sendMessage(type, content2, id, "", !is_app);
//                        MainActivity.main.viewError(ob.toString() + Math.random());
                        PutDataMapRequest putDataMapReq = PutDataMapRequest.create("/" + MESSAGE_KEY);
                        putDataMapReq.getDataMap().putBoolean(KEY + MESSAGE_KEY + ".res", ob.getString("status").equals("success"));
                        putDataMapReq.getDataMap().putBoolean(KEY + MESSAGE_KEY + ".int", false);
                        putDataMapReq.getDataMap().putLong(KEY + MESSAGE_KEY + ".time", System.currentTimeMillis());
                        PutDataRequest putDataReq = putDataMapReq.asPutDataRequest();
                        PendingResult<DataApi.DataItemResult> pendingResult =
                                Wearable.DataApi.putDataItem(google, putDataReq);
                    } catch (Exception e) {
                        MainActivity.main.viewError(e.getMessage());
                        e.printStackTrace();
                    } finally {
                        old_id = id;
                        old_content = content2;
                        old_type = type;
                        old_time = time;
                        swq.release();
                    }
                }
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void sendIconsData() {
//        if (true) return;
        try {
            JSONObject obj = new JSONObject();

            JSONObject sections2 = new JSONObject();
            for (String k : chill_service.sections.keySet()) {
                JSONArray ar = new JSONArray();
                for (int i : chill_service.sections.get(k)) {
                    ar.put(i);
                }
                sections2.put(k, ar);
            }

            JSONArray favorite_icons2 = new JSONArray(), favorite_ids2 = new JSONArray();
            for (String k : chill_service.favorite_icons) {
                favorite_icons2.put(k);
            }
            for (String k : chill_service.favorite_ids) {
                favorite_ids2.put(k);
            }

            obj.put("sections", sections2);
//            obj.put("icons", icons2);
            obj.put("favorite_icons", favorite_icons2);
            obj.put("favorite_ids", favorite_ids2);
            obj.put("available_icons", chill_service.available_icons);

            String dt = obj.toString();
            if (step.get(ICONS_KEY) == 0)
                sendData(ICONS_KEY, dt);

//            MainActivity.main.viewError("sendUsers");
        } catch (Exception e) {
//            MainActivity.main.viewError(e.toString());
            e.printStackTrace();
        }
    }

    public static void sendUsersData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (awe.isEmpty()) Thread.sleep(200L);
                    JSONObject obj = new JSONObject();
                    JSONArray js_ar = new JSONArray();
                    for (JSONObject js : awe) {
                        js_ar.put(js);
                    }
                    obj.put("users", js_ar);

                    String dt = obj.toString();

                    if (step.get(USERS_KEY) == 0)
                        sendData(USERS_KEY, /*str2.toString()*/dt);

//            MainActivity.main.viewError("sendUsers");
                } catch (Exception e) {
//            MainActivity.main.viewError(e.toString());
                    e.printStackTrace();
                }
            }
        }).start();
    }

    // Get json object from the data item
    public static boolean isEnd(DataMap dataMap, String k) {
        return dataMap.getBoolean(KEY + "get_" + k + ".end");
    }

    // Send the request to the handheld in order to get some data
    public static void sendData(String key) {
        PutDataMapRequest putDataMapReq = PutDataMapRequest.create("/" + key);
        putDataMapReq.getDataMap().putInt(KEY + key, cnt++);
        putDataMapReq.getDataMap().putBoolean(KEY + key + ".int", true);
        PutDataRequest putDataReq = putDataMapReq.asPutDataRequest();
        PendingResult<DataApi.DataItemResult> pendingResult =
                Wearable.DataApi.putDataItem(google, putDataReq);
    }

    @Override
    public void onChannelOpened(Channel c) {
//        Toast.makeText(getApplicationContext(), "channel open ", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onCapabilityChanged(CapabilityInfo info) {
//        Toast.makeText(getApplicationContext(), "capability changed " + info.getName(), Toast.LENGTH_LONG).show();
    }

    private static Semaphore semaphore = new Semaphore(1);

    // Send the data to the handheld
    public static void sendData(final String key, final String data2) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String data = data2;
                try {
                    semaphore.acquire();
                    final String add;
                    if (data.length() >= 90 * 1024) {
                        add = data.substring(90 * 1024);
                        data = data.substring(0, 90 * 1024);
                    } else add = null;
//                    if (add == null)
//                        MainActivity.main.viewError("Data: " + data.length() + " " + step.get(key) + " " + key + " " + (add == null));
                    PutDataMapRequest putDataMapReq = PutDataMapRequest.create("/" + key);
                    putDataMapReq.getDataMap().putString(KEY + key, data);
                    putDataMapReq.getDataMap().putLong(KEY + "get_" + key + ".time", System.currentTimeMillis());
                    putDataMapReq.getDataMap().putBoolean(KEY + key + ".int", false);
                    putDataMapReq.getDataMap().putInt(KEY + key + ".step", step.get(key));
                    step.replace(key, step.get(key) + 1);
                    putDataMapReq.getDataMap().putBoolean(KEY + key + ".end", add == null);
                    PutDataRequest putDataReq = putDataMapReq.asPutDataRequest();
                    PendingResult<DataApi.DataItemResult> pendingResult =
                            Wearable.DataApi.putDataItem(google, putDataReq);
                    pendingResult.setResultCallback(new ResultCallback<DataApi.DataItemResult>() {
                        @Override
                        public void onResult(@NonNull DataApi.DataItemResult dataItemResult) {
                            semaphore.release();
                        }
                    });
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            if (add != null) {
                                sendData(key, add);
                            } else {
                                step.replace(key, 0);
//                                MainActivity.main.viewError("Total count: " + (step.get(key) - 1));
                            }
                        }
                    }).start();
                } catch (Exception e) {
                    MainActivity.main.viewError(e.getMessage() + " dsds");
                    e.printStackTrace();
                }
            }
        }).start();
    }

    // Get json object from the data item
    public static String getJSONString(DataMap dataMap, String k) {
//        MainActivity.main.viewError(dataMap.toString());
//        MainActivity.main.viewError(KEY + "get_" + k);
        return dataMap.getString(KEY + "get_" + k);
    }
}
