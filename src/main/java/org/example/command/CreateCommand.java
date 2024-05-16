package org.example.command;

import org.example.bot.user.StringMessage;
import org.example.bot.user.UserMessage;
import org.example.internal.ConstantManager;
import org.example.internal.FileManager;
import org.example.state.State;
import org.example.state.StateSwitcher;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.io.IOException;

/**
 * Команда /create.
 */
public class CreateCommand extends AbstractCommand
{
    private final FileManager fileManager;

    public CreateCommand(FileManager fileManager)
    {
        super(new StateSwitcher(State.ON_COMMAND_FROM_USER));
        this.fileManager = fileManager;
    }

    @Override
    public CommandResult handle(UserMessage<?> messageFromUser, String chatId, State state) throws IOException
    {
        if (!(messageFromUser instanceof StringMessage))
            return new CommandResult(new SendMessage(chatId, ConstantManager.NOT_SUPPORT_FILE_FORMAT), false);
        final String stringContent = ((StringMessage) messageFromUser).getContent();
        String[] arguments = getSplitArguments(stringContent);

        if (!checkArgumentsCount(2, arguments))
        {
            return new CommandResult(new SendMessage(chatId, ConstantManager.NO_FILE_NAME_FOUND), true);
        }
        if (!arguments[0].equals("/create"))
            return new CommandResult(new SendMessage(chatId, "Некорректная команда!"), false);
        final String fileName = arguments[1];
        try
        {
            fileManager.checkCorrectFileSaved(fileName, chatId);
            return new CommandResult(new SendMessage(chatId, "Файл %s успешно создан.".formatted(fileName)), true);
        }
        catch (IOException e)
        {
            switch (e.getMessage())
            {
                case ConstantManager.FILE_ALREADY_EXISTS ->
                {
                    return new CommandResult(new SendMessage(chatId, "Не удалось создать файл %s. ".formatted(fileName)
                            + ConstantManager.FILE_ALREADY_EXISTS), true);
                }
                case ConstantManager.ALLOWED_EXTENSIONS_MISTAKE ->
                {
                    return new CommandResult(new SendMessage(chatId, "Не удалось создать файл %s. ".formatted(fileName)
                            + "Неверное расширение файла. " + "Допустимые расширения: txt, json, xml."), true);
                }
                default ->
                        throw new IOException("Не удалось создать файл %s. ".formatted(fileName) + e.getMessage(), e);
            }
        }
    }
}
