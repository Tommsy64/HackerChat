package io.github.tommsy64.netchat;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import com.google.common.hash.Hashing;
import com.google.common.primitives.Bytes;

public class Encryptor {
    public static String encrypt(String key, String value) throws NoSuchAlgorithmException, NoSuchPaddingException, UnsupportedEncodingException, InvalidKeyException,
            InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
        if (value == null || value.isEmpty())
            return value;
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");

        // setup an IV (initialization vector) that should be
        // randomly generated for each input that's encrypted
        byte[] initVector = new byte[cipher.getBlockSize()];
        new SecureRandom().nextBytes(initVector);

        SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");
        IvParameterSpec ivSpec = new IvParameterSpec(initVector);
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec, ivSpec);

        byte[] encrypted = cipher.doFinal(value.getBytes());

        return Base64.getEncoder().encodeToString(Bytes.concat(initVector, encrypted));
    }

    public static String decrypt(String key, String encryptedStr) throws NoSuchAlgorithmException, NoSuchPaddingException, UnsupportedEncodingException, InvalidKeyException,
            InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
        if (encryptedStr == null || encryptedStr.isEmpty())
            return encryptedStr;
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");

        byte[] encrypted = Base64.getDecoder().decode(encryptedStr);

        byte[] initVector = new byte[cipher.getBlockSize()];
        System.arraycopy(encrypted, 0, initVector, 0, initVector.length);
        encrypted = Arrays.copyOfRange(encrypted, initVector.length, encrypted.length);

        SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");
        IvParameterSpec ivSpec = new IvParameterSpec(initVector);
        cipher.init(Cipher.DECRYPT_MODE, skeySpec, ivSpec);

        byte[] original = cipher.doFinal(encrypted);

        return new String(original);
    }

    public static String hashKey(String key) {
        return Hashing.concatenating(Hashing.adler32(), Hashing.murmur3_32()).hashString(key, Charset.forName("UTF-8")).toString();
    }
}