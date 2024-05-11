package org.example.state;

/**
 * Переключатель состояний команд
 */
public class StateSwitcher
{
    /**
     * Финальное состояние команды
     */
    private final State finalState;
    public StateSwitcher(State state)
    {
        finalState = state;
    }
    /**
     * Получить следующее состояние по старому
     */
    public State newState(State state)
    {
        if (state.equals(finalState))
            return null;
        return State.values()[state.ordinal()+1];
    }
}
