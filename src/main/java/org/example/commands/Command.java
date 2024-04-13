package org.example.commands;

import org.telegram.telegrambots.meta.api.methods.BotApiMethod;


/**
 * Интерфейс для определения команд, обрабатывающих сообщения пользователя.
 */
public interface Command {

    /**
     * Метод для обработки команды пользователя.
     *
     * @param messageFromUser Сообщение от пользователя.
     * @param chatId          ID чата.
     * @return Объект, который нужно отправить пользователю (текстовое сообщение, документ и т.д.).
     */
    BotApiMethod handle(String messageFromUser, String chatId);

}