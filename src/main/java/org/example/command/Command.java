package org.example.command;

import org.example.bot.user.UserMessage;
import org.example.state.State;

import java.io.IOException;

/**
 * Интерфейс для определения команд, обрабатывающих сообщения пользователя.
 * Служит для объединения команд, которые работают отталкиваясь от состояния
 * и обычных команд
 */
public interface Command
{
    /**
     * Выполнить действие, запрашиваемое пользователем
     */
    CommandResult handle(UserMessage<?> messageFromUser, String chatId, State state) throws IOException;
}