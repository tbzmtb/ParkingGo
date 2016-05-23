package com.matescorp.parkinggo.util;

import android.content.Context;
import android.graphics.Point;
import android.util.TypedValue;
import android.view.Display;
import android.view.WindowManager;

import java.util.Calendar;

/**
 * Created by tbzm on 16. 4. 18.
 */
public class Config {
    public static final String SERVER_URL = "http://www.matescorp.com/ps/";
    public static final String SERVER_POST_URL = "http://www.matescorp.com/ps/manager/";

    public static final String LOGIN_PHP = "manager_login_post.php";
    public static final String GET_TOKEN_PHP = "manager_get_push_token_post.php";
    public static final String DELETE_TOKEN_PHP = "manager_delete_push_token_post.php";
    public static final String GET_SENSOR_DATA_PHP = "getSensorData_post_kr.php";
    public static final String GET_TOTAL_PARKING_HISTORY_PHP = "manager_total_parking_history_post.php";
    public static final String GET_SINGLE_PARKING_HISTORY_PHP = "manager_single_parking_history_post.php";
    public static final String GET_MAP_FILE_PATH_PHP = "manager_get_map_file_path_post.php";

    public static final String PARAM_LOGIN_ID = "login_id";
    public static final String PARAM_LOGIN_PWD = "login_pwd";
    public static final String PARAM_START_INDEX = "sindex";
    public static final String PARAM_END_INDEX = "eindex";
    public static final String PARAM_EQUALS = "=";
    public static final String PARAM_AND = "&";
    public static final String PARAM_SENSOR_ID = "sensorid";
    public static final String PARAM_GWIDX = "gwidx";
    public static final String PARAM_GWID = "gwid";
    public static final String PARAM_FILE_PATH = "file_path";

    public static final String SENSORID_KEY = "sensorid";
    public static final String DATE_KEY = "date";
    public static final String STATE_KEY = "state";
    public static final String BATTERY_KEY = "battery";
    public static final String GWID_KEY = "gwid";
    public static final String GWIDX_KEY = "gwidx";
    public static final String NAME_KEY = "name";
    public static final String ADDRESS_KEY = "address";
    public static final String CODE_KEY = "code";

    public static final String PARKING_NAME_KEY = "parking_name";
    public static final String MAX_POSITION_X_KEY = "max_position_x";
    public static final String MAX_POSITION_Y_KEY = "max_position_y";
    public static final String FLOOR_COUNT_KEY = "floor_count";
    public static final String FLOOR_NAME_KEY = "floor_name";
    public static final String FLOOR_LIST = "floor_list";
    public static final String FLOOR_DATA = "data";

    public static final String POSITION_X_KEY = "position_x";
    public static final String POSITION_Y_KEY = "position_y";
    public static final String SENSOR_ID = "sensorid";
    public static final String RESERVATION_KEY = "reservation";
    public static final String PARKING_STATE_KEY = "parking_state";
    public static final String LOT_NAME_KEY = "lot_name";

    public static final int PARKING_DATA = 1000;
    public static final int PARKING_LOT_DATA_HANDLER = PARKING_DATA * 0x01;
    public static final int PARKING_SEVER_DATA_HANDLER = PARKING_DATA * 0x02;
    public static final int PARKING_GET_MAP_PATH_HANDLER = PARKING_DATA * 0x03;
    public static final int PARKING_TOTAL_LOT_DATA_HANDLER = PARKING_DATA * 0x04;
    public static final int PARKING_SINGLE_LOT_DATA_HANDLER = PARKING_DATA * 0x05;
    public static final int PARKING_SINGLE_LOT_TIMER_HANDLER = PARKING_DATA * 0x06;
    private static int sScreenWidthDP = -1;
    private static int sScreenWidth = -1;
    private static int sScreenHeight = -1;

    public static final String PREF_LOGIN_KEY = "login_id_key";
    public static final String PREF_PASS_WORDK_KEY = "pass_wordk_key";
    public static final String PREF_GWIDX_KEY = "gwidx_key";
    public static final String PREF_PUSH_NOTIFICATION_KEY = "push_notification_key";

    public static final String PARKING_STATE_PARKED = "50";
    public static final String PARKING_STATE_BOOKED = "55";
    public static final String PARKING_STATE_OFF = "0";
    public static final String PARKING_STATE_AVAILABLE_PARKING = "45";
    public static final String PARKING_STATE_CAR_OUTING = "1";

    public static final String LOT_DATA_INTENT_KEY = "LOT_DATA_INTENT_KEY";
    public static final String LOT_TOTAL_DATA_INTENT_KEY = "LOT_TOTAL_DATA_INTENT_KEY";

    public static final String GCM_SEND_KEY = "com.matescorp.parkinggo.gcmsend";

    public static int getScreenHeight(Context context) {
        if (sScreenHeight == -1) {
            Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            sScreenHeight = size.y;
        }
        return sScreenHeight;
    }

    public static int getScreenHeightInDp(Context context) {
        if (sScreenHeight == -1) {
            Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            sScreenHeight = size.y;
        }
        return pixelsToDp(context, sScreenHeight);
    }

    public static int getScreenWidthInDp(Context context) {
        if (sScreenWidthDP == -1) {
            Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            sScreenWidthDP = pixelsToDp(context, size.x);
        }
        return sScreenWidthDP;
    }

    public static int getScreenWidth(Context context) {
        if (sScreenWidth == -1) {
            Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            sScreenWidth = size.x;
        }

        return sScreenWidth;
    }

    public static float dpToPixels(Context context, int dp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }

    public static int pixelsToDp(Context context, float pixels) {
        float density = context.getResources().getDisplayMetrics().densityDpi;
        return Math.round(pixels / (density / 160f));
    }


    public static String milliSecond2Time(String milliSecond) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(Long.parseLong(milliSecond));
        int hr = c.get(Calendar.HOUR_OF_DAY);
        int min = c.get(Calendar.MINUTE);
        String s_hr = String.valueOf(hr);
        String s_min = String.valueOf(min);
        if (hr < 10) {
            s_hr = "0" + hr;
        }
        if (min < 10) {
            s_min = "0" + min;
        }
        return s_hr + ":" + s_min;
    }

    public static String milliSecond2HeaderTime(String milliSecond) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(Long.parseLong(milliSecond));
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH)+1;
        int day = c.get(Calendar.DAY_OF_MONTH);
        String s_year = String.valueOf(year);
        String s_month = String.valueOf(month);
        String s_day = String.valueOf(day);
        if (month < 10) {
            s_month = "0" + month;
        }
        return s_year + "/" + s_month+"/"+s_day;
    }


    public static String milliSecond2Time(long time) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(time);
        int hr = c.get(Calendar.HOUR_OF_DAY);
        int min = c.get(Calendar.MINUTE);
        String s_hr = String.valueOf(hr);
        String s_min = String.valueOf(min);
        if (hr < 10) {
            s_hr = "0" + hr;
        }
        if (min < 10) {
            s_min = "0" + min;
        }
        return s_hr + ":" + s_min;
    }
}
