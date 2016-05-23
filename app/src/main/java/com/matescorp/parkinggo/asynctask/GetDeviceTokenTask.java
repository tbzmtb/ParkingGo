package com.matescorp.parkinggo.asynctask;

import android.os.AsyncTask;

import com.matescorp.parkinggo.util.Config;
import com.matescorp.parkinggo.util.DataPreference;
import com.matescorp.parkinggo.util.Logger;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by tbzm on 16. 5. 11.
 */
public class GetDeviceTokenTask extends AsyncTask<String, Void, String> {
    private final String TAG = getClass().getName();

    @Override
    protected String doInBackground(String... args) {
        String returnValue = null;
        try {
            String urlString = Config.SERVER_POST_URL + Config.GET_TOKEN_PHP;
            Logger.e("!!!", "urlString = " + urlString);
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoInput(true);
            conn.setDoOutput(false);
            conn.setUseCaches(false);
            conn.setReadTimeout(20000);
            conn.setRequestMethod("POST");
            StringBuffer params = new StringBuffer("");
            params.append(Config.PARAM_LOGIN_ID + Config.PARAM_EQUALS + DataPreference.getLoginId());
            PrintWriter output = new PrintWriter(conn.getOutputStream());
            output.print(params.toString());
            output.close();

            // Response받기
            StringBuffer sb = new StringBuffer();
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            for (; ; ) {
                String line = br.readLine();
                if (line == null) break;
                sb.append(line + "\n");
            }

            br.close();
            conn.disconnect();

            returnValue = sb.toString();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return returnValue;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        Logger.i(TAG, "token = " + s.trim());
    }
}
