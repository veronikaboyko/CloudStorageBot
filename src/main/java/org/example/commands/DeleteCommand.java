package org.example.commands;

import org.example.internal.ConstantManager;
import org.example.state.State;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import org.example.internal.FileManager;

import java.io.IOException;

/**
 * Команда /delete.
 */
public class DeleteCommand extends AbstractCommand implements OneStateCommand
{
    private final FileManager fileManager;

    public DeleteCommand()
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
        try
        {
            fileManager.deleteFile(fileName, chatId);
            return new SendMessage(chatId, "Файл успешно удален.");
        }
        catch (IOException e)
        {
            e.printStackTrace();
            throw new IOException("Ошибка при работе с файлом!");
        }
    }
}
