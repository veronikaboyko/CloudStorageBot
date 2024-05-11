package org.example.command;

import org.example.state.State;
import org.example.state.StateSwitcher;

/**
 * Описывает поведение любой команды
 */
public abstract class AbstractCommand implements Command
{
    private final StateSwitcher stateSwitcher;

    public AbstractCommand(StateSwitcher stateSwitcher)
    {
        this.stateSwitcher = stateSwitcher;
    }

    /**
     * Проверить, что количество переданных в команду аргументов совпадает с необходимым
     *
     * @param wordCount Ожидаемое количество аргументов.
     * @param arguments Массив, хранящий в себе все слова из сообщения пользователя.
     * @return True - если количество параметров соответствует ожидаемому, False - иначе.
     */
    public boolean checkArgumentsCount(int wordCount, String[] arguments)
    {
        return arguments.length == wordCount;
    }

    /**
     * Получить разделенное входное сообщение
     */
    public String[] getSplitArguments(String messageFromUser)
    {
        return messageFromUser.split("\\s+");
    }

    /**
     * Получить новое состояние по старому
     */
    public State getNewState(State oldState)
    {
        return stateSwitcher.newState(oldState);
    }
}
