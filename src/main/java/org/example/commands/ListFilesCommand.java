package org.example.commands;

import org.example.internal.DirectoryManager;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

/**
 * Команда /listFiles
 */
public class ListFilesCommand implements Command
{

    @Override
    public BotApiMethod handle(String messageFromUser, String chatId)
    {
        return new SendMessage(chatId, new DirectoryManager().listFiles(chatId));
    }
}
