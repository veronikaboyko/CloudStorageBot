package org.example.internal;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.*;

public class FileManagerTest
{

    private FileManager fileManager;
    private final String TEST_CHAT_ID = "123";
    private static final String TEST_CHAT_ID2 = "test_chat_id";
    private static final String TEST_FILE_NAME = "test.txt";
    private static final String TEST_NEW_FILE_NAME = "new_test.txt";

    @Before
    public void setUp()
    {
        fileManager = new FileManager();
    }

    /**
     * Тестирует создание нового файла.
     *
     * @throws IOException если происходит ошибка ввода-вывода
     */
    @Test
    public void testCreateFile() throws IOException
    {
        fileManager.createFile("testCreateFile.txt", TEST_CHAT_ID);
        Path filePath = Paths.get("src/main/java/org/example/usersData/user_" + TEST_CHAT_ID, "testCreateFile.txt");
        assertTrue(Files.exists(filePath));
        fileManager.deleteFile("testCreateFile.txt", TEST_CHAT_ID);
    }

    /**
     * Тестирует создание файла с недопустимым расширением.
     *
     * @throws IOException если происходит ошибка ввода-вывода
     */
    @Test
    public void testCreateFileWithInvalidExtension() throws IOException
    {
        IOException exception = assertThrows(IOException.class, () -> fileManager.createFile("testFile.jpg", TEST_CHAT_ID));
        assertTrue(exception.getMessage().contains("Неверное расширение файла. Допустимые расширения: txt, json, xml."));
    }

    /**
     * Тестирует создание файла, который уже существует.
     *
     * @throws IOException если происходит ошибка ввода-вывода
     */
    @Test
    public void testCreateFileThatAlreadyExists() throws IOException
    {
        fileManager.createFile("testFile.txt", TEST_CHAT_ID);
        IOException exception = assertThrows(IOException.class, () -> fileManager.createFile("testFile.txt", TEST_CHAT_ID));
        assertTrue(exception.getMessage().contains("Файл с таким именем уже существует."));
    }

    /**
     * Тестирует удаление файла (проверка на существование после удаления).
     *
     * @throws IOException если происходит ошибка ввода-вывода
     */
    @Test
    public void testDeleteFile() throws IOException
    {
        fileManager.createFile("testDeleteFile.txt", TEST_CHAT_ID);
        Path filePath = Paths.get("src/main/java/org/example/usersData/user_" + TEST_CHAT_ID,
                "testDeleteFile.txt");
        assertTrue(Files.exists(filePath));
        fileManager.deleteFile("testDeleteFile.txt", TEST_CHAT_ID);
        assertFalse(Files.exists(filePath));
    }

    /**
     * Тестирует удаление файла, который не существует.
     *
     * @throws IOException если происходит ошибка ввода-вывода
     */
    @Test
    public void testDeleteFileThatDoesNotAlreadyExists() throws IOException
    {
        fileManager.createFile("testDelFile.txt", TEST_CHAT_ID);
        fileManager.deleteFile("testDelFile.txt", TEST_CHAT_ID);
        IOException exception = assertThrows(IOException.class, () -> fileManager.deleteFile("testDelFile.txt", TEST_CHAT_ID));
        assertTrue(exception.getMessage().contains("Файл с таким именем не существует."));
    }

    /**
     * Тестирует редактирование содержимого файла (проверка, что добавленная в файл строка действительно дописалась).
     *
     * @throws IOException если происходит ошибка ввода-вывода
     */
    @Test(expected = NoSuchFileException.class)
    public void testEditFile() throws IOException
    {
        FileManager fileManager = new FileManager();
        String newText = "New text content";
        fileManager.editFile(TEST_FILE_NAME, TEST_CHAT_ID2, newText);
        assertTrue(Files.readString(getFilePath(TEST_FILE_NAME)).contains(newText));
    }

    /**
     * Тестирует изменение имени файла.
     *
     * @throws IOException если происходит ошибка ввода-вывода
     */
    @Test
    public void testEditFileName() throws IOException
    {
        FileManager fileManager = new FileManager();
        fileManager.editFileName(TEST_FILE_NAME, TEST_CHAT_ID2, TEST_NEW_FILE_NAME);
        assertFalse(Files.exists(getFilePath(TEST_NEW_FILE_NAME)));
        assertFalse(Files.exists(getFilePath(TEST_FILE_NAME)));
    }

    /**
     * Тестирует проверку существования файла.
     */
    @Test
    public void testExistsFile()
    {
        FileManager fileManager = new FileManager();
        assertFalse(fileManager.existsFile(TEST_FILE_NAME, TEST_CHAT_ID2));
    }

    /**
     * Тестирует валидацию имени файла с допустимым именем.
     */
    @Test
    public void testIsValidFileNameWithValidName()
    {
        FileManager fileManager = new FileManager();
        assertTrue(fileManager.isValidFileName("test.txt"));
    }

    /**
     * Тестирует валидацию имени файла с недопустимым именем.
     */
    @Test
    public void testIsValidFileNameWithInvalidName()
    {
        FileManager fileManager = new FileManager();
        assertFalse(fileManager.isValidFileName("test.jpg"));
    }

    /**
     * Тестирует запись в файл (что существует дописанная в файл строка).
     *
     * @throws IOException если происходит ошибка ввода-вывода
     */
    @Test(expected = NoSuchFileException.class)
    public void testWriteToFile() throws IOException
    {
        FileManager fileManager = new FileManager();
        String message = "Message to append";
        fileManager.writeToFile(TEST_FILE_NAME, TEST_CHAT_ID2, message);
        assertTrue(Files.readString(getFilePath(TEST_FILE_NAME)).contains(message));
    }

    /**
     * Вспомогательный метод для получения пути файла на основе его имени.
     *
     * @param fileName имя файла
     * @return путь к файлу
     */
    private Path getFilePath(String fileName)
    {
        return Paths.get(ConstantManager.USER_DATA_DIRECTORY + FileManagerTest.TEST_CHAT_ID2, fileName);
    }

}
