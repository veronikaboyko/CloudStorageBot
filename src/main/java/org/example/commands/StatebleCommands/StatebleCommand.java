package org.example.commands.StatebleCommands;

import org.example.commands.Command;

/**
 * Интерфейс, определяющий, что команда должна хранить состояние
 * с предыдущих сообщений пользователя.
 */
public interface StatebleCommand extends Command
{
    /**
     * Провряет, что команда находится в заключительном состоянии
     */
    boolean onLastState();
}
