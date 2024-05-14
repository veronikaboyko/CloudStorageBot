package org.example.command;


import org.example.internal.ConstantManager;
import org.example.internal.FileManager;
import org.example.state.State;
import org.example.state.StateSwitcher;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.io.IOException;

/**
 * Команда /findFile
 */
public class FindFileCommand extends AbstractCommand
{
    private final FileManager fileManager;

    public FindFileCommand(FileManager fileManager)
    {
        super(new StateSwitcher(State.ON_COMMAND_FROM_USER));
        this.fileManager = fileManager;
    }

    @Override
    public CommandResult handle(String messageFromUser, String chatId, State state) throws IOException
    {
        String searchString = messageFromUser.substring(10);

        if (searchString.isEmpty())
        {
            return new CommandResult(new SendMessage(chatId, ConstantManager.NO_SEARCH_STRING), true);
        }
        try
        {
            String listFiles = fileManager.getListFiles(chatId, searchString, true);
            return new CommandResult(new SendMessage(chatId, "По запросу “%s” найдены следующие файлы:\n".formatted(searchString) + listFiles), true);
        }
        catch (IOException e)
        {
            if (e.getMessage().equals(ConstantManager.NO_USER_FILES_FOUND))
                return new CommandResult(new SendMessage(chatId, "По запросу “%s” не найдено файлов.".formatted(searchString)), true);
            throw new IOException("Не удалось получить список файлов. " + e.getMessage(), e);
        }
    }

}
