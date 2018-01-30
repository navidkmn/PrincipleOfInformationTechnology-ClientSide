package com.example.navid.androidproject.AndroidEncoderUtil;

import java.security.MessageDigest;

/**
 * Created by Arsham on 4/6/2016.
 */
public class MD5 {

    public String getMD5(String text) throws Exception {
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(text.getBytes());
        byte byteData[] = md.digest();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < byteData.length; i++) {
            sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
        }
        return sb.toString();
    }
    public MD5()
    {

    }
}
