package org.example.bot;

import org.example.commands.Command;
import org.example.commands.CreateCommand;
import org.example.commands.DeleteCommand;
import org.example.commands.StartCommand;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.HashMap;
import java.util.Map;


/**
 * Класс, который отвечает за обработку сообщений
 * (определяет, какая команда была введена пользователем, и вызывает ее обработчик).
 */
public class MessageHandler {
    private Map<String, Command> commands;

    public MessageHandler() {
        registerCommands();
    }

    /**
     * Метод для регистрации команд Telegram бота.
     */
    private void registerCommands() {
        commands = new HashMap<>();
        commands.put("/start", new StartCommand());
        commands.put("/help", new StartCommand());
        commands.put("/create", new CreateCommand());
        commands.put("/delete", new DeleteCommand());
    }

    /**
     * Метод для вызова обработчика команды, которую ввел пользователь.
     *
     * @param messageFromUser Сообщение от пользователя.
     * @param chatId          ID чата.
     * @return Объект, который нужно отправить.
     */
    public BotApiMethod handleUserMessage(String messageFromUser, String chatId) {
        String key = messageFromUser.split("\\s+")[0];
        BotApiMethod messageToSend;
        if (commands.containsKey(key)) {
            messageToSend = commands.get(key).handle(messageFromUser, chatId);
        } else {
            messageToSend = new SendMessage(chatId, "Не понимаю вас. Команда /help для получения справки.");
        }
        return messageToSend;
    }
}
