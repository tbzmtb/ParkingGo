package com.matescorp.parkinggo.asynctask;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.matescorp.parkinggo.util.Config;
import com.matescorp.parkinggo.util.DataPreference;
import com.matescorp.parkinggo.util.Logger;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class GetServerDataTask extends AsyncTask<String, Void, String> {
    private Context mContext;
    private Handler mHandler;
    private final String TAG = getClass().getName();
    private final int WAIT_TIME = 2000;

    public GetServerDataTask(Context context, Handler handler) {
        mContext = context;
        mHandler = handler;
    }

    @Override
    protected String doInBackground(String... args) {
        String returnValue = "";
        HttpURLConnection conn = null;
        try {
            String urlString = Config.SERVER_URL + args[0];
            Log.e("!!!", "urlString = " + urlString);
            URL url = new URL(urlString);

            conn = (HttpURLConnection) url.openConnection();
            conn.setDoInput(true);            // 입력스트림 사용여부
            conn.setDoOutput(false);            // 출력스트림 사용여부
            conn.setUseCaches(false);        // 캐시사용 여부
            conn.setReadTimeout(WAIT_TIME);        // 타임아웃 설정 ms단위
//                conn.setRequestMethod("GET");  // or GET
            conn.setRequestMethod("POST");

            // POST 값 전달 하기
            StringBuffer params = new StringBuffer("");
//                params.append("name=" + URLEncoder.encode(name)); //한글일 경우 URL인코딩
            params.append(Config.PARAM_GWID + Config.PARAM_EQUALS + DataPreference.getGwdix());
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
            msg.obj = result;
            msg.what = Config.PARKING_SEVER_DATA_HANDLER;
            mHandler.sendMessage(msg);
        }
        super.onPostExecute(result);
    }

}
