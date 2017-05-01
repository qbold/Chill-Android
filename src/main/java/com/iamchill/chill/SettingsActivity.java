package com.iamchill.chill;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONObject;


public class SettingsActivity extends Activity {

    private ProgressDialog dlg;

    public void updateData() {
        dlg = new ProgressDialog(SettingsActivity.this);
        dlg.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dlg.setMessage("wait please");
        dlg.setIndeterminate(true); // выдать значек ожидания
        dlg.setCancelable(true);
        dlg.show();
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    JSONObject d = Networking.getUserData(DataKeeper.userId);
                    JSONObject resp = d.getJSONArray("response").getJSONObject(0);
                    String em = resp.getString("email");
                    DataKeeper.setLogin(resp.getString("login"));
                    DataKeeper.setEmail(em.equals("empty") ? "" : em);
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            ((TextView) findViewById(R.id.nickname_id)).setText(DataKeeper.login);
                            ((EditText) findViewById(R.id.promocode_id)).setText(DataKeeper.email);
                            ((EditText) findViewById(R.id.editPassword_id)).setText("");
                            dlg.dismiss();
                            dlg = null;
                        }
                    });
                } catch (Exception exc) {
                    // MainActivity.main.viewError("Error: " + exc.getMessage());
                    exc.printStackTrace();
                }
            }
        }).start();
    }

    public static void updateDataNet() {
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    JSONObject d = Networking.getUserData(DataKeeper.userId);
                    JSONObject resp = d.getJSONArray("response").getJSONObject(0);
                    String em = resp.getString("email");
                    DataKeeper.setLogin(resp.getString("login"));
                    DataKeeper.setEmail(em.equals("empty") ? "" : em);
                } catch (Exception exc) {
                    // MainActivity.main.viewError("Error: " + exc.getMessage());
                    exc.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        updateData();
        setContentView(R.layout.activity_settings);

        ((TextView) findViewById(R.id.textView10)).setTypeface(MainActivity.font);
        ((TextView) findViewById(R.id.nickname_id)).setTypeface(MainActivity.font_bold);
        ((Button) findViewById(R.id.textView11)).setTypeface(MainActivity.font);
        ((Button) findViewById(R.id.logout_button)).setTypeface(MainActivity.font);
        ((TextView) findViewById(R.id.textView5)).setTypeface(MainActivity.font);
        ((TextView) findViewById(R.id.textView7)).setTypeface(MainActivity.font);
        ((TextView) findViewById(R.id.promocode_id)).setTypeface(MainActivity.font);
        ((TextView) findViewById(R.id.editPassword_id)).setTypeface(MainActivity.font);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.settings, menu);
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

    public void done(View view) {
        // Click done button
        Editable em = ((EditText) findViewById(R.id.promocode_id)).getText(), psw = ((EditText) findViewById(R.id.editPassword_id)).getText();
        final String email_ = em == null ? "" : em.toString(), password_ = psw == null ? "" : psw.toString();
        finish();
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    JSONObject res = Networking.updateUserData(email_, password_, "", "");
                    if (res.getString("status").equals("success")) {
                        DataKeeper.setEmail(email_);
                    } //else MainActivity.main.viewError("Error: " + res.getString("response"));
                } catch (Exception exc) {
                    // MainActivity.main.viewError("Error: " + exc.getMessage());
                    exc.printStackTrace();
                }
            }
        }).start();
    }

    public void logOut(View view) {
        // Click logout button
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    DataKeeper.clear();
                    Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                } catch (Exception exc) {
                    // MainActivity.main.viewError("Error: " + exc.getMessage());
                    exc.printStackTrace();
                }
            }
        }).start();
    }
}
