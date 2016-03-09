package io.github.tommsy64.netchat.user;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import lombok.NonNull;

public class User {
    private final Socket socket;

    private final Object readLock = new Object[0];
    private final ObjectInputStream in;
    private final ObjectOutputStream out;

    private String username;

    public User(@NonNull Socket socket) throws IOException {
        this.socket = socket;
        this.in = new ObjectInputStream(socket.getInputStream());
        this.out = new ObjectOutputStream(socket.getOutputStream());
    }

    public synchronized void send(Message msg) throws IOException {
        out.writeObject(msg);
    }

    public Message recieve() throws IOException, ClassNotFoundException {
        // This may be a horribly wrong spot to put a lock
        synchronized (readLock) {
            Object data = in.readObject();
            if (data instanceof Message)
                return (Message) data;
            else
                return null;
        }
    }

    public synchronized void setUsername(String username) {
        this.username = username;
    }

    public synchronized String getUsername() {
        return this.username;
    }

    public synchronized void close() throws IOException {
        this.socket.close();
    }
}
