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
    public BotApiMethod handle(String messageFromUser, String chatId, State state) throws IOException
    {
        switch (state)
        {
            case GOT_COMMAND_FROM_USER ->
            {
                if (!checkArgumentsCount(2, messageFromUser))
                {
                    throw new IOException(ConstantManager.NO_FILE_NAME_FOUND);
                }
                final String fileName = messageFromUser.split("\\s+")[1];
                if (!fileManager.isValidFileName(fileName))
                {
                    throw new IOException("Некорректное название файла");
                }
                if (!fileManager.existsFile(fileName, chatId))
                {
                    throw new IOException("Файла с таким названием не существует");
                }
                fileNamesCasher.add(chatId, fileName);
                return new SendMessage(chatId, "Введите новое содержимое файла.");
            }
            case GOT_DATA_FROM_USER ->
            {
                try
                {
                    fileManager.editFile(fileNamesCasher.getData(chatId), chatId, messageFromUser);
                    fileNamesCasher.clearUserCash(chatId);
                    return new SendMessage(chatId, "Файл успешно сохранен.");
                }
                catch (IOException exception)
                {
                    throw new IOException("Ошибка при работе с файлом.");
                }
            }
            default -> throw new IOException("Ни одного корректного состояния не было передано");
        }
    }
}
