package org.example.command;

import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;

/**
 * Результат работы команды.
 * Представляет пару:
 * dataForUser - BotApiMethod<?> - сообщение/файл для пользователя
 * success - Команда отработала успешно
 */
public class CommandResult
{
    private final PartialBotApiMethod<?> dataForUser;
    private final boolean success;

    public CommandResult(PartialBotApiMethod<?> dataForUser, boolean success)
    {
        this.dataForUser = dataForUser;
        this.success = success;
    }

    /**
     * @return данные от команды пользователю
     */
    public PartialBotApiMethod<?> getDataForUser()
    {
        return dataForUser;
    }

    /**
     * @return Успешно ли отработала команда
     */
    public boolean success()
    {
        return success;
    }
}
