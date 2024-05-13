package org.example.command;

import org.example.internal.ConstantManager;
import org.example.internal.FileManager;
import org.example.state.State;
import org.example.state.StateSwitcher;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.InputFile;

import java.io.File;
import java.io.IOException;

/**
 * Команда /getFile
 */
public class GetFileCommand extends AbstractCommand
{
    private final FileManager fileManager;

    public GetFileCommand(FileManager fileManager)
    {
        super(new StateSwitcher(State.ON_COMMAND_FROM_USER));
        this.fileManager = fileManager;
    }

    @Override
    public CommandResult handle(String messageFromUser, String chatId, State state) throws IOException
    {
        String[] arguments = getSplitArguments(messageFromUser);

        if (!checkArgumentsCount(2, arguments))
        {
            return new CommandResult(new SendMessage(chatId, ConstantManager.NO_FILE_NAME_FOUND), true);
        }
        final String fileName = arguments[1];
        try
        {
            File fileToSend = fileManager.getFile(fileName, chatId);
            return new CommandResult(new SendDocument(chatId, new InputFile(fileToSend)), true);
        }
        catch (IOException e)
        {
            if (e.getMessage().equals(ConstantManager.NO_SUCH_FILE_EXISTS))
                return new CommandResult(new SendMessage(chatId, "Не удалось отправить файл %s. ".formatted(fileName)
                        + ConstantManager.NO_SUCH_FILE_EXISTS), true);
            throw new IOException("Не удалось отправить файл %s. ".formatted(fileName) + e.getMessage(), e);
        }
    }


}
