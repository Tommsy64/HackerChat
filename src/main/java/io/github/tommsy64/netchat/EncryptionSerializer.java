package io.github.tommsy64.netchat;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

public class EncryptionSerializer extends Serializer<String> {
    {
        setImmutable(true);
        setAcceptsNull(true);
    }

    private final String key;

    public EncryptionSerializer(String key) {
        this.key = key;
    }

    public void write(Kryo kryo, Output output, String object) {
        try {
            output.writeString(Encryptor.encrypt(key, object));
        } catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | UnsupportedEncodingException | InvalidAlgorithmParameterException | IllegalBlockSizeException
                | BadPaddingException e) {
            e.printStackTrace();
        }
    }

    public String read(Kryo kryo, Input input, Class<String> type) {
        try {
            return Encryptor.decrypt(key, input.readString());
        } catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | UnsupportedEncodingException | InvalidAlgorithmParameterException | IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            return "";
        }
        return null;
    }
}