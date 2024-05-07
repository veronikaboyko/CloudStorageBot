package org.example.state;

/**
 * Состояние команды, чтобы понять, какой функционал команды задействовать
 */
public enum State
{
    /**
     * Получили команду
     */
    GOT_COMMAND_FROM_USER,
    /**
     * Получили данные
     */
    GOT_DATA_FROM_USER
}
