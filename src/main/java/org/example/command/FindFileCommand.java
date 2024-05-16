package org.example.command;


import org.example.bot.user.StringMessage;
import org.example.bot.user.UserMessage;
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
    public CommandResult handle(UserMessage<?> messageFromUser, String chatId, State state) throws IOException
    {
        if (!(messageFromUser instanceof StringMessage))
            return new CommandResult(new SendMessage(chatId, ConstantManager.NOT_SUPPORT_FILE_FORMAT), false);
        final String stringContent = ((StringMessage) messageFromUser).getContent();
        String searchString = stringContent.substring(10);

        if (searchString.isEmpty())
        {
            return new CommandResult(new SendMessage(chatId, ConstantManager.NO_SEARCH_STRING), true);
        }
        try
        {
            String listFiles = fileManager.findFilesByContent(chatId, searchString);
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