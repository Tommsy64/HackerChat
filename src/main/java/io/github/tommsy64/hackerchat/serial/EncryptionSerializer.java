package io.github.tommsy64.hackerchat.serial;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import io.github.tommsy64.hackerchat.util.Encryptor;

public class EncryptionSerializer implements Serializer<String> {

    private final String key;

    public EncryptionSerializer(String key) {
        this.key = key;
    }

    @Override
    public String serialize(String object) {
        try {
            return Encryptor.encrypt(key, object);
        } catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | UnsupportedEncodingException | InvalidAlgorithmParameterException | IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {

        }
        return object;
    }

    @Override
    public String deserialize(String object) {
        try {
            return Encryptor.decrypt(key, object);
        } catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | UnsupportedEncodingException | InvalidAlgorithmParameterException | IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {

        }
        return object;
    }

}
