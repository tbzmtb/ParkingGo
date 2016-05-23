package com.matescorp.parkinggo.activity;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ViewFlipper;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.Volley;
import com.matescorp.parkinggo.R;
import com.matescorp.parkinggo.adapter.ParkingViewPagerAdapter;
import com.matescorp.parkinggo.asynctask.DeleteDeviceTokenTask;
import com.matescorp.parkinggo.asynctask.GetFilePathTask;
import com.matescorp.parkinggo.asynctask.GetParkingLotDataTask;
import com.matescorp.parkinggo.asynctask.GetServerDataTask;
import com.matescorp.parkinggo.asynctask.GetTotalParkingLotDataTask;
import com.matescorp.parkinggo.data.ParkingFloorInfoData;
import com.matescorp.parkinggo.data.ParkingLotInfoData;
import com.matescorp.parkinggo.service.DataService;
import com.matescorp.parkinggo.service.IDataService;
import com.matescorp.parkinggo.service.IDataServiceCallback;
import com.matescorp.parkinggo.util.Config;
import com.matescorp.parkinggo.util.DataPreference;
import com.matescorp.parkinggo.util.InputStreamVolleyRequest;
import com.matescorp.parkinggo.util.Logger;
import com.matescorp.parkinggo.view.LotViewFragment;
import com.matescorp.parkinggo.view.LotViewPager;
import com.matescorp.parkinggo.view.SlidingTabLayout;

import org.apache.log4j.chainsaw.Main;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.WeakHashMap;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {
    public static MainActivity INSTANCE;
    private DataHandler mDataHandler;
    private IDataService mService;
    private final String TAG = getClass().getName();
    public static ArrayList<ParkingFloorInfoData> mFloorInfoDataList = new ArrayList<>();
    private ArrayList<String> mFloorTitles = new ArrayList<>();
    private SlidingTabLayout slidingTabLayout;
    private LotViewPager mViewPager;
    private ViewFlipper mViewFlipper;
    private ParkingViewPagerAdapter mPagerAdapter;
    private ImageView mBtnTotalHistory;
    private final int LOT_DISPLAY = 0;
    private final int HISTORY_DISPLAY = 1;
    private IntentFilter mFilter;
    public WeakHashMap<Integer, Fragment> mFragments = new WeakHashMap<>();
    private boolean IS_FINISH_MAP_PARSING = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Logger.d(TAG, "MainActivity onCreate call");
        INSTANCE = this;
        DataPreference.PREF = PreferenceManager.getDefaultSharedPreferences(this);

        if (DataPreference.getLoginId() == null) {
            Intent i = new Intent(MainActivity.this, IntroActivity.class);
            startActivity(i);
            finish();
            return;

        }

        mDataHandler = new DataHandler();
        mFilter = new IntentFilter(Config.GCM_SEND_KEY);
        setContentView(R.layout.activity_main);
        mViewFlipper = (ViewFlipper) findViewById(R.id.view_flipper);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        mBtnTotalHistory = (ImageButton) findViewById(R.id.btn_history);
        mBtnTotalHistory.setOnClickListener(this);
        setSupportActionBar(toolbar);

        mViewPager = (LotViewPager) findViewById(R.id.viewpager);
        mViewPager.setSwipeable(false);
        slidingTabLayout = (SlidingTabLayout) findViewById(R.id.sliding_tabs);
        mPagerAdapter = new ParkingViewPagerAdapter(getSupportFragmentManager(), mFloorTitles, mFloorInfoDataList) {


            @Override
            public void updateFragmentItem(int position, Fragment fragment) {
                Logger.d("!!!", "updateFragmentItem position =" + position);
                ((LotViewFragment) fragment).notifyDataChange2Adapter(mFloorInfoDataList.get(position));
            }
        };

        mViewPager.setAdapter(mPagerAdapter);
        slidingTabLayout.setViewPager(mViewPager);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                Log.d(TAG, "onPageSelected call " + position);
                mPagerAdapter.notifyDataSetChanged();

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        slidingTabLayout.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return Color.WHITE;
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, 0, 0);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        new GetFilePathTask(INSTANCE, mDataHandler).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        int currentItem = mViewFlipper.getDisplayedChild();
        super.onConfigurationChanged(newConfig);
        setDisplayChildView(currentItem);
    }

    ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            if (service != null) {
                mService = IDataService.Stub.asInterface(service);
                try {
                    mService.registerCallback(mCallbcak);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

            if (mService != null) {
                try {
                    mService.unregisterCallback(mCallbcak);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    public class DataHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == Config.PARKING_SEVER_DATA_HANDLER) {
                JSONArray json;
                String result = msg.obj.toString();
                result = result.trim();
                Log.i(TAG, "result = " + result);
                if (result.trim().equals("") || result.trim().equals("[]") || result.trim().contains("null")) {
                    Log.i(TAG, "not data in server");
                } else {
                    try {
                        json = new JSONArray(result);
                        if (json.length() > 0) {
                            for (int i = 0; i < json.length(); i++) {
                                ParkingLotInfoData data = getDataFromServerSensorId(json.getJSONObject(i).getString(Config.SENSORID_KEY));
                                if (data == null) {
                                    continue;
                                }
                                data.setParkingDate(json.getJSONObject(i).getString(Config.DATE_KEY));
                                data.setParkingState(json.getJSONObject(i).getString(Config.STATE_KEY));
                                data.setBattery(json.getJSONObject(i).getString(Config.BATTERY_KEY));
                                data.setGwid(json.getJSONObject(i).getString(Config.GWID_KEY));
                                data.setServerParkingName(json.getJSONObject(i).getString(Config.NAME_KEY));
                                data.setAddress(json.getJSONObject(i).getString(Config.ADDRESS_KEY));
                                data.setCode(json.getJSONObject(i).getString(Config.CODE_KEY));
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }

                    pagerAdapterDataChangeNotify();
                }
            } else if (msg.what == Config.PARKING_LOT_DATA_HANDLER) {
                String result = msg.obj.toString();
                Log.i(TAG, "result = " + result);
                mFloorInfoDataList.clear();
                JSONObject json;
                try {
                    json = new JSONObject(result);
                    JSONArray array = json.getJSONArray(Config.FLOOR_LIST);
                    for (int i = 0; i < array.length(); i++) {
                        ArrayList<ParkingLotInfoData> parkingLotInfoDataList = new ArrayList<>();
                        ParkingFloorInfoData data = new ParkingFloorInfoData();
                        JSONObject floorObject = array.getJSONObject(i);
                        Log.i(TAG, "floorObject = " + floorObject.toString());
                        data.setParkingName(json.getString(Config.PARKING_NAME_KEY));
                        data.setFloorName(json.getString(Config.FLOOR_COUNT_KEY));
                        int maxPositionX = floorObject.getInt(Config.MAX_POSITION_X_KEY);
                        int maxPositionY = floorObject.getInt(Config.MAX_POSITION_Y_KEY);
                        data.setMaxPositionX(floorObject.getString(Config.MAX_POSITION_X_KEY));
                        data.setMaxPositionY(floorObject.getString(Config.MAX_POSITION_Y_KEY));
                        data.setFloorName(floorObject.getString(Config.FLOOR_NAME_KEY));
                        JSONArray dataArray = floorObject.getJSONArray(Config.FLOOR_DATA);
                        for (int j = 0; j < maxPositionX * maxPositionY; j++) {
                            ParkingLotInfoData lotInfoData = new ParkingLotInfoData();
                            lotInfoData.setFloorName(null);
                            lotInfoData.setPositionX(null);
                            lotInfoData.setPositionY(null);
                            lotInfoData.setParkingState(null);
                            lotInfoData.setLotName(null);
                            parkingLotInfoDataList.add(lotInfoData);
                        }
                        for (int j = 0; j < dataArray.length(); j++) {
                            ParkingLotInfoData lotInfoData = new ParkingLotInfoData();
                            JSONObject lotJson = dataArray.getJSONObject(j);
                            int position = getPositionFromXY(maxPositionX, lotJson.getString(Config.POSITION_X_KEY), lotJson.getString(Config.POSITION_Y_KEY));
                            lotInfoData.setFloorName(floorObject.getString(Config.FLOOR_NAME_KEY));
                            lotInfoData.setPositionX(lotJson.getString(Config.POSITION_X_KEY));
                            lotInfoData.setPositionY(lotJson.getString(Config.POSITION_Y_KEY));
                            if (!lotJson.isNull(Config.SENSOR_ID)) {
                                lotInfoData.setSensorId(lotJson.getString(Config.SENSOR_ID));
                            }
                            lotInfoData.setParkingState(lotJson.getString(Config.PARKING_STATE_KEY));
                            lotInfoData.setLotName(lotJson.getString(Config.LOT_NAME_KEY));
                            parkingLotInfoDataList.add(position, lotInfoData);
                        }
                        data.setLotDataObject(parkingLotInfoDataList);
                        mFloorInfoDataList.add(data);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                setParkingFloor();
                IS_FINISH_MAP_PARSING = true;
//                new GetServerDataTask(INSTANCE, mDataHandler).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, Config.GET_SENSOR_DATA_PHP);
            } else if (msg.what == Config.PARKING_GET_MAP_PATH_HANDLER) {
                String result = msg.obj.toString().trim();
                Log.i(TAG, "result = " + result);
                result = result.replace("\\/", "");
                String mFileUrl = null;
                String mGwidx = null;
                JSONArray json;
                try {
                    json = new JSONArray(result);
                    for (int i = 0; i < json.length(); i++) {
                        Logger.d(TAG, "json =" + json);
                        JSONObject data = json.getJSONObject(i);
                        mFileUrl = data.getString(Config.PARAM_FILE_PATH);
                        mGwidx = data.getString(Config.PARAM_GWIDX);


                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Logger.d(TAG, "mFileUrl = " + mFileUrl + " , mGwidx == " + mGwidx);
                if (mFileUrl == null || mFileUrl.equals("")) {
                    return;
                }

                DataPreference.setGwidx(mGwidx);

                InputStreamVolleyRequest request = new InputStreamVolleyRequest(Request.Method.GET, mFileUrl,
                        new Response.Listener<byte[]>() {
                            @Override
                            public void onResponse(byte[] response) {
                                try {
                                    if (response != null) {

                                        FileOutputStream outputStream;
                                        String fileName = DataPreference.getLoginId() + ".json";
                                        outputStream = openFileOutput(fileName, Context.MODE_PRIVATE);
                                        outputStream.write(response);
                                        outputStream.close();
                                        Log.d(TAG, "Download complete.");
                                        new GetParkingLotDataTask(INSTANCE, mDataHandler, fileName).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null);

                                    }
                                } catch (Exception e) {
                                    Log.d("KEY_ERROR", "UNABLE TO DOWNLOAD FILE");
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                }, null);
                RequestQueue mRequestQueue = Volley.newRequestQueue(getApplicationContext(), new HurlStack());
                mRequestQueue.add(request);
            }
        }
    }

    public ParkingLotInfoData getDataFromServerSensorId(String serverSensorId) {
        if (serverSensorId == null || mFloorInfoDataList == null) {
            return null;
        }

        for (int i = 0; i < mFloorInfoDataList.size(); i++) {
            ArrayList<ParkingLotInfoData> lotInfoData = mFloorInfoDataList.get(i).getLotDataObject();

            for (int j = 0; j < lotInfoData.size(); j++) {
                String sensorId = lotInfoData.get(j).getSensorId();
                if (sensorId != null) {
                    if (sensorId.equals(serverSensorId)) {
                        return lotInfoData.get(j);
                    }
                }
            }
        }
        return null;
    }

    private int getPositionFromXY(int maxX, String positionX, String positionY) {
        int posX = Integer.parseInt(positionX);
        int posY = Integer.parseInt(positionY);

        return maxX * posY + posX;
    }

    private void setParkingFloor() {
        if (mFloorInfoDataList == null) {
            Log.i(TAG, "mFloorInfoDataList = null");
            return;
        }
        mFloorTitles.clear();
        for (int i = 0; i < mFloorInfoDataList.size(); i++) {
            Log.i(TAG, "mFloorInfoDataList.get(i).getFloorName() == " + mFloorInfoDataList.get(i).getFloorName());

            mFloorTitles.add(mFloorInfoDataList.get(i).getFloorName());
        }
        slidingTabLayout.setViewPager(mViewPager);
        pagerAdapterDataChangeNotify();
    }

    public synchronized void pagerAdapterDataChangeNotify() {
        Log.i(TAG, "pagerAdapterDataChangeNotify call");
        if (mPagerAdapter != null) {
            mPagerAdapter.notifyDataSetChanged();
        }
    }

    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (getDisplayChildView() == HISTORY_DISPLAY) {
            setDisplayChildView(LOT_DISPLAY);
            return;
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
//        if (id == R.id.action_settings) {
//            return true;
//        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_reservation) {
            Intent intent = new Intent(MainActivity.this, ReservationActivity.class);
            startActivity(intent);
        } else if (id == R.id.menu_first_residents_parking_management) {

        } else if (id == R.id.menu_setting) {
            Intent intent = new Intent(MainActivity.this, SettingActivity.class);
            startActivity(intent);
        } else if (id == R.id.menu_logout) {
            new DeleteDeviceTokenTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            DataPreference.setLoginId(null);
            DataPreference.setPassWord(null);
            Intent intent = new Intent(MainActivity.this, IntroActivity.class);
            startActivity(intent);
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        unbindService(mConnection);
        unregisterReceiver(mReceiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        bindService(new Intent(this,
                DataService.class), mConnection, Context.BIND_AUTO_CREATE);
        registerReceiver(mReceiver, mFilter);
        startIntervalHandler();
    }

    private Runnable mGetServerData = new Runnable() {
        @Override
        public void run() {
            Logger.d(TAG, "IS_FINISH_MAP_PARSING = " + IS_FINISH_MAP_PARSING);
            if (IS_FINISH_MAP_PARSING) {
                new GetServerDataTask(INSTANCE, mDataHandler).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, Config.GET_SENSOR_DATA_PHP);
                stopIntervalHandler();
            } else {
                startIntervalHandler();
            }
        }
    };
    private Handler mIntervalHandler;

    private void startIntervalHandler() {
        mIntervalHandler = new Handler();
        mIntervalHandler.postDelayed(mGetServerData, 200);
    }

    private void stopIntervalHandler() {
        if (mIntervalHandler != null) {
            mIntervalHandler.removeCallbacks(mGetServerData);
        }
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Config.GCM_SEND_KEY)) {
                new GetServerDataTask(INSTANCE, mDataHandler).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, Config.GET_SENSOR_DATA_PHP);
            }
        }
    };

    IDataServiceCallback mCallbcak = new IDataServiceCallback.Stub() {

        @Override
        public void valueChanged(long value) throws RemoteException {
            Log.i("BHC_TEST", "Activity Callback value : " + value);
        }
    };

    private void setDisplayChildView(int index) {
        if (mViewFlipper.getChildAt(index) != mViewFlipper.getCurrentView()) {
            mViewFlipper.setDisplayedChild(index);
        }
    }

    private int getDisplayChildView() {
        return mViewFlipper.getDisplayedChild();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_history:
//                setDisplayChildView(HISTORY_DISPLAY);
                Intent intent = new Intent(MainActivity.this, TotalHistoryActivity.class);
                intent.putParcelableArrayListExtra(Config.LOT_TOTAL_DATA_INTENT_KEY, mFloorInfoDataList);
                startActivity(intent);
                break;

        }
    }


}
