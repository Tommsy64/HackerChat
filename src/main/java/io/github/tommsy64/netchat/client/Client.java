package io.github.tommsy64.netchat.client;

import java.io.IOException;
import java.util.Scanner;

import io.github.tommsy64.netchat.user.User;
import lombok.Cleanup;

public class Client {
    private User user;

    public Client(User user) {
        this.user = user;
    }

    public void start() {
        Thread thread = new Thread(receiving, user + "-receiving");
        thread.start();

        @Cleanup
        Scanner in = new Scanner(System.in);
        running = true;
        while (running) {
            user.send(in.nextLine());
        }
    }

    private volatile boolean running;

    private final Runnable receiving = new Runnable() {
        public void run() {
            while (running) {
                try {
                    System.out.println(user.recieve());
                } catch (IOException e) {
                    running = false;
                }
            }
        }
    };
}
