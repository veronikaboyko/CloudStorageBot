package org.example.commands;

import org.example.internal.ArgumentChecker;
import org.example.internal.FileManager;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.io.IOException;

/**
 * Команда /create.
 */
public class CreateCommand implements Command
{
    private final ArgumentChecker argumentChecker;
    private final FileManager fileManager;

    public CreateCommand()
    {
        argumentChecker = new ArgumentChecker();
        fileManager = new FileManager();
    }

    @Override
    public BotApiMethod handle(String messageFromUser, String chatId)
    {
        if (!argumentChecker.checkArguments(2, messageFromUser))
        {
            return new SendMessage(chatId, argumentChecker.fileNameParameter);
        }

        String fileName = messageFromUser.split("\\s+")[1];
        try
        {
            fileManager.createFile(fileName, chatId);
            return new SendMessage(chatId, "Файл успешно создан.");
        }
        catch (IllegalArgumentException e)
        {
            e.printStackTrace();
            return new SendMessage(chatId, e.getMessage());
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return new SendMessage(chatId, "Не удалось создать файл.");
        }
    }
}
