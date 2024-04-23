package org.example.commands;

import org.example.internal.FileManager;
import org.junit.Test;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ListFilesCommandTest
{
    private static final String TEST_CHAT_ID = "TEST_USER_1";

    @Test
    public void handle() throws IOException
    {
        //предварительные действия по созданию файлов
        FileManager fileManager = new FileManager();
        fileManager.createFile("1.txt",TEST_CHAT_ID);
        fileManager.createFile("2.xml",TEST_CHAT_ID);
        //---------------------------
        Command command = new ListFilesCommand();
        BotApiMethod result = command.handle("/listFiles",TEST_CHAT_ID);
        assertTrue(result instanceof SendMessage);
        assertEquals("Список ваших файлов:\n1.txt\n2.xml\n", ((SendMessage) result).getText());
        //удаляем файлы
        fileManager.deleteFile("1.txt",TEST_CHAT_ID);
        fileManager.deleteFile("2.xml",TEST_CHAT_ID);
    }
}