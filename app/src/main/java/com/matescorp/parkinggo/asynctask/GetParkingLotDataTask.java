package com.matescorp.parkinggo.asynctask;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.matescorp.parkinggo.util.Config;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by tbzm on 16. 4. 25.
 */
public class GetParkingLotDataTask extends AsyncTask<String, Void, String> {
    private Context mContext;
    private Handler mHandler;
    private String mFileName;
    private final String TAG = getClass().getName();

    public GetParkingLotDataTask(Context context, Handler handler, String fileName) {
        mContext = context;
        mHandler = handler;
        mFileName = fileName;
    }

    @Override
    protected String doInBackground(String... args) {
        return loadAssetTextAsString(mContext, mFileName);
    }

    private String loadAssetTextAsString(Context context, String name) {
        BufferedReader in = null;
        try {
            StringBuilder buf = new StringBuilder();
            InputStream is = context.openFileInput(name);
            in = new BufferedReader(new InputStreamReader(is));

            String str;
            boolean isFirst = true;
            while ((str = in.readLine()) != null) {
                if (isFirst)
                    isFirst = false;
                else
                    buf.append('\n');
                buf.append(str);
            }
            return buf.toString();
        } catch (IOException e) {
            Log.e(TAG, "Error opening asset " + name);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    Log.e(TAG, "Error closing asset " + name);
                }
            }
        }

        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        if (mHandler != null) {
            Message msg = Message.obtain();
            msg.what = Config.PARKING_LOT_DATA_HANDLER;
            msg.obj = result;
            mHandler.sendMessage(msg);
        }
        super.onPostExecute(result);
    }
}
