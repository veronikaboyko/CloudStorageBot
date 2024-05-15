package org.example.internal;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
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
    private String WORKING_DIRECTORY;

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Before
    public void setUp() throws IOException
    {
        File currentDirectory = temporaryFolder.newFolder("forTesting");
        WORKING_DIRECTORY = currentDirectory.getAbsolutePath() + "/";
        fileManager = new FileManager(WORKING_DIRECTORY);
    }

    /**
     * Тестирует создание нового файла.
     *
     * @throws IOException если происходит ошибка ввода-вывода
     */
    @Test
    public void testCheckCorrectFileSaved() throws IOException
    {

        fileManager.checkCorrectFileSaved("testCreateFile1.txt", TEST_CHAT_ID);
        Path filePath = Paths.get(WORKING_DIRECTORY + "user_" + TEST_CHAT_ID, "testCreateFile1.txt");
        assertTrue(Files.exists(filePath));
        fileManager.deleteFile("testCreateFile1.txt", TEST_CHAT_ID);
    }

    /**
     * Тестирует создание файла с недопустимым расширением.
     *
     * @throws IOException если происходит ошибка ввода-вывода
     */
    @Test
    public void testCheckCorrectFileSavedWithInvalidExtension()
    {
        IOException exception = assertThrows(IOException.class, () -> fileManager.checkCorrectFileSaved("testFile.jpg", TEST_CHAT_ID));
        assertEquals("Неверное расширение файла. Допустимые расширения: txt, json, xml.",exception.getMessage());
    }

    /**
     * Тестирует создание файла, который уже существует.
     *
     * @throws IOException если происходит ошибка ввода-вывода
     */
    @Test
    public void testCheckCorrectFileSavedThatAlreadyExists() throws IOException
    {
        fileManager.checkCorrectFileSaved("testFile.txt", TEST_CHAT_ID);
        IOException exception = assertThrows(IOException.class, () -> fileManager.checkCorrectFileSaved("testFile.txt", TEST_CHAT_ID));
        assertEquals("Файл с таким именем уже существует.", exception.getMessage());
        fileManager.deleteFile("testFile.txt", TEST_CHAT_ID);

    }

    /**
     * Тестирует удаление файла (проверка на существование после удаления).
     *
     * @throws IOException если происходит ошибка ввода-вывода
     */
    @Test
    public void testDeleteFile() throws IOException
    {
        fileManager.checkCorrectFileSaved("testDeleteFile2.txt", TEST_CHAT_ID);
        Path filePath = Paths.get(WORKING_DIRECTORY + "user_" + TEST_CHAT_ID,
                "testDeleteFile2.txt");
        assertTrue(Files.exists(filePath));
        fileManager.deleteFile("testDeleteFile2.txt", TEST_CHAT_ID);
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
        fileManager.checkCorrectFileSaved("testDelFile.txt", TEST_CHAT_ID);
        fileManager.deleteFile("testDelFile.txt", TEST_CHAT_ID);
        IOException exception = assertThrows(IOException.class, () -> fileManager.deleteFile("testDelFile.txt", TEST_CHAT_ID));
        assertEquals("Файла с таким названием не существует.", exception.getMessage());
    }

    /**
     * Тестирует редактирование содержимого файла (проверка, что добавленная в файл строка действительно дописалась).
     */
    @Test
    public void testEditFile() throws IOException
    {

        fileManager.checkCorrectFileSaved(TEST_FILE_NAME, TEST_CHAT_ID2);
        String newText = "New text content";
        fileManager.editFile(TEST_FILE_NAME, TEST_CHAT_ID2, newText);
        assertEquals("New text content", Files.readString(getFilePath(TEST_FILE_NAME)));
        fileManager.deleteFile(TEST_FILE_NAME, TEST_CHAT_ID2);
    }

    /**
     * Тестирует изменение имени файла.
     *
     * @throws IOException если происходит ошибка ввода-вывода
     */
    @Test
    public void testEditFileName() throws IOException
    {
        fileManager.checkCorrectFileSaved(TEST_FILE_NAME, TEST_CHAT_ID2);
        fileManager.editFileName(TEST_FILE_NAME, TEST_CHAT_ID2, TEST_NEW_FILE_NAME);
        assertTrue(Files.exists(getFilePath(TEST_NEW_FILE_NAME)));
        assertFalse(Files.exists(getFilePath(TEST_FILE_NAME)));
        fileManager.deleteFile(TEST_NEW_FILE_NAME, TEST_CHAT_ID2);
    }

    /**
     * Тестирует проверку существования файла.
     */
    @Test
    public void testExistsFile()
    {
        assertFalse(fileManager.existsFile(TEST_FILE_NAME, TEST_CHAT_ID2));
    }

    /**
     * Тестирует валидацию имени файла с допустимым именем.
     */
    @Test
    public void testIsValidFileNameWithValidName()
    {
        assertTrue(fileManager.isValidFileName("test.txt"));
        assertTrue(fileManager.isValidFileName("txt.json"));
        assertTrue(fileManager.isValidFileName("xml.xml"));
    }

    /**
     * Тестирует валидацию имени файла с недопустимым именем.
     */
    @Test
    public void testIsValidFileNameWithInvalidName()
    {
        assertFalse(fileManager.isValidFileName("test.jpg"));
        assertFalse(fileManager.isValidFileName("test.pdf"));
        assertFalse(fileManager.isValidFileName("test.txtt"));
        assertFalse(fileManager.isValidFileName("test.jsonj"));
        assertFalse(fileManager.isValidFileName("test.xmll"));
    }

    /**
     * Тестирует запись в файл (что существует дописанная в файл строка).
     */
    @Test
    public void testWriteToFile()
    {
        String message = "Message to append";
        IOException exception = assertThrows(IOException.class, () -> fileManager.writeToFile(TEST_FILE_NAME, TEST_CHAT_ID2, message));
        assertEquals("Файла с таким названием не существует.", exception.getMessage());
    }

    /**
     * Вспомогательный метод для получения пути файла на основе его имени.
     *
     * @param fileName имя файла
     * @return путь к файлу
     */
    private Path getFilePath(String fileName)
    {
        return Paths.get(WORKING_DIRECTORY + "user_" + FileManagerTest.TEST_CHAT_ID2, fileName);
    }


    /**
     * Тестирует получение списка всех файлов
     */
    @Test
    public void testListFiles() throws IOException
    {
        fileManager.checkCorrectFileSaved(TEST_FILE_NAME, TEST_CHAT_ID2);
        fileManager.checkCorrectFileSaved(TEST_NEW_FILE_NAME, TEST_CHAT_ID2);
        assertTrue("test.txt\nnew_test.txt\n".equals(fileManager.getListFiles(TEST_CHAT_ID2, null, false))
        || "new_test.txt\ntest.txt\n".equals(fileManager.getListFiles(TEST_CHAT_ID2, null, false)));
    }

    /**
     * Тестирует получение списка всех файлов с найденной подстрокой в названии
     */
    @Test
    public void testListFilesSearchName() throws IOException
    {
        fileManager.checkCorrectFileSaved(TEST_FILE_NAME, TEST_CHAT_ID2);
        fileManager.checkCorrectFileSaved(TEST_NEW_FILE_NAME, TEST_CHAT_ID2);
        assertTrue("test.txt\nnew_test.txt\n".equals(fileManager.getListFiles(TEST_CHAT_ID2, "txt", false))
        || "new_test.txt\ntest.txt\n".equals(fileManager.getListFiles(TEST_CHAT_ID2, "txt", false)));
        assertEquals("new_test.txt\n", fileManager.getListFiles(TEST_CHAT_ID2, "new", false));
    }

    /**
     * Тестирует получение списка всех файлов с найденной подстрокой в содержании
     */
    @Test
    public void testListFilesSearchContent() throws IOException
    {
        fileManager.checkCorrectFileSaved(TEST_FILE_NAME, TEST_CHAT_ID2);
        fileManager.checkCorrectFileSaved(TEST_NEW_FILE_NAME, TEST_CHAT_ID2);
        fileManager.writeToFile(TEST_FILE_NAME, TEST_CHAT_ID2, "первый текст");
        fileManager.writeToFile(TEST_NEW_FILE_NAME, TEST_CHAT_ID2, "второй текст");
        assertEquals("test.txt\n", fileManager.getListFiles(TEST_CHAT_ID2, "первый", true));
        assertEquals("new_test.txt\n", fileManager.getListFiles(TEST_CHAT_ID2, "второй", true));
        assertTrue("test.txt\nnew_test.txt\n".equals(fileManager.getListFiles(TEST_CHAT_ID2, "текст", true)) ||
                "new_test.txt\ntest.txt\n".equals(fileManager.getListFiles(TEST_CHAT_ID2, "текст", true)));
    }

    /**
     * Тестируем, что метод createOrCheckIfCreatedFile() корректно работает
     */
    @Test
    public void testCorrectWorkCreateOrCheckIfCreatedFile() throws IOException
    {
        fileManager.createOrCheckIfCreatedFile(new File("test.txt"),TEST_CHAT_ID);
        fileManager.createOrCheckIfCreatedFile(new File("test1.txt"),TEST_CHAT_ID);
        fileManager.createOrCheckIfCreatedFile(new File("test1.txt"),TEST_CHAT_ID);
        fileManager.createOrCheckIfCreatedFile(new File("test.txt"),TEST_CHAT_ID);
        assertTrue("test.txt\ntest1.txt\n".equals(fileManager.getListFiles(TEST_CHAT_ID,null,false)) ||
                "test1.txt\ntest.txt\n".equals(fileManager.getListFiles(TEST_CHAT_ID, null, false)));
    }

}
