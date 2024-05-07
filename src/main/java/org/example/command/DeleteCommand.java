package org.example.command;

import org.example.internal.ConstantManager;
import org.example.state.State;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import org.example.internal.FileManager;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.io.IOException;

/**
 * Команда /delete.
 */
public class DeleteCommand extends AbstractCommand implements OneStateCommand
{
    private final FileManager fileManager;

    public DeleteCommand()
    {
        fileManager = new FileManager();
    }

    @Override
    public BotApiMethod<Message> handle(String messageFromUser, String chatId, State state) throws IOException
    {
        String[] arguments = getSplitArguments(messageFromUser);

        if (!checkArgumentsCount(2, arguments)) {
            throw new IOException(ConstantManager.NO_FILE_NAME_FOUND);
        }
        final String fileName = arguments[1];
        try {
            fileManager.deleteFile(fileName, chatId);
            return new SendMessage(chatId, "Файл успешно удален.");
        } catch (IOException e) {
            throw new IOException("Не удалось удалить файл %s. ".formatted(fileName) + e.getMessage(), e);
        }
    }
}
