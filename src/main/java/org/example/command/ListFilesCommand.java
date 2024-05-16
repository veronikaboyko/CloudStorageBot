package org.example.command;

import org.example.bot.user.UserMessage;
import org.example.internal.ConstantManager;
import org.example.internal.FileManager;
import org.example.state.State;
import org.example.state.StateSwitcher;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

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
    public CommandResult handle(UserMessage<?> messageFromUser, String chatId, State state) throws IOException
    {
        try {
            String listFiles = fileManager.getListFiles(chatId);
            return new CommandResult(new SendMessage(chatId, "Список ваших файлов:\n" + listFiles), true);
        } catch (IOException e) {
            if (e.getMessage().equals(ConstantManager.NO_USER_FILES_FOUND))
                return new CommandResult(new SendMessage(chatId, "У вас пока еще нет файлов."), true);
            throw new IOException("Не удалось получить список файлов. " + e.getMessage(), e);
        }
    }
}

