package org.example.bot;

import org.example.commands.*;
import org.example.commands.StatebleCommands.EditFileCommand;
import org.example.commands.StatebleCommands.EditFileNameCommand;
import org.example.commands.StatebleCommands.StatebleCommand;
import org.example.commands.StatebleCommands.WriteToFileCommand;
import org.example.internal.ArgumentChecker;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.HashMap;
import java.util.Map;


/**
 * Класс, который отвечает за обработку сообщений
 * (определяет, какая команда была введена пользователем, и вызывает ее обработчик).
 */
public class MessageHandler
{

    /**
     * Отображение название команды -> обработчик
     */
    private Map<String, Command> commands;


    /**
     * Map для отображения пользователь -> текущая команда (хранящая состояние), ассоциированная с ним
     */
    private final Map<String, StatebleCommand> userStatebleCommands;


    private final ArgumentChecker argumentChecker;


    public MessageHandler()
    {
        registerCommands();
        userStatebleCommands = new HashMap<>();
        argumentChecker = new ArgumentChecker();
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
     * @return Объект, который нужно отправить.
     */
    public BotApiMethod handleUserMessage(String messageFromUser, String chatId)
    {
        if (argumentChecker.isCommand(messageFromUser))
        {
            final Command command = commands.get(messageFromUser.split(" ")[0]);
            if (command instanceof StatebleCommand)
            {
                if (userStatebleCommands.get(chatId) == command)
                    ((StatebleCommand) command).toStart();
                userStatebleCommands.put(chatId, (StatebleCommand) command);
            }
            return command.handle(messageFromUser, chatId);
        } else
        {
            final StatebleCommand currentStatebleCommand = userStatebleCommands.get(chatId);
            if (currentStatebleCommand == null)
                return new SendMessage(chatId, "Не понимаю вас. Команда /help для получения справки.");
            else
            {
                BotApiMethod forUser = currentStatebleCommand.handle(messageFromUser, chatId);
                if (currentStatebleCommand.onLastState())
                {
                    userStatebleCommands.put(chatId, null);
                    currentStatebleCommand.toStart(); //откатываем к первоначальному состоянию
                }
                return forUser;
            }
        }
    }
}
