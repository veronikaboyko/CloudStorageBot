package org.example.bot;

import org.example.internal.FileManager;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.InputFile;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class MessageHandlerTest
{
    private MessageHandler messageHandler;
    private final String TEST_CHAT_ID = "1_";

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Before
    public void setUp() throws IOException
    {
        File directoryForTempFiles = temporaryFolder.newFolder("forTesting");
        messageHandler = new MessageHandler(
                new FileManager(directoryForTempFiles.getAbsolutePath() + "/")
        );
    }

    /**
     * Тест команды /create, /delete и /listFiles
     */
    @Test
    public void testHandleCreateDeleteListCommand()
    {
        final String file = "file.txt";
        SendMessage createMessage = (SendMessage) messageHandler.handleUserMessage("/create %s".formatted(file), TEST_CHAT_ID);
        assertEquals(new SendMessage(TEST_CHAT_ID, "Файл %s успешно создан.".formatted(file)), createMessage);
        SendMessage listFiles = (SendMessage) messageHandler.handleUserMessage("/listFiles", TEST_CHAT_ID);
        assertEquals(new SendMessage(TEST_CHAT_ID, "Список ваших файлов:\n%s\n".formatted(file)), listFiles);
        SendMessage delMessage = (SendMessage) messageHandler.handleUserMessage("/delete %s".formatted(file), TEST_CHAT_ID);
        assertEquals(new SendMessage(TEST_CHAT_ID, "Файл успешно удален."), delMessage);
        listFiles = (SendMessage) messageHandler.handleUserMessage("/listFiles", TEST_CHAT_ID);
        assertEquals(new SendMessage(TEST_CHAT_ID, "У вас пока еще нет файлов."), listFiles);
        messageHandler.handleUserMessage("/delete %s".formatted(file), TEST_CHAT_ID);
    }

    /**
     * Тест команды /create с недопустимым расширением файла
     */
    @Test
    public void testHandleCreateCommandWrongExtension()
    {
        String testingFile = "file.txtt";
        SendMessage createMessage = (SendMessage) messageHandler.handleUserMessage("/create %s".formatted(testingFile), TEST_CHAT_ID);
        assertEquals(new SendMessage(TEST_CHAT_ID, "Не удалось создать файл %s.".formatted(testingFile)
                + " Неверное расширение файла. Допустимые расширения: txt, json, xml."), createMessage);
        //Если же все-таки создался обязательно удаляем.
        messageHandler.handleUserMessage("/delete %s".formatted("file.txt"), TEST_CHAT_ID);
    }

    /**
     * Тест команды /create (создание уже существующего файла)
     */
    @Test
    public void testHandleCreateCommandAlreadyExist()
    {
        messageHandler.handleUserMessage("/create ff.txt", TEST_CHAT_ID);
        SendMessage secondCreateMessage = (SendMessage) messageHandler.handleUserMessage("/create ff.txt", TEST_CHAT_ID);
        assertEquals(new SendMessage(TEST_CHAT_ID, "Не удалось создать файл %s.".formatted("ff.txt")
                + " Файл с таким именем уже существует."), secondCreateMessage);
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
        assertEquals(new SendMessage(TEST_CHAT_ID, "Не удалось удалить файл %s.".formatted("f.txt")
                + " Файла с таким названием не существует."), secondDeleteMessage);
        //вруг при создании возникли ошибки
        messageHandler.handleUserMessage("/delete f.txt", TEST_CHAT_ID);
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
        messageHandler.handleUserMessage("/delete f.txt", TEST_CHAT_ID); //вдруг при создании f.txt возникли ошибки
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
        assertEquals(new SendMessage(TEST_CHAT_ID, "Некорректное название файла."), sendMessage);
        messageHandler.handleUserMessage("/delete f.txt", TEST_CHAT_ID);
    }

    /**
     * Тест команды /EditFileName (смену имени несуществующего файла)
     */
    @Test
    public void testHandleEditFileNameCommandNotExistingFile()
    {
        final SendMessage sendMessage = (SendMessage) messageHandler.handleUserMessage("/editFileName f.txt", TEST_CHAT_ID);
        assertEquals(new SendMessage(TEST_CHAT_ID, "Файла с таким названием не существует."), sendMessage);
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
        assertEquals(new SendMessage(TEST_CHAT_ID, "Файла с таким названием не существует."), sendMessage);
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
        assertEquals(new SendMessage(TEST_CHAT_ID, "Файл с таким именем не найден."), sendMessage);
    }

    /**
     * Тест команды /viewFileContent (просмотр содержимого у несуществующего файла)
     */
    @Test
    public void testHandleViewFileContentTestIncorrectInputFIle()
    {
        final SendMessage sendMessage = (SendMessage) messageHandler.handleUserMessage("/viewFileContent f.txt", TEST_CHAT_ID);
        assertEquals(new SendMessage(TEST_CHAT_ID, "Файла с таким названием не существует."), sendMessage);
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
    }

    /**
     * Тест команды /findFileName (поиск подстроки в именах файлов)
     */
    @Test
    public void testHandleFindFileName()
    {
        messageHandler.handleUserMessage("/create f.txt", TEST_CHAT_ID);
        messageHandler.handleUserMessage("/create t.txt", TEST_CHAT_ID);
        final SendMessage sendMessage = (SendMessage) messageHandler.handleUserMessage("/findFileName f", TEST_CHAT_ID);
        assertEquals("По запросу “f” найдены следующие файлы:\nf.txt\n", sendMessage.getText());
    }

    /**
     * Тест команды /findFileName (не найдено таких файлов)
     */
    @Test
    public void testHandleFindFileNameNotFound()
    {
        messageHandler.handleUserMessage("/create f.txt", TEST_CHAT_ID);
        messageHandler.handleUserMessage("/create t.txt", TEST_CHAT_ID);
        final SendMessage sendMessage = (SendMessage) messageHandler.handleUserMessage("/findFileName r", TEST_CHAT_ID);
        assertEquals("По запросу “r” не найдено файлов.", sendMessage.getText());
    }


    /**
     * Тест команды /findFile (поиск подстроки в содержимом файлов)
     */
    @Test
    public void testHandleFindFile()
    {
        messageHandler.handleUserMessage("/create f.txt", TEST_CHAT_ID);
        messageHandler.handleUserMessage("/writeToFile f.txt", TEST_CHAT_ID);
        messageHandler.handleUserMessage("какой-то текст", TEST_CHAT_ID);
        final SendMessage sendMessage = (SendMessage) messageHandler.handleUserMessage("/findFile -то текст", TEST_CHAT_ID);
        assertEquals("По запросу “-то текст” найдены следующие файлы:\nf.txt\n", sendMessage.getText());
    }

    /**
     * Тест команды /findFile (не найдено таких файлов)
     */
    @Test
    public void testHandleFindFileNotFound()
    {
        messageHandler.handleUserMessage("/create f.txt", TEST_CHAT_ID);
        messageHandler.handleUserMessage("/writeToFile f.txt", TEST_CHAT_ID);
        messageHandler.handleUserMessage("какой-то текст", TEST_CHAT_ID);
        final SendMessage sendMessage = (SendMessage) messageHandler.handleUserMessage("/findFile мемеме", TEST_CHAT_ID);
        assertEquals("По запросу “мемеме” не найдено файлов.", sendMessage.getText());
    }
}