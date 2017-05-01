package com.iamchill.chill;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Random;


// AIzaSyDxrlaVrKqCdDWiE95lU957c1L5FsongcA

public class MessagesActivity extends FragmentActivity {

    public static String id_contact;
    private static LinkedList<JSONObject> msg;
    private static int location;
    //    private static OnSwipeTouchListener swiper;
    private static JSONObject sess;
    private static GoogleMap gmap;
    private static Random random;

    public static boolean view_messages;

    private static final String PATH = "/storage/emulated/0/Pictures/ChillPictures/";

    static {
        msg = new LinkedList<JSONObject>();
        random = new Random();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_messages_timeline);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().
                findFragmentById(R.id.map);
        gmap = mapFragment.getMap();

//        swiper = new OnSwipeTouchListener() {
//
//            public boolean onSwipeLeft() {
////                go(prev());
////                update();
//                return true;
//            }
//
//            public boolean onSwipeRight() {
////                go(next());
////                update();
//                return true;
//            }
//        };

//        Intent intent = getIntent();
//        boolean from_ = intent.getBooleanExtra("from_notification", false);
//        System.out.println("from notification " + from_);
//        System.out.println("from notification " + intent.getExtras().containsKey("from_notification"));
//        System.out.println("id " + intent.getExtras().containsKey("id_contact"));
//        if (from_ && false) {
//            System.out.println("MakeDefault");
//            ChillService.chill_service.makeDefault();
//        }
//        final String id_c = intent.getStringExtra("id_contact");
//        if (id_c != null && !"".equals(id_c.trim())) {
//            new Thread(new Runnable() {
//
//                @Override
//                public void run() {
//                    setData(id_c);
//                    runOnUiThread(new Runnable() {
//
//                        @Override
//                        public void run() {
//                            updateViewData();
//                        }
//                    });
////                    runOnUiThread(new Runnable() {
////
////                        @Override
////                        public void run() {
//////                            setFavoriteIcons();
//////                            goBegin();
////                            update();
////                        }
////                    });
//                }
//            }).start();
//        } else
            updateViewData();
//        else {
////            setFavoriteIcons();
////            goBegin();
//            update();
//        }
//        ((TextView) findViewById(R.id.adr)).setTypeface(MainActivity.font);
        ((TextView) findViewById(R.id.t1)).setTypeface(MainActivity.font);
        ((TextView) findViewById(R.id.t2)).setTypeface(MainActivity.font);
        ((TextView) findViewById(R.id.t3)).setTypeface(MainActivity.font);
        ((TextView) findViewById(R.id.t4)).setTypeface(MainActivity.font);
        ((TextView) findViewById(R.id.t5)).setTypeface(MainActivity.font);
        ((TextView) findViewById(R.id.replay)).setTypeface(MainActivity.font);
        ((TextView) findViewById(R.id.adr)).setTypeface(MainActivity.font);

        view_messages = true;

        MainActivity.main.dismissOpen();
    }

    private void update() {
        final ProgressDialog prog1 = new ProgressDialog(this);
        prog1.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        prog1.setMessage("wait please");
        prog1.setIndeterminate(true); // выдать значек ожидания
        prog1.setCancelable(true);
        prog1.show();
        try {
            final String type = sess.getString("type"), content = sess.getString("content");
//            String hash = "";
//            if (sess.has("text")) {
//                hash = sess.getString("text");
//            }
//            RelativeLayout r = ((RelativeLayout) findViewById(R.id.map_rl));
//            RelativeLayout imgr = ((RelativeLayout) findViewById(R.id.photo_rel));
            final ImageView img = (ImageView) findViewById(R.id.photo_img);
//            final TextView adr = ((TextView) findViewById(R.id.adr));
//            ImageView v = ((ImageView) findViewById(R.id.big_icon));
//            if (type.equals("icon")) {
//                adr.setVisibility(hash.equals("") ? ImageView.INVISIBLE : ImageView.VISIBLE);
//                adr.setText("#" + hash);
//                r.setVisibility(ImageView.INVISIBLE);
//                imgr.setVisibility(ImageView.INVISIBLE);
//                v.setVisibility(ImageView.VISIBLE);
//                v.setImageBitmap(MainActivity.getIconResource(content));
//            } else
            if (type.equals("location")) {
//                adr.setVisibility(ImageView.VISIBLE);
//                r.setVisibility(ImageView.VISIBLE);
//                imgr.setVisibility(ImageView.INVISIBLE);
//                v.setVisibility(ImageView.INVISIBLE);
                //content = "55.679882, 37.284033";
                String[] ar = content.trim().split("[, ]");
                final double x = Double.parseDouble(ar[0]), y = Double.parseDouble(ar[ar.length - 1]);
//                adr.setText(getAddress(x, y));
                new Thread(new Runnable() {

                    @Override
                    public void run() {
                        try {
//.icon(BitmapDescriptorFactory.fromResource(R.id.send_map))
                            final Bitmap bt = BitmapFactory.decodeResource(MainActivity.main.getResources(), R.drawable.location);
                            int w = (int) (bt.getWidth() * 0.5), h = (int) (bt.getHeight() * 0.5);
                            final BitmapDescriptor bd = BitmapDescriptorFactory.fromBitmap(Bitmap.createScaledBitmap(bt, w, h, true));
//                            w *= 1.5;
//                            h *= 1.5;
                            runOnUiThread(new Runnable() {

                                @Override
                                public void run() {
                                    LatLng sydney = new LatLng(x, y);
                                    gmap.clear();
                                    gmap.addMarker(new MarkerOptions().icon(bd).position(sydney).title("Location"));
                                    //gmap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
                                    gmap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
                                    gmap.animateCamera(CameraUpdateFactory.zoomTo(17.0f));
                                }
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                            MainActivity.main.viewError("Error: " + e.getMessage());
                        }
                        prog1.dismiss();
                    }
                }).start();
            } else if (type.equals("parse")) {
//                adr.setVisibility(hash.equals("") ? ImageView.INVISIBLE : ImageView.VISIBLE);
//                adr.setText("#" + hash);
//                r.setVisibility(ImageView.INVISIBLE);
//                imgr.setVisibility(ImageView.VISIBLE);
//                v.setVisibility(ImageView.INVISIBLE);

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String st = DataKeeper.loadCachePath(content);
                            Bitmap bitmapOrg = null;
                            if (!st.equals("") && new File(st).exists()) {
                                // load from file system
                                bitmapOrg = BitmapFactory.decodeFile(st);
                            } else {
                                bitmapOrg = getImageBitmap(content);
                            }
//                            Matrix matrix = new Matrix();
//                            matrix.postRotate(90);
//                            Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmapOrg, bitmapOrg.getWidth(), bitmapOrg.getHeight(), true);
                            rotatedBitmap = bitmapOrg;//Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);
//                            int w = rotatedBitmap.getWidth(), h = rotatedBitmap.getHeight();
//                            DisplayMetrics metrics = new DisplayMetrics();
//                            getWindowManager().getDefaultDisplay().getMetrics(metrics);
//                            float dw = (float) (metrics.widthPixels) / (float) (w);
//                System.out.println(dw + " " + w + " " + h + " " + metrics.widthPixels + " " + (int) (dw * h));
//                            rotatedBitmap = Bitmap.createScaledBitmap(rotatedBitmap, metrics.widthPixels, (int) (dw * h), false);
//                            if (rotatedBitmap.getWidth() >= rotatedBitmap.getHeight()) {
//                                rotatedBitmap = Bitmap.createBitmap(
//                                        rotatedBitmap,
//                                        rotatedBitmap.getWidth() / 2 - rotatedBitmap.getHeight() / 2,
//                                        0,
//                                        rotatedBitmap.getHeight(),
//                                        rotatedBitmap.getHeight()
//                                );
//                            } else {
//                                rotatedBitmap = Bitmap.createBitmap(
//                                        rotatedBitmap,
//                                        0,
//                                        rotatedBitmap.getHeight() / 2 - rotatedBitmap.getWidth() / 2,
//                                        rotatedBitmap.getWidth(),
//                                        rotatedBitmap.getWidth()
//                                );
//                            }
                            runOnUiThread(new Runnable() {

                                @Override
                                public void run() {
                                    img.setImageBitmap(rotatedBitmap);
                                }
                            });
                            prog1.dismiss();
                        } catch (Exception exc) {
                            MainActivity.main.viewError("Error: " + exc.getMessage());
                            exc.printStackTrace();
                        }
                    }
                }).start();
            }
        } catch (Exception e) {
            MainActivity.main.viewError("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        view_messages = false;
    }

    @Override
    public void onResume() {
        super.onResume();
        view_messages = true;
    }

    @Override
    public void onPause() {
        super.onPause();
        view_messages = false;
    }

    public static Bitmap bm = null, rotatedBitmap = null;

    private static Bitmap getImageBitmap(final String url) throws Exception {
        bm = null;
        URL aURL = new URL(url);
        URLConnection conn = aURL.openConnection();
        conn.connect();
        InputStream is = conn.getInputStream();
        BufferedInputStream bis = new BufferedInputStream(is);
        bm = BitmapFactory.decodeStream(bis);
        bis.close();
        is.close();
//        if (bt.length > 0) {
        File pt = new File(PATH);
        if (!pt.exists()) pt.mkdir();
        String name = PATH + random.nextInt();
        FileOutputStream out = new FileOutputStream(name);
        bm.compress(Bitmap.CompressFormat.JPEG, 100, out);
        DataKeeper.putCachePath(url, name);
//        }
        return bm;
    }

//    private String getDataFromParse(String id) {
//        ParseQuery<ParseObject> query = ParseQuery.getQuery("PhotoObject");
//        query.getInBackground(id, new GetCallback<ParseObject>() {
//
//            @Override
//            public void done(ParseObject object, ParseException e) {
//                if (e == null) {
//                    // object will be your game score
//                    Set<String> s = object.keySet();
//                    for (String sw : s) System.out.println(s);
//                    //ParseFile f = (ParseFile) object.get();
//                } else {
//                    // something went wrong
//                    MainActivity.main.viewError("Error loading images.");
//                }
//            }
//        });
//        return null;
//    }

    private String getAddress(double latitude, double longitude) {
        StringBuilder result = new StringBuilder();
        try {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses.size() > 0) {
                Address address = addresses.get(0);
                result.append(address.getAddressLine(0));//.append("\n");
                //  result.append(address.getCountryName());
            }
        } catch (IOException e) {
            Log.e("tag", e.getMessage());
        }
        return result.toString();
    }

//    private void go(JSONObject s) {
//        sess = s;
//    }

//    private void goBegin() {
//        if (msg.isEmpty()) return;
//        sess = msg.getFirst();
//    }
//
//    private void goEnd() {
//        if (msg.isEmpty()) return;
//        sess = msg.getLast();
//    }

    public static void setData(String id_contact) {
        MessagesActivity.id_contact = id_contact;
        msg.clear();
        try {
            JSONObject object = Networking.getMessages(id_contact);
            if (object.getString("status").equals("success")) {
                JSONArray array = object.getJSONArray("response");
                for (int i = 0; i < array.length(); i++) {
                    JSONObject obj = array.getJSONObject(i);
                    msg.addLast(obj);
                }
            } else MainActivity.main.viewError("Error updating data");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private int getIconID(int i) {
        if (i == 0) return R.id.ic1;
        if (i == 1) return R.id.ic2;
        if (i == 2) return R.id.ic3;
        if (i == 3) return R.id.ic4;
        if (i == 4) return R.id.ic5;
        return 0;
    }

    private int getTextID(int i) {
        if (i == 0) return R.id.t1;
        if (i == 1) return R.id.t2;
        if (i == 2) return R.id.t3;
        if (i == 3) return R.id.t4;
        if (i == 4) return R.id.t5;
        return 0;
    }

    public void updateViewData() {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                try {
                    for (int i = 0; i < msg.size(); i++) {
                        JSONObject ob = msg.get(i);
//                        System.out.println(ob.toString());
                        final String type = ob.getString("type"), content = ob.getString("content");
                        String hash = "";
                        if (ob.has("text")) {
                            hash = ob.getString("text");
                        }
                        TextView adr = ((TextView) findViewById(getTextID(i)));
                        ImageView v = ((ImageView) findViewById(getIconID(i)));
                        if (type.equals("icon")) {
                            if (!hash.equals("")) hash = "#" + hash;
                            adr.setText(hash);
                            v.setImageBitmap(MainActivity.getIconResource(content));
                        } else if (type.equals("location")) {
                            String[] ar = content.trim().split("[, ]");
                            double x = Double.parseDouble(ar[0]), y = Double.parseDouble(ar[ar.length - 1]);
                            adr.setText(getAddress(x, y));
                            v.setImageBitmap(MainActivity.getIconResource("location"));
                        } else if (type.equals("parse")) {
//                            if (!hash.equals("")) hash = "#" + hash;
                            adr.setText(R.string.presstoopen);
                            v.setImageBitmap(MainActivity.getIconResource("pic"));
                        }
                    }
                    for (int i = msg.size(); i < 5; i++) {
                        TextView adr = ((TextView) findViewById(getTextID(i)));
                        ImageView v = ((ImageView) findViewById(getIconID(i)));
                        adr.setText("");
                        v.setImageBitmap(MainActivity.getIconResource("transparent"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                findViewById(R.id.m_rel).setVisibility(View.VISIBLE);
                findViewById(R.id.photo_rel).setVisibility(View.INVISIBLE);
                findViewById(R.id.map_rl).setVisibility(View.INVISIBLE);
            }
        });
    }

    public void replay(View v) {
        SendActivity.setData(id_contact);
        Intent inf = new Intent(this, SendActivity.class);
        startActivity(inf);
    }

    public void start() {
        findViewById(R.id.map_rl).setVisibility(View.INVISIBLE);
        findViewById(R.id.photo_rel).setVisibility(View.INVISIBLE);
        findViewById(R.id.m_rel).setVisibility(View.VISIBLE);
    }

    @Override
    public void onBackPressed() {
        if (vis) {
            vis = false;
            start();
        } else super.onBackPressed();
    }

    public boolean vis;

    public void click(int i) {
        if (vis) return;
        try {
            JSONObject ob = msg.get(i);
            final String type = ob.getString("type");
//            , content = ob.getString("content");
//            String hash = "";
//            if (ob.has("text")) {
//                hash = ob.getString("text");
//            }
//            TextView adr = ((TextView) findViewById(getTextID(i)));
//            ImageView v = ((ImageView) findViewById(getIconID(i)));
            if (type.equals("icon")) return;
            findViewById(R.id.m_rel).setVisibility(View.INVISIBLE);
            if (type.equals("location")) {
                ((TextView) findViewById(R.id.adr)).setText(((TextView) findViewById(getTextID(i))).getText());
                findViewById(R.id.map_rl).setVisibility(View.VISIBLE);
            } else if (type.equals("parse")) {
                findViewById(R.id.photo_rel).setVisibility(View.VISIBLE);
            }
            vis = true;
            sess = ob;
            update();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void click1(View v) {
        click(0);
    }

    public void click2(View v) {
        click(1);
    }

    public void click3(View v) {
        click(2);
    }

    public void click4(View v) {
        click(3);
    }

    public void click5(View v) {
        click(4);
    }

//    public static JSONObject next() {
//        location--;
//        if (location < 0) location = msg.size() - 1;
//        return msg.get(location);
//    }
//
//    public static JSONObject prev() {
//        location++;
//        if (location >= msg.size()) location = 0;
//        return msg.get(location);
//    }

//    public void sendMsg0(View v) {
//        new Thread(new Runnable() {
//
//            @Override
//            public void run() {
//                try {
//                    JSONObject object = Networking.sendMessage("icon", DataKeeper.favorite_icons.get(0), id_contact, "");
//                    if (object.getString("status").equals("success")) {
//                        MainActivity.main.viewError("Message has been sent");
//                    } else
//                        MainActivity.main.viewError("Error.");
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        }).start();
//        finish();
//    }
//
//    public void sendMsg1(View v) {
//        new Thread(new Runnable() {
//
//            @Override
//            public void run() {
//                try {
//                    JSONObject object = Networking.sendMessage("icon", DataKeeper.favorite_icons.get(1), id_contact, "");
//                    if (object.getString("status").equals("success")) {
//                        MainActivity.main.viewError("Message has been sent");
//                    } else
//                        MainActivity.main.viewError("Error.");
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        }).start();
//        finish();
//    }
//
//    public void sendMsg2(View v) {
//        new Thread(new Runnable() {
//
//            @Override
//            public void run() {
//                try {
//                    JSONObject object = Networking.sendMessage("icon", DataKeeper.favorite_icons.get(2), id_contact, "");
//                    if (object.getString("status").equals("success")) {
//                        MainActivity.main.viewError("Message has been sent");
//                    } else
//                        MainActivity.main.viewError("Error.");
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        }).start();
//        finish();
//    }
//
//    public void sendMsg3(View v) {
//        new Thread(new Runnable() {
//
//            @Override
//            public void run() {
//                try {
//                    JSONObject object = Networking.sendMessage("icon", DataKeeper.favorite_icons.get(3), id_contact, "");
//                    if (object.getString("status").equals("success")) {
//                        MainActivity.main.viewError("Message has been sent");
//                    } else
//                        MainActivity.main.viewError("Error.");
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        }).start();
//        finish();
//    }
//
//    public void sendMsg4(View v) {
//        new Thread(new Runnable() {
//
//            @Override
//            public void run() {
//                try {
//                    JSONObject object = Networking.sendMessage("icon", DataKeeper.favorite_icons.get(4), id_contact, "");
//                    if (object.getString("status").equals("success")) {
//                        MainActivity.main.viewError("Message has been sent");
//                    } else
//                        MainActivity.main.viewError("Error.");
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        }).start();
//        finish();
//    }
//
//    public void sendMsg5(View v) {
//        new Thread(new Runnable() {
//
//            @Override
//            public void run() {
//                try {
//                    JSONObject object = Networking.sendMessage("icon", DataKeeper.favorite_icons.get(5), id_contact, "");
//                    if (object.getString("status").equals("success")) {
//                        MainActivity.main.viewError("Message has been sent");
//                    } else
//                        MainActivity.main.viewError("Error.");
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        }).start();
//        finish();
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.messages, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//        if (id == R.id.action_settings) {
//            return true;
//        }
        return super.onOptionsItemSelected(item);
    }
}
