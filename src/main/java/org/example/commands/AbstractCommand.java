package org.example.commands;

/**
 * Описывает поведение любой команды
 */
public abstract class AbstractCommand implements Command
{
    /**
     * Проверить, что количество переданных в команду аргументов совпадает с необходимым
     */
    public boolean checkArgumentsCount(int wordCount, String message)
    {
        String[] words = message.split("\\s+");
        return words.length == wordCount;
    }
}
