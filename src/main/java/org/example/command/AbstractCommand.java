package org.example.command;

/**
 * Описывает поведение любой команды
 */
public abstract class AbstractCommand implements Command
{
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
}
