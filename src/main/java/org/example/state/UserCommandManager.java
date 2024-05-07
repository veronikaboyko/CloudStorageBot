package org.example.state;

import org.example.command.AbstractCommand;
import org.example.command.CreateCommand;
import org.example.command.OneStateCommand;
import org.example.command.TwoStateCommand;

import java.util.HashMap;
import java.util.Map;

/**
 * Класс, который управляет состоянием команд для каждого пользователя
 */
public class UserCommandManager
{
    /**
     * Хранит текущую команду и её состояние для каждого пользователя
     */
    private final Map<String, CommandState> userCommandState;

    public UserCommandManager()
    {
        userCommandState = new HashMap<>();
    }

    /**
     * Получить текущее состояние команды пользователя
     */
    public State getCurrentState(String chatID)
    {
        return userCommandState.get(chatID).currentState;
    }

    /**
     * Проверяет, существует ли пользователь с таким chatId
     */
    public boolean exists(String chatId)
    {
        return userCommandState.containsKey(chatId);
    }

    /**
     * Добавляет пользователя с командой в начальном состоянии
     */
    public void add(String chatId, AbstractCommand command)
    {
        userCommandState.put(chatId, new CommandState(command, State.GOT_COMMAND_FROM_USER));
    }

    /**
     * Обновляет состояние команды текущего пользователя
     */
    public void updateCommandState(String chatId)
    {
        CommandState commandState = userCommandState.get(chatId);
        final AbstractCommand currentCommand = commandState.currentCommand;
        if (currentCommand instanceof OneStateCommand)
            userCommandState.remove(chatId);
        else if (currentCommand instanceof TwoStateCommand)
        {
            if (commandState.currentState == State.GOT_DATA_FROM_USER)
                userCommandState.remove(chatId);
            commandState.currentState = State.GOT_DATA_FROM_USER;
        }
    }

    public AbstractCommand getCurrentCommand(String chatId)
    {
        return userCommandState.get(chatId).currentCommand;
    }

    /**
     * Хранит текущую команду и состояние, в котором она должна запускаться
     * Класс вложенный, т.к. логично, что команда+состояние всегда привязаны к пользователю
     */
    private class CommandState
    {
        /**
         * текущая команда
         */
        private final AbstractCommand currentCommand;
        /**
         * текущее состояние
         */
        private State currentState;

        public CommandState(AbstractCommand currentCommand, State currentState)
        {
            this.currentCommand = currentCommand;
            this.currentState = currentState;
        }
    }

}
