package cn.com.bellmann.payment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by Administrator on 2018/6/7.
 * The document in project Payment.
 *
 * @author Jack
 */

public class ParametersParser {
    private static final String TAG = "ParametersParser";
    private JSONObject json;
    private Bundle bundle;

    public ParametersParser(Intent intent) {
        String jsonData = intent.getStringExtra("jsonData");
        if (jsonData != null) {
            try {
                json = new JSONObject(jsonData);
            } catch (JSONException e) {
                Log.w(TAG, e);
            }
        } else {
            bundle = intent.getExtras();
        }
        Log.i(TAG, toString());
    }
    public String getStringExtra(String name) {
        String result = null;
        if (json != null) {
            try {
                result = json.getString(name);
            } catch (JSONException ignore) {
            }
        } else {
            if(bundle!=null){
                result = bundle.getString(name);
            }
        }
        return result;
    }

    public Object get(String name) {
        return get(name, null);
    }

    public Object get(String name, Object defaultValue) {
        Object result = defaultValue;
        if (json != null) {
            try {
                result = json.get(name);
            } catch (JSONException ignore) {
            }
        } else {
            result = bundle.get(name);
            if (result == null) {
                result = defaultValue;
            }
        }
        return result;
    }

    public int getIntExtra(String name) {
        return getIntExtra(name, 0);
    }

    public int getIntExtra(String name, int defaultValue) {
        int result = defaultValue;
        if (json != null) {
            try {
                result = json.getInt(name);
            } catch (JSONException ignore) {
            }
        } else {
            result = bundle.getInt(name, defaultValue);
        }
        return result;
    }

    public Iterator<String>keys() {
        if (json != null) {
            return json.keys();
        } else  if(bundle!=null){
            return bundle.keySet().iterator();
        }else{
           return null;
        }
    }

    private String mXMLInfoCache;
    public String generateXMLInfo(String backURL) {
        if (mXMLInfoCache != null) {
            return mXMLInfoCache;
        }

        String optFlag = getStringExtra("optFlag");
        String productName = getStringExtra("productName");
        if (productName != null) {
            try {
                productName = URLEncoder.encode(productName, "GBK");
            } catch (UnsupportedEncodingException e) {
                productName = URLEncoder.encode(productName);
            }
        }

        StringBuilder builder = new StringBuilder(1024);
        //谦容老版本
        concatXML(builder, "transactionID", getStringExtra("transactionID"));
        concatXML(builder, "SPID", getStringExtra("SPID"));
        concatXML(builder, "userId", getStringExtra("userId"));
        concatXML(builder, "userToken", getStringExtra("userToken"));
        concatXML(builder, "key", getStringExtra("key"));
        concatXML(builder, "productID", getStringExtra("productID"));
        concatXML(builder, "price", getStringExtra("price"));
        concatXML(builder, "productName", productName);
        concatXML(builder, "backurl", URLEncoder.encode(backURL));
        concatXML(builder, "optFlag", optFlag);
        if ("EPG".equals(optFlag)) {
            concatXML(builder, "purchaseType", getIntExtra("purchaseType", 0));
            concatXML(builder, "categoryID", getStringExtra("categoryID"));
            concatXML(builder, "contentID", getStringExtra("contentID"));
            concatXML(builder, "contentType", getIntExtra("contentType", 0));
        }
        String notifyUrl = getStringExtra("notifyUrl");
        if (notifyUrl != null) {
            concatXML(builder, "notifyUrl", URLEncoder.encode(notifyUrl));
        }
        //新版本数据
        Iterator<String>iterator = keys();
        if(iterator!=null){
            while (iterator.hasNext()) {
                String key = iterator.next();
                if (isOldVersionInfo(key)) {
                    continue;
                }
                concatXML(builder, key, get(key));
            }
        }
        mXMLInfoCache = builder.toString();
        return mXMLInfoCache;
    }

    private void concatXML(StringBuilder builder, String name, Object value) {
        builder.append('<').append(name).append('>')
                .append(String.valueOf(value))
                .append("</").append(name).append('>');
    }

    private boolean isOldVersionInfo(String name) {
        String[]oldInfo = {
                "transactionID", "SPID", "userId", "userToken", "key", "productID", "price",
                "productName", "optFlag", "purchaseType", "categoryID", "contentID", "contentType"
        };

        for (String s : oldInfo) {
            if (s.equals(name)) {
                return true;
            }
        }
        return false;
    }

    private String mToStringCache;
    @Override
    public String toString() {
        if (mToStringCache != null) {
            return mToStringCache;
        }

        if (json != null) {
            mToStringCache = json.toString();
        } else {
            JSONObject tmp = new JSONObject();
            if(bundle!=null){
                Set<String> keys = bundle.keySet();
                for (String key : keys) {
                    Object value = bundle.get(key);
                    try {
                        tmp.put(key, value);
                    } catch (JSONException ignore) {
                    }
                }
            }
            mToStringCache = tmp.toString();
        }
        return mToStringCache;
    }
}
