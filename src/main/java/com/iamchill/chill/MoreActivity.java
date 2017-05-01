package com.iamchill.chill;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONObject;


public class MoreActivity extends Activity {

    public String id_contact;
    public String login;
    public boolean is_app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_more);
        id_contact = getIntent().getStringExtra("id_contact");
        login = getIntent().getStringExtra("login");
        is_app = getIntent().getBooleanExtra("is_app", false);

        ((TextView) findViewById(R.id.hashTagCount)).setTypeface(MainActivity.font);
        ((TextView) findViewById(R.id.hashT)).setTypeface(MainActivity.font);

        ((TextView) findViewById(R.id.textView3)).setTypeface(MainActivity.font);
        ((TextView) findViewById(R.id.textView9)).setTypeface(MainActivity.font);

        final GridView grid = (GridView) findViewById(R.id.gridView2);
        grid.setAdapter(new ImageAdapter(this));
        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    sendIcon(ChillService.chill_service.available_icons.getJSONObject(ChillService.chill_service.sections.get("main").get(position)).getString("name"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
//        grid.setColumnWidth(MainActivity.dp2px(70));
//        grid.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
//
//            @Override
//            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
//                if (grid.getCheckedItemCount() > 6) {
//                    grid.setItemChecked(position, false);
//                }
//            }
//
//            @Override
//            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
//                return false;
//            }
//
//            @Override
//            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
//                return false;
//            }
//
//            @Override
//            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
//                return false;
//            }
//
//            @Override
//            public void onDestroyActionMode(ActionMode mode) {
//
//            }
//        });

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

    public void sendIcon(final String st) {
        final String hash = ((TextView) findViewById(R.id.hashTag)).getText().toString();
//        final ProgressDialog prog1 = new ProgressDialog(MoreActivity.this);
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
                            st, id_contact, hash, !is_app);
                    if (object.getString("status").equals("success")) {
                        MainActivity.main.viewError("Message has been sent");
                    } else
                        MainActivity.main.viewError("Error sending message");
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    MainActivity.main.dismissSent(login);
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
        SendActivity.more = true;
//        while (prog1.isShowing()) ;
        finish();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_more, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

    public class ImageAdapter extends BaseAdapter {

        private Context mContext;

        public ImageAdapter(Context c) {
            mContext = c;
        }

        public int getCount() {
//            System.out.println(DataKeeper.sections.containsKey("main"));
//            System.out.println(DataKeeper.sections.toString());
            return ChillService.chill_service.sections.get("main").size();
        }

        public Object getItem(int position) {
            return null;
        }

        public long getItemId(int position) {
            return 0;
        }

        // create a new ImageView for each item referenced by the Adapter
        public View getView(int position, View convertView, ViewGroup parent) {
//            RelativeLayout rl = new RelativeLayout(mContext);
            ImageView imageView;
            if (convertView == null) {
                // if it's not recycled, initialize some attributes
                imageView = new ImageView(mContext);
                GridView.LayoutParams parm = new GridView.LayoutParams(MainActivity.dp2px(70), MainActivity.dp2px(70));
//                parm.setMargins(MainActivity.dp2px(10), MainActivity.dp2px(10), MainActivity.dp2px(10), MainActivity.dp2px(10));
                imageView.setLayoutParams(parm);
//                imageView.setBackground(null);
//                imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                imageView.setPadding(MainActivity.dp2px(20), MainActivity.dp2px(20), MainActivity.dp2px(20), MainActivity.dp2px(20));
            } else {
//                rl = (RelativeLayout) convertView;
                imageView = (ImageView) convertView;
//                imageView = (Button) rl.getChildAt(0);
            }
//            rl.removeAllViews();
//            rl.addView(imageView);

            try {
//                imageView.setBackground(new BitmapDrawable(getResources(), MainActivity.getIconResource(ChillService.chill_service.available_icons.getJSONObject(ChillService.chill_service.sections.get("main").get(position)).getString("name"))));
                imageView.setImageBitmap(MainActivity.getIconResource(ChillService.chill_service.available_icons.getJSONObject(ChillService.chill_service.sections.get("main").get(position)).getString("name")));
            } catch (Exception e) {
                e.printStackTrace();
            }
            return imageView;
        }
    }
}
