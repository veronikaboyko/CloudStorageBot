package org.example.bot;

import org.example.command.*;
import org.example.command.stateful.EditFileCommand;
import org.example.command.stateful.EditFileNameCommand;
import org.example.command.stateful.WriteToFileCommand;
import org.example.internal.FileManager;
import org.example.state.State;
import org.example.state.UserCommandState;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.HashMap;
import java.util.Map;


/**
 * Класс, который отвечает за обработку сообщений
 */
public class MessageHandler
{

    /**
     * Отображение название команды -> сама команда
     */
    private Map<String, AbstractCommand> commands;

    private final UserCommandState userCommandState;

    public MessageHandler(FileManager fileManager)
    {
        registerCommands(fileManager);
        userCommandState = new UserCommandState();
    }

    /**
     * Метод для регистрации команд Telegram бота.
     */
    private void registerCommands(FileManager fileManager)
    {
        commands = new HashMap<>();
        commands.put("/start", new StartCommand());
        commands.put("/help", new StartCommand());
        commands.put("/create", new CreateCommand(fileManager));
        commands.put("/delete", new DeleteCommand(fileManager));
        commands.put("/writeToFile", new WriteToFileCommand(fileManager));
        commands.put("/editFile", new EditFileCommand(fileManager));
        commands.put("/editFileName", new EditFileNameCommand(fileManager));
        commands.put("/listFiles", new ListFilesCommand(fileManager));
        commands.put("/viewFileContent", new ViewFileContentCommand(fileManager));
        commands.put("/findFileName", new FindFileNameCommand(fileManager));
        commands.put("/findFile", new FindFileCommand(fileManager));
        commands.put("/getFile", new GetFileCommand(fileManager));
    }

    /**
     * Метод для вызова обработчика команды, которую ввел пользователь.
     *
     * @param messageFromUser Сообщение от пользователя.
     * @param chatId          ID чата.
     * @return Объект, который нужно отправить пользователю.
     */
    public PartialBotApiMethod<?> handleUserMessage(String messageFromUser, String chatId)
    {
        try
        {
            final String potentialCommand = messageFromUser.split(" ")[0];
            if (isCommand(potentialCommand))
            {
                final AbstractCommand currentCommand = commands.get(potentialCommand);
                if (!userCommandState.exists(chatId))
                    userCommandState.add(chatId, currentCommand);
                final State currentState = userCommandState.getCurrentState(chatId);
                CommandResult result = currentCommand.handle(messageFromUser, chatId, currentState);
                if (result.canUpdate())
                    userCommandState.updateCommandState(chatId);
                else
                    userCommandState.removeUser(chatId);
                return result.getDataForUser();
            } else
            {
                if (!userCommandState.exists(chatId))
                    return new SendMessage(chatId, "Не понимаю вас! Вызовите /help для получения справки по боту.");
                else
                {
                    final AbstractCommand command = userCommandState.getCurrentCommand(chatId);
                    final State currentUserState = userCommandState.getCurrentState(chatId);
                    CommandResult result = command.handle(messageFromUser, chatId, currentUserState);
                    if (result.canUpdate())
                        userCommandState.updateCommandState(chatId);
                    return result.getDataForUser();
                }
            }
        }
        catch (Exception exception)
        {
            System.err.println(exception.getMessage());
            exception.printStackTrace();
            return new SendMessage(chatId, "Внутрення ошибка работы бота");
        }
    }


    /**
     * Проверяет, является ли сообщение от пользователя командой
     *
     * @param potentialCommand - пользовательская строка
     */
    public boolean isCommand(String potentialCommand)
    {
        if (potentialCommand == null)
            return false;
        return commands.containsKey(potentialCommand);
    }
}
