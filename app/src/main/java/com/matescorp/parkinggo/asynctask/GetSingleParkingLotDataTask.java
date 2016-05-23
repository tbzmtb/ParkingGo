package com.matescorp.parkinggo.asynctask;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.matescorp.parkinggo.util.Config;
import com.matescorp.parkinggo.util.Logger;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by tbzm on 16. 5. 12.
 */
public class GetSingleParkingLotDataTask extends AsyncTask<String, Void, String> {
    private Context mContext;
    private Handler mHandler;
    private final String TAG = getClass().getName();
    private final int WAIT_TIME = 2000;
    private final String POST = "POST";
    private String startIndex = "0";
    private String endIndex = "99";
    private String sensorID;

    public GetSingleParkingLotDataTask(Context context, Handler handler, String sensorID) {
        mContext = context;
        mHandler = handler;
        this.sensorID = sensorID;
    }

    @Override
    protected String doInBackground(String... args) {
        String returnValue = "";
        HttpURLConnection conn = null;
        try {
            String urlString = Config.SERVER_POST_URL + Config.GET_SINGLE_PARKING_HISTORY_PHP;
            Log.e("!!!", "urlString = " + urlString);
            URL url = new URL(urlString);
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoInput(true);
            conn.setDoOutput(false);
            conn.setUseCaches(false);
            conn.setReadTimeout(WAIT_TIME);
            conn.setRequestMethod(POST);
            StringBuffer params = new StringBuffer("");
            params.append(Config.PARAM_START_INDEX + Config.PARAM_EQUALS + startIndex + Config.PARAM_AND + Config.PARAM_END_INDEX + Config.PARAM_EQUALS + endIndex + Config.PARAM_AND + Config.PARAM_SENSOR_ID + Config.PARAM_EQUALS + sensorID);
            PrintWriter output = new PrintWriter(conn.getOutputStream());
            output.print(params.toString());
            output.close();

            StringBuffer sb = new StringBuffer();
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            for (; ; ) {
                String line = br.readLine();
                if (line == null) break;
                sb.append(line + "\n");
            }

            br.close();
            conn.disconnect();
            br = null;
            conn = null;
            returnValue = sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
        return returnValue;
    }

    @Override
    protected void onPostExecute(String result) {
        if (mHandler != null) {
            Message msg = Message.obtain();
            if (result != null) {
                result = result.trim();
            }
            msg.obj = result;
            msg.what = Config.PARKING_SINGLE_LOT_DATA_HANDLER;
            mHandler.sendMessage(msg);
        }
        super.onPostExecute(result);
    }

}


