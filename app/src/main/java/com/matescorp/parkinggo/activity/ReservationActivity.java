package com.matescorp.parkinggo.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextClock;
import android.widget.TextView;

import com.matescorp.parkinggo.R;
import com.matescorp.parkinggo.util.DataPreference;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by sjkim on 16. 5. 23.
 */
public class ReservationActivity extends Activity implements View.OnClickListener {
    private ImageButton mBtnBack;
    private Date today = new Date();
    private TextView today_book;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_book_list);
        DataPreference.PREF = PreferenceManager.getDefaultSharedPreferences(this);
        mBtnBack = (ImageButton) findViewById(R.id.btn_back);
        mBtnBack.setOnClickListener(this);
        today_book = (TextView) findViewById(R.id.today_book);
        SimpleDateFormat simple = new SimpleDateFormat("yyyy-MM-dd");
        String date = simple.format(today);
        today_book.setText(date);
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
                finish();
                break;

        }
    }

}
