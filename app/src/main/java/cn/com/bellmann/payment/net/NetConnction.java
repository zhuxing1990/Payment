package cn.com.bellmann.payment.net;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;

public class NetConnction {

    public NetConnction(final String url, final HttpMethod method, final Map<String, String> map, final ConnCallback callback) {
        new AsyncTask<Void, Void, String>() {

            @Override
            protected void onPostExecute(String result) {
                if (result!=null){
                    if (callback!=null){
                        callback.onSuccess(result);
                    }
                }else {
                    if (callback!=null){
                        callback.onFail();
                    }

                }


            }

            @Override
            protected String doInBackground(Void... params) {
                StringBuffer sb = new StringBuffer();
                for (Map.Entry me : map.entrySet()) {
                    sb.append(me.getKey());
                    sb.append("=");
                    sb.append(me.getValue());
                    sb.append("&");
                }
                URLConnection conn = null;
                switch (method) {
                    case GET:
                        try {
                            conn = new URL(url + "?" + sb.toString()).openConnection();
                            conn.setReadTimeout(5000);
                            conn.setConnectTimeout(5000);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        break;
                    case POST:
                        try {
                            conn = new URL(url).openConnection();
                            conn.setReadTimeout(5000);
                            conn.setConnectTimeout(5000);

                            //添加post请求的两行属性
                            //conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                            //conn.setRequestProperty("Content-Length", data.length() + "");
                            conn.setDoOutput(true);
                            BufferedWriter bw = new BufferedWriter(
                                    new OutputStreamWriter(conn.getOutputStream(), "utf-8"));
                            bw.write(sb.toString());
                            bw.flush();
                            bw.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        break;
                }
                InputStream is = null;
                BufferedReader br = null;
                try {
                     is = conn.getInputStream();
                     br = new BufferedReader(new InputStreamReader(is));
                    String line;
                    StringBuffer result = new StringBuffer();
                    while ((line =br.readLine())!=null){
                        result.append(line);
                    }
                    is.close();
                    br.close();
                    return result.toString();

                } catch (IOException e) {
                    e.printStackTrace();
                }finally {

                }

                return null;
            }
        }.execute();


    }

    public interface ConnCallback{
        void onSuccess(String result);
        void onFail();
    }


}
