package cn.com.bellmann.payment.utils;

import android.content.Context;

import java.io.UnsupportedEncodingException;

public class Tools {
	
	
	static String string2Unicode(String s) {  
	    try {  
	      StringBuffer out = new StringBuffer("");  
	      byte[] bytes = s.getBytes("unicode");  
	      for (int i = 2; i < bytes.length - 1; i += 2) {  
	        out.append("u");  
	        String str = Integer.toHexString(bytes[i + 1] & 0xff);  
	        for (int j = str.length(); j < 2; j++) {  
	          out.append("0");  
	        }  
	        String str1 = Integer.toHexString(bytes[i] & 0xff);  
	  
	        out.append(str);  
	        out.append(str1);  
	        out.append(" ");  
	      }  
	      return out.toString().toUpperCase();  
	    }  
	    catch (UnsupportedEncodingException e) {  
	      e.printStackTrace();  
	      return null;  
	    }  
	  }   
	  
	   
	  
	static String unicode2String(String unicodeStr){  
	    StringBuffer sb = new StringBuffer();  
	    String str[] = unicodeStr.toUpperCase().split("U");  
	    for(int i=0;i<str.length;i++){  
	      if(str[i].equals("")) continue;  
	      char c = (char)Integer.parseInt(str[i].trim(),16);  
	      sb.append(c);  
	    }  
	    return sb.toString();  
	  }  
	
	
	/** 
     * 把中文转成Unicode码
     * @param str 
     * @return 
     */  
    public static String chinaToUnicode(String str){  
        String result="";  
        for (int i = 0; i < str.length(); i++){  
            int chr1 = (char) str.charAt(i);  
            if(chr1>=19968&&chr1<=171941){//汉字范围 \u4e00-\u9fa5 (中文)
                result+="\\u" + Integer.toHexString(chr1);  
            }else{  
                result+=str.charAt(i);  
            }  
        }  
        return result;  
    }  
  
    /** 
     * 判断是否为中文字符
     * @param c 
     * @return 
     */  
    public  boolean isChinese(char c) {  
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);  
        if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS  
                || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS  
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A  
                || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION  
                || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION  
                || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {  
            return true;  
        }  
        return false;  
    }  
    
    
	/** dipת转换px */
	public static int dip2px(Context context ,int dip) {
		 float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dip * scale + 0.5f);
	}

}
