package org.example.commands;

/**
 * Маркирующий интерыейс, сообщающий о том, что команда составная.
 * То есть после ввода команды, пользователь еще хотя бы 1 раз должен что-то ввести
 */
public interface PartialCommand extends Command
{
}
