package com.matescorp.parkinggo.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.Window;

import com.matescorp.parkinggo.R;
import com.matescorp.parkinggo.util.DataPreference;

/**
 * Created by tbzm on 16. 5. 4.
 */
public class IntroActivity extends Activity {

    private Handler mHandler;
    private int delay = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_intro);
        DataPreference.PREF = PreferenceManager.getDefaultSharedPreferences(this);
        mHandler = new Handler();
        mHandler.postDelayed(mrun, delay);
    }

    Runnable mrun = new Runnable() {
        @Override
        public void run() {
            Intent i;
            if (DataPreference.getLoginId() == null) {
                i = new Intent(IntroActivity.this, LoginActivity.class);
            } else {
                i = new Intent(IntroActivity.this, MainActivity.class);
            }
            startActivity(i);
            finish();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

        }
    };

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mHandler.removeCallbacks(mrun);
    }
}
