package org.example.commands;

import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import org.example.internal.ArgumentChecker;
import org.example.internal.FileManager;

import java.io.IOException;

/**
 * Команда /delete.
 */
public class DeleteCommand implements Command {
    private ArgumentChecker argumentChecker;
    private FileManager fileManager;

    public DeleteCommand() {
        argumentChecker = new ArgumentChecker();
        fileManager = new FileManager();
    }

    @Override
    public BotApiMethod handle(String messageFromUser, String chatId) {

        if (!argumentChecker.checkArguments(2, messageFromUser)) {
            return new SendMessage(chatId, "В качестве параметра укажите название файла.");
        }

        String fileName = messageFromUser.split("\\s+")[1];
        try {
            fileManager.deleteFile(fileName, chatId);
            return new SendMessage(chatId, "Файл успешно удален.");
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return new SendMessage(chatId, e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            return new SendMessage(chatId, "Не удалось удалить файл.");
        }
    }
}
