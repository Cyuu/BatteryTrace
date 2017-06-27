package com.thdz.bt.util;

import android.text.TextUtils;

import java.security.MessageDigest;

/**
 * MD5加密类
 */
public class MD5Utils {


    // 16进制下数字到字符的映射数组
    private final static String[] hexDigits = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f"};


    public static String encodePwdByMD5(String str) {
        String value = "";
        if (TextUtils.isEmpty(str)) {
            return null;
        }

        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] results = md.digest(str.getBytes());
            value = byteArrayToHexString(results);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return value;
    }

    private static String byteArrayToHexString(byte[] bytes) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < bytes.length; i++) {
            sb.append(byteToHexString(bytes[i]));
        }
        return sb.toString();
    }

    private static String byteToHexString(byte b) {
        int n = b;
        if (n < 0) {
            n = 256 + n;
        }
        int d1 = n / 16;
        int d2 = n % 16;
        return hexDigits[d1] + hexDigits[d2];

    }

}
