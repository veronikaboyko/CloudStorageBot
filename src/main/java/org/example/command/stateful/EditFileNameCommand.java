package org.example.commands.stateful;

import org.example.commands.AbstractCommand;
import org.example.commands.TwoStateCommand;
import org.example.internal.ConstantManager;
import org.example.state.State;
import org.example.internal.FileManager;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

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
                String[] arguments = messageFromUser.split("\\s+");
                if (!checkArgumentsCount(2, arguments)) {
                    throw new IOException(ConstantManager.NO_FILE_NAME_FOUND);
                }
                final String fileName = arguments[1];
                if (!fileManager.isValidFileName(fileName)) {
                    throw new IOException("Некорректное название файла");
                }
                if (!fileManager.existsFile(fileName, chatId)) {
                    throw new IOException("Файла с таким названием не существует");
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
            default -> throw new IOException("Ни одного корректного состояние не было передано");
        }
    }
}
