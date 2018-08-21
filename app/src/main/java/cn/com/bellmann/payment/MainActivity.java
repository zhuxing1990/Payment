package cn.com.bellmann.payment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.webkit.ValueCallback;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONObject;

import cn.com.bellmann.payment.javascriptInterface.JavaScriptObject;
import cn.com.bellmann.payment.modle.StartAppsBean;
import cn.com.bellmann.payment.utils.Utils;

public class MainActivity extends Activity {
	private static final String TAG = "MainActivity";
	private WebView web;
	private String spId;
	private String userId;
	private String userIdType;
	private String userToken;
	private ProgressDialog dialog = null;
	private long exitTime = 0;

	@SuppressLint("SetJavaScriptEnabled")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		Toast.makeText(this, "当前版本："+Utils.getVersionName(getApplicationContext()), Toast.LENGTH_SHORT).show();
		Uri uri = Uri
				.parse("content://com.hunantv.operator.mango.hndxiptv/userinfo");
		Cursor mCursor = this.getContentResolver().query(uri, null, null, null,
				null);
		setContentView(R.layout.activity_main);
		web = (WebView) findViewById(R.id.webordey);
		web.setBackgroundColor(0);
		spId = "99999999";
		userId = "";
		userIdType = "0";
		userToken = "";
		web.getSettings().setJavaScriptEnabled(true);
		web.addJavascriptInterface(new JavaScriptObject(this,web), "AppFunction");
		// 设置android与web的js交互的桥梁
		// 客户端回调 覆盖浏览器
		WebViewClient client = new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				Log.i(TAG, "shouldOverrideUrlLoading: url:"+url);
				view.loadUrl(url);
				return true;
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
				dialog.dismiss();
			}

			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				super.onPageStarted(view, url, favicon);
			}
		};
		if (mCursor != null) {
			while (mCursor.moveToNext()) {
				String name = mCursor.getString(mCursor.getColumnIndex("name"));
				if ("user_id".equals(name)) {
					userId = mCursor.getString(mCursor.getColumnIndex("value"));
//					userId = "ztezte";

				} else if ("user_token".equals(name)) {
					userToken = mCursor.getString(mCursor
							.getColumnIndex("value"));
//					userToken = "00183835484397574853860613181435";
				}
			}
			mCursor.close();
		}
		web.setWebViewClient(client);
		dialog = ProgressDialog.show(this, null, "页面加载中，请稍后..");
		String url = "http://124.232.135.225:8082/AppStoreTV4/service/queryOrderInfo.do?spId="//正式
//		String url = "http://124.232.136.236:8099/AppStoreTV/service/queryOrderInfo.do?spId=" //测试
//		String url = "http://124.232.135.229:8083/AppStoreTV4/service/queryOrderInfo.do?spId=" //测试
				+ spId
				+ "&userId="
				+ userId
				+ "&userIdType="
				+ userIdType
				+ "&userToken=" + userToken;
		Log.e("test-->", url+"-->url");
		web.getSettings().setNeedInitialFocus(false);
		web.reload();
		web.loadUrl(url);
		Log.e("test", url);
		web.getSettings().setBuiltInZoomControls(true);
		web.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
		getAppIntent();
//		PayActivity.startup(this);
	}
	private String LoadUrl;
	public void getAppIntent(){
		Log.i(TAG, "getAppIntent: ");
		Intent intent = getIntent();
		if (intent.hasExtra("startApp")){
			String jsonData = intent.getStringExtra("startApp");
			Log.i(TAG, "getAppIntent: jsonData:"+jsonData);
			try {
				StartAppsBean startAppBean = new Gson().fromJson(jsonData, StartAppsBean.class);
				StartAppsBean.StartAppBean startApp= startAppBean.getStartApp();
				String s = "";
				if (startApp.getJsonData()!=null){
					s = new Gson().toJson(startApp.getJsonData());
				}
				Log.i(TAG, "getAppIntent: jsonData:"+s);
				Utils.StartAPP(startApp.getPackageName(),startApp.getClassName(),s,getApplicationContext());
			}catch (Exception e){
				e.printStackTrace();
			}
		}
		if (intent.hasExtra("LoadUrl")){
			LoadUrl = intent.getStringExtra("LoadUrl");
			Log.i(TAG, "getAppIntent: LoadUrl:"+LoadUrl);
			web.loadUrl(LoadUrl);
		}
		if(intent.hasExtra("mangoJson")){
			String manggoJson = intent.getStringExtra("mangoJson");
			Log.i(TAG, "getAppIntent: get manggoJson:"+manggoJson);
			try {
				if(!TextUtils.isEmpty(manggoJson)){
					JSONObject json = new JSONObject(manggoJson);
					if(json.has("loadURL")){
						LoadUrl = json.getString("loadURL");
//					LoadUrl = "http://124.232.135.225:8082/AppStoreTV4/service/branchPage/newPage/Sta_exchange/Exchange.jsp";
						Log.i(TAG, "getAppIntent: LoadUrl:"+LoadUrl);
						web.loadUrl(LoadUrl);
					}
				}
			}catch (Exception e){
				e.printStackTrace();
			}
		}
		if(intent.hasExtra("jsonData")){
			String jsonData = intent.getStringExtra("jsonData");
			Log.i(TAG, "getAppIntent: get jsonData:"+jsonData);
			try {
				JSONObject json = new JSONObject(jsonData);
				if(json.has("loadURL")){
					LoadUrl = json.getString("loadURL");
//					LoadUrl = "http://124.232.135.225:8082/AppStoreTV4/service/branchPage/newPage/Sta_exchange/Exchange.jsp";
					Log.i(TAG, "getAppIntent: LoadUrl:"+LoadUrl);
					web.loadUrl(LoadUrl);
				}
			}catch (Exception e){
				e.printStackTrace();
			}
		}
//		Bundle bundle = intent.getExtras();
//		if(bundle!=null){
//			Set<String> set = bundle.keySet();
//			for (Iterator iterator = set.iterator(); iterator.hasNext();) {
//				String key = (String) iterator.next();
//				Object value = bundle.get(key);
//				Log.i("getAppIntent", "key:" + key + " value:" + value);
//			}
//		}
	}
	public boolean onCreateOptionsMenu(Menu paramMenu) {
		getMenuInflater().inflate(R.menu.main, paramMenu);
		return true;
	}
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode ==KeyEvent.KEYCODE_BACK){
			goBack();
		}
		if ((keyCode == KeyEvent.KEYCODE_BACK) && web.canGoBack()) {
			Log.i(TAG, "onKeyDown: web.getUrl:"+web.getUrl());
//			if (web.getUrl().contains("http://124.232.135.229:8083/AppStoreTV4/service/queryOrderInfo.do")) {//测试
//			if (web.getUrl().contains("http://124.232.136.236:8099/AppStoreTV/service/queryOrderInfo.do")) {//测试
			if (web.getUrl().contains("http://124.232.135.225:8082/AppStoreTV4/service/queryOrderInfo.do")) {//正式地址
				exit();
				return false;
			}
			if(web.getUrl().contains("NoResponse") ){
				return true;
			}
			web.goBack();
			return true;
		}else if (keyCode == KeyEvent.KEYCODE_BACK&&!web.canGoBack()){
			exit();
			return false;
		}
		return super.onKeyDown(keyCode, event);
	}
	public void exit() {
		if (System.currentTimeMillis() - exitTime > 2000L) {
			Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
			exitTime = System.currentTimeMillis();
			return;
		}
		finish();
		System.exit(0);
	}
	public void goBack(){
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			web.evaluateJavascript("javascript:goBack()", new ValueCallback<String>() {
				@Override
				public void onReceiveValue(String value) {
					//此处为 js 返回的结果
				}
			});
		}else{
			web.loadUrl("javascript:goBack()");
		}
	}

}
