package com.j13.zed.util;

import android.util.Base64;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.Key;
import java.security.MessageDigest;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * <p>
 * AES加密解密工具包
 * </p>
 *
 * @version 1.0
 * @date 2012-5-18
 */
public class AESUtils {

// public static void main(String[] args) throws Exception {
// final byte[] key = DigestUtils.md5("bbmCaaacHJnLTydddKrLLUGLddagYRA");
//
// String source = "这是一行测试AES加密/解密的文字，你看完也等于没看，是不是啊？！";
// System.err.println("原文:\t" + source);
// byte[] inputData = source.getBytes();
// inputData = AESUtils.encrypt(inputData, key);
// System.err.println("加密后:\t" + Base64Utils.encode(inputData));
// byte[] outputData = AESUtils.decrypt(inputData, key);
// String outputStr = new String(outputData);
// System.err.println("解密后:\t" + outputStr);
//
// }

    private static final String ALGORITHM = "AES";
    private static final int KEY_SIZE = 128;
    private static final int CACHE_SIZE = 1024;

    /**
     * <p>
     * 生成随机密钥
     * </p>
     *
     * @return
     * @throws Exception
     */
    public static String getSecretKey() throws Exception {
        return getSecretKey(null);
    }

    /**
     * <p>
     * 生成密钥
     * </p>
     *
     * @param seed 密钥种子
     * @return
     * @throws Exception
     */
    public static String getSecretKey(String seed) throws Exception {
        KeyGenerator keyGenerator = KeyGenerator.getInstance(ALGORITHM);
        SecureRandom secureRandom;
        if (seed != null && !"".equals(seed)) {
            secureRandom = new SecureRandom(seed.getBytes());
        } else {
            secureRandom = new SecureRandom();
        }
        keyGenerator.init(KEY_SIZE, secureRandom);
        SecretKey secretKey = keyGenerator.generateKey();
        return Base64Utils.encode(secretKey.getEncoded());
    }

    /**
     * <p>
     * 加密
     * </p>
     *
     * @param data
     * @param key
     * @return
     * @throws Exception
     */
    public static byte[] encrypt(byte[] data, byte[] key) throws Exception {
        Key k = toKey(key);
        byte[] raw = k.getEncoded();
        SecretKeySpec secretKeySpec = new SecretKeySpec(raw, ALGORITHM);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
        return cipher.doFinal(data);
    }

    /**
     * <p>
     * 文件加密
     * </p>
     *
     * @param key
     * @param sourceFilePath
     * @param destFilePath
     * @throws Exception
     */
    public static void encryptFile(byte[] key, String sourceFilePath, String destFilePath) throws Exception {
        File sourceFile = new File(sourceFilePath);
        File destFile = new File(destFilePath);
        if (sourceFile.exists() && sourceFile.isFile()) {
            if (!destFile.getParentFile().exists()) {
                destFile.getParentFile().mkdirs();
            }
            destFile.createNewFile();
            InputStream in = new FileInputStream(sourceFile);
            OutputStream out = new FileOutputStream(destFile);
            Key k = toKey(key);
            byte[] raw = k.getEncoded();
            SecretKeySpec secretKeySpec = new SecretKeySpec(raw, ALGORITHM);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
            CipherInputStream cin = new CipherInputStream(in, cipher);
            byte[] cache = new byte[CACHE_SIZE];
            int nRead = 0;
            while ((nRead = cin.read(cache)) != -1) {
                out.write(cache, 0, nRead);
                out.flush();
            }
            out.close();
            cin.close();
            in.close();
        }
    }

    /**
     * <p>
     * 解密
     * </p>
     *
     * @param data
     * @param key
     * @return
     * @throws Exception
     */
    public static byte[] decrypt(byte[] data, byte[] key) throws Exception {
        Key k = toKey(key);
        byte[] raw = k.getEncoded();
        SecretKeySpec secretKeySpec = new SecretKeySpec(raw, ALGORITHM);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
        return cipher.doFinal(data);
    }

    /**
     * <p>
     * 文件解密
     * </p>
     *
     * @param key
     * @param sourceFilePath
     * @param destFilePath
     * @throws Exception
     */
    public static void decryptFile(byte[] key, String sourceFilePath, String destFilePath) throws Exception {
        File sourceFile = new File(sourceFilePath);
        File destFile = new File(destFilePath);
        if (sourceFile.exists() && sourceFile.isFile()) {
            if (!destFile.getParentFile().exists()) {
                destFile.getParentFile().mkdirs();
            }
            destFile.createNewFile();
            FileInputStream in = new FileInputStream(sourceFile);
            FileOutputStream out = new FileOutputStream(destFile);
            Key k = toKey(key);
            byte[] raw = k.getEncoded();
            SecretKeySpec secretKeySpec = new SecretKeySpec(raw, ALGORITHM);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
            CipherOutputStream cout = new CipherOutputStream(out, cipher);
            byte[] cache = new byte[CACHE_SIZE];
            int nRead = 0;
            while ((nRead = in.read(cache)) != -1) {
                cout.write(cache, 0, nRead);
                cout.flush();
            }
            cout.close();
            out.close();
            in.close();
        }
    }

    /**
     * <p>
     * 转换密钥
     * </p>
     *
     * @param key
     * @return
     * @throws Exception
     */
    private static Key toKey(byte[] key) throws Exception {
        SecretKey secretKey = new SecretKeySpec(key, ALGORITHM);
        return secretKey;
    }

    public static String aesEncode(String content, String key) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            byte[] md5array = messageDigest.digest(key.getBytes());
            final byte[] outBytes = AESUtils.encrypt(content.getBytes(), md5array);
            return Base64Utils.encode(outBytes);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }


    /**
     * 原本使用的是apache的BASE64. 下面是相关注释
     * ---------------------------------------------------------------------------------------------------------------------------------------------------------------
     * Provides Base64 encoding and decoding as defined by RFC 2045.
     * This class implements section 6.8. Base64 Content-Transfer-Encoding from RFC 2045 Multipurpose Internet Mail Extensions (MIME)
     * Part One: Format of Internet Message Bodies by Freed and Borenstein.
     * The class can be parameterized in the following manner with various constructors:
     * URL-safe mode: Default off.
     * Line length: Default 76. Line length that aren't multiples of 4 will still essentially end up being multiples of 4 in the encoded data.
     * Line separator: Default is CRLF ("\r\n")
     * Since this class operates directly on byte streams, and not character streams,
     * it is hard-coded to only encode/decode character encodings which are compatible with the lower 127 ASCII chart (ISO-8859-1, Windows-1252, UTF-8, etc).
     * ---------------------------------------------------------------------------------------------------------------------------------------------------------------
     * 现在改用android Base64 api来实现, 根据http://stackoverflow.com/questions/17912119/is-there-any-difference-between-apaches-base64-encodebase64-and-androids-base6
     * android Base64 使用上需要加上NO_WRAP flag 来保证与apache的lib一致。
     */

    public static class Base64Utils {
        /**
         * <p>
         * BASE64字符串解码为二进制数据
         * </p>
         *
         * @param base64
         * @return
         * @throws Exception
         */
        public static byte[] decode(String base64) throws Exception {
            return Base64.decode(base64.getBytes(), Base64.NO_WRAP);
        }

        /**
         * <p>
         * 二进制数据编码为BASE64字符串
         * </p>
         *
         * @param bytes
         * @return
         * @throws Exception
         */
        public static String encode(byte[] bytes) throws Exception {
            return new String(Base64.encode(bytes, Base64.NO_WRAP));
        }
    }


}