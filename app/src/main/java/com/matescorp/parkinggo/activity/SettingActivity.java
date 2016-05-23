package com.matescorp.parkinggo.activity;

import android.app.Activity;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.Window;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Switch;

import com.matescorp.parkinggo.R;
import com.matescorp.parkinggo.util.DataPreference;

/**
 * Created by tbzm on 16. 5. 11.
 */
public class SettingActivity extends Activity implements View.OnClickListener {

    private final String TAG = getClass().getName();
    private ImageButton mBtnBack;
    private Switch mSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_setting);
        DataPreference.PREF = PreferenceManager.getDefaultSharedPreferences(this);
        mBtnBack = (ImageButton) findViewById(R.id.btn_back);
        mBtnBack.setOnClickListener(this);
        mSwitch = (Switch) findViewById(R.id.push_switch);
        mSwitch.setOnCheckedChangeListener(onCheckChange);
    }

    CompoundButton.OnCheckedChangeListener onCheckChange = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            DataPreference.setPush(isChecked);

        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        boolean check = DataPreference.getPush();
        if (mSwitch != null) {
            mSwitch.setChecked(check);
        }
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
