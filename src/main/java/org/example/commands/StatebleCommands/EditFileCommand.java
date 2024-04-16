package org.example.commands.StatebleCommands;

import org.example.internal.ArgumentChecker;
import org.example.internal.FileManager;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.io.IOException;

/**
 * Команда /editFile
 */
public class EditFileCommand implements StatebleCommand
{
    private final ArgumentChecker argumentChecker;
    private final FileManager fileManager;
    private UserStatement currentUserStatement = UserStatement.STATE_1;
    private String fileToEdit = null;

    public EditFileCommand()
    {
        argumentChecker = new ArgumentChecker();
        fileManager = new FileManager();
    }

    @Override
    public BotApiMethod handle(String messageFromUser, String chatId)
    {
        if (!argumentChecker.checkArguments(2, messageFromUser) && currentUserStatement == UserStatement.STATE_1)
        {
            return new SendMessage(chatId, argumentChecker.fileNameParameter);
        }
        String fileName = null;
        if (currentUserStatement == UserStatement.STATE_1)
        {
            fileName = messageFromUser.split("\\s+")[1];
        }
        BotApiMethod forUser = null;
        switch (currentUserStatement)
        {
            case STATE_1:
                if (!fileManager.isValidFileName(fileName))
                {
                    forUser = new SendMessage(chatId, "Некорректное название файла!");
                    break;
                }
                if (!fileManager.existsFile(fileName, chatId))
                {
                    forUser = new SendMessage(chatId, "Файла с таким названием не существует!");
                    break;
                }
                currentUserStatement = UserStatement.STATE_2;
                fileToEdit = fileName;
                forUser = new SendMessage(chatId, "Введите новое содержимое файла.");
                break;
            case STATE_2:
                try
                {
                    fileManager.editFile(fileToEdit, chatId, messageFromUser);
                    forUser = new SendMessage(chatId, "Файл успешно сохранен.");
                    currentUserStatement = UserStatement.LAST_STATE;
                }
                catch (IOException exception)
                {
                    forUser = new SendMessage(chatId, "Ошибка при работе с файлом.");
                }
                break;
        }
        return forUser;
    }

    @Override
    public boolean onLastState()
    {
        return currentUserStatement == UserStatement.LAST_STATE;
    }

    @Override
    public void toStart()
    {
        currentUserStatement = UserStatement.STATE_1;
        fileToEdit = null;
    }
}
