package org.example.bot;

import org.example.commands.*;
import org.example.commands.StatebleCommands.EditFileCommand;
import org.example.commands.StatebleCommands.EditFileNameCommand;
import org.example.commands.StatebleCommands.StatebleCommand;
import org.example.commands.StatebleCommands.WriteToFileCommand;
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
     * Отображение Строка -> соответсвующая команда
     */
    private Map<String, Command> commands;


    /**
     * Map для отображения пользователь -> текущая команда (хранящая состояние), ассоциированная с ним
     */
    private final Map<String, StatebleCommand> userStatebleCommands;


    public MessageHandler()
    {
        registerCommands();
        userStatebleCommands = new HashMap<>();
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
        String[] messageParts = messageFromUser.split("\\s+");
        String key = messageParts[0];
        Command userCommand = commands.get(key);

        if (userCommand != null)
        {
            StatebleCommand currentUserStatebleCommand = userStatebleCommands.get(chatId);

            if (currentUserStatebleCommand == null)
            {
                userStatebleCommands.put(chatId, userCommand instanceof StatebleCommand ? (StatebleCommand) userCommand : null);
                if (userCommand instanceof StatebleCommand)
                    return userStatebleCommands.get(chatId).handle(messageFromUser, chatId);
            } else
            {
                if (!currentUserStatebleCommand.onLastState())
                    return currentUserStatebleCommand.handle(messageFromUser, chatId);
                else
                    userStatebleCommands.put(chatId, null);
            }
            return userCommand.handle(messageFromUser, chatId);
        } else
        {
            if (userStatebleCommands.get(chatId) != null)
            {
                BotApiMethod messageToSend = userStatebleCommands.get(chatId).handle(messageFromUser, chatId);
                if (userStatebleCommands.get(chatId).onLastState())
                {
                    userStatebleCommands.put(chatId, null);
                }
                return messageToSend;
            }
            return new SendMessage(chatId, "Не понимаю вас. Команда /help для получения справки.");
        }
    }
}
