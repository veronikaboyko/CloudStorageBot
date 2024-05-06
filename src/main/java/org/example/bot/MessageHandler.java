package org.example.bot;

import org.example.command.*;
import org.example.command.stateful.EditFileCommand;
import org.example.command.stateful.EditFileNameCommand;
import org.example.command.stateful.WriteToFileCommand;
import org.example.state.State;
import org.example.state.UserCommandManager;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.io.IOException;
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

    private final UserCommandManager userCommandManager;

    public MessageHandler()
    {
        registerCommands();
        userCommandManager = new UserCommandManager();
    }

    /**
     * Метод для регистрации команд Telegram бота.
     */
    private void registerCommands()
    {
        commands = new HashMap<>();
        commands.put("/start", new StartCommand());
        commands.put("/help", new StartCommand());
        commands.put("/create", new CreateCommand());
        commands.put("/delete", new DeleteCommand());
        commands.put("/writeToFile", new WriteToFileCommand());
        commands.put("/editFile", new EditFileCommand());
        commands.put("/editFileName", new EditFileNameCommand());
        commands.put("/listFiles", new ListFilesCommand());
        commands.put("/viewFileContent", new ViewFileContentCommand());
    }

    /**
     * Метод для вызова обработчика команды, которую ввел пользователь.
     *
     * @param messageFromUser Сообщение от пользователя.
     * @param chatId          ID чата.
     * @return Объект, который нужно отправить пользователю.
     */
    public BotApiMethod<?> handleUserMessage(String messageFromUser, String chatId)
    {
        final String potentialCommand = messageFromUser.split(" ")[0];
        try
        {
            if (isCommand(potentialCommand))
            {
                final AbstractCommand currentCommand = commands.get(potentialCommand);
                if (!userCommandManager.exists(chatId))
                    userCommandManager.add(chatId, currentCommand);
                final State currentState = userCommandManager.getCurrentState(chatId);
                BotApiMethod<?> botApiMethod = currentCommand.handle(messageFromUser, chatId, currentState);
                userCommandManager.updateCommandState(chatId);
                return botApiMethod;
            } else
            {
                if (!userCommandManager.exists(chatId))
                    return new SendMessage(chatId, "Не понимаю вас! Вызовите /help для получения справки по боту.");
                else
                {
                    final AbstractCommand command = userCommandManager.getCurrentCommand(chatId);
                    final State currentUserState = userCommandManager.getCurrentState(chatId);
                    BotApiMethod<?> botApiMethod = command.handle(messageFromUser, chatId, currentUserState);
                    userCommandManager.updateCommandState(chatId);
                    return botApiMethod;
                }
            }
        }
        catch (IOException exception)
        {
            System.err.println(exception.getMessage());
            exception.printStackTrace();
            return new SendMessage(chatId, exception.getMessage());
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
