package org.example.command;


import javassist.NotFoundException;
import org.example.internal.ConstantManager;
import org.example.internal.FileManager;
import org.example.state.State;
import org.example.state.StateSwitcher;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

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
    public CommandResult handle(Message messageFromUser, String chatId, State state) throws IOException
    {
        if (!(messageFromUser.hasText()))
            return new CommandResult(new SendMessage(chatId, ConstantManager.NOT_SUPPORT_FILE_FORMAT), false);
        final String stringContent = messageFromUser.getText();
        String searchString = stringContent.substring(10);

        if (searchString.isEmpty())
        {
            return new CommandResult(new SendMessage(chatId, ConstantManager.NO_SEARCH_STRING), true);
        }
        try
        {
            String listFiles = fileManager.findFilesBySearchString(chatId, searchString, true);
            return new CommandResult(new SendMessage(chatId, "По запросу “%s” найдены следующие файлы:\n".formatted(searchString) + listFiles), true);
        }
        catch (NotFoundException e)
        {
            return new CommandResult(new SendMessage(chatId, e.getMessage()), true);
        }
    }

}
