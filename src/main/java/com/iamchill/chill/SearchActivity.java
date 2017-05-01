package com.iamchill.chill;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.hardware.camera2.params.Face;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;

import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.Session;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterAuthToken;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterAuthClient;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookRequestError;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.parse.ParseInstallation;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.twitter.sdk.android.core.internal.TwitterSessionVerifier;
import com.twitter.sdk.android.core.models.User;
import com.twitter.sdk.android.core.models.UserEntities;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import io.fabric.sdk.android.Fabric;


public class SearchActivity extends Activity {

    private volatile AtomicBoolean need_update;
    private volatile AtomicLong old_change;
    private final static long SLEEP_LIVE_SEARCH = 500L;
    private Thread worker;
    private AppAdapter ad;
    private HashMap<String, String> map;
    private LoginButton loginButton;
    private CallbackManager callbackManager;
    private EditText sr;
    private ListView listView;
    private TabHost tab;
    private boolean cnt;
    private ProgressBar pb;

    private HashMap<String, Contact> contacts;
    private boolean fb, adr, tw;

    public SearchActivity() {
        need_update = new AtomicBoolean();
        old_change = new AtomicLong();
        contacts = new HashMap<>();
    }

    void hideKeyboard(View v) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!FacebookSdk.isInitialized())
            FacebookSdk.sdkInitialize(getApplicationContext());
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_search);

        cnt = true;

        map = new HashMap<String, String>();
        ad = new AppAdapter();
        listView = ((ListView) findViewById(R.id.listSearch));
        listView.setAdapter(ad);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(final AdapterView<?> parent, View view, final int position, long id) {
                new Thread(new Runnable() {

                    @Override
                    public void run() {
                        try {
                            final String name = parent.getAdapter().getItem(position).toString();
                            if (adr) {
                                runOnUiThread(new Runnable() {

                                    @Override
                                    public void run() {
                                        Intent intent = new Intent(SearchActivity.this, SMSSender.class);
                                        Contact cnm = contacts.get(name);
                                        intent.putExtra("number", cnm.getNumber());
                                        intent.putExtra("name", name);
                                        startActivity(intent);
                                    }
                                });
                            } else {
                                final View v = listView.getChildAt(position - listView.getFirstVisiblePosition());
                                final AppAdapter.ViewHolder holder = (AppAdapter.ViewHolder) v.getTag();
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        holder.bar.setAlpha(1);
                                        holder.icon.setVisibility(View.INVISIBLE);
                                    }
                                });

                                JSONObject object = Networking.addUser(map.get(name), cnt);
                                if (object != null && object.getString("status").equals("success")) {
                                    //MainActivity.main.reload();
                                    if (DataKeeper.login.equals(""))
                                        SettingsActivity.updateDataNet();
                                    while (DataKeeper.login.equals("")) ;
                                    String msg = DataKeeper.login + " wants to Chill with you!";

                                    HashMap<String, String> qu = new HashMap<String, String>();
                                    qu.put("id_user", DataKeeper.userId + "");
                                    qu.put("content", "");
//                                    qu.put("type", type);
                                    qu.put("id_contact", map.get(name));
                                    qu.put("text", "");

//                                    ParseQuery q = ParseInstallation.getQuery();
                                    JSONObject js = Networking.runQuery("/notifications/contact/", "POST", qu, 2);
//                                    Networking.sendMessage("icon", "logo", map.get(name), "Hi!");
//                                    ArrayList<String> sw = new ArrayList<>();
//                                    JSONArray aw = js.getJSONObject("response").getJSONArray("channels");
//                                    for (int i = 0; i < aw.length(); i++) {
//                                        sw.add(aw.getString(i));
//                                    }
//                                    q.whereContainsAll("channels", sw);

//                                    try {
//                                        ParsePush iOSPush = new ParsePush();
//                                        iOSPush.setMessage(msg);
//                                        iOSPush.setQuery(q);
//                                        iOSPush.setData(js.getJSONObject("response").getJSONObject("data"));
//                                        iOSPush.sendInBackground();
//                                    } catch (Exception ex) {
//                                        ex.printStackTrace();
//                                    }
                                    MainActivity.main.viewError("The user " + name + " has been added to your friends list");
                                } else
                                    MainActivity.main.viewError("Error: " + object.getString("response"));

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        holder.bar.setAlpha(0);
                                        holder.icon.setVisibility(View.VISIBLE);
                                    }
                                });
                            }
                        } catch (Exception exc) {
                            MainActivity.main.viewError("Error: " + exc.getMessage());
                            exc.printStackTrace();
                        }
                    }
                }).start();
            }
        });
        listView.setOnHierarchyChangeListener(new ViewGroup.OnHierarchyChangeListener() {

            @Override
            public void onChildViewAdded(View parent, View child) {
                ((TextView) child.findViewById(R.id.label123)).setTypeface(MainActivity.font);
            }

            @Override
            public void onChildViewRemoved(View parent, View child) {
            }
        });

        sr = (EditText) findViewById(R.id.search_field);
        sr.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(sr);
                }
                findViewById(R.id.twitter_text).setAlpha(hasFocus || ad.getCount() > 0 ? 0 : 1);
                findViewById(R.id.facebook_text).setAlpha(hasFocus || ad.getCount() > 0 ? 0 : 1);
                findViewById(R.id.addressbook_text).setAlpha(hasFocus || ad.getCount() > 0 ? 0 : 1);
                findViewById(R.id.listSearch).setAlpha(hasFocus || ad.getCount() > 0 ? 1 : 0);
                findViewById(R.id.twitter_text).setClickable(sr.isFocused() || ad.getCount() > 0);
                findViewById(R.id.facebook_text).setClickable(sr.isFocused() || ad.getCount() > 0);
                findViewById(R.id.addressbook_text).setClickable(sr.isFocused() || ad.getCount() > 0);
                listView.setClickable(sr.isFocused() || ad.getCount() > 0);
                need_update.set(true);
                old_change.set(System.currentTimeMillis());
            }
        });
        sr.setOnKeyListener(new View.OnKeyListener() {

            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    need_update.set(true);
                    old_change.set(System.currentTimeMillis() - 2000);
                    return true;
                }
                return false;
            }
        });
        sr.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                need_update.set(true);
                old_change.set(System.currentTimeMillis());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        pb = (ProgressBar) findViewById(R.id.pb);
        worker = new Thread(new Runnable() {

            @Override
            public void run() {
                while (true) {
                    try {
                        long s = System.currentTimeMillis();
                        if (need_update.get() && s - old_change.get() - SLEEP_LIVE_SEARCH > 0) {
                            pb.setAlpha(1);
                            // search users and print them
                            final Editable ed = ((EditText) findViewById(R.id.search_field)).getText();
                            if (ed != null) {
                                if (!adr && !fb && !tw) {
                                    final JSONObject object = cnt ? Networking.searchUser(ed.toString()) : Networking.searchApp(ed.toString());
                                    runOnUiThread(new Runnable() {

                                        @Override
                                        public void run() {
                                            try {
                                                ad.clear();
                                                map.clear();
                                                if (object != null && object.getString("status").equals("success")) {
                                                    JSONArray array = object.getJSONArray("response");
                                                    for (int i = 0; i < array.length(); i++) {
                                                        JSONObject object1 = array.getJSONObject(i);
                                                        if (object1.getString("name").trim().equals("(null)")) {
                                                            object1.remove("name");
                                                            object1.put("name", object1.getString("login"));
                                                        }
                                                        ad.add(object1.getString("name"));
                                                        map.put(object1.getString("name"), object1.getString("id"));
                                                    }
                                                } else {
                                                    findViewById(R.id.twitter_text).setAlpha((sr.isFocused() || ad.getCount() > 0) ? 0 : 1);
                                                    findViewById(R.id.facebook_text).setAlpha((sr.isFocused() || ad.getCount() > 0) ? 0 : 1);
                                                    findViewById(R.id.addressbook_text).setAlpha((sr.isFocused() || ad.getCount() > 0) ? 0 : 1);
                                                    findViewById(R.id.listSearch).setAlpha((sr.isFocused() || ad.getCount() > 0) ? 1 : 0);
                                                    findViewById(R.id.twitter_text).setClickable(!(sr.isFocused() || ad.getCount() > 0));
                                                    findViewById(R.id.facebook_text).setClickable(!(sr.isFocused() || ad.getCount() > 0));
                                                    findViewById(R.id.addressbook_text).setClickable(!(sr.isFocused() || ad.getCount() > 0));
                                                    listView.setClickable((sr.isFocused() || ad.getCount() > 0));
                                                }
                                                ad.notifyDataSetInvalidated();
                                            } catch (Exception ec) {
                                                MainActivity.main.viewError("Error: " + ec.getMessage());
                                                ec.printStackTrace();
                                            }
                                        }
                                    });
                                } else {
                                    runOnUiThread(new Runnable() {

                                        @Override
                                        public void run() {
                                            String sa = ed.toString().toLowerCase().trim();
                                            ad.clear();
                                            if (adr) {
                                                for (Contact cd : contacts.values()) {
                                                    if (cd.getName().toLowerCase().trim().indexOf(sa) >= 0) {
                                                        ad.add(cd.getName());
                                                    }
                                                }
                                            } else if (fb) {
                                                for (String nm : map.keySet()) {
                                                    if (nm.toLowerCase().trim().indexOf(sa) >= 0) {
                                                        ad.add(nm);
                                                    }
                                                }
                                            } else if (tw) {
                                                for (String nm : map.keySet()) {
                                                    if (nm.toLowerCase().trim().indexOf(sa) >= 0) {
                                                        ad.add(nm);
                                                    }
                                                }
                                            }
                                            ad.notifyDataSetInvalidated();
                                        }
                                    });
                                }
                            } else
                                runOnUiThread(new Runnable() {

                                    @Override
                                    public void run() {
                                        try {
                                            ad.clear();
                                            ad.notifyDataSetInvalidated();
                                        } catch (Exception ec) {
                                            MainActivity.main.viewError("Error: " + ec.getMessage());
                                            ec.printStackTrace();
                                        }
                                    }
                                });

                            need_update.set(false);
                            old_change.set(s);
                            pb.setAlpha(0);
                        }
                        Thread.sleep(500L);
                    } catch (InterruptedException ex) {
                    } catch (Exception ec) {
                        MainActivity.main.viewError("Error: " + ec.getMessage());
                        ec.printStackTrace();
                    }
                }
            }
        });
        worker.start();
        tab = (TabHost) findViewById(R.id.tabHost2);
        tab.setup();

        TabHost.TabSpec sp1 = tab.newTabSpec("tab1");
        sp1.setIndicator(getResources().getString(R.string.tabcontacts));
        sp1.setContent(R.id.tvTab1);
        tab.addTab(sp1);
        TabHost.TabSpec sp2 = tab.newTabSpec("tab2");
        sp2.setIndicator(getResources().getString(R.string.tabapps));
        sp2.setContent(R.id.tvTab2);
        tab.addTab(sp2);

        TabWidget widg = tab.getTabWidget();
        for (int i = 0; i < widg.getChildCount(); i++)
            widg.getChildAt(i).setBackgroundResource(R.drawable.tabs);

        tab.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                cnt = tabId.equals("tab1");
                ad.clear();
                ad.notifyDataSetInvalidated();
                findViewById(R.id.twitter_text).setClickable(cnt);
                findViewById(R.id.facebook_text).setClickable(cnt);
                findViewById(R.id.addressbook_text).setClickable(cnt);
                findViewById(R.id.twitter_text).setAlpha(cnt ? 1 : 0);
                findViewById(R.id.facebook_text).setAlpha(cnt ? 1 : 0);
                findViewById(R.id.addressbook_text).setAlpha(cnt ? 1 : 0);
                findViewById(R.id.twitter_text).setVisibility(cnt ? View.VISIBLE : View.INVISIBLE);
                findViewById(R.id.facebook_text).setVisibility(cnt ? View.VISIBLE : View.INVISIBLE);
                findViewById(R.id.addressbook_text).setVisibility(cnt ? View.VISIBLE : View.INVISIBLE);
            }
        });
//        ((TextView) findViewById(R.id.adr)).setTypeface(MainActivity.font);
        ((TextView) findViewById(R.id.search_field)).setTypeface(MainActivity.font);
        ((TextView) findViewById(R.id.textView8)).setTypeface(MainActivity.font);
        findViewById(R.id.textView8).setVisibility(View.INVISIBLE);
        findViewById(R.id.shareButton).setVisibility(View.INVISIBLE);

        loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions("read_custom_friendlists");
        loginButton.setReadPermissions("user_friends");
//        loginButton.setClickable(false);
        loginButton.setVisibility(View.INVISIBLE);
        callbackManager = CallbackManager.Factory.create();
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {

            @Override
            public void onSuccess(final LoginResult loginResult) {
                // App code
                loginButton.setVisibility(View.INVISIBLE);
//                loginButton.setClickable(false);
                final ProgressDialog prog1 = new ProgressDialog(SearchActivity.this);
                prog1.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                prog1.setMessage("wait please");
                prog1.setIndeterminate(true); // выдать значек ожидания
                prog1.setCancelable(true);
                prog1.show();
                new Thread(new Runnable() {

                    @Override
                    public void run() {
                        DataKeeper.setFacebookId(loginResult.getAccessToken().getUserId());
                        Networking.updateUserData("", "", loginResult.getAccessToken().getUserId(), "");
                        getListFriendsFacebook();
                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                try {
                                    prog1.dismiss();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                }).start();
            }

            @Override
            public void onCancel() {
                // App code
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
                MainActivity.main.viewError("Error: " + exception.getMessage());
            }
        });

        Intent intent = getIntent();
        if (intent.hasExtra("app")) {
            tab.setCurrentTab(1);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        ad.clear();
        ad.notifyDataSetInvalidated();
        if (worker != null) {
            worker.interrupt();
            worker = null;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ad.clear();
        ad.notifyDataSetInvalidated();
        if (worker != null) {
            worker.interrupt();
            worker = null;
        }
    }

    class AppAdapter extends BaseAdapter {

        private ArrayList<String> arr;

        public AppAdapter() {
            arr = new ArrayList<>();
        }

        public void clear() {
            arr.clear();
        }

        public void add(String s) {
            arr.add(s);
        }

        @Override
        public int getCount() {
            return arr.size();
        }

        @Override
        public String getItem(int position) {
            return arr.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = View.inflate(getApplicationContext(),
                        R.layout.button_search, null);
                new ViewHolder(convertView);
            }
            ViewHolder holder = (ViewHolder) convertView.getTag();
            String item = getItem(position);
            //  holder.iv_icon.setImageDrawable(Drawable.);
            holder.label123.setText(item);
            holder.icon.setVisibility(View.VISIBLE);
            holder.bar.setAlpha(0);
//            if (position < arr.size()) {
//                try {
////                System.out.println(item);
////                System.out.println(ChillService.chill_service.map.get(item));
////                System.out.println("Name: " + ChillService.chill_service.map.get(item).getString("name"));
////                System.out.println("Login: " + ChillService.chill_service.map.get(item).getString("login"));
//                    String twitter = ChillService.chill_service.map.get(item).getString("name");
//                    if (twitter.equals("empty")) twitter = "";
//                    if (twitter.equals(ChillService.chill_service.map.get(item).getString("login")))
//                        twitter = "";
//                    holder.label1234.setText(twitter);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
            return convertView;
        }

        class ViewHolder {
            //            ImageView iv_icon;
            TextView label123;
            ImageView icon;
            ProgressBar bar;
//            TextView label1234;

            public ViewHolder(View view) {
//                iv_icon = (ImageView) view.findViewById(R.id.iv_icon);
                label123 = (TextView) view.findViewById(R.id.label123);
                icon = (ImageView) view.findViewById(R.id.icon);
                bar = (ProgressBar) view.findViewById(R.id.pb_add);
//                label1234 = (TextView) view.findViewById(R.id.label1234);
                view.setTag(this);
            }
        }
    }

    private boolean getListFriendsTwitter(TwitterSession ses) {
        if (ses == null) {
            return false;
        }
        try {
            TwitterFollowers f = new TwitterFollowers(ses);
            f.getCustomService().show(ses.getUserId(), new Callback<FriendsResult>() {

                @Override
                public void success(final Result<FriendsResult> result) {
//                result.response.getHeaders();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
//                                System.out.println("aw3");
//                                StringBuilder tw1 = new StringBuilder();
//                                for (int i = 0; i < rs.length; i++) {
//                                    tw1.append(rs[i]);
//                                    if (i != rs.length - 1) {
//                                        tw1.append("-");
//                                    }
//                                }
//                                JSONObject obj1 = Networking.getTwitterDataUser(tw1.toString());
//                                if (!obj1.getString("status").equals("success")) {
//                                    MainActivity.main.viewError("Error: " + obj1.getJSONArray("response"));
//                                    return;
//                                }
//                                JSONArray objects = obj1.getJSONArray("response");
                                runOnUiThread(new Runnable() {

                                    @Override
                                    public void run() {
                                        sr.setFocusableInTouchMode(true);
                                        sr.setFocusable(true);
                                        sr.setVisibility(View.VISIBLE);
                                        listView.setAlpha(1);
                                    }
                                });
//                                String[] s = new String[objects.length()];
//                                for (int i = 0; i < objects.length(); i++) {
//                                    JSONObject obj = objects.getJSONObject(i);
//                                    s[i] = obj.getString("id");
//                                }
                                final ArrayList<JSONObject> aw = new ArrayList<JSONObject>();
//                                System.out.println("aw4 " + result.data.ids.length);
                                for (long sq : result.data.ids) {
                                    JSONObject kq = Networking.getTwitterDataUser(sq + "");
                                    if (kq.getString("status").equals("success") && kq.getJSONObject("response").getJSONArray("chill").length() > 0)
                                        aw.add(kq);
                                }
                                runOnUiThread(new Runnable() {

                                    @Override
                                    public void run() {
                                        sr.setVisibility(View.VISIBLE);
                                        sr.setFocusable(true);
                                        sr.setFocusableInTouchMode(true);
                                        listView.setAlpha(1);
                                        listView.setClickable(true);
                                        findViewById(R.id.textView8).setVisibility(View.INVISIBLE);
                                        findViewById(R.id.shareButton).setVisibility(View.INVISIBLE);
                                        ad.clear();
                                        map.clear();
                                        if (aw.isEmpty()) {
                                            findViewById(R.id.textView8).setVisibility(View.VISIBLE);
//                                            findViewById(R.id.shareButton).setVisibility(View.VISIBLE);
                                            ((TextView) findViewById(R.id.textView8)).setText(getString(R.string.oops) + " " + getString(R.string.twitter) + " " + getString(R.string.oops2));
                                        }
                                        for (JSONObject fo : aw) {
                                            try {
                                                if (fo.getString("status").equals("success")) {
//                                                    System.out.println(fo.toString());
                                                    JSONArray a = fo.getJSONObject("response").getJSONArray("chill");
                                                    if (a.length() == 0) continue;
                                                    a = a.getJSONArray(0);
                                                    for (int i = 0; i < a.length(); i++) {
                                                        JSONObject object1 = a.getJSONObject(i);
                                                        ad.add(object1.getString("name"));
                                                        map.put(object1.getString("name"), object1.getString("id"));
                                                    }
                                                } else
                                                    MainActivity.main.viewError("Error: " + fo.getString("response"));
                                            } catch (Exception e) {
                                                MainActivity.main.viewError("Error: " + e.getMessage());
                                                e.printStackTrace();
                                            }
                                        }
                                    }
                                });
                            } catch (Exception e) {
                                MainActivity.main.viewError("Error: " + e.getMessage());
//                    fb = false;
//                    System.out.println("Errs " + e.getMessage());
                                e.printStackTrace();
                            }
                        }
                    }).start();
                }

                @Override
                public void failure(TwitterException e) {
//                    System.out.println("aw4");
                    MainActivity.main.viewError("Error: " + e.getMessage());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
//        FacebookRequestError err = null;
//        if ((err = GraphRequest.newMyFriendsRequest(AccessToken.getCurrentAccessToken(), new GraphRequest.GraphJSONArrayCallback() {
//
//            @Override
//            public void onCompleted(final JSONArray objects, GraphResponse response) {
//
//            }
//        }).executeAndWait().getError()) != null) {
////            MainActivity.main.viewError(err.toString() + " ERROR");
//            System.out.println(err.toString() + " ERROR");
//            return false;
//        }
        return true;
    }

    private boolean getListFriendsFacebook() {
//        try {
        FacebookRequestError err = null;
        if ((err = GraphRequest.newMyFriendsRequest(AccessToken.getCurrentAccessToken(), new GraphRequest.GraphJSONArrayCallback() {

            @Override
            public void onCompleted(final JSONArray objects, GraphResponse response) {
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        sr.setFocusableInTouchMode(true);
                        sr.setFocusable(true);
                        sr.setVisibility(View.VISIBLE);
                        listView.setAlpha(1);
                    }
                });
                if (objects == null) return;
                try {
                    String[] s = new String[objects.length()];
                    for (int i = 0; i < objects.length(); i++) {
                        JSONObject obj = objects.getJSONObject(i);
                        s[i] = obj.getString("id");
                    }
                    final ArrayList<JSONObject> aw = new ArrayList<JSONObject>();
                    for (String sq : s) {
                        aw.add(Networking.getFacebookDataUser(sq));
                    }
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            sr.setVisibility(View.VISIBLE);
                            sr.setFocusable(true);
                            sr.setFocusableInTouchMode(true);
                            listView.setAlpha(1);
                            listView.setClickable(true);
                            findViewById(R.id.textView8).setVisibility(View.INVISIBLE);
                            findViewById(R.id.shareButton).setVisibility(View.INVISIBLE);
                            ad.clear();
                            map.clear();
                            if (aw.isEmpty()) {
                                findViewById(R.id.textView8).setVisibility(View.VISIBLE);
                                findViewById(R.id.shareButton).setVisibility(View.VISIBLE);
                                ((TextView) findViewById(R.id.textView8)).setText(getString(R.string.oops) + " " + getString(R.string.facebook) + " " + getString(R.string.oops2));
                            }
                            for (JSONObject fo : aw) {
                                try {
                                    if (fo.getString("status").equals("success")) {
//                                                    System.out.println(fo.toString());
                                        JSONArray a = fo.getJSONObject("response").getJSONArray("chill");
                                        if (a.length() == 0) continue;
                                        a = a.getJSONArray(0);
                                        for (int i = 0; i < a.length(); i++) {
                                            JSONObject object1 = a.getJSONObject(i);
                                            ad.add(object1.getString("name"));
                                            map.put(object1.getString("name"), object1.getString("id"));
                                        }
                                    } else
                                        MainActivity.main.viewError("Error: " + fo.getString("response"));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
                } catch (Exception e) {
//                    fb = false;
//                    System.out.println("Errs " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }).executeAndWait().getError()) != null) {
//            MainActivity.main.viewError(err.toString() + " ERROR");
            return false;
        }
//        } catch (Exception e) {
//            e.printStackTrace();
//            return false;
//        }
        return true;
    }


    @Override
    public void onBackPressed() {
        if (fb || adr || tw) {
            tw = false;
            adr = false;
            fb = false;
            loginButton.setVisibility(View.INVISIBLE);
//            loginButton.setClickable(false);
            findViewById(R.id.twitter_text).setAlpha(1);
            findViewById(R.id.facebook_text).setAlpha(1);
            findViewById(R.id.addressbook_text).setAlpha(1);
            findViewById(R.id.twitter_text).setVisibility(View.VISIBLE);
            findViewById(R.id.facebook_text).setVisibility(View.VISIBLE);
            findViewById(R.id.addressbook_text).setVisibility(View.VISIBLE);
            findViewById(R.id.twitter_text).setClickable(true);
            findViewById(R.id.facebook_text).setClickable(true);
            findViewById(R.id.addressbook_text).setClickable(true);
            findViewById(R.id.textView8).setVisibility(View.INVISIBLE);
            findViewById(R.id.shareButton).setVisibility(View.INVISIBLE);
            sr.setVisibility(View.VISIBLE);
            ad.clear();
            ad.notifyDataSetChanged();
            listView.setClickable(false);
        } else {
            super.onBackPressed();
        }
    }

    public HashMap<String, Contact> getContacts(Context context) {
        HashMap<String, Contact> contacts = new HashMap<>();
        Contact contact;
        Cursor cursor = context.getContentResolver().query(
                ContactsContract.Contacts.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                contact = new Contact();
                final String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
//                contact.setId(id);
                String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                contact.setName(name);
                if (Integer.parseInt(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
//                    Log.i("t", "Contact name=" + name + ", Id=" + id);
                    // get the phone number
                    Cursor pCur = context.getContentResolver().query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{id},
                            null);
                    while (pCur.moveToNext()) {
                        String phone = pCur.getString(
                                pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
//                        String label = HelpUtils.getPhoneLabel(context, pCur.getInt(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE)),
//                                pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.LABEL)));
//                        PhoneNumber phoneNumber = new PhoneNumber();
//                        phoneNumber.setPhoneNumber(phone);
//                        phoneNumber.setType(label);
                        contact.setPhoneNumber(phone);
//                        Log.i("t", "phone=" + phone);
                    }
                    pCur.close();
                    contacts.put(name, contact);
                }
            }
        }
        return contacts;
    }

    private boolean twitterLoginWasCanceled;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (fb)
            callbackManager.onActivityResult(requestCode, resultCode, data);
        else if (tw) {
            final TwitterAuthClient twitterAuthClient = new TwitterAuthClient();
            if (twitterAuthClient.getRequestCode() == requestCode) {
                twitterLoginWasCanceled = (resultCode == RESULT_CANCELED);
                twitterAuthClient.onActivityResult(requestCode, resultCode, data);
            }
        }
    }

    public void twitterClick(View v) {
//        MainActivity.main.viewError("twitter click");
        tw = true;
        findViewById(R.id.twitter_text).setClickable(false);
        findViewById(R.id.facebook_text).setClickable(false);
        findViewById(R.id.addressbook_text).setClickable(false);
        findViewById(R.id.twitter_text).setAlpha(0);
        findViewById(R.id.facebook_text).setAlpha(0);
        findViewById(R.id.addressbook_text).setAlpha(0);
        findViewById(R.id.twitter_text).setVisibility(View.INVISIBLE);
        findViewById(R.id.facebook_text).setVisibility(View.INVISIBLE);
        findViewById(R.id.addressbook_text).setVisibility(View.INVISIBLE);
        final ProgressDialog prog1 = new ProgressDialog(SearchActivity.this);
        prog1.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        prog1.setMessage("wait please");
        prog1.setIndeterminate(true); // выдать значек ожидания
        prog1.setCancelable(true);
        prog1.show();

        if (Twitter.getSessionManager().getActiveSession() != null) {
            try {
                loginButton.setVisibility(View.INVISIBLE);
//                loginButton.setClickable(false);
                final ProgressDialog prog12 = new ProgressDialog(SearchActivity.this);
                prog1.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                prog1.setMessage("wait please");
                prog1.setIndeterminate(true); // выдать значек ожидания
                prog1.setCancelable(true);
                prog1.show();
                new Thread(new Runnable() {

                    @Override
                    public void run() {
                        TwitterSession data = Twitter.getSessionManager().getActiveSession();
                        DataKeeper.setTwitterId(data.getUserId() + "");
                        Networking.updateUserData("", "", "", data.getUserId() + "");
                        getListFriendsTwitter(data);
                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                try {
                                    prog12.dismiss();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                }).start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    TwitterCore.getInstance().logIn(SearchActivity.this, new Callback<TwitterSession>() {

                        @Override
                        public void success(final Result<TwitterSession> result) {
//                System.out.println("sa1");
                            runOnUiThread(new Runnable() {

                                @Override
                                public void run() {
                                    try {
                                        loginButton.setVisibility(View.INVISIBLE);
//                loginButton.setClickable(false);
                                        final ProgressDialog prog1 = new ProgressDialog(SearchActivity.this);
                                        prog1.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                                        prog1.setMessage("wait please");
                                        prog1.setIndeterminate(true); // выдать значек ожидания
                                        prog1.setCancelable(true);
                                        prog1.show();
                                        new Thread(new Runnable() {

                                            @Override
                                            public void run() {
                                                DataKeeper.setTwitterId(result.data.getUserId() + "");
                                                Networking.updateUserData("", "", "", result.data.getUserId() + "");
                                                getListFriendsTwitter(result.data);
                                                runOnUiThread(new Runnable() {

                                                    @Override
                                                    public void run() {
                                                        try {
                                                            prog1.dismiss();
                                                        } catch (Exception e) {
                                                            e.printStackTrace();
                                                        }
                                                    }
                                                });
                                            }
                                        }).start();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        }

                        @Override
                        public void failure(TwitterException exception) {
                            // Do something on failure
//                System.out.println("sa2");
//                System.out.println("Error: " + exception.getMessage());
                            MainActivity.main.viewError("Error: " + exception.getMessage());
                            runOnUiThread(new Runnable() {

                                @Override
                                public void run() {
                                    try {
                                        prog1.dismiss();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        }
                    });
                }
            }).start();
        }

        if (!twitterLoginWasCanceled)
            new Thread(new Runnable() {

                @Override
                public void run() {
                    try {
                        TwitterSession sw = Twitter.getSessionManager().getActiveSession();
                        try {
                            if (sw == null) return;
                            getListFriendsTwitter(sw);
                        } finally {
                            runOnUiThread(new Runnable() {

                                @Override
                                public void run() {
                                    try {
                                        prog1.dismiss();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
    }

    public void addressClick(View vb) {
//        MainActivity.main.viewError("address click");
        adr = true;
        findViewById(R.id.twitter_text).setClickable(false);
        findViewById(R.id.facebook_text).setClickable(false);
        findViewById(R.id.addressbook_text).setClickable(false);
        listView.setAlpha(1);
        listView.setClickable(true);
        findViewById(R.id.twitter_text).setAlpha(0);
        findViewById(R.id.facebook_text).setAlpha(0);
        findViewById(R.id.addressbook_text).setAlpha(0);
        findViewById(R.id.twitter_text).setVisibility(View.INVISIBLE);
        findViewById(R.id.facebook_text).setVisibility(View.INVISIBLE);
        findViewById(R.id.addressbook_text).setVisibility(View.INVISIBLE);
        final ProgressDialog prog1 = new ProgressDialog(SearchActivity.this);
        prog1.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        prog1.setMessage("wait please");
        prog1.setIndeterminate(true); // выдать значек ожидания
        prog1.setCancelable(true);
        prog1.show();
        new Thread(new Runnable() {

            @Override
            public void run() {
                final HashMap<String, Contact> cd = getContacts(SearchActivity.this);
                if (cd.isEmpty()) {
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            findViewById(R.id.textView8).setVisibility(View.VISIBLE);
                            findViewById(R.id.shareButton).setVisibility(View.VISIBLE);
                            try {
                                prog1.dismiss();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } else {
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            for (Contact c : cd.values()) {
                                ad.add(c.getName());
                            }
                            ad.notifyDataSetChanged();
                            try {
                                prog1.dismiss();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
                contacts = cd;
            }
        }).start();
    }

    public void facebookClick(View v) {
//        MainActivity.main.viewError("facebook click");
        fb = true;
        findViewById(R.id.twitter_text).setClickable(false);
        findViewById(R.id.facebook_text).setClickable(false);
        findViewById(R.id.addressbook_text).setClickable(false);
        findViewById(R.id.twitter_text).setAlpha(0);
        findViewById(R.id.facebook_text).setAlpha(0);
        findViewById(R.id.addressbook_text).setAlpha(0);
        findViewById(R.id.twitter_text).setVisibility(View.INVISIBLE);
        findViewById(R.id.facebook_text).setVisibility(View.INVISIBLE);
        findViewById(R.id.addressbook_text).setVisibility(View.INVISIBLE);
        final ProgressDialog prog1 = new ProgressDialog(SearchActivity.this);
        prog1.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        prog1.setMessage("wait please");
        prog1.setIndeterminate(true); // выдать значек ожидания
        prog1.setCancelable(true);
        prog1.show();
        new Thread(new Runnable() {

            @Override
            public void run() {
                if (!getListFriendsFacebook()) {
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            loginButton.setVisibility(View.VISIBLE);
//        loginButton.setClickable(true);
                            sr.setVisibility(View.INVISIBLE);
//                            sr.setFocusable(false);
//                            sr.setFocusableInTouchMode(false);
                            listView.setAlpha(0);
                            listView.setClickable(false);
                            try {
                                prog1.dismiss();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } else {
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            findViewById(R.id.textView8).setVisibility(View.INVISIBLE);
                            findViewById(R.id.shareButton).setVisibility(View.INVISIBLE);
                            try {
                                prog1.dismiss();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }
        }).start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.search, menu);
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

    class Contact {

        private String number, name;

        public Contact() {
        }

        public String getName() {
            return name;
        }

        public String getNumber() {
            return number;
        }

        public void setPhoneNumber(String n) {
            number = n;
        }

        public void setName(String n) {
            name = n;
        }
    }
}
