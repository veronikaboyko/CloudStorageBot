package org.example.command.stateful;

import org.example.command.AbstractCommand;
import org.example.command.TwoStateCommand;
import org.example.internal.ConstantManager;
import org.example.state.State;
import org.example.internal.FileManager;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.io.IOException;

/**
 * Команда /editFile
 */
public class EditFileCommand extends AbstractCommand implements TwoStateCommand
{
    private final FileManager fileManager;
    private final Casher<String> fileNamesCasher = new FileNamesCasher();

    public EditFileCommand()
    {
        fileManager = new FileManager();
    }

    @Override
    public BotApiMethod<Message> handle(String messageFromUser, String chatId, State state)
    {
        try
        {
            String[] arguments = getSplitArguments(messageFromUser);
            switch (state)
            {
                case ON_COMMAND_FROM_USER ->
                {

                    if (!checkArgumentsCount(2, arguments))
                    {
                        throw new IOException(ConstantManager.NO_FILE_NAME_FOUND);
                    }
                    final String fileName = arguments[1];
                    if (!fileManager.isValidFileName(fileName))
                    {
                        throw new IOException(ConstantManager.INCORRECT_FILE_NAME);
                    }
                    if (!fileManager.existsFile(fileName, chatId))
                    {
                        throw new IOException(ConstantManager.NO_SUCH_FILE_EXISTS);
                    }
                    fileNamesCasher.add(chatId, fileName);
                    return new SendMessage(chatId, ConstantManager.INPUT_NEW_FILE_CONTENT);
                }
                case ON_DATA_FROM_USER ->
                {
                    final String fileToEdit = fileNamesCasher.getData(chatId);
                    try
                    {
                        fileManager.editFile(fileToEdit, chatId, messageFromUser);
                        fileNamesCasher.clearUserCash(chatId);
                        return new SendMessage(chatId, "Файл %s успешно сохранен.".formatted(fileToEdit));
                    }
                    catch (IOException e)
                    {
                        throw new IOException("Не удалось отредактировать файл %s. ".formatted(fileToEdit) + e.getMessage(), e);
                    }
                }
                default -> throw new IOException(ConstantManager.BOT_BROKEN_INSIDE_MESSAGE);
            }
        }
        catch (IOException exception)
        {
            return handleException(exception, chatId);
        }
    }
}
