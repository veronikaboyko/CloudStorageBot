package org.example.state;

import org.example.bot.MessageHandler;

/**
 * Хранит текущую команду и состояние, в котором она должна запускаться
 */
public class CommandWithState
{
    /**
     * текущая команда
     */
    private String currentCommand;
    /**
     * текущее состояние
     */
    public State currentState;

    public CommandWithState(String currentCommand, State currentState)
    {
        if (!new MessageHandler().isCommand(currentCommand))
            throw new IllegalArgumentException("Нет такой команды!");
        this.currentCommand = currentCommand;
        this.currentState = currentState;
    }

    /**
     * @return команда
     */
    public String getCurrentCommand()
    {
        return currentCommand;
    }
}
