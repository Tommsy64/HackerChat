package io.github.tommsy64.hackerchat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;

import io.github.tommsy64.hackerchat.arguments.ClientArguments;
import io.github.tommsy64.hackerchat.serial.EncryptionSerializer;
import io.github.tommsy64.hackerchat.serial.Serializer;
import io.github.tommsy64.hackerchat.serial.StringSerializer;
import io.github.tommsy64.hackerchat.util.Encryptor;

public class Client extends Thread {

    private final Socket socket;
    private final Serializer<String> serializer;

    private final BufferedReader in;
    private final PrintWriter out;

    public Client(ClientArguments args) throws IOException {
        this.socket = new Socket(args.getHost(), args.getPort());
        if (args.getPassword() == null || args.getPassword().isEmpty())
            this.serializer = new StringSerializer();
        else
            this.serializer = new EncryptionSerializer(Encryptor.hashKey(args.getPassword()));
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.out = new PrintWriter(socket.getOutputStream(), true);
    }

    public Client(Socket socket) throws IOException {
        this(socket, null);
    }

    public Client(Socket socket, String key) throws IOException {
        this.socket = socket;
        if (key == null || key.isEmpty())
            this.serializer = new StringSerializer();
        else
            this.serializer = new EncryptionSerializer(key);
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.out = new PrintWriter(socket.getOutputStream(), true);
    }

    @Override
    public void run() {
        try {
            String message;
            while (!socket.isClosed()) {
                message = in.readLine();
                if (message == null)
                    break;
                System.out.print(serializer.deserialize(message));
            }
            System.out.println("Server closed.");
        } catch (SocketException e) {
            System.out.println("Server error.");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (socket != null)
                    socket.close();
            } catch (IOException e) {
                System.err.println("Error closing socket: " + e.getLocalizedMessage());
                e.printStackTrace();
            }
        }
    }

    @Override
    public void start() {
        super.start();
        final BufferedReader console = new BufferedReader(new InputStreamReader(System.in));
        String input;
        outer: while (this.isAlive() && !socket.isClosed()) {
            try {
                while (!console.ready())
                    if (socket.isClosed())
                        break outer;
                    else
                        Thread.sleep(10);
                input = console.readLine();
                if (input.toLowerCase().startsWith("..q") || input.equalsIgnoreCase("..exit"))
                    Client.this.socket.close();
                String serialized = serializer.serialize(input);
                out.println(serialized);
            } catch (SocketException e) {

            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }
}
