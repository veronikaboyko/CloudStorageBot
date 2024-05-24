package org.example.command;

import javassist.NotFoundException;
import org.example.internal.FileManager;
import org.example.state.State;
import org.example.state.StateSwitcher;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.io.IOException;

/**
 * Команда /listFiles
 */
public class ListFilesCommand extends AbstractCommand
{
    private final FileManager fileManager;

    public ListFilesCommand(FileManager fileManager)
    {
        super(new StateSwitcher(State.ON_COMMAND_FROM_USER));
        this.fileManager = fileManager;
    }

    @Override
    public CommandResult handle(Message messageFromUser, String chatId, State state) throws IOException
    {
        try {
            String listFiles = fileManager.getListFiles(chatId);
            return new CommandResult(new SendMessage(chatId, "Список ваших файлов:\n" + listFiles), true);
        } catch (NotFoundException e) {
            return new CommandResult(new SendMessage(chatId, e.getMessage()), true);
        }
    }
}

