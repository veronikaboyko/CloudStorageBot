package org.example.command;

import org.example.internal.ConstantManager;
import org.example.state.State;
import org.example.state.StateSwitcher;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;


/**
 * Команда /start.
 */
public class StartCommand extends AbstractCommand
{
    public StartCommand()
    {
        super(new StateSwitcher(State.ON_COMMAND_FROM_USER));
    }

    @Override
    public CommandResult handle(Message messageFromUser, String chatId, State state)
    {
        return new CommandResult(new SendMessage(chatId, ConstantManager.HELP_MESSAGE),true);
    }
}
