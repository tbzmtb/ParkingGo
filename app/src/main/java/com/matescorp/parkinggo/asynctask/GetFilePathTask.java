package com.matescorp.parkinggo.asynctask;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;

import com.matescorp.parkinggo.R;
import com.matescorp.parkinggo.activity.MainActivity;
import com.matescorp.parkinggo.util.Config;
import com.matescorp.parkinggo.util.DataPreference;
import com.matescorp.parkinggo.util.Logger;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by tbzm on 16. 5. 4.
 */
public class GetFilePathTask extends AsyncTask<String, Void, String> {
    private Context mContext;
    private Handler mHandler;
    private final String TAG = getClass().getName();
    private final String POST = "POST";
    private final int WAIT_TIME = 2000;

    public GetFilePathTask(Context context, Handler handler) {
        mContext = context;
        mHandler = handler;
    }

    @Override
    protected String doInBackground(String... args) {
        String returnValue = "";
        HttpURLConnection conn = null;
        try {
            String urlString = Config.SERVER_POST_URL + Config.GET_MAP_FILE_PATH_PHP;
            Logger.e("!!!", "urlString = " + urlString);
            URL url = new URL(urlString);
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoInput(true);
            conn.setDoOutput(false);
            conn.setUseCaches(false);
            conn.setReadTimeout(WAIT_TIME);
            conn.setRequestMethod(POST);

            StringBuffer params = new StringBuffer("");
            params.append(Config.PARAM_LOGIN_ID + Config.PARAM_EQUALS + DataPreference.getLoginId() + Config.PARAM_AND + Config.PARAM_LOGIN_PWD + Config.PARAM_EQUALS + DataPreference.getPassword());
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
            msg.what = Config.PARKING_GET_MAP_PATH_HANDLER;
            msg.obj = result;
            mHandler.sendMessage(msg);
        }
        super.onPostExecute(result);

    }
}
