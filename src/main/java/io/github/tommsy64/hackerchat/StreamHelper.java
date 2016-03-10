package io.github.tommsy64.hackerchat;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class StreamHelper {
    public static void writeString(OutputStream out, String str) throws IOException {
        byte[] data = str.getBytes();
        out.write(data.length);
        out.write(data);
        out.flush();
    }

    public static String readString(InputStream in) throws IOException {
        int size = in.read();
        if (size == -1)
            return null;
        byte[] bytes = new byte[size];
        if (in.read(bytes) == -1)
            return null;
        return new String(bytes);
    }
}
