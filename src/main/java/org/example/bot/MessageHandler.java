package org.example.bot;

import org.example.command.*;
import org.example.command.stateful.EditFileCommand;
import org.example.command.stateful.EditFileNameCommand;
import org.example.command.stateful.SendFileCommand;
import org.example.command.stateful.WriteToFileCommand;
import org.example.internal.ConstantManager;
import org.example.internal.FileManager;
import org.example.state.State;
import org.example.state.UserCommandState;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

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

    public MessageHandler(FileManager fileManager,TelegramBot telegramBot)
    {
        registerCommands(fileManager,telegramBot);
        userCommandState = new UserCommandState();
    }

    /**
     * Метод для регистрации команд Telegram бота.
     */
    private void registerCommands(FileManager fileManager,TelegramBot telegramBot)
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
        commands.put("/sendFile", new SendFileCommand(fileManager,telegramBot));
    }

    /**
     * Метод для вызова обработчика команды, которую ввел пользователь.
     *
     * @param messageFromUser Сообщение от пользователя.
     * @param chatId          ID чата.
     * @return Объект, который нужно отправить пользователю.
     */
    public PartialBotApiMethod<?> handleUserMessage(Message messageFromUser, String chatId)
    {
        try
        {
            if (messageFromUser.hasText())
            {
                final String potentialCommand = messageFromUser.getText().split(" ")[0];
                AbstractCommand currentCommand;
                if (isCommand(potentialCommand))
                {
                    currentCommand = commands.get(potentialCommand);
                    userCommandState.add(chatId,currentCommand);
                } else
                {
                    if (!userCommandState.exists(chatId))
                        return new SendMessage(chatId, ConstantManager.NOT_UNDERSTAND);
                    else
                        currentCommand = userCommandState.getCurrentCommand(chatId);
                }
                CommandResult result = currentCommand.handle(messageFromUser, chatId, userCommandState.getCurrentState(chatId));
                if (result.success())
                    userCommandState.updateCommandState(chatId);
                return result.getDataForUser();
            } else
            {
                final AbstractCommand command = userCommandState.getCurrentCommand(chatId);
                if (command == null)
                    return new SendMessage(chatId, ConstantManager.NOT_UNDERSTAND);
                final State currentUserState = userCommandState.getCurrentState(chatId);
                CommandResult result = command.handle(messageFromUser, chatId, currentUserState);
                if (result.success())
                    userCommandState.updateCommandState(chatId);
                else
                    userCommandState.removeUser(chatId);
                return result.getDataForUser();
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
