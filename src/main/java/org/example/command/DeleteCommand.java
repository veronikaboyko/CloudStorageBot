package org.example.command;

import org.example.internal.ConstantManager;
import org.example.internal.FileManager;
import org.example.state.State;
import org.example.state.StateSwitcher;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.io.IOException;

import static org.example.state.State.ON_COMMAND_FROM_USER;

/**
 * Команда /delete.
 */
public class DeleteCommand extends AbstractCommand
{
    private final FileManager fileManager;

    public DeleteCommand(FileManager fileManager)
    {
        super(new StateSwitcher(ON_COMMAND_FROM_USER));
        this.fileManager = fileManager;
    }

    @Override
    public CommandResult handle(Message messageFromUser, String chatId, State state) throws IOException
    {
        if (!(messageFromUser.hasText()))
            return new CommandResult(new SendMessage(chatId, ConstantManager.NOT_SUPPORT_FILE_FORMAT), false);
        final String stringContent = messageFromUser.getText();
        String[] arguments = getSplitArguments(stringContent);
        if (!checkArgumentsCount(2, arguments))
        {
            return new CommandResult(new SendMessage(chatId, ConstantManager.NO_FILE_NAME_FOUND), true);
        }
        if (!arguments[0].equals("/delete"))
            return new CommandResult(new SendMessage(chatId, "Некорректная команда!"), false);
        final String fileName = arguments[1];
        try
        {
            fileManager.deleteFile(fileName, chatId);
            return new CommandResult(new SendMessage(chatId, "Файл успешно удален."), true);
        }
        catch (IOException e)
        {
            if (e.getMessage().equals(ConstantManager.NO_SUCH_FILE_EXISTS))
                return new CommandResult(new SendMessage(chatId, "Не удалось удалить файл %s. ".formatted(fileName)
                        + ConstantManager.NO_SUCH_FILE_EXISTS), true);
            throw new IOException("Не удалось удалить файл %s. ".formatted(fileName) + e.getMessage(), e);
        }
    }
}
