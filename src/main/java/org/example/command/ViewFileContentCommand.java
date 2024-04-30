package org.example.command;

import org.example.internal.ConstantManager;
import org.example.internal.FileManager;
import org.example.state.State;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.io.FileNotFoundException;
import java.io.IOException;


/**
 * Просмтореть содержимое файла
 */
public class ViewFileContentCommand extends AbstractCommand implements OneStateCommand
{
    private final FileManager fileManager;

    public ViewFileContentCommand()
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
        if (!fileManager.isValidFileName(fileName)) {
            throw new IOException("Некорректное название файла.");
        }
        if (!fileManager.existsFile(fileName, chatId)) {
            throw new IOException("Файла с таким названием не существует.");
        }
        try {
            final String fileContent = fileManager.getFileContent(fileName, chatId);
            if (!fileContent.isEmpty())
                return new SendMessage(chatId, fileContent);
            else
                return new SendMessage(chatId, "Файл пуст.");
        } catch (FileNotFoundException e) {
            System.out.println("Внутрення ошибка при работе с файлом. " + e.getMessage());
            throw new IOException("Внутрення ошибка при работе с файлом.");
        }
    }
}
