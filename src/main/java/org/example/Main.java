package org.example;


import org.example.bot.TelegramBot;

public class Main {
    public static void main(String[] args) {
        String botUsername = System.getenv("botUsername");
        String botToken = System.getenv("botToken");

        new TelegramBot(botUsername, botToken).start();
    }
}