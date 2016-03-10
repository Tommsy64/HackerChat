package io.github.tommsy64.netchat;

import java.io.IOException;
import java.util.Scanner;

import com.beust.jcommander.JCommander;

import io.github.tommsy64.netchat.arguments.ClientArguments;
import io.github.tommsy64.netchat.arguments.NetchatArguments;
import io.github.tommsy64.netchat.arguments.ServerArguments;
import lombok.Cleanup;

public class Netchat {
    public static void main(String[] args) {
        NetchatArguments mainArgs = new NetchatArguments();
        ClientArguments clientArgs = new ClientArguments();
        ServerArguments serverArgs = new ServerArguments();
        JCommander jc = new JCommander(mainArgs);
        jc.addCommand("server", serverArgs, "serve", "s");
        jc.addCommand("client", clientArgs, "connect", "join", "c");
        jc.parse(args);
        if (jc.getParsedCommand() == null) {
            if (mainArgs.isHelp() || args.length == 0)
                jc.usage();
            else if (mainArgs.isVersion())
                System.out.println("Netchat by version ${ project.version } Tommsy64");
            return;
        }

        if (jc.getParsedCommand().equalsIgnoreCase("client")) {
            if (clientArgs.isHelp()) {
                jc.usage("client");
                return;
            }
            Client client = null;
            try {
                client = new Client(clientArgs);
                client.start();
            } catch (IOException e) {
                System.err.print("Error creating client: " + e.getLocalizedMessage());
            }
        } else if (jc.getParsedCommand().equalsIgnoreCase("server")) {
            if (serverArgs.isHelp()) {
                jc.usage("server");
                return;
            }
            Server server = null;
            try {
                server = new Server(serverArgs);
                System.out.println("Listening on " + serverArgs.getPort());
                server(server);
            } catch (IOException e) {
                System.err.print("Error creating server: " + e.getLocalizedMessage());
            }
        }
    }

    private static void server(final Server server) {
        server.setName("server");
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                tryClose(server);
            }
        });
        server.start();
        System.out.println("Message processing server started!");
        @Cleanup
        Scanner in = new Scanner(System.in);
        String input;
        do
            input = in.nextLine();
        while (!(input.equalsIgnoreCase("exit") || input.equalsIgnoreCase("quit")));
        tryClose(server);
    }

    private static void tryClose(Server server) {
        if (server != null && !server.isClosed())
            try {
                server.close();
                System.out.println("\nServer closed");
            } catch (IOException e) {
                System.err.println("\nError closing server: " + e.getLocalizedMessage());
                e.printStackTrace();
            }
    }
}
