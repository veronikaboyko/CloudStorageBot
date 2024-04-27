package org.example.commands.stateful;

/**
 * Кэширует информацию для отдельного пользователя,
 * которая в дальнейшем пригодится командам.
 * Например, имя файла, с которым нужно работать.
 */
public interface Casher<T>
{
    /**
     * Добавить данные в кэш
     */
    void add(String chatID, T data);

    /**
     * Извлечь данные из кэша для отдельного пользователя
     */
    T getData(String chatID);

    /**
     * Очистить данные из кэша по конретному пользователю
     */
    void clearUserCash(String chatID);
}
