package com.iamchill.chill;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.text.Editable;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
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
import com.parse.*;

import android.widget.Toast;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuLayout;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.twitter.Regex;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Pattern;


public class MainActivity extends Activity implements Runnable
//        , GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener
{

    public static MainActivity main;

    public static final int gR = 0xd8, gG = 0xa, gB = 0x38, yR = 0xec, yG = 0xc, yB = -0x9f;

    public static int state;
    public static boolean activityVisible;
    private static long old_update_visibility, old_reload;
    public static boolean get_location;
    public final static int START = 0, TEACH0 = 6, TEACH1 = 1, TEACH2 = 2, TEACH3 = 3, TEACH4 = 4, MAIN = 5;

    public static AppAdapter adp;
    private static SwipeMenuListView listView;
    private static OnSwipeTouchListener swiper;
    private static Thread loader;
//    private static ChillService service;

    public static ArrayList<String> ad;
    public static ArrayList<String> refr_icons;
    //  public static SwipeMenuAdapter ad;

    private RelativeLayout old;
//    private float bx, by;

    private static long oldPadding;
    private static ConcurrentHashMap<RelativeLayout, Double> speed;
    private static ConcurrentHashMap<RelativeLayout, Double> posTo;
    private static ConcurrentHashMap<RelativeLayout, Long> oldTime;
    private static double rem_width;

    private LocationManager locationManager;
    //    private static GoogleApiClient mGoogleApiClient;
    public static double x, y;
    public static Typeface font, font_bold;

    public static SwipeMenuLayout swipe;

    public MainActivity() {
        main = this;
        swiper = new OnSwipeTouchListener() {
            public boolean onSwipeLeft() {
                if (state == TEACH1) {
                    goTeach(TEACH2);
                } else if (state == TEACH2) {
                    DataKeeper.setTeach(true);
                    goMainScreen();
                    // goTeach(TEACH3);
                }// else if (state == TEACH3) {
                //    goTeach(TEACH4);
                // }
                return true;
            }
        };
        speed = new ConcurrentHashMap<RelativeLayout, Double>();
        posTo = new ConcurrentHashMap<RelativeLayout, Double>();
        oldTime = new ConcurrentHashMap<RelativeLayout, Long>();
        refr_icons = new ArrayList<>();
        loader = new Thread(this);
        ad = new ArrayList<>();
    }

    static {
        sent_ = new ConcurrentSkipListSet<>();
//        System.out.println("create");
    }

    private LocationListener locationListener = new LocationListener() {

        @Override
        public void onLocationChanged(Location location) {
            showLocation(location);
        }

        @Override
        public void onProviderDisabled(String provider) {
//            checkEnabled();
        }

        @Override
        public void onProviderEnabled(String provider) {
//            checkEnabled();
            showLocation(locationManager.getLastKnownLocation(provider));
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    };

    private void showLocation(Location location) {
        if (location == null)
            return;
        get_location = true;
        x = location.getLatitude();
        y = location.getLongitude();
    }

//    private void checkEnabled() {
//        viewError(locationManager
//                .isProviderEnabled(LocationManager.GPS_PROVIDER) ? "gps enabled" : locationManager
//                .isProviderEnabled(LocationManager.NETWORK_PROVIDER) ? "network enabled" : "disabled");
//    }

    @Override
    public void onResume() {
        super.onResume();
//        if (!google.isConnected())
//            google.connect();
        activityVisible = true;
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                while (ChillService.chill_service == null) try {
//                    Thread.sleep(1000L);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
////                ChillService.chill_service.resumePebble();
//            }
//        }).start();
//        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
//                1000 * 10, 10, locationListener);
//        locationManager.requestLocationUpdates(
//                LocationManager.NETWORK_PROVIDER, 1000 * 10, 10,
//                locationListener);
//        checkEnabled();
    }

    @Override
    public void onPause() {
        super.onPause();
//        Wearable.DataApi.removeListener(google, this);
//        google.disconnect();
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                while (ChillService.chill_service == null) try {
//                    Thread.sleep(1000L);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                ChillService.chill_service.closePebble();
//            }
//        }).start();
        activityVisible = false;
//        locationManager.removeUpdates(locationListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        System.out.println("onCreate 1");
        try {
            state = START;
            DataKeeper.init();
//            System.out.println("onCreate 2");
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

//            System.out.println("SDK " + android.os.Build.VERSION.SDK_INT);
            if (android.os.Build.VERSION.SDK_INT >= 23) {
                int hasLocationPermission = checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);
                int hasSMSPermission = checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION);
                int hasInternet = checkSelfPermission(Manifest.permission.INTERNET);
                int s1 = checkSelfPermission(Manifest.permission.CAMERA);
                int s2 = checkSelfPermission(Manifest.permission.ACCESS_WIFI_STATE);
                int s3 = checkSelfPermission(Manifest.permission.ACCESS_NETWORK_STATE);
                int s4 = checkSelfPermission(Manifest.permission.VIBRATE);
                int s5 = checkSelfPermission(Manifest.permission.RECEIVE_BOOT_COMPLETED);
                int s6 = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                int s7 = checkSelfPermission(Manifest.permission.WAKE_LOCK);
                int s11 = checkSelfPermission(Manifest.permission.SEND_SMS);
                int s12 = checkSelfPermission(Manifest.permission.READ_CONTACTS);
                int s8 = checkSelfPermission(Manifest.permission.GET_ACCOUNTS);
                int s9 = checkSelfPermission("com.google.android.c2dm.permission.RECEIVE");
                int s10 = checkSelfPermission("com.iamchill.chill.permission.C2D_MESSAGE");

                List<String> permissions = new ArrayList<String>();
                if (hasLocationPermission != PackageManager.PERMISSION_GRANTED) {
                    permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
                }
                if (hasSMSPermission != PackageManager.PERMISSION_GRANTED) {
                    permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
                }
                if (hasInternet != PackageManager.PERMISSION_GRANTED) {
                    permissions.add(Manifest.permission.INTERNET);
                }
                if (s1 != PackageManager.PERMISSION_GRANTED) {
                    permissions.add(Manifest.permission.CAMERA);
                }
                if (s2 != PackageManager.PERMISSION_GRANTED) {
                    permissions.add(Manifest.permission.ACCESS_WIFI_STATE);
                }
                if (s3 != PackageManager.PERMISSION_GRANTED) {
                    permissions.add(Manifest.permission.ACCESS_NETWORK_STATE);
                }
                if (s4 != PackageManager.PERMISSION_GRANTED) {
                    permissions.add(Manifest.permission.VIBRATE);
                }
                if (s5 != PackageManager.PERMISSION_GRANTED) {
                    permissions.add(Manifest.permission.RECEIVE_BOOT_COMPLETED);
                }
                if (s6 != PackageManager.PERMISSION_GRANTED) {
                    permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                }
                if (s7 != PackageManager.PERMISSION_GRANTED) {
                    permissions.add(Manifest.permission.WAKE_LOCK);
                }
                if (s11 != PackageManager.PERMISSION_GRANTED) {
                    permissions.add(Manifest.permission.SEND_SMS);
                }
                if (s12 != PackageManager.PERMISSION_GRANTED) {
                    permissions.add(Manifest.permission.READ_CONTACTS);
                }
                if (s8 != PackageManager.PERMISSION_GRANTED) {
                    permissions.add(Manifest.permission.GET_ACCOUNTS);
                }
                if (s9 != PackageManager.PERMISSION_GRANTED) {
                    permissions.add("com.google.android.c2dm.permission.RECEIVE");
                }
                if (s10 != PackageManager.PERMISSION_GRANTED) {
                    permissions.add("com.iamchill.chill.permission.C2D_MESSAGE");
                }
                if (!permissions.isEmpty()) {
                    requestPermissions(permissions.toArray(new String[permissions.size()]), 0);
                } else {
                    loadLocation();
                }
            } else loadLocation();

            if (font == null) {
                font = Typeface.createFromAsset(getAssets(), "helvetica.ttf");
            }

            if (font_bold == null) {
                font_bold = Typeface.createFromAsset(getAssets(), "helvetica_bold.ttf");
            }

//        buildGoogleApiClient();

//            System.out.println("onCreate");
            if (!DataKeeper.token.equals("")) {
                new Thread(new Runnable() {

                    @Override
                    public void run() {
                        try {
//                            if (!DataKeeper.teach) {
//                                goTeach(TEACH1);
//                            } else {
                            goMainScreen();
//                            }
                        } catch (Exception exc) {
                            viewError("Error: " + exc.getMessage());
                            exc.printStackTrace();
                        }
                    }
                }).start();
            } else {
                setContentView(R.layout.activity_main);
                ((TextView) findViewById(R.id.textView4)).setTypeface(font);
                ((TextView) findViewById(R.id.nickname_id)).setTypeface(font);
                ((TextView) findViewById(R.id.button)).setTypeface(font);
                ((TextView) findViewById(R.id.promocode_id)).setTypeface(font);
                ((TextView) findViewById(R.id.editPassword_id)).setTypeface(font);
                ((ProgressBar) findViewById(R.id.pb)).setAlpha(0);
                ((ProgressBar) findViewById(R.id.pb)).getIndeterminateDrawable().setColorFilter(0xFFFFFFFF, PorterDuff.Mode.MULTIPLY);
            }

            Intent in = getIntent();
            if (in != null) {
                if (in.getBooleanExtra("from_notification", false)) {
                    SendActivity.setData(in.getStringExtra("id_contact"));
                    Intent inte = new Intent(this, SendActivity.class
//                            MessagesActivity.class
                    );
//                            .putExtra("id_contact", in.getStringExtra("id_contact"))
//                            .putExtra("from_notification", true);
                    startActivity(inte);
                }
            }
        } catch (Exception r) {
            r.printStackTrace();
        }
    }

    public void loadLocation() {
        try {
            locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
//            locationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, 50000,
//                    10, locationListener);
//            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 50000,
//                    10, locationListener);
//            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 50000,
//                    10, locationListener);
            locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, locationListener, Looper.getMainLooper());
            locationManager.requestSingleUpdate(LocationManager.PASSIVE_PROVIDER, locationListener, Looper.getMainLooper());
            locationManager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, locationListener, Looper.getMainLooper());
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        System.out.println("Permissions");
        switch (requestCode) {
            case 0: {
                for (int i = 0; i < permissions.length; i++) {
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        System.out.println("Permission Granted: " + permissions[i]);
                        loadLocation();
                    } else if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                        System.out.println("Permission Denied: " + permissions[i]);
                    }
                }
            }
            break;
            default: {
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            }
        }
    }

    public void got(View v) {
        Editable rd = ((EditText) findViewById(R.id.promocode_id)).getText();
        if (rd != null) {
            final String code = rd.toString().trim();
            new Thread(new Runnable() {

                @Override
                public void run() {
                    try {
                        JSONObject obj = Networking.checkPromocode(code);
                        if (!obj.getString("status").equals("success")) {
                            viewError("Incorrect promocode");
                        } else {
//                            JSONObject wq = Networking.addUser(obj.getJSONObject("response").getString("id_user_invited"));
//                            if (wq.getString("status").equals("success")) {
                            runOnUiThread(new Runnable() {

                                @Override
                                public void run() {
                                    goTeach(TEACH1);
                                }
                            });
//                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        } else
            goTeach(TEACH1);
    }

    public void skip(View v) {
        goTeach(TEACH1);
    }

    public void clear() {
        old = null;
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                for (int i = 0; i < listView.getChildCount(); i++) {
                    RelativeLayout rel = (RelativeLayout) listView.getChildAt(i).findViewById(R.id.rel_layout);
                    rel.setPadding(0, 0, 0, 0);
                    speed.clear();
                    posTo.clear();
                    oldTime.clear();
                }
            }
        });
    }

    public static int send;

    public synchronized void sendGO(View v) {
        if (send > 0) return;
        send++;
//        System.out.println(send);
        SendActivity.setData(ChillService.chill_service.map.get(((TextView) v.findViewById(R.id.label123)).getText().toString()));
        Intent intent = new Intent(this, SendActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    public void onItemClick(final AdapterView<?> parent, View view, final int position, long id) {
//        viewError("click");
        if (position < ad.size()) {
            try {
//                viewError("first condition");
                JSONObject ob = ChillService.chill_service.map.get(ad.get(position));
//            System.out.println(ob.toString());
                String tp = ob.getString("type");
                if (!"null".equals(tp) && tp != null && (!"1".equals(ob.getString("id_contact")) || ob.getBoolean("is_app"))) {
                    //if (getIcon(position, R.id.sent_icon_image).getVisibility() == ImageView.VISIBLE)
//                    viewError("view messages");
//                    System.out.println("on item click2 " + ad.get(position));
                    goViewMessages(ad.get(position));
                } else {
//                    viewError("try to tutorial " + ob.getString("id_contact"));
                    if ("1".equals(ob.getString("id_contact"))) {
//                        viewError("going to tutorial");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Intent is = new Intent(MainActivity.this, Tutorial.class);
                                startActivity(is);
                            }
                        });
                    }
//                    else {
//                        RelativeLayout r = (RelativeLayout) view.findViewById(R.id.rel);
//                        animateSwipe(r, r.getWidth() / 10);
//                    }
                }
            } catch (Exception e) {
                viewError(position + " " + ad.size() + " " + e.getMessage());
                e.printStackTrace();
            }
        } else {
//            viewError("going to last");
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    int ps = position;
                    ps -= ad.size();
                    Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                    if (ps == 1 || has_users) {
//                        viewError("app");
                        intent.putExtra("app", "1");
                    }
//                    viewError("starting activity " + ps);
                    if (ps == 0 || ps == 1) {
                        startActivity(intent);
                    }
                }
            });
        }
    }

//    public void animateSwipe(RelativeLayout r, double to) {
//        posTo.put(r, to);
//    }

    private static ConcurrentSkipListSet<String> sent_;

    public void animateSend(final String login) {
//        System.out.println("animate send " + login);
        sent_.add(login.intern());
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                View view = listView.getChildAt(ad.indexOf(login) - listView.getFirstVisiblePosition());
                if (view == null) return;
                view = view.findViewById(R.id.rel);
                final AppAdapter.ViewHolder holder = (AppAdapter.ViewHolder) view.getTag();
                holder.bar.setAlpha(1);
                holder.icon.setVisibility(View.INVISIBLE);
//                holder.icon.setAlpha(0);
            }
        });
    }

    public void dismissSent(final String login) {
//        System.out.println("dismiss send " + login);
        sent_.remove(login.intern());
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                View view = listView.getChildAt(ad.indexOf(login) - listView.getFirstVisiblePosition());
                if (view == null) return;
                view = view.findViewById(R.id.rel);
                final AppAdapter.ViewHolder holder = (AppAdapter.ViewHolder) view.getTag();
                holder.bar.setAlpha(0);
                try {
                    if (ChillService.map.get(login).getString("read").equals("0"))
                        holder.icon.setVisibility(View.VISIBLE);
                } catch (Exception exc) {
                    exc.printStackTrace();
                }
//                System.out.println("sas");
//                holder.icon.setAlpha(1);
            }
        });
    }

    public void onClickEvent(final String v) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    if (!ChillService.chill_service.map.get(v).getString("id_contact").equals("1")) {
                        final View view = listView.getChildAt(ad.indexOf(v) - listView.getFirstVisiblePosition()).findViewById(R.id.rel);
                        final AppAdapter.ViewHolder holder = (AppAdapter.ViewHolder) view.getTag();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                holder.bar.setAlpha(1);
                                holder.icon.setVisibility(View.INVISIBLE);
                            }
                        });
                        JSONObject js1 = ChillService.chill_service.map.get(v);
                        JSONObject jsonObject = Networking.removeUser(js1.getString("id_contact"), !js1.getBoolean("is_app"));
                        clear();
                        if (jsonObject.getString("status").equals("success")) {
                            viewError("The user " + v + " has been removed from your contacts list.");
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    swipe.clearSwipe();
                                    ad.remove(ad.indexOf(v));
                                    adp.notifyDataSetChanged();
                                }
                            });
                        } else {
                            viewError("Error: " + jsonObject.getString("response"));
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                holder.bar.setAlpha(0);
                                holder.icon.setVisibility(View.VISIBLE);
                            }
                        });
                    }
                } catch (JSONException e) {
                    viewError("Error: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (state == TEACH1 || state == TEACH2) {
            return swiper.onTouch(getCurrentFocus(), event);
        }
//        if (state == MAIN) {
//            return advancedSwipe(event);
//        }
        return false;
    }

    public void goToFirstTeach(View v) {
        goTeach(TEACH1);
    }

    public void goToSecondTeach(View v) {
        goTeach(TEACH2);
    }

    private void goTeach(final int td) {
        // Переход на экран обучалки
        if (td == TEACH1)
            DataKeeper.refreshIcons();
        activityVisible = false;
        state = td;
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                if (td == TEACH0) {
                    setContentView(R.layout.activity_promo);
                    ((Button) findViewById(R.id.got_button)).setTypeface(font);
                    ((Button) findViewById(R.id.skip_button)).setTypeface(font);
                    ((TextView) findViewById(R.id.twitter_text)).setTypeface(font);
                    ((TextView) findViewById(R.id.done_button_profile)).setTypeface(font);
                    ((TextView) findViewById(R.id.nickname_id)).setTypeface(font);
                    ((EditText) findViewById(R.id.promocode_id)).setTypeface(font);
                } else if (td == TEACH1) {
                    setContentView(R.layout.activity_teach);
                } else if (td == TEACH2) {
                    ((ImageView) findViewById(R.id.line_new2)).setImageResource(R.drawable.teach2);
                    ((Button) findViewById(R.id.plot0)).setBackgroundResource(R.drawable.circle);
                    ((Button) findViewById(R.id.plot1)).setBackgroundResource(R.drawable.circlefill);
                } else if (td == TEACH3) {
                    // setContentView(R.layout.activity_teach2);
                } else if (td == TEACH4) {
                }
            }
        });
    }

    public void openSettings(View v) {
        activityVisible = false;
        clear();
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    public static int dp2px(int dip) {
        float scale = main.getResources().getDisplayMetrics().density;
        return (int) (dip * scale + 0.5f);
    }

    private static SwipeMenu swipeMenu;
    public static SwipeMenuItem deleteItem, anotherItem;

    private ProgressDialog dlg_icons;

//    private void sendMSg() {
//        try {
//            String[] aw = "255 telephone".split(" ");
//            String user = aw[0].trim();
//            String content = aw[1].trim();
////                                MainActivity.main.viewError("d" + user + "d OHO d" + content + "d");
////                                MainActivity.main.viewError("QQQ " + (map.containsKey("kirill")) + " " + (map.containsKey(user)));
////                                user = map.get(user).getString("id_contact");
//            String type = content.equals("location") ? "location" : "icon";
//            if (type.equals("location")) {
//                content = MainActivity.x + "," + MainActivity.y;
//            }
//            MainActivity.main.viewError(user + " SSS " + content);
//            JSONObject rs = Networking.sendMessage(type, content, user, "");
//            MainActivity.main.viewError(rs.toString());
//            System.out.println("pebble " + (rs.getString("status").equals("success") ? 1 : 0));
//        } catch (Exception e) {
//            System.out.println("pebble 0");
//            MainActivity.main.viewError("Error Error Rorre Rorre");
//            e.printStackTrace();
//        }
//    }

    private void goMainScreen() {
        // Переход на основной экран
//        System.out.println("goMainScreen");
        try {
            state = MAIN;
            activityVisible = true;
            DataKeeper.iniValues();
            ParsePush.subscribeInBackground("us" + DataKeeper.userId);
//            sendMSg();
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    setContentView(R.layout.activity_dialogs);
                    ((TextView) findViewById(R.id.textView19)).setTypeface(font);
                    ((TextView) findViewById(R.id.adr)).setTypeface(font);
                    //ad = new ArrayAdapter<String>(MainActivity.this, R.layout.button_dialogs, R.id.label123, new ArrayList<String>());
                    listView = ((SwipeMenuListView) findViewById(R.id.dialogs_list));
                    SwipeMenuCreator creator = new SwipeMenuCreator() {

                        @Override
                        public void create(SwipeMenu menu) {
                            swipeMenu = menu;

                            // create "delete" item
                            deleteItem = new SwipeMenuItem(
                                    getApplicationContext());
                            deleteItem.setBackground(new ColorDrawable(Color.rgb(0xFF,
                                    0x3B, 0x2F)));
                            deleteItem.setWidth(dp2px(90));
                            deleteItem.setTitleSize(18);
                            deleteItem.setTitle("Delete");
                            deleteItem.setTitleColor(Color.rgb(0xD8, 0xCF, 0xEC));
                            swipeMenu.addMenuItem(deleteItem);

                            // create "another" item
                            DisplayMetrics metrics = new DisplayMetrics();
                            getWindowManager().getDefaultDisplay().getMetrics(metrics);
                            anotherItem = new SwipeMenuItem(
                                    getApplicationContext());
                            anotherItem.setBackground(new ColorDrawable(Color.rgb(0x23,
                                    0xC5, 0x9E)));
                            anotherItem.setWidth(metrics.widthPixels);
                            swipeMenu.addMenuItem(anotherItem);
                        }
                    };
                    listView.setMenuCreator(creator);

                    listView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {

                        @Override
                        public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                            switch (index) {
                                case 10:
                                    // delete
                                    onClickEvent(ad.get(position));
                                    break;
                                case 11:
                                    // green line
                                    break;
                            }
                            // false : close the menu; true : not close the menu
                            return false;
                        }
                    });

                    listView.setOnSwipeListener(new SwipeMenuListView.OnSwipeListener() {

                        @Override
                        public void onSwipeStart(int position) {
                            // swipe start
//                        DisplayMetrics metrics = new DisplayMetrics();
//                        getWindowManager().getDefaultDisplay().getMetrics(metrics);
//                        int dir = listView.getSwipeDirection();
//                        SwipeMenuView mv = (SwipeMenuView) listView.findViewById(SwipeMenuLayout.MENU_VIEW_ID);
////                        swipeMenu.addMenuItem(deleteItem);
//                        mv.removeAllViews();
//                        mv.mMenu.clear();
//                        if (dir == SwipeMenuListView.DIRECTION_LEFT) {
////                            System.out.println(deleteItem.getWidth() + " width remove");
//                            mv.mMenu.addMenuItem(deleteItem);
////                            deleteItem.setWidth(dp2px(90));
////                            anotherItem.setWidth(500);
//                        } else {
//                            mv.mMenu.addMenuItem(anotherItem);
////                            deleteItem.setWidth(500);
////                            anotherItem.setWidth(metrics.widthPixels - dp2px(90));
//                        }
//                        mv.addViews();
//                        View lin1 = mv.findViewById(10), lin2 = mv.findViewById(11);
//                        ViewGroup.LayoutParams l1 = lin1.getLayoutParams();
//                        ViewGroup.LayoutParams l2 = lin2.getLayoutParams();
//                        l1.width = deleteItem.getWidth();
//                        l2.width = anotherItem.getWidth();
//                        lin1.setLayoutParams(l1);
//                        lin2.setLayoutParams(l2);
//                        lin1.
//                        mv.findViewById(10).setMinimumWidth(500);
//                        mv.findViewById(10).refreshDrawableState();
//                        mv.findViewById(10).invalidate();
//                        System.out.println("swipe begin " + (mv.findViewById(10).getWidth()));
                        }

                        @Override
                        public void onSwipeEnd(int position) {
                            // swipe end
//                        System.out.println("swipe end");
                        }
                    });

                    listView.setOnScrollListener(new AbsListView.OnScrollListener() {
                        @Override
                        public void onScrollStateChanged(AbsListView view, int scrollState) {

                        }

                        @Override
                        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                            old_update_visibility = System.currentTimeMillis() - 200;
                            updateListViewData();
                        }
                    });


                    listView.setSwipeDirection(SwipeMenuListView.DIRECTION_LEFT);
                    adp = new AppAdapter();
                    listView.setAdapter(adp);
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                        @Override
                        public void onItemClick(final AdapterView<?> parent, View view, final int position, long id) {
//                            viewError("from itemclick");
                            main.onItemClick(parent, view, position, id);
                        }
                    });
                }
            });
//        new Thread(new Runnable() {
//            @Override
////            public void run() {
            if (ChillService.chill_service == null) {
//            System.out.println("starting");
                startService(new Intent(MainActivity.this, ChillService.class));
//            service = ChillService.chill_service;
//            service.refresh();
//            is_g = false;
//            NodeApi nodeApi = Wearable.NodeApi;
//            if (nodeApi != null) {
////                ChillService.iniGoogle(getApplicationContext());
//                PendingResult<NodeApi.GetConnectedNodesResult> r = nodeApi.getConnectedNodes(ChillService.google);
//                if (r != null) {
//                    NodeApi.GetConnectedNodesResult nr = r.await();
//                    if (nr != null) {
//                        List<Node> connectedNodes =
//                                nr.getNodes();
//                        MainActivity.main.viewError("Nodes: " + connectedNodes.size());
//                        if (connectedNodes != null && connectedNodes.size() > 0) {
//                            is_g = true;
//                            makeDefault();
//                        }
//                    }
//                }
//            }
//            System.out.println("Starting service " + (service == null));
            }
//            }
//        }).start();
            if (!loader.isAlive()) {
                loader.start();
            }
        } catch (Exception e) {
            viewError(e.getMessage());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }

    public void viewError(final String err) {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), err, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private static void setVisible(int index, int id, boolean vis) {
        View v = listView.getChildAt(index -
                listView.getFirstVisiblePosition());

        if (v == null)
            return;

        ImageView someText = (ImageView) v.findViewById(id);
        someText.setVisibility(vis && !sent_.contains(((AppAdapter.ViewHolder) v.findViewById(R.id.rel).getTag()).label123.getText()) ? ImageView.VISIBLE : ImageView.INVISIBLE);
    }

//    private void setTouchListener1(int index) {
//        final View vq = listView.getChildAt(index -
//                listView.getFirstVisiblePosition());
//
//        if (vq == null)
//            return;

//        vq.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                return onTouchEvent(event);
//            }
//        });
//        vq.findViewById(R.id.remove_button).setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                onClickEvent(((TextView) vq.findViewById(R.id.label123)).getText().toString());
//            }
//        });
//    }

    private void setIcon(int index, int id, Bitmap id_r) {
        View v = listView.getChildAt(index -
                listView.getFirstVisiblePosition());

        if (v == null)
            return;

        ImageView someText = (ImageView) v.findViewById(id);
        someText.setImageBitmap(id_r);
    }

    private ImageView getIcon(int index, int id) {
        return (ImageView) listView.getChildAt(index -
                listView.getFirstVisiblePosition()).findViewById(id);
    }

    public void networkError(final boolean v) {
        System.out.println(v);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                findViewById(R.id.internet_l).setVisibility(v ? View.VISIBLE : View.GONE);
            }
        });
    }

    private void updateListViewData() {
        if (listView == null) return;
        // In UI Thread
        long s = System.currentTimeMillis();

        // Update visibility
        if (s - old_update_visibility - 100 > 0) {
            for (int i = 0; i < ad.size(); i++) {
                try {
                    JSONObject object = ChillService.chill_service.map.get(ad.get(i));
                    if (object == null) continue;
                    boolean vis = object.getString("read").equals("0");
                    //setVisible(i, R.id.line_new, vis);
                    //setVisible(i, R.id.line_new2, vis);
                    setVisible(i, R.id.sent_icon_image, vis);
                    String type = object.getString("type");
                    String content = object.getString("content");
                    if (type != null) {
                        if (type.equals("location")) {
                            content = "location";
                        } else if (type.equals("parse")) {
                            content = "pic";
                        }
                        setIcon(i, R.id.sent_icon_image, getIconResource(content));
                    }
//                    setTouchListener(i);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            old_update_visibility = s;
        }

        // Update items coordinates
        if (s - oldPadding - 30 > 0) {
            for (RelativeLayout r2 : posTo.keySet()) {
                RelativeLayout rel = (RelativeLayout) r2.findViewById(R.id.rel_layout);
                double to = posTo.get(r2);
                //System.out.println("inside");
                //  if (rel != old || old == null) {
                int l = rel.getPaddingLeft(), r = 0;

                double spd = listView.getWidth() / 16;
                if (speed.containsKey(rel))
                    spd = speed.get(rel);
                else
                    speed.put(rel, spd);

                if (Math.abs(l - to) < spd) {
                    l = (int) to;
                }
                if (l < to) {
                    l += spd;
                }

                if (Math.abs(l - to) == 0) {
                    speed.put(rel, 0.0);
                    if (oldTime.containsKey(rel)) {
                        long ln = oldTime.get(rel);
                        if (s - ln - 1700 > 0) {
                            oldTime.remove(rel);
                            posTo.remove(r2);
                            speed.remove(rel);
                        }
                    } else oldTime.put(rel, s);
                }
                rel.setPadding(l, 0, r, 0);
                //  }
            }
            for (int i = 0; i < listView.getChildCount(); i++) {
                SwipeMenuLayout sl = (SwipeMenuLayout) listView.getChildAt(i);
                RelativeLayout r2 = (RelativeLayout) sl.findViewById(R.id.rel);// listView.getChildAt(i);
                RelativeLayout rel = (RelativeLayout) r2.findViewById(R.id.rel_layout);
                if (posTo.containsKey(r2)) continue;
                if (rel != old || old == null) {
                    int l = rel.getPaddingLeft(), r = rel.getPaddingRight();

                    double spd = listView.getWidth() / 32;
                    if (speed.containsKey(rel))
                        spd = speed.get(rel);
                    else
                        speed.put(rel, spd);

                    if (Math.abs(l) < spd) {
                        l = 0;
                    } else if (Math.abs(r) < spd) {
                        r = 0;
                    }
                    if (l > 0) {
                        l -= spd;
                    } else if (r > 0 && r < rem_width * 0.9) {
                        r -= spd;
                    }

                    if (l == 0 && r == 0) {
                        speed.remove(rel);
                    } else if (spd != 0) {
                        spd *= 1.2;
                        speed.put(rel, spd);
                    }
                    rel.setPadding(l, 0, r, 0);
                }
            }
            oldPadding = s;
        }
    }

    public void updateReload(final ArrayList<JSONObject> ar) {
        if (!activityVisible || state != MAIN || DataKeeper.refreshing.get()
                ) return;
        // обновляет данные сообщений и пользователей
        try {
//            if (ad.size() != ar.size()) {
//                MainActivity.main.viewError("REFRESH");
//                ChillService.chill_service.sendUsersDataPebble();
//            }
            ad.clear();
            for (int i = 0; i < ar.size(); i++) {
                JSONObject object1 = ar.get(i);
                ad.add(object1.getString("login"));
            }
        } catch (Exception ex) {
//                    if (state == MAIN)
//                        MainActivity.main.viewError("Error.");
            ex.printStackTrace();
        }
        first_load = true;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    adp.notifyDataSetChanged();
                    // ad.notifyDataSetChanged();
                } catch (Exception ex) {
//                    if (state == MAIN)
//                        MainActivity.main.viewError("Error.");
                    ex.printStackTrace();
                }
            }
        });
    }

    private ProgressDialog dlg;

    public void goViewMessages(final String name) {
        activityVisible = false;
        dlg = new ProgressDialog(MainActivity.this);
        dlg.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dlg.setMessage("wait please");
        dlg.setIndeterminate(true); // выдать значек ожидания
        dlg.setCancelable(true);
        dlg.show();
        // открываем окно просмотра сообщений
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    MessagesActivity.setData(ChillService.chill_service.map.get(name).getString("id_contact"));
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            clear();
                            Intent intent = new Intent(MainActivity.this, MessagesActivity.class);
                            startActivity(intent);
                        }
                    });
                } catch (Exception s) {
                    viewError("Error loading messages.");
                    s.printStackTrace();
                }
            }
        }).start();
    }

    public void dismissOpen() {
        dlg.dismiss();
    }

    public void openSearchActivity(View v) {
        activityVisible = false;
        // открываем окно поиска пользователей
        clear();
        Intent intent = new Intent(this, SearchActivity.class);
        startActivity(intent);
    }

    public boolean isCorrect(Editable login, Editable password) {
        if (login == null) {
            viewError("Login cannot be empty");
            return false;
        }
        if (password == null) {
            viewError("Password cannot be empty");
            return false;
        }
        String l = login.toString().trim().toLowerCase();
        String p = password.toString().trim().toLowerCase();
        Pattern pt = Pattern.compile("[a-z\\u002D_0-9]{" + l.length() + "}");
        if (!pt.matcher(l).find()) {
            viewError("Login must consists of english symbols, digits and \"_\"");
            return false;
        }
        if (p.length() < 6) {
            viewError("Password must be at least 6 characters");
            return false;
        }
        if (l.length() < 4) {
            viewError("Login must be at least 4 characters");
            return false;
        }
        return true;
    }

    public synchronized void clickOk(View v) {
        // нажали на GO!
        final ProgressBar pb = (ProgressBar) findViewById(R.id.pb);
        final Button b = ((Button) findViewById(R.id.button));
        pb.setAlpha(1);
        final String stra = b.getText().toString();
        b.setClickable(false);
        b.setText("");
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
//                    Thread.sleep(5000L);
                    EditText login = (EditText) findViewById(R.id.promocode_id);
                    EditText password = (EditText) findViewById(R.id.editPassword_id);
                    if (isCorrect(login.getText(), password.getText())) {
                        JSONObject res = Networking.authUser(login.getText().toString().trim().toLowerCase().replaceAll("-", "_"), password.getText().toString());
                        if (res.getString("status").equals("success")) {
                            JSONObject resp = res.getJSONObject("response");
                            DataKeeper.setToken(resp.getString("token"));
                            DataKeeper.setID(resp.getInt("id_user"));
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    pb.setAlpha(0);
                                    b.setClickable(true);
                                    b.setText(stra);
                                }
                            });
                            if (res.getJSONObject("response").getString("auth").equals("0")) {
                                goTeach(TEACH0);
                            } else {
                                goMainScreen();
                            }
                            //DataKeeper.setLogin(resp.getString("login"));
                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    b.setClickable(true);
                                    b.setText(stra);
                                    pb.setAlpha(0);
                                }
                            });
                            viewError("Incorrect input data!");
                        }
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                b.setClickable(true);
                                b.setText(stra);
                                pb.setAlpha(0);
                            }
                        });
                    }
                } catch (Exception exc) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            b.setClickable(true);
                            b.setText(stra);
                            pb.setAlpha(0);
                        }
                    });
                    MainActivity.main.viewError("Error: " + exc.getMessage());
                    exc.printStackTrace();
                }
            }
        }).start();
    }

    private boolean has_users, has_apps;
    public static boolean first_load;

    private int addition() {
        has_users = false;
        has_apps = false;
//        boolean has_iamchill = false;
        if (ad == null || ChillService.map == null) return 0;
        for (String s : ad) {
            JSONObject obj = ChillService.map.get(s);
            try {
//                if ("1".equals(obj.getString("id_contact"))) {
//                    has_iamchill = true;
//                    continue;
//                }
                if (obj.getBoolean("is_app")) {
                    has_apps = true;
                } else {
                    has_users = true;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if (!first_load) return 0;
//        if (!has_iamchill) return 0;
//        System.out.println(has_apps + " " + has_users);
        return (has_apps ? 0 : 1) + (has_users ? 0 : 1);
    }

    class AppAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return ad.size() + addition();
        }

        @Override
        public String getItem(int position) {
            if (position >= ad.size()) {
                position -= ad.size();
//                System.out.println(position);
                if (position == 0) {
                    return !has_users ? "Add Chiller" : "Add Chill App";
                } else {
                    return "Add Chill App";
                }
            }
            return ad.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
//                System.out.println("inflate");
                convertView = View.inflate(getApplicationContext(),
                        R.layout.button_dialogs, null);
                new ViewHolder(convertView);
            }
//            System.out.println("get");
            ViewHolder holder = (ViewHolder) convertView.getTag();
            String item = getItem(position).intern();
            //  holder.iv_icon.setImageDrawable(Drawable.);
            JSONObject object = ChillService.chill_service.map.get(item);
            holder.label123.setText(item);
            if (position < ad.size()) {
                try {
//                System.out.println(item);
//                System.out.println(ChillService.chill_service.map.get(item));
//                System.out.println("Name: " + ChillService.chill_service.map.get(item).getString("name"));
//                System.out.println("Login: " + ChillService.chill_service.map.get(item).getString("login"));
                    String twitter = object.getString("name");
                    if (twitter.equals("empty") || twitter.trim().equals("(null)")) twitter = "";
                    if (twitter.equals(object.getString("login")))
                        twitter = "";
                    holder.label1234.setText(twitter);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                holder.label1234.setText("");
            }
//            System.out.println("refresh");
            try {
                boolean vis = object != null && object.has("read") && object.getString("read").equals("0");
                if (sent_.contains(item)) {
//                System.out.println("contains");
                    holder.bar.setAlpha(1);
                    holder.icon.setVisibility(View.INVISIBLE);
//                holder.icon.setAlpha(0);
                } else {
//                System.out.println("not contains");
                    holder.bar.setAlpha(0);
                    if (vis)
                        holder.icon.setVisibility(View.VISIBLE);
//                holder.icon.setAlpha(1);
                }
                if (object != null) {
                    String type = object.getString("type");
                    String content = object.getString("content");
                    if (type != null) {
                        if (type.equals("location")) {
                            content = "location";
                        } else if (type.equals("parse")) {
                            content = "pic";
                        }
                        holder.icon.setImageBitmap(getIconResource(content));
                    }
                }
            } catch (Exception exc) {
                exc.printStackTrace();
            }
//            System.out.println("get viewholder " + item + " " + holder);
//            holder.icon.setVisibility(View.VISIBLE);
//            holder.bar.setAlpha(0);
            return convertView;
        }

        class ViewHolder {
            ImageView icon;
            TextView label123;
            TextView label1234;
            ProgressBar bar;

            public ViewHolder(View view) {
                icon = (ImageView) view.findViewById(R.id.sent_icon_image);
                label123 = (TextView) view.findViewById(R.id.label123);
                label1234 = (TextView) view.findViewById(R.id.label1234);
                bar = (ProgressBar) view.findViewById(R.id.pb_);
                view.setTag(this);
//                System.out.println("set viewholder");
            }
        }
    }

    public static Bitmap getIconResource(String nm) {
        return getIconResource(nm, 0);
    }

    public static Bitmap getIconResource(String nm, int n) {
        nm = nm.intern();
        int rs = -1;
        if (nm.equals("add_people")) {
            rs = R.drawable.add_people;
        }
        if (nm.equals("logo_chill")) {
            rs = R.drawable.logo_chill;
        }
        if (nm.equals("teach1")) {
            rs = R.drawable.teach1;
        }
        if (nm.equals("teach2")) {
            rs = R.drawable.teach2;
        }
        if (nm.equals("location")) {
            rs = R.drawable.location;
        }
        if (nm.equals("pic")) {
            rs = R.drawable.pic;
        }
        if (nm.equals("tutor_android")) {
            rs = R.drawable.tutor_android;
        }
        if (rs + 1 != 0) {
            return BitmapFactory.decodeResource(main.getResources(), rs);
        }
        if (ChillService.chill_service.icons.containsKey(nm)) {
            return ChillService.chill_service.icons.get(nm)[n];
        } else {
            if (!refr_icons.contains(nm)) {
                refr_icons.add(nm);
                DataKeeper.refreshIcons();
//            System.out.println(nm);
                while (!DataKeeper.refreshing.get()) ;
                while (!ChillService.chill_service.icons.containsKey(nm) && DataKeeper.refreshing.get())
                    ;
            }
            if (!ChillService.chill_service.icons.containsKey(nm))
                return BitmapFactory.decodeResource(main.getResources(), R.drawable.transparent);
//            System.out.println(nm + " end");
            return ChillService.chill_service.icons.get(nm)[n];
        }
    }

    public static Bitmap changeColorBitmap(Bitmap src, int rc, int gc, int bc) {
        Bitmap bt = src.copy(src.getConfig(), true);
        int[] pix = new int[bt.getWidth() * bt.getHeight()];
        bt.getPixels(pix, 0, bt.getWidth(), 0, 0, bt.getWidth(), bt.getHeight());
        for (int i = 0; i < pix.length; i++) {
            int c = pix[i];
            int a = (c >> 24) & 0xff;
            int r = (c >> 16) & 0xff;
            int g = (c >> 8) & 0xff;
            int b = c & 0xff;
            if (a > 100) {
                r += rc;
                g += gc;
                b += bc;
            }
            if (r > 255) r = 255;
            if (g > 255) g = 255;
            if (b > 255) b = 255;
            pix[i] = (a << 24) | (r << 16) | (g << 8) | b;
        }
        bt.setPixels(pix, 0, bt.getWidth(), 0, 0, bt.getWidth(), bt.getHeight());
        return bt;
    }

    private volatile AtomicBoolean is_rf = new AtomicBoolean();

    @Override
    public void run() {
        while (true) {
            try {
//                System.out.println("1");
                if (activityVisible && state == MAIN) {
//                    System.out.println("2");
                    Thread.sleep(500L);
                    while (ChillService.chill_service == null) Thread.sleep(20L);
                    long s = System.currentTimeMillis();
                    long slp = 5000L;
//                    System.out.println(ad.isEmpty());
                    if (ad.isEmpty()) {
                        slp = 500L;
                        if (DataKeeper.refreshing.get()) {
                            if (dlg_icons == null) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            dlg_icons = new ProgressDialog(MainActivity.this);
                                            dlg_icons.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                                            dlg_icons.setMessage("wait please");
                                            dlg_icons.setIndeterminate(true); // выдать значек ожидания
                                            dlg_icons.setCancelable(true);
                                            dlg_icons.show();
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                            }
                        }
                    } else {
                        if (!DataKeeper.refreshing.get())
                            if (dlg_icons != null) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            dlg_icons.dismiss();
                                            dlg_icons = null;
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                            }
                    }
                    if (s - old_reload - slp > 0) {
                        ChillService.chill_service.refresh();
                        old_reload = s;
                    }
                    if (ad != null)
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                is_rf.set(true);
                                updateListViewData();
                                is_rf.set(false);
                            }
                        });
                }
                while (is_rf.get()) Thread.sleep(20L);
                Thread.sleep(50L);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

//    @Override
//    public void onConnectionFailed(ConnectionResult connectionResult) {
//
//    }
}
