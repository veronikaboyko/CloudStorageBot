package org.example.command.stateful;

import org.example.bot.TelegramBot;
import org.example.command.AbstractCommand;
import org.example.command.CommandResult;
import org.example.internal.ConstantManager;
import org.example.internal.FileManager;
import org.example.state.State;
import org.example.state.StateSwitcher;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Document;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.io.IOException;

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
                final String fileName = userDocument.getFileName();
                if (!ConstantManager.ALLOWED_EXTENSIONS.contains("."+fileName.split("\\.")[1]))
                {
                    return new CommandResult(new SendMessage(chatId, ConstantManager.ALLOWED_EXTENSIONS_MISTAKE), false);
                }
                try
                {
                    fileManager.createFile(userDocument,telegramBot,chatId);
                    return new CommandResult(new SendMessage(chatId, "Файл успешно сохранен."), true);
                }
                catch (Exception e)
                {
                    throw new IOException(ConstantManager.BOT_BROKEN_INSIDE_MESSAGE);
                }
            }
            default -> throw new IOException(ConstantManager.BOT_BROKEN_INSIDE_MESSAGE);
        }
    }
}

