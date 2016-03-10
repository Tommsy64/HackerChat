package io.github.tommsy64.hackerchat;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

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
