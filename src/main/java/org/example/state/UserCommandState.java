package org.example.state;

import org.example.command.AbstractCommand;

import java.util.HashMap;
import java.util.Map;

/**
 * Класс, который хранит текущие команды+состояние по каждому пользователю.
 * Отвечает за логику передачи данных команд классу MessageHandler
 */
public class UserCommandState
{
    /**
     * Хранит текущую команду и её состояние для каждого пользователя
     */
    private final Map<String, CommandState> userCommandStateMap;

    public UserCommandState()
    {
        userCommandStateMap = new HashMap<>();
    }

    /**
     * Проверяет, существует ли пользователь с таким chatId
     */
    public boolean exists(String chatId)
    {
        return userCommandStateMap.containsKey(chatId);
    }

    /**
     * Добавляет пользователя с командой в начальном состоянии
     */
    public void add(String chatId, AbstractCommand command)
    {
        userCommandStateMap.put(chatId, new CommandState(command, State.ON_COMMAND_FROM_USER));
    }

    /**
     * Удаляем пользователя
     */
    public void removeUser(String chatId)
    {
        userCommandStateMap.remove(chatId);
    }

    /**
     * Получить текущую команду по пользователю
     */
    public AbstractCommand getCurrentCommand(String chatId)
    {
        return userCommandStateMap.get(chatId).command;
    }

    /**
     * Получить текущее состояние пользователя
     */
    public State getCurrentState(String chatId)
    {
        return userCommandStateMap.get(chatId).state;
    }

    /**
     * Обновляем состояние команды
     */
    public void updateCommandState(String chatId)
    {
        final CommandState currentUserCommandState = userCommandStateMap.get(chatId);
        final AbstractCommand currentUserCommand = currentUserCommandState.command;
        final State currentUserState = currentUserCommandState.state;
        final State newState = currentUserCommand.getNewState(currentUserState);
        if (newState == null)
            removeUser(chatId);
        else
        {
            currentUserCommandState.state = newState;
            userCommandStateMap.put(chatId, currentUserCommandState);
        }
    }

    /**
     * Пара Команда+состояние
     */
    private class CommandState
    {
        private final AbstractCommand command;
        private State state;
        CommandState(AbstractCommand command, State state)
        {
            this.command = command;
            this.state = state;
        }
    }
}
