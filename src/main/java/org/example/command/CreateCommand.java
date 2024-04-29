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
    public BotApiMethod<Message> handle(String messageFromUser, String chatId, State state) throws IOException
    {
        String[] arguments = messageFromUser.split("\\s+");

        if (!checkArgumentsCount(2, arguments)) {
            throw new IOException(ConstantManager.NO_FILE_NAME_FOUND);
        }
        final String fileName = arguments[1];
        try {
            fileManager.createFile(fileName, chatId);
            return new SendMessage(chatId, "Файл успешно создан.");
        } catch (IOException e) {
            System.out.println("Не удалось создать файл. " + e.getMessage());
            throw new IOException(e.getMessage());
        }
    }
}
