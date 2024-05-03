package org.example.bot;

import org.example.internal.ConstantManager;
import org.junit.Before;
import org.junit.Test;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import static org.junit.Assert.*;

public class MessageHandlerTest
{
    private MessageHandler messageHandler;
    private final String TEST_CHAT_ID = "1_";


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
        messageHandler.handleUserMessage("/create ff.txt", TEST_CHAT_ID);
        SendMessage secondCreateMessage = (SendMessage) messageHandler.handleUserMessage("/create ff.txt", TEST_CHAT_ID);
        assertEquals(new SendMessage(TEST_CHAT_ID, "Файл с таким именем уже существует."), secondCreateMessage);
        messageHandler.handleUserMessage("/delete ff.txt", TEST_CHAT_ID);
    }

    /**
     * Тест команды /delete (удаление несуществующего файла)
     */
    @Test
    public void testHandleDeleteCommandNotExist()
    {
        messageHandler.handleUserMessage("/create f.txt", TEST_CHAT_ID);
        messageHandler.handleUserMessage("/delete f.txt", TEST_CHAT_ID);
        SendMessage secondDeleteMessage = (SendMessage) messageHandler.handleUserMessage("/delete f.txt", TEST_CHAT_ID);
        assertEquals(new SendMessage(TEST_CHAT_ID, "Файла с таким названием не существует!"), secondDeleteMessage);
    }

    /**
     * Тест команды /EditFileName (удостоверяемся, что название файла изменилось)
     */
    @Test
    public void testHandleEditFileNameCommandChange()
    {
        messageHandler.handleUserMessage("/create f.txt", TEST_CHAT_ID);
        messageHandler.handleUserMessage("/editFileName f.txt", TEST_CHAT_ID);
        messageHandler.handleUserMessage("f2.txt", TEST_CHAT_ID);
        SendMessage listFiles = (SendMessage) messageHandler.handleUserMessage("/listFiles", TEST_CHAT_ID);
        assertEquals(new SendMessage(TEST_CHAT_ID, "Список ваших файлов:\nf2.txt\n"), listFiles);
        messageHandler.handleUserMessage("/delete f2.txt", TEST_CHAT_ID);
    }

    /**
     * Тест команды /EditFileName (проверяем на некорректное название файла)
     */
    @Test
    public void testHandleEditFileNameCommandNotCorrect()
    {
        messageHandler.handleUserMessage("/create f.txt", TEST_CHAT_ID);
        messageHandler.handleUserMessage("/editFileName f.txt", TEST_CHAT_ID);
        final SendMessage sendMessage = (SendMessage) messageHandler.handleUserMessage("f2.txtt", TEST_CHAT_ID);
        assertEquals(new SendMessage(TEST_CHAT_ID, "Некорректное название файла"), sendMessage);
        messageHandler.handleUserMessage("/delete f.txt", TEST_CHAT_ID);
    }

    /**
     * Тест команды /EditFileName (смену имени несуществующего файла)
     */
    @Test
    public void testHandleEditFileNameCommandNotExistingFile()
    {
        final SendMessage sendMessage = (SendMessage) messageHandler.handleUserMessage("/editFileName f.txt", TEST_CHAT_ID);
        assertEquals(new SendMessage(TEST_CHAT_ID, "Файла с таким названием не существует!"), sendMessage);
    }

    /**
     * Тест команды /EditFile (удостоверяемся, содержимое файла изменилось)
     */
    @Test
    public void testHandleEditFileCommandChange()
    {
        messageHandler.handleUserMessage("/create f.txt", TEST_CHAT_ID);
        messageHandler.handleUserMessage("/editFile f.txt", TEST_CHAT_ID);
        messageHandler.handleUserMessage("Some information", TEST_CHAT_ID);
        final SendMessage sendMessage = (SendMessage) messageHandler.handleUserMessage("/viewFileContent f.txt", TEST_CHAT_ID);
        assertEquals("Some information\n", sendMessage.getText());
        messageHandler.handleUserMessage("/delete f.txt", TEST_CHAT_ID);
    }

    /**
     * Тест команды /EditFile (проверяем на некорректное название файла)
     */
    @Test
    public void testHandleEditFileCommandNotCorrectName()
    {
        final SendMessage sendMessage = (SendMessage) messageHandler.handleUserMessage("/editFile f.txt", TEST_CHAT_ID);
        assertEquals(new SendMessage(TEST_CHAT_ID, "Файла с таким названием не существует!"), sendMessage);
    }

    /**
     * Тест команды /EditFile (реакция на ввод команды)
     */
    @Test
    public void testHandleEditFileCommandTestInputAnotherCommand()
    {
        messageHandler.handleUserMessage("/create f.txt", TEST_CHAT_ID);
        messageHandler.handleUserMessage("/editFile f.txt", TEST_CHAT_ID);
        final SendMessage sendMessage = (SendMessage) messageHandler.handleUserMessage("/listFiles", TEST_CHAT_ID);
        assertNotEquals("Some information\n", sendMessage.getText());
        messageHandler.handleUserMessage("/delete f.txt", TEST_CHAT_ID);
    }

    /**
     * Тест команды /writeToFile (проверка содержимого)
     */
    @Test
    public void testHandleWriteToFileTestInputSaved()
    {
        messageHandler.handleUserMessage("/create f.txt", TEST_CHAT_ID);
        messageHandler.handleUserMessage("/writeToFile f.txt", TEST_CHAT_ID);
        messageHandler.handleUserMessage("1", TEST_CHAT_ID);
        messageHandler.handleUserMessage("/writeToFile f.txt", TEST_CHAT_ID);
        messageHandler.handleUserMessage("2", TEST_CHAT_ID);
        final SendMessage sendMessage = (SendMessage) messageHandler.handleUserMessage("/viewFileContent f.txt", TEST_CHAT_ID);
        assertEquals("12\n", sendMessage.getText());
        messageHandler.handleUserMessage("/delete f.txt", TEST_CHAT_ID);
    }

    /**
     * Тест команды /writeToFile (запись в несуществующий файл)
     */
    @Test
    public void testHandleWriteToFileTestIncorrectInputFIle()
    {
        final SendMessage sendMessage = (SendMessage) messageHandler.handleUserMessage("/writeToFile f.txt", TEST_CHAT_ID);
        assertEquals(new SendMessage(TEST_CHAT_ID, "Сначала создайте этот файл"), sendMessage);
    }

    /**
     * Тест команды /viewFileContent (просмотр содержимого у несуществующего файла)
     */
    @Test
    public void testHandleViewFileContentTestIncorrectInputFIle()
    {
        final SendMessage sendMessage = (SendMessage) messageHandler.handleUserMessage("/viewFileContent f.txt", TEST_CHAT_ID);
        assertEquals(new SendMessage(TEST_CHAT_ID, "Файла с таким названием не существует!"), sendMessage);
    }

    /**
     * Тест команды /viewFileContent на корректное сожержимое файла
     */
    @Test
    public void testHandleViewFileContentTestСorrectFileInside()
    {
        messageHandler.handleUserMessage("/create f.txt", TEST_CHAT_ID);
        messageHandler.handleUserMessage("/writeToFile f.txt", TEST_CHAT_ID);
        messageHandler.handleUserMessage("1", TEST_CHAT_ID);
        messageHandler.handleUserMessage("/editFile f.txt", TEST_CHAT_ID);
        messageHandler.handleUserMessage("2", TEST_CHAT_ID);
        final SendMessage sendMessage = (SendMessage) messageHandler.handleUserMessage("/viewFileContent f.txt", TEST_CHAT_ID);
        assertEquals("2\n", sendMessage.getText());
        messageHandler.handleUserMessage("/delete f.txt", TEST_CHAT_ID);
    }

}