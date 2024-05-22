package org.example.command;

import org.example.state.State;
import org.telegram.telegrambots.meta.api.objects.Message;

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
    CommandResult handle(Message messageFromUser, String chatId, State state) throws IOException;
}