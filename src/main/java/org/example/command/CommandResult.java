package org.example.command;

import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;

/**
 * Результат работы команды.
 * Представляет пару:
 * dataForUser - BotApiMethod<?> - сообщение/файл для пользователя
 * canUpdate - boolean - можно ли команде переходить в следующее состояние
 */
public class CommandResult
{
    private final PartialBotApiMethod<?> dataForUser;
    private final boolean canUpdate;

    public CommandResult(PartialBotApiMethod<?> dataForUser, boolean canUpdate)
    {
        this.dataForUser = dataForUser;
        this.canUpdate = canUpdate;
    }

    public PartialBotApiMethod<?> getDataForUser()
    {
        return dataForUser;
    }

    public boolean canUpdate()
    {
        return canUpdate;
    }
}
