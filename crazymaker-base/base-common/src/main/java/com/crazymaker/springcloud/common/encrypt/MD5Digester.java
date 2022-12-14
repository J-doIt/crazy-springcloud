package com.crazymaker.springcloud.common.encrypt;


//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

import org.apache.commons.lang3.StringUtils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Digester
{
    public MD5Digester()
    {
    }

    public static String getPassMD5(String originalString)
    {
        if (StringUtils.isEmpty(originalString))
        {
            throw new RuntimeException("the original String is not exist!" );
        } else
        {
            String key = null;

            try
            {
                MessageDigest md = MessageDigest.getInstance("MD5" );
                byte[] bPass = originalString.getBytes("UTF-8" );
                md.update(bPass);
                key = byteToHexString(md.digest());
            } catch (NoSuchAlgorithmException var4)
            {
                var4.printStackTrace();
            } catch (UnsupportedEncodingException var5)
            {
                var5.printStackTrace();
            }

            return key;
        }
    }

    private static final String byteToHexString(byte[] bArray)
    {
        StringBuilder sb = new StringBuilder(bArray.length);

        for (int i = 0; i < bArray.length; ++i)
        {
            String tempStr = Integer.toHexString(255 & bArray[i]);
            if (tempStr.length() < 2)
            {
                sb.append(0);
            }

            sb.append(tempStr);
        }

        return sb.toString();
    }

    public static void main(String[] args)
    {
        String password = "";
        System.out.print(getPassMD5(password));
    }
}
