package org.example.commands;

import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * Команда /start.
 */
public class StartCommand implements Command {

    /**
     * Метод для получения приветственного сообщения из файла.
     * @return Строка, содержащая приветственное сообщение.
     */
    private String getStartMessage() {
        String START_COMMAND_FILE = "src/main/resources/startCommandFile.txt";
        StringBuilder stringBuilder = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(START_COMMAND_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }
    @Override
    public BotApiMethod handle(String messageFromUser, String chatId) {
        String messageToSend = getStartMessage();
        return new SendMessage(chatId, messageToSend);
    }
}
