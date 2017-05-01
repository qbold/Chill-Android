package com.iamchill.chill;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.preference.PreferenceManager;
import android.util.Base64;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import oauth.signpost.OAuth;

/**
 * Created by Qbold on 04.11.2015.
 */
public class DataKeeper {

    private static SharedPreferences preferences;
    public static String token, login, email, fid, tid;
    public static boolean teach;
    public static int userId;

    public final static String TOKEN_KEY = "dsjdklsciwLOsak4t2gSDJKAk", TEACH_KEY = "dsiqwmmcjjaHSYiqkdl;aa", IMAGE_KEY = "bdfsjkv2oc92LDSck29e20",
            UID_KEY = "nrgtkck3sdskDKvsjdj2i", LOGIN_KEY = "AJSNVlwkowqv834ov()DS;;", EMAIL_KEY = "sjblxoEDPWKfcsi3e29du(CNSUVu",
            FAVORITE0_KEY = "dsvJKSDOPpo202vbis0idckp21", FAVORITE1_KEY = "Ak1ocwesdpeo2gO;s'vds2234gsd", FAVORITE2_KEY = "ndjqwvnhsivnq8euvINSIhnv8webvwi",
            FAVORITE3_KEY = "852weDVSdbvwekoskqpw---dKA", FAVORITE4_KEY = "1ovMSODcwdsncvoaiE(9u", FAVORITE5_KEY = "paoscpkCAMO9d12jflaskapq1",
            FACEBOOK_ID_KEY = "kdlsgbmLEDLdksL243098t24KDSV", TWITTER_ID_KEY = "QFQGdjksug)02giksdjfiE82jfs";


    static void init() {
        preferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.main.getApplicationContext());
        iniValues();
    }

    static void iniValues() {
        token = getDataString(TOKEN_KEY);
        login = getDataString(LOGIN_KEY);
        fid = getDataString(FACEBOOK_ID_KEY);
        tid = getDataString(TWITTER_ID_KEY);
        login = getDataString(LOGIN_KEY);
        email = getDataString(EMAIL_KEY);
        teach = getDataBoolean(TEACH_KEY);
        userId = getDataInt(UID_KEY);
//        loadFavoriteIcons();

        // WARNING: CAP
//        if (favorite_icons.isEmpty()) {
//            addFavoriteIcon("clock");
//            addFavoriteIcon("beer");
//            addFavoriteIcon("coffee");
//            addFavoriteIcon("question");
//            addFavoriteIcon("logo");
//            addFavoriteIcon("rocket");
//        }

        refreshIcons();
    }

    static void clear() {
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.commit();
        setTeach(teach);
        iniValues();
    }

//    static void addFavoriteIcon(String s) {
//        if (favorite_icons.size() >= 6) return;
//        favorite_icons.add(s);
//
//        SharedPreferences.Editor editor = preferences.edit();
//        switch (favorite_icons.size()) {
//            case 1:
//                editor.putString(FAVORITE0_KEY, s);
//                break;
//            case 2:
//                editor.putString(FAVORITE1_KEY, s);
//                break;
//            case 3:
//                editor.putString(FAVORITE2_KEY, s);
//                break;
//            case 4:
//                editor.putString(FAVORITE3_KEY, s);
//                break;
//            case 5:
//                editor.putString(FAVORITE4_KEY, s);
//                break;
//            case 6:
//                editor.putString(FAVORITE5_KEY, s);
//                break;
//        }
//        editor.commit();
//    }

    static void setFacebookId(String id) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(FACEBOOK_ID_KEY, id);
        editor.commit();
        fid = getDataString(FACEBOOK_ID_KEY);
    }

    static void setTwitterId(String id) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(TWITTER_ID_KEY, id);
        editor.commit();
        tid = getDataString(TWITTER_ID_KEY);
    }

    static void setTwitterData(String a, String b) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(Base64.encodeToString(OAuth.OAUTH_TOKEN.getBytes(), Base64.DEFAULT), Base64.encodeToString(a.getBytes(), Base64.DEFAULT));
        editor.putString(Base64.encodeToString(OAuth.OAUTH_TOKEN_SECRET.getBytes(), Base64.DEFAULT), Base64.encodeToString(b.getBytes(), Base64.DEFAULT));
        editor.commit();
    }

    static String getTwitterToken() {
        return new String(Base64.decode(preferences.getString(Base64.encodeToString(OAuth.OAUTH_TOKEN.getBytes(), Base64.DEFAULT), ""), Base64.DEFAULT));
    }

    static String getTwitterTokenSecret() {
        return new String(Base64.decode(preferences.getString(Base64.encodeToString(OAuth.OAUTH_TOKEN_SECRET.getBytes(), Base64.DEFAULT), ""), Base64.DEFAULT));
    }

    static void putCachePath(String link, String data) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("cache_" + link, data);
        editor.commit();
    }

    static String loadCachePath(String link) {
        return getDataString("cache_" + link);
    }

    public static volatile AtomicBoolean refreshing = new AtomicBoolean();

    static void refreshIcons() {
        if (refreshing.get()) return;
        new Thread(new Runnable() {

            @Override
            public void run() {
                refreshing.set(true);
                while (ChillService.chill_service == null) {
                    try {
                        Thread.sleep(200L);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                try {
                    JSONObject obj = Networking.getAvailableIcons();
//                    System.out.println(obj.toString());
//                    System.out.println("Available Icons");
//                    System.out.println(obj.toString());
                    if (!obj.getString("status").equals("success")) return;
                    ChillService.chill_service.available_icons = obj.getJSONArray("response");

                    obj = Networking.getFavouriteIcons();
//                    System.out.println("Favourite Icons");
//                    System.out.println(obj.toString());
                    if (!obj.getString("status").equals("success")) return;
                    JSONArray a = obj.getJSONArray("response");
                    ArrayList<String> ic = new ArrayList<>();
                    ArrayList<String> id = new ArrayList<>();
                    for (int i = 0; i < a.length(); i++) {
                        JSONObject o = a.getJSONObject(i);
                        ic.add(o.getString("name"));
                        id.add(o.getString("id"));
                    }

//                    ConcurrentHashMap<String, Bitmap> sdw = new ConcurrentHashMap<String, Bitmap>();
                    for (int i = 0; i < ChillService.chill_service.available_icons.length(); i++) {
                        JSONObject ob = ChillService.chill_service.available_icons.getJSONObject(i);
                        if (ChillService.chill_service.icons.containsKey(ob.getString("name")))
                            continue;
//                        System.out.println(ob.getString("pack"));
                        if (!ChillService.chill_service.sections.containsKey(ob.getString("pack"))) {
//                            System.out.println(ob.getString("pack"));
                            ChillService.chill_service.sections.put(ob.getString("pack"), new ArrayList<Integer>());
                        }
                        ChillService.chill_service.sections.get(ob.getString("pack")).add(i);
                        Bitmap bm = getImage(ob.getString("name") + "_" + IMAGE_KEY), bm2 = getImage(ob.getString("name") + "_gray_" + IMAGE_KEY), bm3 = getImage(ob.getString("name") + "_yellow_" + IMAGE_KEY);
//                        String pck = getPack(ob.getString("name"));
                        if (bm == null) {
                            URL aURL = new URL(ob.getString("size214"));
                            URLConnection conn = aURL.openConnection();
                            conn.connect();
                            InputStream is = conn.getInputStream();
                            BufferedInputStream bis = new BufferedInputStream(is);
                            bm = BitmapFactory.decodeStream(bis);
                            bis.close();
                            is.close();
                            bm2 = MainActivity.changeColorBitmap(bm, MainActivity.gR, MainActivity.gG, MainActivity.gB);
                            bm3 = MainActivity.changeColorBitmap(bm, MainActivity.yR, MainActivity.yG, MainActivity.yB);
                            setImage(ob.getString("name"), bm);
                            setImage(ob.getString("name") + "_gray", bm2);
                            setImage(ob.getString("name") + "_yellow", bm3);
//                            setPack(ob.getString("name").intern(), ob.getString("pack"));
                        }

                        ChillService.chill_service.icons.put(ob.getString("name").intern(), new Bitmap[]{bm, bm2, bm3});
//                        System.out.println(sdw.containsKey(ob.getString("name")));
                    }

                    ChillService.chill_service.fav = a;
                    ChillService.chill_service.favorite_icons = ic;
                    ChillService.chill_service.favorite_ids = id;

//                    System.out.println("end icons " + ChillService.chil/**/l_service.favorite_icons.size() + " " + ChillService.chill_service.sections.size());

//                    ChillService.chill_service.refreshIconsData();

                    ChillService.chill_service.sendIconsData();
//                    ChillService.chill_service.sendIconsDataPebble();

                } catch (Exception e) {
                    e.printStackTrace();
                    MainActivity.main.viewError(e.getMessage());
                } finally {
                    refreshing.set(false);
                }
            }
        }).start();
    }

    static void setFavoriteIcons(final ArrayList<String> icons, final ArrayList<String> ids) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    JSONObject obj = Networking.setFavouriteIcons(ids);
//                    if (!obj.getString("status").equals("success")) return;
//                    ChillService.chill_service.favorite_icons = icons;
//                    ChillService.chill_service.favorite_ids = ids;
//                    ChillService.chill_service.fav = obj.getJSONArray("response");
//                    System.out.println(obj.getJSONArray("response"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
//                SendActivity.send.refresh();
            }
        }).start();
    }

//    static void loadFavoriteIcons() {
//        String[] ar = {FAVORITE0_KEY, FAVORITE1_KEY, FAVORITE2_KEY, FAVORITE3_KEY, FAVORITE4_KEY, FAVORITE5_KEY};
//        for (String w : ar) {
//            String s = getDataString(w);
//            if (!"".equals(s))
//                favorite_icons.add(s);
//        }
//    }

    static Bitmap getImage(String d) {
        ByteArrayInputStream bais = null;
        try {
            bais = new ByteArrayInputStream(Base64.decode(getDataString(d), Base64.DEFAULT));
            return BitmapFactory.decodeStream(bais);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (bais != null)
                    bais.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    static void setImage(String d, Bitmap b) {
        ByteArrayOutputStream baos = null;
        try {
            baos = new ByteArrayOutputStream();
            b.compress(Bitmap.CompressFormat.PNG, 100, baos);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(d + "_" + IMAGE_KEY, Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT));
            editor.commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                baos.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    static void setPack(String d, String b) {
        try {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(d + "_pack_" + IMAGE_KEY, b);
            editor.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static String getPack(String d) {
        return getDataString(d + "_pack_" + IMAGE_KEY);
    }

    static void setID(int value) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(UID_KEY, value);
        editor.commit();
        if (ChillService.chill_service != null)
            userId = getDataInt(UID_KEY);
    }

    static void setEmail(String value) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(EMAIL_KEY, value);
        editor.commit();
        email = getDataString(EMAIL_KEY);
    }

    static void setLogin(String value) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(LOGIN_KEY, value);
        editor.commit();
        login = getDataString(LOGIN_KEY);
    }

    static void setToken(String value) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(TOKEN_KEY, value);
        editor.commit();
        token = getDataString(TOKEN_KEY);
    }

    static void setTeach(boolean value) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(TEACH_KEY, value);
        editor.commit();
        teach = getDataBoolean(TEACH_KEY);
    }

    static String getDataString(String key) {
        return preferences.getString(key, "");
    }

    static boolean getDataBoolean(String key) {
        return preferences.getBoolean(key, false);
    }

    static int getDataInt(String key) {
        return preferences.getInt(key, 0);
    }
}
