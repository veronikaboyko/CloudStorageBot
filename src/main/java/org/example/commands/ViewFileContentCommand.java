package org.example.commands;

import org.example.internal.ConstantManager;
import org.example.internal.FileManager;
import org.example.state.State;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.io.FileNotFoundException;
import java.io.IOException;

public class ViewFileContentCommand extends AbstractCommand implements OneStateCommand
{
    private final FileManager fileManager;

    public ViewFileContentCommand()
    {
        fileManager = new FileManager();
    }

    @Override
    public BotApiMethod handle(String messageFromUser, String chatId, State state) throws IOException
    {
        if (!checkArgumentsCount(2, messageFromUser))
        {
            throw new IOException(ConstantManager.NO_FILE_NAME_FOUND);
        }
        final String fileName = messageFromUser.split("\\s+")[1];
        if (!fileManager.isValidFileName(fileName))
        {
            throw new IOException("Некорректное название файла!");
        }
        if (!fileManager.existsFile(fileName, chatId))
        {
            throw new IOException("Файла с таким названием не существует!");
        }
        try
        {
            final String fileContent = fileManager.getFileContent(fileName, chatId);
            if (!fileContent.isEmpty())
                return new SendMessage(chatId, fileContent);
            else
                return new SendMessage(chatId, "Файл пуст!");
        }
        catch (FileNotFoundException ex)
        {
            ex.printStackTrace();
            throw new IOException("Внутрення ошибка при работе с файлом!");
        }
    }
}
