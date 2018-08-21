package cn.com.bellmann.payment;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

import cn.com.bellmann.payment.domain.GsonUtils;
import cn.com.bellmann.payment.domain.UpgradeData;
import cn.com.bellmann.payment.domain.VisitUrlList;
import cn.com.bellmann.payment.domain.sendVisit;

public class UpdataVersion {
	private static final int NOHTTP_VERSION_REQU = 0;
	private static final int NOHTTP_URL_REQU = 1;
	public VisitUrlList visitUrl;
	public UpgradeData upgrade;
	private Context context;
	private UpgradesInterface upgradeIn;
	public UpgradesInterface getUpgradeIn() {
		return upgradeIn;
	}

	public void setUpgradeIn(UpgradesInterface upgradeIn) {
		this.upgradeIn = upgradeIn;
	}

	public UpdataVersion(Context context) {
		this.context = context;
	}

	public void select() {
		
		
		RequestCallBack callBack = new RequestCallBack<String>() {

			@Override
			public void onFailure(HttpException arg0, String arg1) {
				//

			}

			@Override
			public void onSuccess(ResponseInfo<String> arg0) {
					String result = arg0.result;
					upgrade = GsonUtils.GsonToBean(result, UpgradeData.class);
					
					
					RequestParams params = new RequestParams();
					sendVisit vi = new sendVisit();
					vi.findType = "all";
//					vi.typeValue = 0;
					params.addBodyParameter("json",  GsonUtils.GsonString(vi));
					utils.send(HttpMethod.POST, "http://124.232.136.239:8080/pay/getInterfaceInfo.do", params , new RequestCallBack<String>() {

						@Override
						public void onFailure(HttpException arg0, String arg1) {
							Log.e("cn/com/bellmann/payment/test", arg1);
						}

						@Override
						public void onSuccess(ResponseInfo<String> arg0) {
							String result = arg0.result;
							visitUrl = GsonUtils.GsonToBean(result, VisitUrlList.class);
							upgradeIn.onSucceed(upgrade());
							Log.e("cn/com/bellmann/payment/test", result+"-->result");
						}
					});
					
			}

		};
		utils = new HttpUtils();
		// RequestParams params;
		RequestParams params = new RequestParams();
		params.addBodyParameter("json", "{\"type\":\"pay_tv\"}");
		utils .send(HttpMethod.POST, "http://124.232.136.239:8080/pay/getUpdateInfo.do", params, callBack);
		
		
		
	}
	private HttpUtils utils;
	private int getAppTVStoreVersionCode(Context context) {
		int versionCode = 0;
		try {
			// 获取软件版本号，对应AndroidManifest.xml下android:versionCode
			versionCode = context.getPackageManager().getPackageInfo(
					"cn.com.bellmann.payment", 0).versionCode;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return versionCode;
	}
	
	public boolean  upgrade(){
		if (upgrade.data.versionCode == getAppTVStoreVersionCode(context)) {
			return false;
		}else {
			return true;
		}
	}
	public interface UpgradesInterface{
		void onSucceed(boolean flag);
	}
}
