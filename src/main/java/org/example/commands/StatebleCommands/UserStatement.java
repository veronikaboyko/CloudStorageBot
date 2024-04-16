package org.example.commands.StatebleCommands;

/**
 * Отслеживает, в каком состоянии сейчас находится пользователь
 */
public enum UserStatement
{
    STATE_1, //пользователь еще не прислал данные
    STATE_2, //пользователь уже прислал данные
    LAST_STATE //заключительное состояние
}
