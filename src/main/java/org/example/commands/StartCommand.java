package org.example.commands;

import org.example.internal.ConstantManager;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;


/**
 * Команда /start.
 */
public class StartCommand implements Command
{

    private final String START_MESSAGE = ConstantManager.HELP_MESSAGE;

    @Override
    public BotApiMethod handle(String messageFromUser, String chatId)
    {
        return new SendMessage(chatId, START_MESSAGE);
    }
}
