package cn.com.bellmann.payment.view;

import android.content.Context;
import android.util.AttributeSet;
import android.webkit.WebView;

public class MyWebView extends WebView{

	public MyWebView(Context context, AttributeSet attrs, int defStyleAttr,
			boolean privateBrowsing) {
		super(context, attrs, defStyleAttr, privateBrowsing);
	}

	public MyWebView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public MyWebView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public MyWebView(Context context) {
		super(context);
	}
	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		scrollTo(0, 0);
//		super.onScrollChanged(0, 0, 0, 0);
	}
	
	
	

}
