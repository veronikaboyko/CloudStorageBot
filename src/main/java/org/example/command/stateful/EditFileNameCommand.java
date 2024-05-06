package org.example.command.stateful;


import org.example.command.AbstractCommand;
import org.example.command.TwoStateCommand;
import org.example.internal.ConstantManager;
import org.example.internal.FileManager;
import org.example.state.State;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.io.IOException;

/**
 * Команда /editFileName
 */

public class EditFileNameCommand extends AbstractCommand implements TwoStateCommand
{
    private final FileManager fileManager;
    private final Casher<String> fileNamesCasher = new FileNamesCasher();


    public EditFileNameCommand()
    {
        fileManager = new FileManager();
    }

    @Override
    public BotApiMethod<Message> handle(String messageFromUser, String chatId, State state) throws IOException
    {
        switch (state) {
            case GOT_COMMAND_FROM_USER -> {
                String[] arguments = getSplitArguments(messageFromUser);
                if (!checkArgumentsCount(2, arguments)) {
                    throw new IOException(ConstantManager.NO_FILE_NAME_FOUND);
                }
                final String fileName = arguments[1];
                if (!fileManager.isValidFileName(fileName)) {
                    throw new IOException(ConstantManager.INCORRECT_FILE_NAME);
                }
                if (!fileManager.existsFile(fileName, chatId)) {
                    throw new IOException(ConstantManager.NO_SUCH_FILE_EXISTS);
                }
                fileNamesCasher.add(chatId, fileName);
                return new SendMessage(chatId, "Введите новое название файла.");
            }
            case GOT_DATA_FROM_USER -> {
                if (!fileManager.isValidFileName(messageFromUser)) {
                    throw new IOException("Некорректное название файла");
                }
                try {
                    final String oldFileName = fileNamesCasher.getData(chatId);
                    fileManager.editFileName(oldFileName, chatId, messageFromUser);
                    fileNamesCasher.clearUserCash(chatId);
                    return new SendMessage(chatId, oldFileName + " -> " + messageFromUser);
                } catch (IOException exception) {
                    throw new IOException("Ошибка при работе с файлом.");
                }
            }
            default -> throw new IOException(ConstantManager.BOT_BROKEN_INSIDE_MESSAGE);
        }
    }
}
