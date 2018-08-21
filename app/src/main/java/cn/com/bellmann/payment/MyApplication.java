package cn.com.bellmann.payment;

import android.app.Application;
import android.content.Context;

import com.yolanda.nohttp.NoHttp;

public class MyApplication extends Application{
	static Context context;
	@Override
	public void onCreate() {
		super.onCreate();
		context = this;
		NoHttp.initialize(this);
	}
	public static Context getContext() {
		return context;
	}

}
