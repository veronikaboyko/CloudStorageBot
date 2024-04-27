package org.example.commands;

import org.example.internal.ConstantManager;
import org.example.state.State;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;


/**
 * Команда /start.
 */
public class StartCommand extends AbstractCommand implements OneStateCommand
{
    @Override
    public BotApiMethod handle(String messageFromUser, String chatId, State state)
    {
        return new SendMessage(chatId, ConstantManager.HELP_MESSAGE);
    }
}
