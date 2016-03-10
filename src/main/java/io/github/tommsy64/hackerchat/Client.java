package io.github.tommsy64.hackerchat;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.Scanner;

import io.github.tommsy64.hackerchat.arguments.ClientArguments;
import lombok.Cleanup;

public class Client extends Thread {

    private final Socket socket;
    private final Serializer<String> serializer;

    private final InputStream in;
    private final OutputStream out;

    public Client(ClientArguments args) throws IOException {
        this.socket = new Socket(args.getHost(), args.getPort());
        if (args.getPassword() == null || args.getPassword().isEmpty())
            this.serializer = new StringSerializer();
        else
            this.serializer = new EncryptionSerializer(Encryptor.hashKey(args.getPassword()));
        this.in = socket.getInputStream();
        this.out = socket.getOutputStream();
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
        this.in = socket.getInputStream();
        this.out = socket.getOutputStream();
    }

    public void run() {
        try {
            while (!socket.isClosed()) {
                byte[] buffer = new byte[in.read()];
                in.read(buffer);
                String message = serializer.deserialize(new String(buffer));
                System.out.println(message);
            }
        } catch (SocketException e) {

        } catch (IOException e) {
            e.printStackTrace();
        } catch (NegativeArraySizeException e) {

        } finally {
            try {
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
        try {
            @Cleanup
            Scanner in = new Scanner(System.in);
            String input;
            while (this.isAlive()) {
                try {
                    input = in.nextLine();
                    if (input.toLowerCase().startsWith("..q") || input.equalsIgnoreCase("..exit"))
                        break;
                    StreamHelper.writeString(out, serializer.serialize(input));
                } catch (IOException e) {

                }
            }
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                System.err.println("Error closing socket: " + e.getLocalizedMessage());
                e.printStackTrace();
            }
        }
    }
}
