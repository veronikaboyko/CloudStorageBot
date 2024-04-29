//package org.example.command.stateful;
//
//import org.example.internal.FileManager;
//import org.junit.Test;
//import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
//import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
//
//import java.io.IOException;
//
//import static org.junit.Assert.*;
//
//public class EditFileCommandTest
//{
//    private static final String TEST_CHAT_ID = "TEST_USER_1";
//
//    /**
//     * Тестируем валидное редактирование файла
//     */
//    @Test
//    public void testValidFileEdit() throws IOException
//    {
//        //предварительные действия
//        final EditFileCommand editFileCommand = new EditFileCommand();
//        final FileManager fileManager = new FileManager();
//        final String fileName = "/editFile existingFile.txt";
//        String newContent = "This is the new content";
//        fileManager.createFile(fileName, TEST_CHAT_ID);
//        //--------------------------------------------
//        BotApiMethod response = editFileCommand.handle(fileName, TEST_CHAT_ID);
//        assertTrue(response instanceof SendMessage);
//        assertEquals("Введите новое содержимое файла.", ((SendMessage) response).getText());
//
//
//        response = editFileCommand.handle(newContent, TEST_CHAT_ID);
//        assertTrue(response instanceof SendMessage);
//        assertEquals("Файл успешно сохранен.", ((SendMessage) response).getText());
//
//        assertTrue(editFileCommand.onLastState());
//
//        fileManager.deleteFile(fileName, TEST_CHAT_ID);
//    }
//
//    /**
//     * Тестируем реакцию на невалидный файл
//     */
//    @Test
//    public void testInvalidFileName()
//    {
//        final EditFileCommand editFileCommand = new EditFileCommand();
//        String chatId = "testChatId";
//        String invalidFileName = "/editFile invalidFile.tx";
//
//        BotApiMethod response = editFileCommand.handle(invalidFileName, chatId);
//        assertTrue(response instanceof SendMessage);
//        assertEquals("Некорректное название файла", ((SendMessage) response).getText());
//    }
//
//    /**
//     * Тестируем несущетсвующие файлы
//     */
//    @Test
//    public void testNonExistentFile()
//    {
//        final EditFileCommand editFileCommand = new EditFileCommand();
//        String chatId = "testChatId";
//        String nonExistentFileName = "/editFile nonExistentFile.txt";
//        BotApiMethod response = editFileCommand.handle(nonExistentFileName, chatId);
//        assertTrue(response instanceof SendMessage);
//        assertEquals("Файла с таким названием не существует", ((SendMessage) response).getText());
//    }
//
//
//    /**
//     * Тестируем работу аргументов
//     */
//    @Test
//    public void testArgumentChecking()
//    {
//        final EditFileCommand editFileCommand = new EditFileCommand();
//        String chatId = "testChatId";
//        String invalidArguments = "/editFile arguments";
//        BotApiMethod response = editFileCommand.handle(invalidArguments, chatId);
//        assertTrue(response instanceof SendMessage);
//        assertEquals("Некорректное название файла", ((SendMessage) response).getText());
//    }
//
//    /**
//     * Тестируем, что бот в корректном состоянии
//     */
//    @Test
//    public void testStateTransition()
//    {
//        final EditFileCommand editFileCommand = new EditFileCommand();
//        String chatId = "testChatId";
//        String fileName = "/editFile existingFile.txt";
//
//        BotApiMethod response = editFileCommand.handle(fileName, chatId);
//        assertTrue(response instanceof SendMessage);
//        assertEquals("Файла с таким названием не существует", ((SendMessage) response).getText());
//
//        assertFalse(editFileCommand.onLastState());
//    }
//
//}