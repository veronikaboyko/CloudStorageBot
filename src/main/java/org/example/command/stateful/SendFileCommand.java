package org.example.command.stateful;

import org.example.bot.TelegramBot;
import org.example.command.AbstractCommand;
import org.example.command.CommandResult;
import org.example.internal.ConstantManager;
import org.example.internal.FileManager;
import org.example.state.State;
import org.example.state.StateSwitcher;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Document;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/**
 * Команда /sendFile
 */
public class SendFileCommand extends AbstractCommand
{
    private final FileManager fileManager;
    private final TelegramBot telegramBot;

    public SendFileCommand(FileManager fileManager, TelegramBot telegramBot)
    {
        super(new StateSwitcher(State.ON_DATA_FROM_USER));
        this.fileManager = fileManager;
        this.telegramBot = telegramBot;
    }

    @Override
    public CommandResult handle(Message messageFromUser, String chatId, State state) throws IOException
    {
        switch (state)
        {
            case ON_COMMAND_FROM_USER ->
            {
                if (!(messageFromUser.hasText()))
                    return new CommandResult(new SendMessage(chatId, "Введите команду перед тем как отправить файл."), false);
                final String stringContent = messageFromUser.getText();
                String[] arguments = getSplitArguments(stringContent);
                if (!checkArgumentsCount(1, arguments))
                {
                    return new CommandResult(new SendMessage(chatId, "Переданы лишние аргументы."), false);
                }
                if (!arguments[0].equals("/sendFile"))
                    return new CommandResult(new SendMessage(chatId, "Некорректная команда!"), false);
                return new CommandResult(new SendMessage(chatId, ConstantManager.SEND_FILE), true);
            }
            case ON_DATA_FROM_USER ->
            {
                if (!(messageFromUser.hasDocument()))
                    return new CommandResult(new SendMessage(chatId, ConstantManager.SEND_FILE), false);
                final Document userDocument = messageFromUser.getDocument();
                if (userDocument.getFileSize() / ConstantManager.ONE_MB > 1)
                    return new CommandResult(new SendMessage(chatId, ConstantManager.FILE_SIZE_OVERFLOW), false);
                File systemFile;
                Document document = new Document();
                final String fileName = userDocument.getFileName();
                if (!ConstantManager.ALLOWED_EXTENSIONS.contains("."+fileName.split("\\.")[1]))
                    return new CommandResult(new SendMessage(chatId, ConstantManager.ALLOWED_EXTENSIONS_MISTAKE), false);
                document.setFileName(fileName);
                document.setFileSize(userDocument.getFileSize());
                document.setFileId(userDocument.getFileId());
                GetFile getFile = new GetFile();
                getFile.setFileId(document.getFileId());
                try
                {
                    org.telegram.telegrambots.meta.api.objects.File file = telegramBot.execute(getFile);
                    systemFile = new File(ConstantManager.USER_DATA_DIRECTORY + "user_" + chatId + "/" + userDocument.getFileName());
                    telegramBot.downloadFile(file, systemFile);
                    if (Files.exists(systemFile.toPath()))
                        return new CommandResult(new SendMessage(chatId, "Файл успешно сохранен."), true);
                    else
                        return new CommandResult(new SendMessage(chatId, "Ошибка сохранения файла. Попробуйте отправить еще раз."), true);
                }
                catch (TelegramApiException e)
                {
                    e.printStackTrace();
                    throw new IOException(ConstantManager.BOT_BROKEN_INSIDE_MESSAGE);
                }
            }
            default -> throw new IOException(ConstantManager.BOT_BROKEN_INSIDE_MESSAGE);
        }
    }
}

