package io.github.tommsy64.netchat.user;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import lombok.Setter;

public class EncryptedMessage extends Message {
    private static final long serialVersionUID = -3945128201390123314L;

    @Setter
    private transient String key;

    public EncryptedMessage(String data, Date sentTime, String key) {
        super(data, sentTime);
        this.key = key;
    }

    @Override
    public void setData(String data) {
        try {
            super.setData(Encryptor.encrypt(key, data));
        } catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | UnsupportedEncodingException | InvalidAlgorithmParameterException | IllegalBlockSizeException
                | BadPaddingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getData() {
        try {
            return Encryptor.decrypt(key, super.getData());
        } catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | UnsupportedEncodingException | InvalidAlgorithmParameterException | IllegalBlockSizeException
                | BadPaddingException e) {
            e.printStackTrace();
        }
        return null;
    }
}
