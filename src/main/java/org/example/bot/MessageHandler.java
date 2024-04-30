package org.example.bot;

import org.example.command.*;
import org.example.command.stateful.EditFileCommand;
import org.example.command.stateful.EditFileNameCommand;
import org.example.command.stateful.WriteToFileCommand;
import org.example.state.CommandWithState;
import org.example.state.State;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


/**
 * Класс, который отвечает за обработку сообщений
 * (определяет, какая команда была введена пользователем, и вызывает ее обработчик).
 */
public class MessageHandler
{

    /**
     * Отображение название команды -> сама команда
     */
    private Map<String, AbstractCommand> commands;

    /**
     * Отображение пользователь -> текущая команда с сотоянием
     */
    private final Map<String, CommandWithState> usersCommand;

    public MessageHandler()
    {
        registerCommands();
        usersCommand = new HashMap<>();
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
    public BotApiMethod<?> handleUserMessage(String messageFromUser, String chatId)
    {
        final String potentialCommand = messageFromUser.split(" ")[0];
        try {
            if (isCommand(potentialCommand)) {
                usersCommand.put(chatId, new CommandWithState(potentialCommand, State.GOT_COMMAND_FROM_USER));
                final AbstractCommand command = commands.get(potentialCommand);
                BotApiMethod<?> botApiMethod = command.handle(messageFromUser, chatId, State.GOT_COMMAND_FROM_USER);
                if (command instanceof OneStateCommand)
                    usersCommand.remove(chatId);
                return botApiMethod;
            } else {
                if (!usersCommand.containsKey(chatId))
                    return new SendMessage(chatId, "Не понимаю вас! Вызовите /help для получения справки по боту.");
                else {
                    final AbstractCommand command = commands.get(usersCommand.get(chatId).getCurrentCommand());
                    final String currentUserCommand = usersCommand.get(chatId).getCurrentCommand();
                    final State currentUserState = usersCommand.get(chatId).currentState;
                    BotApiMethod<?> botApiMethod = null;
                    switch (currentUserState) {
                        case GOT_COMMAND_FROM_USER -> {
                            botApiMethod = commands.get(currentUserCommand).handle(messageFromUser, chatId, State.GOT_DATA_FROM_USER);
                            usersCommand.get(chatId).currentState = State.GOT_DATA_FROM_USER;
                            if (command instanceof TwoStateCommand)
                                usersCommand.remove(chatId);
                        }
                        //а тут могут быть кейсы, если команда может хранить 3,4,5 и тд состояний.
                    }
                    return botApiMethod;
                }
            }
        } catch (IOException exception) {
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
