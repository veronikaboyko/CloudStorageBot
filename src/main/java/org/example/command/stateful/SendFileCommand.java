package org.example.command.stateful;

import org.example.bot.user.FileMessage;
import org.example.bot.user.StringMessage;
import org.example.bot.user.UserMessage;
import org.example.command.AbstractCommand;
import org.example.command.CommandResult;
import org.example.internal.ConstantManager;
import org.example.internal.FileManager;
import org.example.state.State;
import org.example.state.StateSwitcher;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.io.File;
import java.io.IOException;

public class SendFileCommand extends AbstractCommand
{
    private final FileManager fileManager;

    public SendFileCommand(FileManager fileManager)
    {
        super(new StateSwitcher(State.ON_DATA_FROM_USER));
        this.fileManager = fileManager;
    }

    @Override
    public CommandResult handle(UserMessage<?> messageFromUser, String chatId, State state) throws IOException
    {
        switch (state)
        {
            case ON_COMMAND_FROM_USER ->
            {
                if (!(messageFromUser instanceof StringMessage))
                    return new CommandResult(new SendMessage(chatId, "Введите команду перед тем как отправить файл."), false);
                final String stringContent = ((StringMessage) messageFromUser).getContent();
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
                if (!(messageFromUser instanceof FileMessage))
                    return new CommandResult(new SendMessage(chatId, ConstantManager.SEND_FILE), false);
                final File file = ((FileMessage) messageFromUser).getContent();
                //тут кстати вопрос, надо ли проверять размер файла. по идее он проверяется на несколько уровней выше.
                //но если другой разработчик войдет в проект и будет использовать этот класс отдельно от всех вышестоящих уровней
                //то будут проблемы.
                try
                {
                    fileManager.createOrCheckIfCreatedFile(file, chatId);
                    return new CommandResult(new SendMessage(chatId, "Файл успешно добавлен."), false);
                }
                catch (IOException exception)
                {
                    return new CommandResult(new SendMessage(chatId, exception.getMessage()), false);
                }
            }
            default -> throw new IOException(ConstantManager.BOT_BROKEN_INSIDE_MESSAGE);
        }
    }
}
