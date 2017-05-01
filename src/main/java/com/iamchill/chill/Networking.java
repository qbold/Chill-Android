package com.iamchill.chill;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Environment;

import com.parse.ParseInstallation;
import com.parse.ParsePush;
import com.parse.ParseQuery;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.X509TrustManager;

/**
 * Created by Qbold on 04.11.2015.
 */
public class Networking {

    private static final String host = "api.iamchill.co/v";

    public static boolean error = false;

//    static {
//        HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
//            @Override
//            public boolean verify(String hostname, SSLSession session) {
//                return true;
//            }
//        });
//        SSLContext context = null;
//        try {
//            context = SSLContext.getInstance("TLS");
//            context.init(null, new X509TrustManager[]{new X509TrustManager() {
//                @Override
//                public void checkClientTrusted(final X509Certificate[] chain,
//                                               final String authType) throws CertificateException {
//                }
//
//                @Override
//                public void checkServerTrusted(final X509Certificate[] chain,
//                                               final String authType) throws CertificateException {
//                }
//
//                @Override
//                public X509Certificate[] getAcceptedIssuers() {
//                    return new X509Certificate[0];
//                }
//            }}, new SecureRandom());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        HttpsURLConnection.setDefaultSSLSocketFactory(context.getSocketFactory());
//    }

    public static JSONObject runQuery(String h, String method, final HashMap<String, String> data, int version) {
        return runQuery("http://" + host + version, h, method, data);
    }

    public static JSONObject runQuery(String host2, String h, String method, final HashMap<String, String> data) {
        HttpURLConnection con = null;
        StringBuilder buf = new StringBuilder();
        try {
            error = false;
//            System.out.println("trying to connect");
            URL url = new URL(host2 + h);
            con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod(method);
            con.addRequestProperty("X-API-KEY", "c0728616ee7a8aff908aa495531a3011c69fd811");
            if (DataKeeper.token != null && !DataKeeper.token.equals("")) {
                con.addRequestProperty("X-API-TOKEN", DataKeeper.token);
            }
            con.setDoInput(true);

            if (data != null) {
                con.setDoOutput(true);
                OutputStream os = con.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(getPostDataString(data));
                writer.flush();
                writer.close();
                os.close();
            }

            con.connect();

            int code = con.getResponseCode();
//            System.out.println(code + " " + host2 + h);
//            if (h.contains("messages")) {
//                MainActivity.main.viewError("code. " + code);
//            }

//            System.out.println("code: " + code);

            BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String line = null;
            while ((line = reader.readLine()) != null) {
                buf.append(line + "\n");
            }
            reader.close();
//            Thread.sleep(1000L);
            String sw = buf.toString();
            if (sw.equals("")) {
                sw = "{}";
            }
//            if (h.contains("messages")) {
//                MainActivity.main.viewError("host: " + host2);
//                MainActivity.main.viewError("param: " + h);
//                MainActivity.main.viewError("input: " + sw);
//            }
//            System.out.println("host: " + host2);
//            System.out.println("param: " + h);
//            System.out.println("input: " + sw);
            return new JSONObject(sw);
        } catch (Exception exc) {
//            StringWriter sw = new StringWriter();
//            PrintWriter pw = new PrintWriter(sw);
//            exc.printStackTrace(pw);
//            MainActivity.main.viewError(sw.toString());

//            try {
//                File directory = new File(Environment
//                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
//                        "ChillLogs");
//                if (!directory.exists())
//                    directory.mkdirs();
//                String paths = directory.getPath() + "/log4.txt";
//                File f = new File(paths);
//                if (!f.exists()) f.createNewFile();
//                PrintStream s = new PrintStream(f);
//                exc.printStackTrace(s);
//                s.close();
//                ByteArrayOutputStream baos = new ByteArrayOutputStream();
//                s = new PrintStream(baos);
//                exc.printStackTrace(s);
//                s.close();
//                MainActivity.main.viewError(new String(baos.toByteArray()));
//            } catch (Exception e) {
//                e.printStackTrace();
//                MainActivity.main.viewError("Networking2 " + e.getMessage());
//            }
            exc.printStackTrace();
//            MainActivity.main.viewError("Networking " + exc.getMessage());
            if (con != null) con.disconnect();
            error = true;
        }
        return null;
    }

    private static String getPostDataString(HashMap<String, String> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }
        String st = result.toString();
//        System.out.println("POST String: " + st);
        return st;
    }

    public static JSONObject checkPromocode(String promo) {
        return runQuery("/promocodes/index/?id_user=" + DataKeeper.userId + "&promocode=" + promo, "GET", null, 2);
    }

    public static JSONObject createPromocode() {
        HashMap<String, String> qu = new HashMap<String, String>();
        qu.put("id_user", DataKeeper.userId + "");
        return runQuery("/promocodes/index/?id_user=" + DataKeeper.userId, "POST", qu, 2);
    }

    public static JSONObject searchUser(String name) {
        return runQuery("/search/index/?id_user=" + DataKeeper.userId + "&name=" + name + "&type_search=0", "GET", null, 3);
    }

    public static JSONObject searchApp(String name) {
        return runQuery("/search/index/?id_user=" + DataKeeper.userId + "&name=" + name + "&type_search=1", "GET", null, 3);
    }

    public static JSONObject getUserData(int user_id) {
        return runQuery("/users/index/?id_user=" + user_id, "GET", null, 2);
    }

    public static JSONObject getMessages(String user_id) {
        return runQuery("/messages/index/id_user/" + DataKeeper.userId + "/id_contact/" + user_id, "GET", null, 2);
    }

    public static JSONObject updateUserData(String email, String password, String facebook_id, String twitter_id) {
        HashMap<String, String> qu = new HashMap<String, String>();
        qu.put("id_user", DataKeeper.userId + "");
        if (email.equals("")) email = "empty";
        qu.put("email", email);
        if (!password.equals(""))
            qu.put("password", password);
        if (!facebook_id.equals(""))
            qu.put("id_facebook", facebook_id);
        if (!twitter_id.equals(""))
            qu.put("id_twitter", twitter_id);
        return runQuery("/users/update/", "POST", qu, 2);
    }

    public static JSONObject sendMessage(
            String type,
            String content, String userid, String hash, boolean is_user) {
//        if (hash.equals("")) hash = " ";
        HashMap<String, String> qu = new HashMap<String, String>();
        qu.put("id_user", DataKeeper.userId + "");
        qu.put("content", content);
        qu.put("type", type);
        qu.put("id_contact", userid);
        qu.put("text", hash);
        qu.put("type_message", (is_user ? 0 : 1) + "");

//        MainActivity.main.viewError("sendMessage " + content + " " + type + " " + userid);

        try {
//            if (DataKeeper.login.equals(""))
//                SettingsActivity.updateDataNet();
//            while (DataKeeper.login.equals("")) ;
//
//            String msg = DataKeeper.login + ": " + getEmoji(type, content);
//            if (!hash.equals("")) msg += " #" + hash;
//
//            ParseQuery q = ParseInstallation.getQuery();
//            System.out.println(qu);

            JSONObject js = runQuery("/notifications/index/", "POST", qu, 3);

//            ArrayList<String> sw = new ArrayList<>();
//            JSONArray aw = js.getJSONObject("response").getJSONArray("channels");
//            for (int i = 0; i < aw.length(); i++) {
//                sw.add(aw.getString(i));
//            }
//            q.whereContainsAll("channels", sw);
//            JSONObject obj = new JSONObject();
//            obj.put("alert", msg);
//            obj.put("fromUserId", DataKeeper.userId + "");
//            obj.put("sound", "default");
//            obj.put("type", "Location");
//
//            ParsePush iOSPush = new ParsePush();
//            iOSPush.setMessage(js.getJSONObject("response").getJSONObject("data").getString("alert"));
//            iOSPush.setQuery(q);
//            iOSPush.setData(js.getJSONObject("response").getJSONObject("data"));
//            iOSPush.sendInBackground();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        JSONObject js = runQuery("/messages/index/", "POST", qu, 3);
        return js;
    }

    public static JSONObject getFacebookDataUser(String id_fb) {
        return runQuery("/social/facebook/?id_user=" + DataKeeper.userId + "&id_contacts_facebook=" + id_fb, "GET", null, 3);
    }

    public static JSONObject getTwitterDataUser(String id_fb) {
        return runQuery("/social/twitter/?id_user=" + DataKeeper.userId + "&id_contacts_twitter=" + id_fb, "GET", null, 3);
    }

    public static JSONObject getContacts() {
        return runQuery("/contacts/index/id_user/" + DataKeeper.userId, "GET", null, 3);
    }

    public static JSONObject removeUser(String id, boolean is_user) {
        HashMap<String, String> qu = new HashMap<String, String>();
        qu.put("id_user", DataKeeper.userId + "");
        qu.put("id_contact", id);
        qu.put("type_contact", (is_user ? 0 : 1) + "");
        return runQuery("/contacts/delete/", "POST", qu, 3);
    }

    public static JSONObject addUser(String id, boolean is_user) {
        HashMap<String, String> qu = new HashMap<String, String>();
        qu.put("id_user", DataKeeper.userId + "");
        qu.put("id_contact", id);
        qu.put("type_contact", (is_user ? 0 : 1) + "");

        return runQuery("/contacts/index/", "POST", qu, 3);
    }

    public static JSONObject getAvailableIcons() {
        return runQuery("/icons/index/?name_pack=main&id_user=" + DataKeeper.userId, "GET", null, 3);
    }

    public static JSONObject getFavouriteIcons() {
        return runQuery("/icons/user/?id_user=" + DataKeeper.userId, "GET", null, 2);
    }

    public static JSONObject setFavouriteIcons(ArrayList<String> ar) {
        HashMap<String, String> qu = new HashMap<String, String>();
        qu.put("id_user", DataKeeper.userId + "");
        String us = "";
        for (int i = 0; i < ar.size() - 1; i++) {
            us += ar.get(i) + "-";
        }
        us += ar.get(ar.size() - 1);
        qu.put("id_icons_user", us);
        return runQuery("/icons", "POST", qu, 2);
    }

    public static JSONObject authUser(String login, String password) {
        HashMap<String, String> qu = new HashMap<String, String>();
        qu.put("login", login);
        qu.put("password", password);
        return runQuery("/users/index/", "POST", qu, 2);
    }

//    private static String getEmoji(String type, String nm) {
//        if (type.equals("icon")) {
////            try {
////                for (int i = 0; i < DataKeeper.available_icons.length(); i++) {
////                    JSONObject obj = DataKeeper.available_icons.getJSONObject(i);
////                    if (obj.getString("name").equals(nm)) return obj.getString("description");
////                }
////            } catch (Exception e) {
////                e.printStackTrace();
////            }
////            return null;
//            if (nm.equals("ball")) {
//                return "\u26bd\ufe0f";
//            }
//            if (nm.equals("book")) {
//                return "\ud83d\udcd6";
//            }
//            if (nm.equals("beer")) {
//                return "\ud83c\udf7a";
//            }
//            if (nm.equals("clock")) {
//                return "\ud83d\udd52";
//            }
//            if (nm.equals("coffee")) {
//                return "\u2615\ufe0f";
//            }
//            if (nm.equals("controller")) {
//                return "\ud83c\udfae";
//            }
//            if (nm.equals("dollar")) {
//                return "\ud83d\udcb2";
//            }
//            if (nm.equals("heart")) {
//                return "\u2764\ufe0f";
//            }
//            if (nm.equals("logo")) {
//                return "\u270c\ufe0f";
//            }
//            if (nm.equals("minus")) {
//                return "\u002d";
//            }
//            if (nm.equals("pizza")) {
//                return "\ud83c\udf55";
//            }
//            if (nm.equals("plus")) {
//                return "\u002b";
//            }
//            if (nm.equals("question")) {
//                return "\u2754";
//            }
//            if (nm.equals("rocket")) {
//                return "\ud83d\ude80";
//            }
//            if (nm.equals("sleep")) {
//                return "\ud83d\udca4";
//            }
//            if (nm.equals("telephone")) {
//                return "\ud83d\udcde";
//            }
//            if (nm.equals("trophy")) {
//                return "\ud83c\udfc6";
//            }
//            if (nm.equals("waves")) {
//                return "\ud83d\udcad";
//            }
//        } else if (type.equals("location")) return "\ud83d\udccd";
//        else if (type.equals("parse")) return "\ud83d\udcf7";
//        return "";
//    }
}
