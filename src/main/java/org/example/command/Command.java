package org.example.command;

import org.example.state.State;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;

import java.io.IOException;

/**
 * Интерфейс маркер для определения команд, обрабатывающих сообщения пользователя.
 * Служит для объекдинения команд, которые работают отталкиваясь от состояния
 * и обычных команд
 */
public interface Command
{
    BotApiMethod<?> handle(String messageFromUser, String chatId, State state) throws IOException;
}