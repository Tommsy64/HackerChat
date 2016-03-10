package io.github.tommsy64.netchat;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.KryoException;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.serializers.DefaultSerializers;

import io.github.tommsy64.netchat.arguments.ServerArguments;
import lombok.Getter;
import lombok.Setter;

public class Server extends Thread {

    private final ServerSocket server;
    private final Serializer<String> serializer;
    private final Kryo kryo = new Kryo();
    @Setter
    @Getter
    private volatile boolean log;

    public Server(ServerArguments args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(args.getPort());
        this.server = serverSocket;
        if (args.getPassword() == null || args.getPassword().isEmpty())
            this.serializer = new DefaultSerializers.StringSerializer();
        else
            this.serializer = new EncryptionSerializer(Encryptor.hashKey(args.getPassword()));
        this.log = !args.isNoLog();
    }

    public Server(ServerSocket server) {
        this.server = server;
        this.serializer = new DefaultSerializers.StringSerializer();
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
        private final Input input;
        private final Output output;

        @Getter
        private volatile String username;

        private UserHandler(Socket socket) throws IOException {
            this.socket = socket;
            this.input = new Input(socket.getInputStream());
            this.output = new Output(socket.getOutputStream());
            userHandlers.add(this);
        }

        private static final int BUFFER_SIZE = 2048;

        public void run() {
            try {
                // username = kryo.readObject(input, String.class, serializer);

                while (!isClosed() && !this.socket.isClosed()) {
                    String data = kryo.readObject(input, String.class, serializer);
                    if (log)
                        System.out.println(data);
                    synchronized (userHandlers) {
                        Iterator<UserHandler> i = userHandlers.iterator(); // Must be in synchronized block
                        while (i.hasNext()) {
                            Output o = i.next().getOutput();
                            // o.writeString(data);
                            kryo.writeObject(o, data, serializer); //TEMP
                            o.flush();
                        }
                    }
                }
            } catch (KryoException e) {
                if (!(e.getCause() instanceof SocketException))
                    throw e;
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

        public synchronized Output getOutput() {
            return this.output;
        }

        private void close() throws IOException {
            this.socket.close();
        }
    }
}
