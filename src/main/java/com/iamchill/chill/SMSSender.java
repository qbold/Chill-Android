package com.iamchill.chill;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONObject;

public class SMSSender extends Activity {

    private EditText t, n;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_smssender);
        ((TextView) findViewById(R.id.textView15)).setTypeface(MainActivity.font);
        ((TextView) findViewById(R.id.textView16)).setTypeface(MainActivity.font);
        ((TextView) findViewById(R.id.textView17)).setTypeface(MainActivity.font);
        ((Button) findViewById(R.id.textView18)).setTypeface(MainActivity.font);

        String number = getIntent().getStringExtra("number");
        String name = getIntent().getStringExtra("name");

        ((TextView) findViewById(R.id.textView15)).setText(name);

        t = ((EditText) findViewById(R.id.editText));
        t.setTypeface(MainActivity.font);
        n = ((EditText) findViewById(R.id.editText2));
        n.setTypeface(MainActivity.font);
        n.setText(number);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject obj = Networking.createPromocode();
                    if (obj.getString("status").equals("success")) {
                        final String link = obj.getJSONObject("response").getString("link"), promo = obj.getJSONObject("response").getString("code");
                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                t.setText("Check out Chill - a textless/voiceless communication app I love!\n" + link + "\nPromocode: " + promo);
                            }
                        });
                    } else MainActivity.main.viewError("Error loading promocode");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void sendClick(View v) {
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(n.getText().toString(), null, t.getText().toString(), null, null);
            MainActivity.main.viewError("Invite message has been sent!");
            finish();
        } catch (Exception e) {
            MainActivity.main.viewError("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

}
