package io.github.tommsy64.hackerchat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import io.github.tommsy64.hackerchat.arguments.ServerArguments;
import io.github.tommsy64.hackerchat.serial.EncryptionSerializer;
import io.github.tommsy64.hackerchat.serial.Serializer;
import io.github.tommsy64.hackerchat.serial.StringSerializer;
import io.github.tommsy64.hackerchat.util.Encryptor;
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
        private final BufferedReader input;
        private final PrintWriter output;

        @Getter
        private volatile String username;

        private UserHandler(Socket socket) throws IOException {
            this.socket = socket;
            this.input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.output = new PrintWriter(socket.getOutputStream(), true);
            userHandlers.add(this);
        }

        @Override
        public void run() {
            try {
                while (true) {
                    output.println(serializer.serialize("Username: "));
                    username = serializer.deserialize(input.readLine());
                    if (username == null || username.isEmpty())
                        continue;
                    output.print(serializer.serialize("Username set to " + username + "\n"));
                    break;
                }

                while (!isClosed() && !this.socket.isClosed()) {
                    String recived = serializer.deserialize(input.readLine());
                    if (recived == null)
                        break;
                    String sendData = new StringBuilder().append(username).append(": ").append(recived).append("\n").toString();
                    if (log)
                        System.out.print(sendData);
                    synchronized (userHandlers) {
                        Iterator<UserHandler> i = userHandlers.iterator(); // Must be in synchronized block
                        while (i.hasNext()) {
                            UserHandler uh = i.next();
                            if (uh == this)
                                continue;
                            uh.getOutput().println(serializer.serialize(sendData));
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
                if (log)
                    System.out.println(username + " disconnected.");
            }
        }

        public synchronized PrintWriter getOutput() {
            return this.output;
        }

        private void close() throws IOException {
            this.socket.close();
        }
    }
}
