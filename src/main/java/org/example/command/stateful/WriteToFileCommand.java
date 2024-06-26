package org.example.command.stateful;

import org.example.command.AbstractCommand;
import org.example.command.CommandResult;
import org.example.internal.ConstantManager;
import org.example.internal.FileManager;
import org.example.state.State;
import org.example.state.StateSwitcher;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.io.IOException;

/**
 * Команда /writeToFile
 */
public class WriteToFileCommand extends AbstractCommand
{
    private final FileManager fileManager;
    private final Casher<String> fileNamesCasher = new FileNamesCasher();


    public WriteToFileCommand(FileManager fileManager)
    {
        super(new StateSwitcher(State.ON_DATA_FROM_USER));
        this.fileManager = fileManager;
    }

    @Override
    public CommandResult handle(String messageFromUser, String chatId, State state) throws IOException
    {
        String[] arguments = getSplitArguments(messageFromUser);
        switch (state)
        {
            case ON_COMMAND_FROM_USER ->
            {
                if (!checkArgumentsCount(2, arguments))
                {
                    return new CommandResult(new SendMessage(chatId,ConstantManager.NO_FILE_NAME_FOUND),false);
                }
                if (!arguments[0].equals("/writeToFile"))
                    return new CommandResult(new SendMessage(chatId, "Некорректная команда!"), false);
                final String fileName = arguments[1];
                if (!fileManager.isValidFileName(fileName))
                {
                    return new CommandResult(new SendMessage(chatId,ConstantManager.INCORRECT_FILE_NAME),false);
                }
                if (!fileManager.existsFile(fileName, chatId))
                {
                    return new CommandResult(new SendMessage(chatId,"Файл с таким именем не найден."),false);
                }
                fileNamesCasher.add(chatId, fileName);
                return new CommandResult(new SendMessage(chatId, ConstantManager.INPUT_NEW_FILE_CONTENT),true);
            }
            case ON_DATA_FROM_USER ->
            {
                final String fileToWrite = fileNamesCasher.getData(chatId);
                try
                {
                    fileManager.writeToFile(fileToWrite, chatId, messageFromUser);
                    fileNamesCasher.clearUserCash(chatId);
                    return new CommandResult(new SendMessage(chatId, "Файл %s успешно сохранен.".formatted(fileToWrite)),true);
                }
                catch (IOException e)
                {
                    throw new IOException("Не удалось записать в файл %s. ".formatted(fileToWrite) + e.getMessage(), e);
                }
            }
            default -> throw new IOException(ConstantManager.BOT_BROKEN_INSIDE_MESSAGE);
        }
    }
}
