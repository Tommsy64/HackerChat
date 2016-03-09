package io.github.tommsy64.netchat.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import io.github.tommsy64.netchat.user.EncryptedUser;
import io.github.tommsy64.netchat.user.Encryptor;
import io.github.tommsy64.netchat.user.User;
import lombok.Getter;
import lombok.NonNull;

public class Server {

    private final String key;
    @Getter
    private final short port;
    @Getter
    private final ServerSocket listener;

    public Server(short port, @NonNull String key) throws IOException {
        this.key = Encryptor.hashKey(key);
        this.port = port;
        listener = new ServerSocket(port);
    }

    public void start() throws IOException {
        try {
            while (true) {
                UserHandler uh = new UserHandler(new EncryptedUser(listener.accept(), key));
                uh.start();
            }
        } finally {
            listener.close();
        }
    }

    // Stupid java won't let me put this inside the UserHandler class because it's "not defined as a constant value."
    // It's not MY FAULT the stupid java makes SynchronizedRandomAccessList<T> a PRIVATE STATIC INNER CLASS!!!
    private List<User> users = Collections.synchronizedList(new ArrayList<User>());

    private class UserHandler extends Thread {
        private User user;

        public UserHandler(User user) {
            this.user = user;
        }

        public void run() {
            try {
                login();
                final String username = user.getUsername();
                while (true) {
                    String msg = username + ": " + user.recieve();
                    System.out.println(msg);
                    synchronized (users) {
                        Iterator<User> i = users.iterator();
                        while (i.hasNext())
                            i.next().send(msg);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private boolean login() throws IOException {
            while (true) {
                user.send("Username: ");
                String recieve = user.recieve();
                if (recieve != null && recieve != "") {
                    user.setUsername(recieve);
                    return true;
                }
            }
        }
    }
}
