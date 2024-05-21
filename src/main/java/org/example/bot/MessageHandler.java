package org.example.bot;

import org.example.bot.user.FileMessage;
import org.example.bot.user.StringMessage;
import org.example.bot.user.UserMessage;
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

import java.io.File;
import java.nio.file.Files;
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
        commands.put("/sendFile", new SendFileCommand(fileManager));
    }

    /**
     * Метод для вызова обработчика команды, которую ввел пользователь.
     *
     * @param messageFromUser Сообщение от пользователя.
     * @param chatId          ID чата.
     * @return Объект, который нужно отправить пользователю.
     */
    public PartialBotApiMethod<?> handleUserMessage(UserMessage<?> messageFromUser, String chatId)
    {
        try
        {
            if (messageFromUser instanceof StringMessage stringMessageFromUser)
            {
                final String stringContent = stringMessageFromUser.getContent();
                final String potentialCommand = stringContent.split(" ")[0];
                if (isCommand(potentialCommand))
                {
                    final AbstractCommand currentCommand = commands.get(potentialCommand);
                    userCommandState.add(chatId, currentCommand);
                    final State currentState = userCommandState.getCurrentState(chatId);
                    CommandResult result = currentCommand.handle(stringMessageFromUser, chatId, currentState);
                    if (result.canUpdate())
                        userCommandState.updateCommandState(chatId);
                    else
                        userCommandState.removeUser(chatId);
                    return result.getDataForUser();
                } else
                {
                    if (!userCommandState.exists(chatId))
                        return new SendMessage(chatId, ConstantManager.NOT_UNDERSTAND);
                    else
                    {
                        final AbstractCommand command = userCommandState.getCurrentCommand(chatId);
                        final State currentUserState = userCommandState.getCurrentState(chatId);
                        CommandResult result = command.handle(stringMessageFromUser, chatId, currentUserState);
                        if (result.canUpdate())
                            userCommandState.updateCommandState(chatId);
                        return result.getDataForUser();
                    }
                }
            } else
            {
                if (messageFromUser instanceof FileMessage fileMessageFromUser){
                    File userFile = fileMessageFromUser.getContent();
                    if (!userCommandState.exists(chatId))
                    {
                        //Надо его удалить. т.к. мы его создавали на уровень выше.
                        Files.delete(userFile.toPath());
                        return new SendMessage(chatId, ConstantManager.NOT_UNDERSTAND);
                    }
                    final AbstractCommand currentCommand = userCommandState.getCurrentCommand(chatId);
                    final State currentState = userCommandState.getCurrentState(chatId);
                    if (!(currentCommand instanceof SendFileCommand))
                    {
                        //Если команда SendFileCommand не была вызвана, значит нам просто отправили файл
                        //Соответственно надо его удалить. т.к. мы его создавали.
                        if (Files.exists(userFile.toPath()))
                            Files.delete(userFile.toPath());
                        //Знаем, что вернет false, поэтому возвращаем сразу результат
                        return currentCommand.handle(messageFromUser,chatId,currentState).getDataForUser();
                    } else
                    {
                        CommandResult result = currentCommand.handle(messageFromUser, chatId, currentState);
                        if (result.canUpdate())
                            userCommandState.updateCommandState(chatId);
                        else
                            userCommandState.removeUser(chatId);
                        return result.getDataForUser();
                    }
                }
            }
        }
        catch (Exception exception)
        {
            System.err.println(exception.getMessage());
            exception.printStackTrace();
            return new SendMessage(chatId, "Внутрення ошибка работы бота");
        }
        return null;
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
