package org.example.command;

import org.example.internal.ConstantManager;
import org.example.internal.FileManager;
import org.example.state.State;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.io.IOException;

/**
 * Команда /create.
 */
public class CreateCommand extends AbstractCommand implements OneStateCommand
{
    private final FileManager fileManager;

    public CreateCommand()
    {
        fileManager = new FileManager();
    }

    @Override
    public BotApiMethod<Message> handle(String messageFromUser, String chatId, State state)
    {
        try
        {
            String[] arguments = getSplitArguments(messageFromUser);

            if (!checkArgumentsCount(2, arguments))
            {
                throw new IOException(ConstantManager.NO_FILE_NAME_FOUND);
            }
            final String fileName = arguments[1];
            try
            {
                fileManager.createFile(fileName, chatId);
                return new SendMessage(chatId, "Файл %s успешно создан.".formatted(fileName));
            }
            catch (IOException e)
            {
                throw new IOException("Не удалось создать файл %s. ".formatted(fileName) + e.getMessage(), e);
            }
        }
        catch (IOException exception)
        {
            return handleException(exception, chatId);
        }
    }
}
