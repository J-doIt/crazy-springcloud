package com.crazymaker.springcloud.common.encrypt;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import java.security.Key;
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

@SuppressWarnings("restriction" )
public class DESCoder
{
    public static final String KEY_ALGORITHM = "DES";
    public static final String CIPHER_ALIGORITHM = "DES/ECB/PKCS5Padding";

    public DESCoder()
    {
    }

    public static void generate(String data)
    {
        BASE64Encoder encoder = new BASE64Encoder();

        try
        {
            String key = encoder.encode(initKey());
            String result = encrypt(data, key);
            System.out.println("the key is :" + key);
            System.out.println("this datas is :" + result);
        } catch (Exception var4)
        {
            var4.printStackTrace();
        }

    }

    public static byte[] initKey() throws Exception
    {
        KeyGenerator kg = KeyGenerator.getInstance("DES" );
        kg.init(56);
        SecretKey secretKey = kg.generateKey();
        return secretKey.getEncoded();
    }

    public static String encrypt(String data, String key) throws Exception
    {
        BASE64Encoder encoder = new BASE64Encoder();
        BASE64Decoder decoder = new BASE64Decoder();
        Key k = toKey(decoder.decodeBuffer(key));
        Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding" );
        cipher.init(1, k);
        return encoder.encode(cipher.doFinal(data.getBytes()));
    }

    public static String decrypt(String data, String key) throws Exception
    {
        BASE64Decoder decoder = new BASE64Decoder();
        Key k = toKey(decoder.decodeBuffer(key));
        Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding" );
        cipher.init(2, k);
        return new String(cipher.doFinal(decoder.decodeBuffer(data)), "UTF-8" );
    }

    private static Key toKey(byte[] key) throws Exception
    {
        DESKeySpec dks = new DESKeySpec(key);
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES" );
        return keyFactory.generateSecret(dks);
    }
}
