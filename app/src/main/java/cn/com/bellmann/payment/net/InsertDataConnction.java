package cn.com.bellmann.payment.net;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class InsertDataConnction {

	public InsertDataConnction(String url, String user_token, String back_url,
			String paramInfo, String userId, final InsertDataCallback callback) {

		Map<String, String> map = new HashMap<String, String>();
		Log.i("json", "InsertDataConnction: paramInfo:"+paramInfo);
		map.put("json", paramInfo);
		new NetConnction(url, HttpMethod.POST, map, new NetConnction.ConnCallback() {

			@Override
			public void onSuccess(String result) {
				Log.e("InsertDataConnction", result);
				try {
					JSONObject jsonObject = new JSONObject(result);
					int code = jsonObject.getInt("code");
					switch (code) {
					case 200:
						if (callback != null) {
							callback.onSuccess();

						}
						break;

					case 400:
						if (callback != null) {
							callback.onFail(code,jsonObject.getString("message"));

						}
						break;

					case 500:
						if (callback != null) {
							callback.onFail(code,jsonObject.getString("message"));
						}
						break;

					}
				} catch (JSONException e) {
					if (callback != null) {
						callback.onFail(2,"jons解析异常");

					}
					
				}
			}

			@Override
			public void onFail() {
				if (callback != null) {
					callback.onFail(3,"网络异常");

				}
			}
		});

	}

	public interface InsertDataCallback {
		void onSuccess();

		void onFail(int errorCode, String errorMsg);
	}

}
