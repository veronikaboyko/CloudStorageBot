package org.example.command;

import org.example.internal.FileManager;
import org.example.state.State;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.io.IOException;

/**
 * Команда /listFiles
 */
public class ListFilesCommand extends AbstractCommand implements OneStateCommand
{
    private final FileManager fileManager;

    public ListFilesCommand()
    {
        fileManager = new FileManager();
    }

    @Override
    public BotApiMethod<Message> handle(String messageFromUser, String chatId, State state) throws IOException
    {
        try {
            String listFiles = fileManager.getListFiles(chatId);
            return new SendMessage(chatId, "Список ваших файлов:\n" + listFiles);
        } catch (IOException e) {
            throw new IOException("Внутрення ошибка при работе с файлами.");
        }
    }
}
