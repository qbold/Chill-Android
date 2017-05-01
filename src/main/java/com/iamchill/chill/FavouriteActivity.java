package com.iamchill.chill;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
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
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class FavouriteActivity extends Activity {

    public GridView grid;
    public ArrayList<Integer> chd;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_favourite);

        chd = new ArrayList<Integer>();

        ((TextView) findViewById(R.id.textView13)).setTypeface(MainActivity.font);
        ((Button) findViewById(R.id.textView14)).setTypeface(MainActivity.font);

        grid = (GridView) findViewById(R.id.gridView);
        final ImageAdapter ad = new ImageAdapter(this);
        grid.setAdapter(ad);
        //DataKeeper.favorite_icons.contains(DataKeeper.available_icons.getJSONObject(position).getString("name").intern())
        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                if (/*grid.getCheckedItemCount()*/chd.size() > 6) {
//                    grid.setItemChecked(position, false);
//                }
                if (chd.contains(position))
                    chd.remove((Object) position);
                else if (chd.size() < 6)
                    chd.add(position);
                ad.notifyDataSetChanged();
            }
        });
        for (int i = 0; i < ChillService.chill_service.sections.get("main").size(); i++) {
            try {
                if (ChillService.chill_service.favorite_icons.contains(ChillService.chill_service.available_icons.getJSONObject(ChillService.chill_service.sections.get("main").get(i)).getString("name").intern()))
                    chd.add(i);
//                grid.setItemChecked(i, DataKeeper.favorite_icons.contains(DataKeeper.available_icons.getJSONObject(i).getString("name").intern()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    public void doneClick(View v) {
        if (chd.size() < 6) {
            MainActivity.main.viewError("Please choose 6 icons");
            return;
        }
        ArrayList<String> icons = new ArrayList<>(6);
        ArrayList<String> ids = new ArrayList<>(6);
        for (int j = 0; j < chd.size(); j++) {
            int i = chd.get(j);
            try {
                JSONObject obj = ChillService.chill_service.available_icons.getJSONObject(ChillService.chill_service.sections.get("main").get(i));
//                if (grid.isItemChecked(i)) {
                icons.add(obj.getString("name").intern());
                ids.add(obj.getString("id").intern());
//                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        ChillService.chill_service.favorite_icons = icons;
        ChillService.chill_service.favorite_ids = ids;
        StringBuilder str = new StringBuilder();
        str.append("[");
        for (int i = 0; i < 6; i++) {
            str.append("{\"id_user\":\"");
            str.append(DataKeeper.userId);
            str.append("\",\"id_icon\":\"");
            str.append(ChillService.chill_service.favorite_ids.get(i));
            str.append("\"}");
            if (i != 5) str.append(",");
        }
        str.append("]");
        try {
            ChillService.chill_service.fav = new JSONArray(str.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        SendActivity.send.refresh();
        finish();
        DataKeeper.setFavoriteIcons(icons, ids);
    }

    public class ImageAdapter extends BaseAdapter {

        private Context mContext;

        public ImageAdapter(Context c) {
            mContext = c;
        }

        public int getCount() {
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
//                RelativeLayout.LayoutParams parm = new RelativeLayout.LayoutParams(MainActivity.dp2px(70), MainActivity.dp2px(70));
//                parm.setMargins(MainActivity.dp2px(10), MainActivity.dp2px(10), MainActivity.dp2px(10), MainActivity.dp2px(10));
                imageView.setLayoutParams(new GridView.LayoutParams(MainActivity.dp2px(70), MainActivity.dp2px(70)));
//                imageView.setBackground(null);
                imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                imageView.setPadding(MainActivity.dp2px(20), MainActivity.dp2px(20), MainActivity.dp2px(20), MainActivity.dp2px(20));
            } else {
//                rl = (RelativeLayout) convertView;
                imageView = (ImageView) convertView;
//                imageView = (Button) rl.getChildAt(0);
            }
//            imageView.setClickable(false);
//            rl.removeAllViews();
//            rl.addView(imageView);

            try {
                Bitmap bm = null;
                if (chd.contains(position)) {
                    bm = MainActivity.getIconResource(ChillService.chill_service.available_icons.getJSONObject(ChillService.chill_service.sections.get("main").get(position)).getString("name"), 2);
                } else {
                    bm = MainActivity.getIconResource(ChillService.chill_service.available_icons.getJSONObject(ChillService.chill_service.sections.get("main").get(position)).getString("name"), 1);
                }
                imageView.setImageBitmap(bm);
//                imageView.setBackground(new BitmapDrawable(getResources(), bm));
            } catch (Exception e) {
                e.printStackTrace();
            }
            return imageView;
        }
    }
}
