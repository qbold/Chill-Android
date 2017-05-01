package com.iamchill.chill;

import android.app.Activity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;


public class Tutorial extends Activity {

    private static OnSwipeTouchListener swiper;
    private static int state;
    public final static int TEACH1 = 0, TEACH2 = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_tutorial);
        swiper = new OnSwipeTouchListener() {
            public boolean onSwipeLeft() {
                if (state == TEACH1) {
                    goTeach(TEACH2);
                }
                return true;
            }

            public boolean onSwipeRight() {
                if (state == TEACH2) {
                    goTeach(TEACH1);
                }
                return true;
            }
        };
    }

    public void goToFirstTeach(View v) {
        goTeach(TEACH1);
    }

    public void goToSecondTeach(View v) {
        goTeach(TEACH2);
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

    public void goTeach(int s) {
        state = s;
        if (s == TEACH1) {
            ((Button) findViewById(R.id.plot0)).setBackgroundResource(R.drawable.circlefill);
            ((Button) findViewById(R.id.plot1)).setBackgroundResource(R.drawable.circle);
            ((ImageView) findViewById(R.id.tut)).setImageResource(R.drawable.tutor_android);
        } else if (s == TEACH2) {
            ((Button) findViewById(R.id.plot0)).setBackgroundResource(R.drawable.circle);
            ((Button) findViewById(R.id.plot1)).setBackgroundResource(R.drawable.circlefill);
            ((ImageView) findViewById(R.id.tut)).setImageResource(R.drawable.tutor_android2);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_tutorial, menu);
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
}
