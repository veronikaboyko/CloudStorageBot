package org.example.bot;


import org.example.internal.FileManager;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class MessageHandlerTest
{
    private MessageHandler messageHandler;
    private final String TEST_CHAT_ID = "1_";
    private Message userMessage;

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Before
    public void setUp() throws IOException
    {
        File directoryForTempFiles = temporaryFolder.newFolder("forTesting");
        messageHandler = new MessageHandler(
                new FileManager(directoryForTempFiles.getAbsolutePath() + "/"),
                null
        );
        userMessage = new Message();
    }

    /**
     * Тест команды /create, /delete и /listFiles
     */
    @Test
    public void testHandleCreateDeleteListCommand()
    {
        final String file = "file.txt";
        userMessage.setText("/create %s".formatted(file));
        SendMessage createMessage = (SendMessage) messageHandler.handleUserMessage(userMessage, TEST_CHAT_ID);
        assertEquals(new SendMessage(TEST_CHAT_ID, "Файл %s успешно создан.".formatted(file)), createMessage);
        userMessage.setText("/listFiles");
        SendMessage listFiles = (SendMessage) messageHandler.handleUserMessage(userMessage, TEST_CHAT_ID);
        assertEquals(new SendMessage(TEST_CHAT_ID, "Список ваших файлов:\n%s\n".formatted(file)), listFiles);
        userMessage.setText("/delete %s".formatted(file));
        SendMessage delMessage = (SendMessage) messageHandler.handleUserMessage(userMessage, TEST_CHAT_ID);
        assertEquals(new SendMessage(TEST_CHAT_ID, "Файл успешно удален."), delMessage);
        userMessage.setText("/listFiles");
        listFiles = (SendMessage) messageHandler.handleUserMessage(userMessage, TEST_CHAT_ID);
        assertEquals(new SendMessage(TEST_CHAT_ID, "Список ваших файлов:\n"), listFiles);
        userMessage.setText("/delete %s".formatted(file));
        messageHandler.handleUserMessage(userMessage, TEST_CHAT_ID);
    }

    /**
     * Тест команды /create с недопустимым расширением файла
     */
    @Test
    public void testHandleCreateCommandWrongExtension()
    {
        String testingFile = "file.txtt";
        userMessage.setText("/create %s".formatted(testingFile));
        SendMessage createMessage = (SendMessage) messageHandler.handleUserMessage(userMessage, TEST_CHAT_ID);
        assertEquals(new SendMessage(TEST_CHAT_ID, "Не удалось создать файл %s.".formatted(testingFile)
                + " Неверное расширение файла. Допустимые расширения: txt, json, xml."), createMessage);
        //Если же все-таки создался обязательно удаляем.
        userMessage.setText("/delete %s".formatted("file.txt"));
        messageHandler.handleUserMessage(userMessage, TEST_CHAT_ID);
    }

    /**
     * Тест команды /create (создание уже существующего файла)
     */
    @Test
    public void testHandleCreateCommandAlreadyExist()
    {
        userMessage.setText("/create ff.txt");
        messageHandler.handleUserMessage(userMessage, TEST_CHAT_ID);
        userMessage.setText("/create ff.txt");
        SendMessage secondCreateMessage = (SendMessage) messageHandler.handleUserMessage(userMessage, TEST_CHAT_ID);
        assertEquals(new SendMessage(TEST_CHAT_ID, "Не удалось создать файл %s.".formatted("ff.txt")
                + " Файл с таким именем уже существует."), secondCreateMessage);
        userMessage.setText("/delete ff.txt");
        messageHandler.handleUserMessage(userMessage, TEST_CHAT_ID);
    }

    /**
     * Тест команды /delete (удаление несуществующего файла)
     */
    @Test
    public void testHandleDeleteCommandNotExist()
    {
        userMessage.setText("/create f.txt");
        messageHandler.handleUserMessage(userMessage, TEST_CHAT_ID);
        userMessage.setText("/delete f.txt");
        messageHandler.handleUserMessage(userMessage, TEST_CHAT_ID);
        SendMessage secondDeleteMessage = (SendMessage) messageHandler.handleUserMessage(userMessage, TEST_CHAT_ID);
        assertEquals(new SendMessage(TEST_CHAT_ID, "Не удалось удалить файл %s.".formatted("f.txt")
                + " Файла с таким названием не существует."), secondDeleteMessage);
        //вруг при создании возникли ошибки
        messageHandler.handleUserMessage(userMessage, TEST_CHAT_ID);
    }

    /**
     * Тест команды /EditFileName (удостоверяемся, что название файла изменилось)
     */
    @Test
    public void testHandleEditFileNameCommandChange()
    {
        userMessage.setText("/create f.txt");
        messageHandler.handleUserMessage(userMessage, TEST_CHAT_ID);
        userMessage.setText("/editFileName f.txt");
        messageHandler.handleUserMessage(userMessage, TEST_CHAT_ID);
        userMessage.setText("f2.txt");
        messageHandler.handleUserMessage(userMessage, TEST_CHAT_ID);
        userMessage.setText("/listFiles");
        SendMessage listFiles = (SendMessage) messageHandler.handleUserMessage(userMessage, TEST_CHAT_ID);
        assertEquals(new SendMessage(TEST_CHAT_ID, "Список ваших файлов:\nf2.txt\n"), listFiles);
        userMessage.setText("/delete f.txt");
        messageHandler.handleUserMessage(userMessage, TEST_CHAT_ID); //вдруг при создании f.txt возникли ошибки
        userMessage.setText("/delete f2.txt");
        messageHandler.handleUserMessage(userMessage, TEST_CHAT_ID);
    }

    /**
     * Тест команды /EditFileName (проверяем на некорректное название файла)
     */
    @Test
    public void testHandleEditFileNameCommandNotCorrect()
    {
        userMessage.setText("/create f.txt");
        messageHandler.handleUserMessage(userMessage, TEST_CHAT_ID);
        userMessage.setText("/editFileName f.txt");
        messageHandler.handleUserMessage(userMessage, TEST_CHAT_ID);
        userMessage.setText("f2.txtt");
        final SendMessage sendMessage = (SendMessage) messageHandler.handleUserMessage(userMessage, TEST_CHAT_ID);
        assertEquals(new SendMessage(TEST_CHAT_ID, "Некорректное название файла."), sendMessage);
        userMessage.setText("/delete f.txt");
        messageHandler.handleUserMessage(userMessage, TEST_CHAT_ID);
    }

    /**
     * Тест команды /EditFileName (смену имени несуществующего файла)
     */
    @Test
    public void testHandleEditFileNameCommandNotExistingFile()
    {
        userMessage.setText("/editFileName f.txt");
        final SendMessage sendMessage = (SendMessage) messageHandler.handleUserMessage(userMessage, TEST_CHAT_ID);
        assertEquals(new SendMessage(TEST_CHAT_ID, "Файла с таким названием не существует."), sendMessage);
    }

    /**
     * Тест команды /EditFile (удостоверяемся, содержимое файла изменилось)
     */
    @Test
    public void testHandleEditFileCommandChange()
    {
        userMessage.setText("/create f.txt");
        messageHandler.handleUserMessage(userMessage, TEST_CHAT_ID);
        userMessage.setText("/editFile f.txt");
        messageHandler.handleUserMessage(userMessage, TEST_CHAT_ID);
        userMessage.setText("Some information");
        messageHandler.handleUserMessage(userMessage, TEST_CHAT_ID);
        userMessage.setText("/viewFileContent f.txt");
        final SendMessage sendMessage = (SendMessage) messageHandler.handleUserMessage(userMessage, TEST_CHAT_ID);
        assertEquals("Some information\n", sendMessage.getText());
        userMessage.setText("/delete f.txt");
        messageHandler.handleUserMessage(userMessage, TEST_CHAT_ID);
    }

    /**
     * Тест команды /EditFile (проверяем на некорректное название файла)
     */
    @Test
    public void testHandleEditFileCommandNotCorrectName()
    {
        userMessage.setText("/editFile f.txt");
        final SendMessage sendMessage = (SendMessage) messageHandler.handleUserMessage(userMessage, TEST_CHAT_ID);
        assertEquals(new SendMessage(TEST_CHAT_ID, "Файла с таким названием не существует."), sendMessage);
    }

    /**
     * Тест команды /EditFile (реакция на ввод команды)
     */
    @Test
    public void testHandleEditFileCommandTestInputAnotherCommand()
    {
        userMessage.setText("/create f.txt");
        messageHandler.handleUserMessage(userMessage, TEST_CHAT_ID);
        userMessage.setText("/editFile f.txt");
        messageHandler.handleUserMessage(userMessage, TEST_CHAT_ID);
        userMessage.setText("/listFiles");
        final SendMessage sendMessage = (SendMessage) messageHandler.handleUserMessage(userMessage, TEST_CHAT_ID);
        assertNotEquals("Some information\n", sendMessage.getText());
        userMessage.setText("/delete f.txt");
        messageHandler.handleUserMessage(userMessage, TEST_CHAT_ID);
    }

    /**
     * Тест команды /writeToFile (проверка содержимого)
     */
    @Test
    public void testHandleWriteToFileTestInputSaved()
    {
        userMessage.setText("/create f.txt");
        messageHandler.handleUserMessage(userMessage, TEST_CHAT_ID);
        userMessage.setText("/writeToFile f.txt");
        messageHandler.handleUserMessage(userMessage, TEST_CHAT_ID);
        userMessage.setText("1");
        messageHandler.handleUserMessage(userMessage, TEST_CHAT_ID);
        userMessage.setText("/writeToFile f.txt");
        messageHandler.handleUserMessage(userMessage, TEST_CHAT_ID);
        userMessage.setText("2");
        messageHandler.handleUserMessage(userMessage, TEST_CHAT_ID);
        userMessage.setText("/viewFileContent f.txt");
        final SendMessage sendMessage = (SendMessage) messageHandler.handleUserMessage(userMessage, TEST_CHAT_ID);
        assertEquals("12\n", sendMessage.getText());
        userMessage.setText("/delete f.txt");
        messageHandler.handleUserMessage(userMessage, TEST_CHAT_ID);
    }

    /**
     * Тест команды /writeToFile (запись в несуществующий файл)
     */
    @Test
    public void testHandleWriteToFileTestIncorrectInputFIle()
    {
        userMessage.setText("/writeToFile f.txt");
        final SendMessage sendMessage = (SendMessage) messageHandler.handleUserMessage(userMessage, TEST_CHAT_ID);
        assertEquals(new SendMessage(TEST_CHAT_ID, "Файл с таким именем не найден."), sendMessage);
    }

    /**
     * Тест команды /viewFileContent (просмотр содержимого у несуществующего файла)
     */
    @Test
    public void testHandleViewFileContentTestIncorrectInputFIle()
    {
        userMessage.setText("/viewFileContent f.txt");
        final SendMessage sendMessage = (SendMessage) messageHandler.handleUserMessage(userMessage, TEST_CHAT_ID);
        assertEquals(new SendMessage(TEST_CHAT_ID, "Файла с таким названием не существует."), sendMessage);
    }

    /**
     * Тест команды /viewFileContent на корректное сожержимое файла
     */
    @Test
    public void testHandleViewFileContentTestСorrectFileInside()
    {

        userMessage.setText("/create f.txt");
        messageHandler.handleUserMessage(userMessage, TEST_CHAT_ID);
        userMessage.setText("/writeToFile f.txt");
        messageHandler.handleUserMessage(userMessage, TEST_CHAT_ID);
        userMessage.setText("1");
        messageHandler.handleUserMessage(userMessage, TEST_CHAT_ID);
        userMessage.setText("/editFile f.txt");
        messageHandler.handleUserMessage(userMessage, TEST_CHAT_ID);
        userMessage.setText("2");
        messageHandler.handleUserMessage(userMessage, TEST_CHAT_ID);
        userMessage.setText("/viewFileContent f.txt");
        final SendMessage sendMessage = (SendMessage) messageHandler.handleUserMessage(userMessage, TEST_CHAT_ID);
        assertEquals("2\n", sendMessage.getText());
    }

    /**
     * Тест команды /findFileName (поиск подстроки в именах файлов)
     */
    @Test
    public void testHandleFindFileName()
    {
        userMessage.setText("/create f.txt");
        messageHandler.handleUserMessage(userMessage, TEST_CHAT_ID);
        userMessage.setText("/create t.txt");
        messageHandler.handleUserMessage(userMessage, TEST_CHAT_ID);
        userMessage.setText("/findFileName f");
        final SendMessage sendMessage = (SendMessage) messageHandler.handleUserMessage(userMessage, TEST_CHAT_ID);
        assertEquals("По запросу “f” найдены следующие файлы:\nf.txt\n", sendMessage.getText());
    }

    /**
     * Тест команды /findFileName (не найдено таких файлов)
     */
    @Test
    public void testHandleFindFileNameNotFound()
    {
        userMessage.setText("/create f.txt");
        messageHandler.handleUserMessage(userMessage, TEST_CHAT_ID);
        userMessage.setText("/create t.txt");
        messageHandler.handleUserMessage(userMessage, TEST_CHAT_ID);
        userMessage.setText("/findFileName r");
        final SendMessage sendMessage = (SendMessage) messageHandler.handleUserMessage(userMessage, TEST_CHAT_ID);
        assertEquals("По запросу “r” не найдено файлов.", sendMessage.getText());
    }


    /**
     * Тест команды /findFile (поиск подстроки в содержимом файлов)
     */
    @Test
    public void testHandleFindFile()
    {
        userMessage.setText("/create f.txt");
        messageHandler.handleUserMessage(userMessage, TEST_CHAT_ID);
        userMessage.setText("/writeToFile f.txt");
        messageHandler.handleUserMessage(userMessage, TEST_CHAT_ID);
        userMessage.setText("какой-то текст");
        messageHandler.handleUserMessage(userMessage, TEST_CHAT_ID);
        userMessage.setText("/findFile -то текст");
        final SendMessage sendMessage = (SendMessage) messageHandler.handleUserMessage(userMessage, TEST_CHAT_ID);
        assertEquals("По запросу “-то текст” найдены следующие файлы:\nf.txt\n", sendMessage.getText());
    }

    /**
     * Тест команды /findFile (не найдено таких файлов)
     */
    @Test
    public void testHandleFindFileNotFound()
    {
        userMessage.setText("/create f.txt");
        messageHandler.handleUserMessage(userMessage, TEST_CHAT_ID);
        userMessage.setText("/writeToFile f.txt");
        messageHandler.handleUserMessage(userMessage, TEST_CHAT_ID);
        userMessage.setText("какой-то текст");
        messageHandler.handleUserMessage(userMessage, TEST_CHAT_ID);
        userMessage.setText("/findFile мемеме");
        final SendMessage sendMessage = (SendMessage) messageHandler.handleUserMessage(userMessage, TEST_CHAT_ID);
        assertEquals("По запросу “мемеме” не найдено файлов.", sendMessage.getText());
    }

}