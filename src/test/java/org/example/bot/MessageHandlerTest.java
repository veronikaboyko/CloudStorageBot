package org.example.bot;

import org.junit.Before;
import org.junit.Test;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import static org.junit.Assert.*;

public class MessageHandlerTest
{
    private MessageHandler messageHandler;
    private final String TEST_CHAT_ID = "1";


    @Before
    public void setUp()
    {
        messageHandler = new MessageHandler();
    }

    /**
     * Тест команды /create, /delete и /listFiles
     */
    @Test
    public void testHandleCreateDeleteListCommand()
    {
        SendMessage createMessage = (SendMessage) messageHandler.handleUserMessage("/create file.txt", TEST_CHAT_ID);
        assertEquals(new SendMessage(TEST_CHAT_ID, "Файл успешно создан."), createMessage);
        SendMessage listFiles = (SendMessage) messageHandler.handleUserMessage("/listFiles", TEST_CHAT_ID);
        assertEquals(new SendMessage(TEST_CHAT_ID, "Список ваших файлов:\nfile.txt\n"), listFiles);
        SendMessage delMessage = (SendMessage) messageHandler.handleUserMessage("/delete file.txt", TEST_CHAT_ID);
        assertEquals(new SendMessage(TEST_CHAT_ID, "Файл успешно удален."), delMessage);
        listFiles = (SendMessage) messageHandler.handleUserMessage("/listFiles", TEST_CHAT_ID);
        assertEquals(new SendMessage(TEST_CHAT_ID, "Список ваших файлов:\n"), listFiles);
    }

    /**
     * Тест команды /create с недопустимым расширением файла
     */
    @Test
    public void testHandleCreateCommandWrongExtension()
    {
        SendMessage createMessage = (SendMessage) messageHandler.handleUserMessage("/create file.txtt", TEST_CHAT_ID);
        assertEquals(new SendMessage(TEST_CHAT_ID, "Неверное расширение файла. Допустимые расширения: txt, json, xml."), createMessage);
    }

    /**
     * Тест команды /create (создание уже существующего файла)
     */
    @Test
    public void testHandleCreateCommandAlreadyExist()
    {
        SendMessage firstCreateMessage = (SendMessage) messageHandler.handleUserMessage("/create ff.txt", TEST_CHAT_ID);
        SendMessage secondCreateMessage = (SendMessage) messageHandler.handleUserMessage("/create ff.txt", TEST_CHAT_ID);
        assertEquals(new SendMessage(TEST_CHAT_ID, "Файл с таким именем уже существует."), secondCreateMessage);
        SendMessage delMessage = (SendMessage) messageHandler.handleUserMessage("/delete ff.txt", TEST_CHAT_ID);

    }

    /**
     * Тест команды /delete (удаление несуществующего файла)
     */
    @Test
    public void testHandleDeleteCommandNotExist()
    {
        SendMessage createMessage = (SendMessage) messageHandler.handleUserMessage("/create f.txt", TEST_CHAT_ID);
        SendMessage firstDeleteMessage = (SendMessage) messageHandler.handleUserMessage("/delete f.txt", TEST_CHAT_ID);
        SendMessage secondDeleteMessage = (SendMessage) messageHandler.handleUserMessage("/delete f.txt", TEST_CHAT_ID);
        assertEquals(new SendMessage(TEST_CHAT_ID, "Файл с таким именем не существует."), secondDeleteMessage);
    }


}