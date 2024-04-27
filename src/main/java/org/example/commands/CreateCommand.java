package org.example.commands;

import org.example.internal.ConstantManager;
import org.example.internal.FileManager;
import org.example.state.State;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.io.IOException;

/**
 * Команда /create.
 */
public class CreateCommand extends AbstractCommand implements OneStateCommand
{
    private final FileManager fileManager;

    public CreateCommand()
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
            fileManager.createFile(fileName, chatId);
            return new SendMessage(chatId, "Файл успешно создан.");
        }
        catch (IOException e)
        {
            e.printStackTrace();
            throw new IOException(e.getMessage());
        }
    }
}
