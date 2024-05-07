package org.example.command;

import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.io.IOException;

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

    /**
     * В случае исключение одинаково обрабатываем для всех команд
     */
    public BotApiMethod<Message> handleException(IOException exception, String chatId)
    {
        System.err.println(exception.getMessage());
        exception.printStackTrace();
        return new SendMessage(chatId, exception.getMessage());
    }
}
