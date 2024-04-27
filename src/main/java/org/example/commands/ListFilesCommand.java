package org.example.commands;

import org.example.internal.DirectoryManager;
import org.example.state.State;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.io.IOException;

/**
 * Команда /listFiles
 */
public class ListFilesCommand extends AbstractCommand implements OneStateCommand
{

    @Override
    public BotApiMethod handle(String messageFromUser, String chatId, State state) throws IOException
    {
        try
        {
            return new SendMessage(chatId, new DirectoryManager().listFiles(chatId));
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
            throw new IOException("Внутрення ошибка при работе с файлами!");
        }
    }
}
