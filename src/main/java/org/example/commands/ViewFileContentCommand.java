package org.example.commands;

import org.example.internal.ArgumentChecker;
import org.example.internal.FileManager;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

public class ViewFileContentCommand implements Command
{
    private final FileManager fileManager;
    private final ArgumentChecker argumentChecker;

    public ViewFileContentCommand()
    {
        fileManager = new FileManager();
        argumentChecker = new ArgumentChecker();
    }

    @Override
    public BotApiMethod handle(String messageFromUser, String chatId)
    {
        if (!argumentChecker.checkArguments(2, messageFromUser))
        {
            return new SendMessage(chatId, argumentChecker.fileNameParameter);
        }

        String fileName = messageFromUser.split("\\s+")[1];
        if (!fileManager.isValidFileName(fileName))
        {
            return new SendMessage(chatId, "Некорректное название файла!");
        }
        if (!fileManager.existsFile(fileName, chatId))
        {
            return new SendMessage(chatId, "Файла с таким названием не существует!");
        }
        return new SendMessage(chatId,fileManager.getFileContent(fileName,chatId));
    }

}
