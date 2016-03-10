package io.github.tommsy64.hackerchat;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import io.github.tommsy64.hackerchat.arguments.ServerArguments;
import lombok.Getter;
import lombok.Setter;

public class Server extends Thread {

    private final ServerSocket server;
    private final Serializer<String> serializer;
    @Setter
    @Getter
    private volatile boolean log;

    public Server(ServerArguments args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(args.getPort());
        this.server = serverSocket;
        if (args.getPassword() == null || args.getPassword().isEmpty())
            this.serializer = new StringSerializer();
        else
            this.serializer = new EncryptionSerializer(Encryptor.hashKey(args.getPassword()));
        this.log = !args.isNoLog();
    }

    public Server(ServerSocket server) {
        this.server = server;
        this.serializer = new StringSerializer();
    }

    public Server(ServerSocket server, String key) {
        this.server = server;
        this.serializer = new EncryptionSerializer(key);
    }

    @Override
    public void run() {
        try {
            while (!isClosed()) {
                new UserHandler(server.accept()).start();
                ;
                System.out.println("Accepted new connection. (" + userHandlers.size() + " total)");
            }
        } catch (SocketException e) {

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void close() throws IOException {
        this.server.close();
        synchronized (userHandlers) {
            Iterator<UserHandler> i = userHandlers.iterator(); // Must be in synchronized block
            while (i.hasNext())
                i.next().close();
        }
        userHandlers.clear();
    }

    public boolean isClosed() {
        return this.server.isClosed();
    }

    private List<UserHandler> userHandlers = Collections.synchronizedList(new ArrayList<UserHandler>());

    private class UserHandler extends Thread {

        private final Socket socket;
        private final InputStream input;
        private final OutputStream output;

        @Getter
        private volatile String username;

        private UserHandler(Socket socket) throws IOException {
            this.socket = socket;
            this.input = socket.getInputStream();
            this.output = socket.getOutputStream();
            userHandlers.add(this);
        }

        public void run() {
            try {
                // username = kryo.readObject(input, String.class, serializer);

                while (!isClosed() && !this.socket.isClosed()) {
                    final String sendData = serializer.deserialize(StreamHelper.readString(input));
                    if (sendData == null)
                        continue;
                    if (log)
                        System.out.println(sendData);
                    synchronized (userHandlers) {
                        Iterator<UserHandler> i = userHandlers.iterator(); // Must be in synchronized block
                        while (i.hasNext())
                            try {
                                StreamHelper.writeString(i.next().getOutput(), serializer.serialize(sendData));
                            } catch (IOException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                    }
                }
            } catch (SocketException e) {

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                int i = userHandlers.indexOf(this);
                if (i >= 0)
                    userHandlers.remove(i);
            }
        }

        public synchronized OutputStream getOutput() {
            return this.output;
        }

        private void close() throws IOException {
            this.socket.close();
        }
    }
}
