package org.example.command;

import org.example.internal.ConstantManager;
import org.example.internal.FileManager;
import org.example.state.State;
import org.example.state.StateSwitcher;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;


import java.io.FileNotFoundException;
import java.io.IOException;


/**
 * Просмтореть содержимое файла
 */
public class ViewFileContentCommand extends AbstractCommand
{
    private final FileManager fileManager;

    public ViewFileContentCommand(FileManager fileManager)
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
        if (!arguments[0].equals("/viewFileContent"))
            return new CommandResult(new SendMessage(chatId, "Некорректная команда!"), false);
        final String fileName = arguments[1];
        if (!fileManager.isValidFileName(fileName))
        {
            return new CommandResult(new SendMessage(chatId, ConstantManager.INCORRECT_FILE_NAME), true);
        }
        if (!fileManager.existsFile(fileName, chatId))
        {
            return new CommandResult(new SendMessage(chatId, ConstantManager.NO_SUCH_FILE_EXISTS), true);
        }
        try
        {
            final String fileContent = fileManager.getFileContent(fileName, chatId);
            if (!fileContent.isEmpty())
                return new CommandResult(new SendMessage(chatId, fileContent), true);
            else
                return new CommandResult(new SendMessage(chatId, "Файл пуст."), true);
        }
        catch (FileNotFoundException e)
        {
            throw new IOException("Не удалось получить содержимое файла %s. ".formatted(fileName) + e.getMessage(), e);
        }
    }
}
