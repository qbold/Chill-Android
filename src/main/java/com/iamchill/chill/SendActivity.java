package com.iamchill.chill;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.SaveCallback;

import junit.runner.Version;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class SendActivity extends Activity {

    private static JSONObject u;
    private static String login, id_contact;

    public static SendActivity send;

    public static boolean more, is_app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        send = this;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_send);
        ((TextView) findViewById(R.id.title_send)).setText(login);
        refresh();
//        ImageView vd = (ImageView) findViewById(R.id.send_loc);
//        vd.setImageBitmap(MainActivity.changeColorBitmap(BitmapFactory.decodeResource(MainActivity.main.getResources(), ), MainActivity.gR, MainActivity.gG, MainActivity.gB));
        if (MainActivity.send > 1) {
            finish();
        }
//        MainActivity.main.clearLayout();
        ((TextView) findViewById(R.id.title_send)).setTypeface(MainActivity.font);
        ((Button) findViewById(R.id.textView)).setTypeface(MainActivity.font);
        ((TextView) findViewById(R.id.hashTagCount)).setTypeface(MainActivity.font);
        ((TextView) findViewById(R.id.hashT)).setTypeface(MainActivity.font);

        final EditText hs = ((EditText) findViewById(R.id.hashTag));
        hs.setTypeface(MainActivity.font);
        hs.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        hs.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
//                if (s == null) return;
//                String str = s.toString();
//                if (str.length() > 10) {
//                    hs.setText(str.substring(0, 9));
//                }
                ((TextView) findViewById(R.id.hashTagCount)).setText((10 - hs.length()) + "");
            }
        });
    }

    public void refresh() {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                if (ChillService.chill_service.favorite_icons.size() > 0)
                    ((Button) findViewById(R.id.send1)).setBackground(new BitmapDrawable(getResources(), MainActivity.getIconResource(ChillService.chill_service.favorite_icons.get(0))));
                if (ChillService.chill_service.favorite_icons.size() > 1)
                    ((Button) findViewById(R.id.send2)).setBackground(new BitmapDrawable(getResources(), MainActivity.getIconResource(ChillService.chill_service.favorite_icons.get(1))));
                if (ChillService.chill_service.favorite_icons.size() > 2)
                    ((Button) findViewById(R.id.send3)).setBackground(new BitmapDrawable(getResources(), MainActivity.getIconResource(ChillService.chill_service.favorite_icons.get(2))));
                if (ChillService.chill_service.favorite_icons.size() > 3)
                    ((Button) findViewById(R.id.send4)).setBackground(new BitmapDrawable(getResources(), MainActivity.getIconResource(ChillService.chill_service.favorite_icons.get(3))));
                if (ChillService.chill_service.favorite_icons.size() > 4)
                    ((Button) findViewById(R.id.send5)).setBackground(new BitmapDrawable(getResources(), MainActivity.getIconResource(ChillService.chill_service.favorite_icons.get(4))));
                if (ChillService.chill_service.favorite_icons.size() > 5)
                    ((Button) findViewById(R.id.send6)).setBackground(new BitmapDrawable(getResources(), MainActivity.getIconResource(ChillService.chill_service.favorite_icons.get(5))));
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        send = this;
        if (more) {
            more = false;
            finish();
        }
    }

    public static void setData(JSONObject user) {
        try {
            u = user;
            login = u.getString("login");
            id_contact = u.getString("id_contact");
            is_app = u.getBoolean("is_app");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void setData(String id_contact) {
        JSONObject us = null;
        try {
            for (String s : ChillService.chill_service.map.keySet()) {
                JSONObject obj = ChillService.chill_service.map.get(s);
                if (obj.getString("id_contact").equals(id_contact)) {
                    us = obj;
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        setData(us);
    }

    public void moreClick(View v) {
        Intent nf = new Intent(this, MoreActivity.class)
                .putExtra("id_contact", id_contact).putExtra("login", login).putExtra("is_app", is_app);
        startActivity(nf);
    }

    public void sendIcon0(View v) {
        final String hash = ((TextView) findViewById(R.id.hashTag)).getText().toString();
//        final ProgressDialog prog1 = new ProgressDialog(SendActivity.this);
//        prog1.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//        prog1.setMessage("wait please");
//        prog1.setIndeterminate(true); // выдать значек ожидания
//        prog1.setCancelable(true);
//        prog1.show();
        MainActivity.main.animateSend(login);
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    JSONObject object = Networking.sendMessage(
                            "icon",
                            ChillService.chill_service.favorite_icons.get(0), id_contact, hash, !is_app);
                    if (object.getString("status").equals("success")) {
                        MainActivity.main.viewError("Message has been sent");
                    } else
                        MainActivity.main.viewError("Error sending message");
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            MainActivity.main.dismissSent(login);
                        }
                    });
//                    runOnUiThread(new Runnable() {
//
//                        @Override
//                        public void run() {
//                            try {
////                                prog1.dismiss();
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    });
                }
            }
        }).start();
//        while (prog1.isShowing()) ;
        finish();
    }

    public void sendIcon1(View v) {
        final String hash = ((TextView) findViewById(R.id.hashTag)).getText().toString();
//        final ProgressDialog prog1 = new ProgressDialog(SendActivity.this);
//        prog1.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//        prog1.setMessage("wait please");
//        prog1.setIndeterminate(true); // выдать значек ожидания
//        prog1.setCancelable(true);
//        prog1.show();
        MainActivity.main.animateSend(login);
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    JSONObject object = Networking.sendMessage(
                            "icon",
                            ChillService.chill_service.favorite_icons.get(1), id_contact, hash, !is_app);
                    if (object.getString("status").equals("success")) {
                        MainActivity.main.viewError("Message has been sent");
                    } else
                        MainActivity.main.viewError("Error sending message");
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            MainActivity.main.dismissSent(login);
                        }
                    });
//                    runOnUiThread(new Runnable() {
//
//                        @Override
//                        public void run() {
//                            try {
//                                prog1.dismiss();
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    });
                }
            }
        }).start();
//        while (prog1.isShowing()) ;
        finish();
    }

    public void sendIcon2(View v) {
        final String hash = ((TextView) findViewById(R.id.hashTag)).getText().toString();
//        final ProgressDialog prog1 = new ProgressDialog(SendActivity.this);
//        prog1.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//        prog1.setMessage("wait please");
//        prog1.setIndeterminate(true); // выдать значек ожидания
//        prog1.setCancelable(true);
//        prog1.show();
        MainActivity.main.animateSend(login);
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    JSONObject object = Networking.sendMessage(
                            "icon",
                            ChillService.chill_service.favorite_icons.get(2), id_contact, hash, !is_app);
                    if (object.getString("status").equals("success")) {
                        MainActivity.main.viewError("Message has been sent");
                    } else
                        MainActivity.main.viewError("Error sending message");
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            MainActivity.main.dismissSent(login);
                        }
                    });
//                    runOnUiThread(new Runnable() {
//
//                        @Override
//                        public void run() {
//                            try {
//                                prog1.dismiss();
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    });
                }
            }
        }).start();
//        while (prog1.isShowing()) ;
        finish();
    }

    public void sendIcon3(View v) {
        final String hash = ((TextView) findViewById(R.id.hashTag)).getText().toString();
//        final ProgressDialog prog1 = new ProgressDialog(SendActivity.this);
//        prog1.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//        prog1.setMessage("wait please");
//        prog1.setIndeterminate(true); // выдать значек ожидания
//        prog1.setCancelable(true);
//        prog1.show();
        MainActivity.main.animateSend(login);
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    JSONObject object = Networking.sendMessage(
                            "icon",
                            ChillService.chill_service.favorite_icons.get(3), id_contact, hash, !is_app);
                    if (object.getString("status").equals("success")) {
                        MainActivity.main.viewError("Message has been sent");
                    } else
                        MainActivity.main.viewError("Error sending message");
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            MainActivity.main.dismissSent(login);
                        }
                    });
//                    runOnUiThread(new Runnable() {
//
//                        @Override
//                        public void run() {
//                            try {
//                                prog1.dismiss();
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    });
                }
            }
        }).start();
//        while (prog1.isShowing()) ;
        finish();
    }

    public void sendIcon4(View v) {
        final String hash = ((TextView) findViewById(R.id.hashTag)).getText().toString();
//        final ProgressDialog prog1 = new ProgressDialog(SendActivity.this);
//        prog1.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//        prog1.setMessage("wait please");
//        prog1.setIndeterminate(true); // выдать значек ожидания
//        prog1.setCancelable(true);
//        prog1.show();
        MainActivity.main.animateSend(login);
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    JSONObject object = Networking.sendMessage(
                            "icon",
                            ChillService.chill_service.favorite_icons.get(4), id_contact, hash, !is_app);
                    if (object.getString("status").equals("success")) {
                        MainActivity.main.viewError("Message has been sent");
                    } else
                        MainActivity.main.viewError("Error sending message");
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            MainActivity.main.dismissSent(login);
                        }
                    });
//                    runOnUiThread(new Runnable() {
//
//                        @Override
//                        public void run() {
//                            try {
//                                prog1.dismiss();
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    });
                }
            }
        }).start();
//        while (prog1.isShowing()) ;
        finish();
    }

    public void sendIcon5(View v) {
        final String hash = ((TextView) findViewById(R.id.hashTag)).getText().toString();
//        final ProgressDialog prog1 = new ProgressDialog(SendActivity.this);
//        prog1.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//        prog1.setMessage("wait please");
//        prog1.setIndeterminate(true); // выдать значек ожидания
//        prog1.setCancelable(true);
//        prog1.show();
        MainActivity.main.animateSend(login);
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    JSONObject object = Networking.sendMessage(
                            "icon",
                            ChillService.chill_service.favorite_icons.get(5), id_contact, hash, !is_app);
                    if (object.getString("status").equals("success")) {
                        MainActivity.main.viewError("Message has been sent");
                    } else
                        MainActivity.main.viewError("Error sending message");
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            MainActivity.main.dismissSent(login);
                        }
                    });
//                    runOnUiThread(new Runnable() {
//
//                        @Override
//                        public void run() {
//                            try {
//                                prog1.dismiss();
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    });
                }
            }
        }).start();
//        while (prog1.isShowing()) ;
        finish();
    }

    @Override
    public void onPause() {
        super.onPause();
        System.out.println("pause");
//        send = null;
//        MainActivity.send--;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        System.out.println("destroy");
        send = null;
//        send = null;
//        MainActivity.send--;
    }

    @Override
    public void onStop() {
        super.onStop();
        System.out.println("stop");
//        send = null;
        if (MainActivity.swipe != null)
            MainActivity.swipe.clearSwipe();
        MainActivity.send--;
    }

    static final int REQUEST_IMAGE_CAPTURE = 1;

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent intent) {
        if (requestCode == REQUEST_IMAGE_CAPTURE) {
            if (resultCode == RESULT_OK) {
                if (intent == null) {
                    System.out.println("Intent is null");
                } else {
//                    System.out.println("Photo uri: " + intent.getData());
//                    System.out.println("Path: " + paths);
//                    System.out.println(paths);
                    final String hash = ((TextView) findViewById(R.id.hashTag)).getText().toString();
//                    final ProgressDialog prog1 = new ProgressDialog(SendActivity.this);
//                    prog1.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//                    prog1.setMessage("wait please");
//                    prog1.setIndeterminate(true); // выдать значек ожидания
//                    prog1.setCancelable(true);
//                    prog1.show();
                    MainActivity.main.animateSend(login);
                    new Thread(new Runnable() {

                        @Override
                        public void run() {
                            try {
//                                System.out.println(paths);
                                ParseFile f = new ParseFile(new File(paths));
                                f.save();

                                ParseObject object1 = new ParseObject("PhotoObject");
                                object1.put("image", f);
                                object1.save();

                                JSONObject object = Networking.sendMessage(
                                        "parse",
                                        f.getUrl(), id_contact, hash, !is_app);
                                if (object.getString("status").equals("success")) {
                                    MainActivity.main.viewError("Message has been sent");
                                } else
                                    MainActivity.main.viewError("Error sending message");
                            } catch (Exception exc) {
                                exc.printStackTrace();
                                MainActivity.main.viewError("Error: " + exc.getMessage());
                            } finally {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        MainActivity.main.dismissSent(login);
                                    }
                                });
//                                runOnUiThread(new Runnable() {
//
//                                    @Override
//                                    public void run() {
//                                        try {
//                                            prog1.dismiss();
//                                        } catch (Exception e) {
//                                            e.printStackTrace();
//                                        }
//                                    }
//                                });
                            }
                        }
                    }).start();
//                    while (prog1.isShowing()) ;
                    finish();
//                    Bundle bndl = intent.getExtras();
//                    if (bndl != null) {
//                        Object obj = intent.getExtras().get("data");
//                        if (obj instanceof Bitmap) {
//                            Bitmap bitmap = (Bitmap) obj;
//                            System.out.println("bitmap " + bitmap.getWidth() + " x "
//                                    + bitmap.getHeight());
//                            //   ivPhoto.setImageBitmap(bitmap);
//                        }
//                    }
                }
            } else if (resultCode == RESULT_CANCELED) {
                System.out.println("Canceled");
            }
        }

    }

    private static File directory;
    private static String paths;

    private Uri generateFileUri(String s) {
        File file = new File(s);
        return Uri.fromFile(file);
    }

    private void createDirectory() {
        directory = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                "ChillPictures");
        if (!directory.exists())
            directory.mkdirs();
    }

    public void setFavorite(View v) {
        Intent intent = new Intent(this, FavouriteActivity.class);
        startActivity(intent);
    }

    public void sendPhoto(View v) {
        try {
            createDirectory();
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            paths = directory.getPath() + "/" + "pic_"
                    + System.currentTimeMillis() + ".jpg";
            takePictureIntent = takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, generateFileUri(paths));
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            System.out.println("After started");
        } catch (Exception ex) {
            ex.printStackTrace();
            MainActivity.main.viewError("Error: " + ex.getMessage());
        }
//        System.out.println(mCurrentPhotoPath);
    }

    public void sendLocation(View v) {
//        final ProgressDialog prog1 = new ProgressDialog(SendActivity.this);
//        prog1.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//        prog1.setMessage("wait please");
//        prog1.setIndeterminate(true); // выдать значек ожидания
//        prog1.setCancelable(true);
//        prog1.show();
        MainActivity.main.animateSend(login);
        MainActivity.main.loadLocation();
        new Thread(new Runnable() {

            @Override
            public void run() {
                long s = System.currentTimeMillis();
                while (!MainActivity.get_location) {
                    if (System.currentTimeMillis() - s > 5000) {
                        MainActivity.main.viewError("Please enable location determination.");
                        return;
                    }
                }
                try {
                    JSONObject object = Networking.sendMessage(
                            "location",
                            MainActivity.x + ", " + MainActivity.y, id_contact, "", !is_app);
                    // MainActivity.main.viewError(MainActivity.x + ", " + MainActivity.y);
                    if (object.getString("status").equals("success")) {
                        MainActivity.main.viewError("Message has been sent");
                    } else
                        MainActivity.main.viewError("Error sending message");
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            MainActivity.main.dismissSent(login);
                        }
                    });
//                    runOnUiThread(new Runnable() {
//
//                        @Override
//                        public void run() {
//                            try {
//                                prog1.dismiss();
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    });
                }
            }
        }).start();
//        while (prog1.isShowing()) ;
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.send, menu);
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
