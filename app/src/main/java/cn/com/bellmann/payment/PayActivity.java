/**
 * 支付App
 */
package cn.com.bellmann.payment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

import cn.com.bellmann.payment.modle.AppInfoBean;
import cn.com.bellmann.payment.net.InsertDataConnction;
import cn.com.bellmann.payment.utils.Tools;
import cn.com.bellmann.payment.utils.UserTools;
import cn.com.bellmann.payment.utils.Utils;

/**
 * @author Administrator
 *
 */
public class PayActivity extends Activity {

	private static final String TAG = "PayActivity";

//	public static void startup(Context context) {
//		context.startActivity(new Intent(context, PayActivity.class).putExtra("LoadUrl", "http://124.232.136.236:8099/SportsArea/sportsIndex.html?width=1280"));
//	}

	/*

				JSONObject localJSONObject = new JSONObject(str1);
				this.transactionID = localJSONObject.getString("transactionID");
				this.sPID = localJSONObject.getString("SPID");
				this.e_userID = localJSONObject.getString("userId");
				this.e_userToken = localJSONObject.getString("userToken");
				this.key = localJSONObject.getString("key");
				this.productID = localJSONObject.getString("productID");
				this.price = localJSONObject.getString("price");
				this.productName = localJSONObject.getString("productName");
				this.backPackage = localJSONObject.getString("backPackage");
				this.backClass = localJSONObject.getString("backClass");
				this.notifyUrl = localJSONObject.getString("notifyUrl");
				this.optFlag = localJSONObject.getString("optFlag");
				if ("EPG".equals(this.optFlag)) {
					this.purchaseType = localJSONObject.getInt("purchaseType");
					this.categoryID = localJSONObject.getString("categoryID");
					this.contentID = localJSONObject.getString("contentID");
					this.contentType = localJSONObject.getInt("contentType");
				}
	 */
	private static final String BASE_URL = "http://124.232.135.225:8082";
	// 回调包名.
	private String mBackPackage;
	// 回调类名
	private String mBackClass;
	// 事务编号
	private String mTransactionID;
	// 支付结果
	private String mResult;
	// 结果描述
	private String mDescription;
	// 获得的签名
	private String mGetSign;
	// 验证签名
	private String mSign;
	private WebView webview;
	private String uri;

	private String backURL = "http://124.232.135.225:8082/AppStoreTV/back.do";
	// 交易id
	private Dialog dialog;
	private boolean flagUrl = false;

	private View mBackgroundViewForWebView;
	private View mForegroundViewForWebView;

	ParametersParser mParametersParser;

	private void insertDataToService() {
		String str1 = UserTools.getOriginalInfo(mParametersParser.getStringExtra("key"), mParametersParser.getStringExtra("userToken"));
		String str2 = UserTools.getOriginalInfo(mParametersParser.getStringExtra("key"), mParametersParser.getStringExtra("userId"));
		new InsertDataConnction(
				"http://124.232.135.225:8082/AppStoreTV/create.do", str1,
				this.backURL, mParametersParser.toString(), str2,
				new InsertDataConnction.InsertDataCallback() {
					public void onFail(int paramAnonymousInt,
									   String paramAnonymousString) {
						Log.e("insertDataToService", "onFail:"
								+ paramAnonymousInt + ";errorMsg:"
								+ paramAnonymousString);
						insertDataToService2();
					}

					public void onSuccess() {
						Log.e("insertDataToService", "onSuccess");

					}
				});
	}
  private void insertDataToService2(){
	  String str1 = UserTools.getOriginalInfo(mParametersParser.getStringExtra("key"), mParametersParser.getStringExtra("userToken"));
	  String str2 = UserTools.getOriginalInfo(mParametersParser.getStringExtra("key"), mParametersParser.getStringExtra("userId"));
	  new InsertDataConnction(
			  "http://124.232.135.225:8082/AppStoreTV4/inter/PayInterfaceJsonData.do", str1,
			  this.backURL, mParametersParser.toString(), str2,
			  new InsertDataConnction.InsertDataCallback() {
				  public void onFail(int paramAnonymousInt,
									 String paramAnonymousString) {
					  Log.e("insertDataToService", "onFail:"
							  + paramAnonymousInt + ";errorMsg:"
							  + paramAnonymousString);
				  }

				  public void onSuccess() {
					  Log.e("insertDataToService", "onSuccess");

				  }
			  });
  }

	@SuppressLint({"NewApi" , "JavascriptInterface", "SetJavaScriptEnabled" })
	protected void onCreate(Bundle paramBundle) {
		super.onCreate(paramBundle);
//		setTitle("支付页面");
//		Toast.makeText(this, "当前版本："+Utils.getVersionName(getApplicationContext()), Toast.LENGTH_SHORT).show();
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_pay);
		// pay_vesition = (TextView) findViewById(R.id.pay_vesition);
		// pay_vesition.setText("当前版本:"+Utils.getVersionName(getApplicationContext()));
		mBackgroundViewForWebView = findViewById(R.id.background_view);
		mForegroundViewForWebView = findViewById(R.id.foreground_view);
		this.webview = ((WebView) findViewById(R.id.webview));
		this.webview.setVisibility(View.GONE);
		this.webview.setBackgroundColor(Color.TRANSPARENT);
		this.webview.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
		this.dialog = new Dialog(this, R.style.LodingDialog);
		View.inflate(this, R.layout.view_dialog, null);
		this.dialog.setContentView(R.layout.view_dialog);
		this.dialog.show();
		initTimer();
		mParametersParser = new ParametersParser(getIntent());
//		Bundle bundle = getIntent().getExtras();
//		if(bundle!=null){
//			Set<String> set = bundle.keySet();
//			for (Iterator iterator = set.iterator(); iterator.hasNext();) {
//				String key = (String) iterator.next();
//				Object value = bundle.get(key);
//				Log.i("getAppIntent", "key:" + key + " value:" + value);
//			}
//		}
		insertDataToService();
		this.mBackClass = mParametersParser.getStringExtra("backClass");
		this.mBackPackage = mParametersParser.getStringExtra("backPackage");
		WebSettings localWebSettings = this.webview.getSettings();
		localWebSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
		localWebSettings.setAllowFileAccess(true);
		localWebSettings.setJavaScriptEnabled(true);
//		 String str2 = "http://124.232.135.227:8296/UserOrderHN?INFO="//联创测试
//		 String str2 = "http://124.232.135.228:8296/UserOrderHN?INFO="//联创测试
//		String str2 = "http://124.232.136.236:8099//UserOrderHN?INFO="//测试地址
		String str2 = "http://222.246.132.231:8296/UserOrderHN?INFO="//正式地址
				+ mParametersParser.generateXMLInfo(backURL);
		Log.d(TAG, "onCreate: submit xml: " + str2);
		this.webview.addJavascriptInterface(this, "AppFunction");
		this.webview.setInitialScale(Tools.dip2px(this, 135));
		localWebSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
		localWebSettings.setLoadWithOverviewMode(true);
		this.webview.requestFocus();
		localWebSettings.setDefaultTextEncodingName("GBK");
		Log.e("url", str2);
		this.webview.setWebViewClient(new HelloWebViewClient());

		String loadUrl = mParametersParser.getStringExtra("LoadUrl");
		if (!TextUtils.isEmpty(loadUrl)) {
			this.webview.loadUrl(loadUrl);
		} else {
			this.webview.loadUrl(str2);
			loadUrl = str2;
		}
		boolean extraViewVisible = "1280".equals(Uri.parse(loadUrl).getQueryParameter("width"));
		mBackgroundViewForWebView.setVisibility(extraViewVisible ? View.GONE : View.VISIBLE);
		mForegroundViewForWebView.setVisibility(extraViewVisible ? View.GONE : View.VISIBLE);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		timer.cancel();
		try{
			if (dialog != null&&dialog.isShowing()) {
				dialog.dismiss();
				dialog = null;
			}
		}catch (Exception e){
			e.printStackTrace();
		}

	}

	/*
	private void notFlag() {
		// VAS
		// MD5验证
		String content = transactionID + sPID + e_userID + e_userToken + key
				+ productID + price + productName + backPackage + backClass
				+ notifyUrl + optFlag;
		mSign = MD5Utils.encode(content);

		try {
			uri = "<transactionID>" + transactionID + "</transactionID><SPID>"
					+ sPID + "</SPID><userId>" + e_userID
					+ "</userId><userToken>" + e_userToken
					+ "</userToken><key>" + key + "</key><productID>"
					+ productID + "</productID><price>" + price
					+ "</price><productName>"
					+ URLEncoder.encode(String.valueOf(productName), "GBK")
					+ "</productName><backurl>" + URLEncoder.encode(String.valueOf(backURL))
					+ "</backurl><optFlag>" + optFlag + "</optFlag><notifyUrl>"
					+ URLEncoder.encode(String.valueOf(notifyUrl)) + "</notifyUrl>";
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	private void opFlag() {
		mSign = MD5Utils.encode(transactionID + sPID + e_userID + e_userToken
				+ key + productID + price + productName + backPackage
				+ backClass + notifyUrl + optFlag + purchaseType + categoryID
				+ contentID + contentType);
		try {
			uri = "<transactionID>" + transactionID + "</transactionID><SPID>"
					+ sPID + "</SPID><userId>" + e_userID
					+ "</userId><userToken>" + e_userToken
					+ "</userToken><key>" + key + "</key><productID>"
					+ productID + "</productID><price>" + price
					+ "</price><productName>"
					+ URLEncoder.encode(productName, "GBK")
					+ "</productName><backurl>" + URLEncoder.encode(backURL)
					+ "</backurl><optFlag>" + optFlag
					+ "</optFlag><purchaseType>" + purchaseType
					+ "</purchaseType><categoryID>" + categoryID
					+ "</categoryID><contentID>" + contentID
					+ "</contentID><contentType>" + contentType
					+ "</contentType><notifyUrl>"
					+ URLEncoder.encode(notifyUrl) + "</notifyUrl>";
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	*/

	private void initTimer() {
		timer = new Timer();
		task = new TimerTask() {

			@Override
			public void run() {
				// 这里是要执行的逻辑
				mySubmit("00000000", "访问出错", "网络异常或网址错误");
			}
		};
	}

	private String result = "";
	private String desc = "";
	private String transationId = "";
	private Timer timer;
	private TimerTask task;

	// js->java
	@JavascriptInterface
	public void mySubmit(String transationId, String result, String desc) {
		// Toast.makeText(this, transationId +";"+result+";"+desc, 0).show();
		this.result = result;
		this.transationId = transationId;
		this.desc = desc;
		SendResult();
	}

	@Override
	// 设置回退
	// 覆盖Activity类的onKeyDown(int keyCoder,KeyEvent event)方法
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode ==KeyEvent.KEYCODE_BACK){
			goBack();
		}
		if ((keyCode != KeyEvent.KEYCODE_BACK) || (flagUrl)) {
			return false;
		}
		if (webview.canGoBack()) {
			String webUrl = webview.getUrl();
			if (webUrl != null) {
				if (webUrl.contains("main/hunan/unifiedorder/order_second.action") || webUrl.contains("fbdre") ||
						(webUrl.contains("main/hunan/unifiedorder/thirdpay_success.action") ||
								webUrl.contains("/main/hunan/unifiedordernew/order_second.action") ||
								webUrl.contains("/main/hunan/unifiedordernew/thirdpay_success.action"))) {
					return false;
				}
			}
		}
		SendResult();
		return true;
	}

	// Web视图
	private class HelloWebViewClient extends WebViewClient {
		@Override
		public void onReceivedError(WebView view, int errorCode,
									String description, String failingUrl) {
			Log.e("onReceivedError", errorCode + ";" + description + ";"
					+ failingUrl);
			super.onReceivedError(view, errorCode, description, failingUrl);
		}

		@Override
		public void onPageFinished(WebView view, String url) {
			webview.setVisibility(View.VISIBLE);

			if (!url.contains(BASE_URL)) {
				boolean showing = dialog.isShowing();
				if (showing) {
					dialog.dismiss();
				}
			}

			super.onPageFinished(view, url);
			webview.loadUrl("javascript:funFromjs()");
			// html加载完成之后，添加监听图片的点击js函数
		}

		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			if (url.contains("main/hunan/unifiedorder/order_second.action") ||
					url.contains("main/hunan/unifiedordernew/thirdpay_success.action")) {
				flagUrl = true;
			}else {
				flagUrl = false;
			}
			super.onPageStarted(view, url, favicon);
		}

		/**
		 * 所有跳转的链接都会在此方法中回调
		 */
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			// tel:110
			try{
				if (dialog != null) {
					if (!dialog.isShowing()) {
						dialog.show();
					}
				}
			}catch (Exception e){
				e.printStackTrace();
			}
			if (url.contains(backURL)) {
				if (task != null && timer != null) {
					// 起动定时器
					timer.schedule(task, 5000, 5000);
				}
			}
			boolean extraViewVisible = "1280".equals(Uri.parse(url).getQueryParameter("width"));
			Log.i(TAG, "shouldOverrideUrlLoading: url = " + url);
			mBackgroundViewForWebView.setVisibility(extraViewVisible ? View.GONE : View.VISIBLE);
			mForegroundViewForWebView.setVisibility(extraViewVisible ? View.GONE : View.VISIBLE);
			view.loadUrl(url); // 强制使用WebView加载页面，而不用系统浏览器加载
			return true;
		}

		/*
		 * public boolean shouldOverrideKeyEvent(WebView view, KeyEvent event) {
		 * Log.e("shouldOverrideKeyEvent", "shouldOverrideKeyEvent"); return
		 * true;
		 *
		 * }
		 */

	}

	// 返回支付结果
	private void SendResult() {
		if(TextUtils.isEmpty(mBackPackage)&&TextUtils.isEmpty(mBackClass)){
			finish();
			return;
		}
		Intent sendResultIntent = new Intent();
		// 设置启动app（包名，完整类名）
		sendResultIntent.setClassName(mBackPackage, mBackClass);
		JSONObject js = new JSONObject();
		try {
			js.put("transactionID", transationId);// String类参数
			js.put("result", result);
			js.put("description", desc);
			sendResultIntent.putExtra("jsonData", js.toString());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		sendResultIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(sendResultIntent);
		this.finish();
		if (dialog != null) {
			dialog.dismiss();
		}
	}
	@JavascriptInterface
	public void getAppData(){
		try{
				PayActivity.this.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						AppInfoBean appInfoBean = new AppInfoBean();
						AppInfoBean.JsonDataBean  jsonDataBean = new AppInfoBean.JsonDataBean();
						int versionCode = Utils.getVersionCode(PayActivity.this);
						String versionName = Utils.getVersionName(PayActivity.this);
						jsonDataBean.setVersionName(versionName);
						jsonDataBean.setVersionCode(versionCode);
						jsonDataBean.setPackageName("cn.com.bellmann.payment");
						appInfoBean.setJsonData(jsonDataBean);
						setAppData(new Gson().toJson(appInfoBean));
					}
				});

		}catch (Exception e){
			e.printStackTrace();
		}
	}
	public void setAppData(String jsonData){
		Log.i(TAG, "setAppData: jsonData:"+jsonData);
		if(TextUtils.isEmpty(jsonData)||jsonData.equals("undefined")){
			Log.i(TAG, "setAppData: get jsonData is null ro  is undefined");
			return ;
		}
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			webview.evaluateJavascript("javascript:setAppData(" + jsonData + ")", new ValueCallback<String>() {
				@Override
				public void onReceiveValue(String value) {
					//此处为 js 返回的结果
				}
			});
		}else{
			webview.loadUrl("javascript:setAppData(" + jsonData + ")");
		}
	}
	@JavascriptInterface
	public void showToast(final String data){
		PayActivity.this.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(PayActivity.this,data,Toast.LENGTH_SHORT).show();
			}
		});
	}
	public void goBack(){
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			webview.evaluateJavascript("javascript:goBack()", new ValueCallback<String>() {
				@Override
				public void onReceiveValue(String value) {
					//此处为 js 返回的结果
				}
			});
		}else{
			webview.loadUrl("javascript:goBack()");
		}
	}
}
