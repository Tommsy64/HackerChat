package io.github.tommsy64.netchat;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.Scanner;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.KryoException;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.serializers.DefaultSerializers;

import io.github.tommsy64.netchat.arguments.ClientArguments;
import lombok.Cleanup;

public class Client extends Thread {

    private final Socket socket;
    private final Serializer<String> serializer;
    private final Kryo kryo = new Kryo();

    private final Input in;
    private final Output out;

    public Client(ClientArguments args) throws IOException {
        this.socket = new Socket(args.getHost(), args.getPort());
        if (args.getPassword() == null || args.getPassword().isEmpty())
            this.serializer = new DefaultSerializers.StringSerializer();
        else
            this.serializer = new EncryptionSerializer(Encryptor.hashKey(args.getPassword()));
        this.in = new Input(socket.getInputStream());
        this.out = new Output(socket.getOutputStream());
    }

    public Client(Socket socket) throws IOException {
        this(socket, null);
    }

    public Client(Socket socket, String key) throws IOException {
        this.socket = socket;
        if (key == null || key.isEmpty())
            this.serializer = new DefaultSerializers.StringSerializer();
        else
            this.serializer = new EncryptionSerializer(key);
        this.in = new Input(socket.getInputStream());
        this.out = new Output(socket.getOutputStream());
    }

    public void run() {
        try {
            while (!socket.isClosed()) {
                String message = kryo.readObject(in, String.class, serializer);
                System.out.println(message);
            }
        } catch (KryoException e) {
            if (!(e.getCause() instanceof SocketException))
                throw e;
        }
    }

    @Override
    public void start() {
        super.start();
        @Cleanup
        Scanner in = new Scanner(System.in);
        String input;
        do {
            kryo.writeObject(out, input = in.nextLine());
            out.flush();
        } while (this.isAlive() && !(input.toLowerCase().startsWith("..q") || input.equalsIgnoreCase("..exit")));
        try {
            socket.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
