package org.example.bot;

import org.example.commands.*;
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
     * Map для отображения пользователь -> текущая команда, ассоциированная с ним
     */
    private Map<String, PartialCommand> userPartialCommands;


    public MessageHandler()
    {
        registerCommands();
        userPartialCommands = new HashMap<>();
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
        String key = messageFromUser.split("\\s+")[0];
        BotApiMethod messageToSend;
        if (commands.containsKey(key))
        {
            final Command userCommand = commands.get(key);
            messageToSend = userCommand.handle(messageFromUser, chatId);
            if (userCommand instanceof PartialCommand)
            {
                userPartialCommands.put(chatId, (PartialCommand) userCommand);
            }

        } else
        {
            final PartialCommand currentUserPartialCommand = userPartialCommands.get(chatId);
            if (currentUserPartialCommand == null)
            {
                messageToSend = new SendMessage(chatId, "Не понимаю вас. Команда /help для получения справки.");
            } else
            {
                messageToSend = currentUserPartialCommand.handle(messageFromUser, chatId);
            }
        }
        return messageToSend;
    }
}
