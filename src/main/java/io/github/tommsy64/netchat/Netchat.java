package io.github.tommsy64.netchat;

import java.io.IOException;
import java.net.NoRouteToHostException;
import java.net.Socket;
import java.util.Scanner;

import io.github.tommsy64.netchat.client.Client;
import io.github.tommsy64.netchat.server.Server;
import io.github.tommsy64.netchat.user.EncryptedUser;
import lombok.Cleanup;

public class Netchat {
    public static void main(String[] args) {
        
        
        
        
        runServer();
        System.exit(0);
        if (args.length == 0) {
            runClient();
        } else if (args[0].toLowerCase().startsWith("s")) {
            runServer();
        }
    }

    private static void runServer() {
        System.out.println("*** Netchat Server ***");
        @Cleanup
        Scanner in = new Scanner(System.in);

        System.out.print("Port: ");
        short port = 8888;
        try {
            port = Short.parseShort(in.nextLine());
        } catch (NumberFormatException e) {
            System.err.println("Invalid port!");
            System.exit(1);
        }
        System.out.print("Key: ");
        String key = in.nextLine();
        try {
            Server server = new Server(port, key);
            System.out.println("Starting server...");
            server.start();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    private static void runClient() {
        System.out.println("*** Netchat Client ***");
        @Cleanup
        Scanner in = new Scanner(System.in);

        System.out.print("Host: ");
        String host = in.nextLine();
        System.out.print("Port: ");
        short port = 8888;
        try {
            port = Short.parseShort(in.nextLine());
        } catch (NumberFormatException e) {
            System.err.println("Invalid port!");
            System.exit(1);
        }
        System.out.print("Key: ");
        String key = in.nextLine();
        try {
            System.out.println("Attempting to connect...");
            Socket socket = null;
            try {
                socket = new Socket(host, port);
            } catch (NoRouteToHostException e) {
                System.err.println("Connection failed: " + e.getLocalizedMessage());
                System.exit(1);
            }

            System.out.println("Connection succesful!");
            Client client = new Client(new EncryptedUser(socket, key));
            client.start();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
