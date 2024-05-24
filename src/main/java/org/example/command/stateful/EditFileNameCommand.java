package org.example.command.stateful;


import org.example.command.AbstractCommand;
import org.example.command.CommandResult;
import org.example.internal.ConstantManager;
import org.example.internal.FileManager;
import org.example.state.State;
import org.example.state.StateSwitcher;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.io.IOException;

/**
 * Команда /editFileName
 */

public class EditFileNameCommand extends AbstractCommand
{
    private final FileManager fileManager;
    private final Casher<String> fileNamesCasher = new FileNamesCasher();


    public EditFileNameCommand(FileManager fileManager)
    {
        super(new StateSwitcher(State.ON_DATA_FROM_USER));
        this.fileManager = fileManager;
    }

    @Override
    public CommandResult handle(Message messageFromUser, String chatId, State state) throws IOException
    {
        if (!(messageFromUser.hasText()))
            return new CommandResult(new SendMessage(chatId, ConstantManager.NOT_SUPPORT_FILE_FORMAT), false);
        final String stringContent = messageFromUser.getText();
        String[] arguments = getSplitArguments(stringContent);
        switch (state)
        {
            case ON_COMMAND_FROM_USER ->
            {
                if (!checkArgumentsCount(2, arguments))
                {
                    return new CommandResult(new SendMessage(chatId,ConstantManager.NO_FILE_NAME_FOUND), false);
                }
                if (!arguments[0].equals("/editFileName"))
                    return new CommandResult(new SendMessage(chatId, "Некорректная команда!"), false);
                final String fileName = arguments[1];
                if (!fileManager.isValidFileName(fileName))
                {
                    return new CommandResult(new SendMessage(chatId,ConstantManager.INCORRECT_FILE_NAME), false);
                }
                if (!fileManager.existsFile(fileName, chatId))
                {
                    return new CommandResult(new SendMessage(chatId,ConstantManager.NO_SUCH_FILE_EXISTS), false);
                }
                fileNamesCasher.add(chatId, fileName);
                return new CommandResult(new SendMessage(chatId, "Введите новое название файла."), true);
            }
            case ON_DATA_FROM_USER ->
            {
                if (!fileManager.isValidFileName(stringContent))
                {
                    return new CommandResult(new SendMessage(chatId,"Некорректное название файла."), false);
                }
                final String oldFileName = fileNamesCasher.getData(chatId);
                try
                {
                    fileManager.editFileName(oldFileName, chatId, stringContent);
                    fileNamesCasher.clearUserCash(chatId);
                    return new CommandResult(new SendMessage(chatId, oldFileName + " -> " + messageFromUser), true);
                }
                catch (IOException e)
                {
                    throw new IOException("Не удалось переименовать файл %s. ".formatted(oldFileName) + e.getMessage(), e);
                }
            }
            default -> throw new IOException(ConstantManager.BOT_BROKEN_INSIDE_MESSAGE);
        }
    }
}
