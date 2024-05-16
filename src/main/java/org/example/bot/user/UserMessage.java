package org.example.bot.user;

/**
 * Контракт входящего от пользователя сообщения
 */
public interface UserMessage<T>
{
    /**
     * Получить содержимое входного сообщения
     */
    T getContent();
}
