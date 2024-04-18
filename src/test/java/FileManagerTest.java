import org.example.internal.ConstantManager;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import org.example.internal.FileManager;

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

    @Test
    public void testCreateFile() throws IOException
    {
        fileManager.createFile("testCreateFile.txt", TEST_CHAT_ID);
        Path filePath = Paths.get("src/main/java/org/example/usersData/user_" + TEST_CHAT_ID, "testCreateFile.txt");
        assertTrue(Files.exists(filePath));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateFileWithInvalidExtension() throws IOException
    {
        fileManager.createFile("testFile.jpg", TEST_CHAT_ID);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateFileThatAlreadyExists() throws IOException
    {
        fileManager.createFile("testFile.txt", TEST_CHAT_ID);
        fileManager.createFile("testFile.txt", TEST_CHAT_ID);
    }

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

    @Test(expected = IllegalArgumentException.class)
    public void testDeleteFileThatAlreadyExists() throws IOException
    {
        fileManager.createFile("testFile.txt", TEST_CHAT_ID);
        fileManager.deleteFile("testFile.txt", TEST_CHAT_ID);
        fileManager.deleteFile("testFile.txt", TEST_CHAT_ID);
    }

    @Test(expected = NoSuchFileException.class)
    public void testEditFile() throws IOException
    {
        FileManager fileManager = new FileManager();
        String newText = "New text content";
        fileManager.editFile(TEST_FILE_NAME, TEST_CHAT_ID2, newText);
        assertTrue(Files.readString(getFilePath(TEST_FILE_NAME)).contains(newText));
    }

    @Test
    public void testEditFileName() throws IOException
    {
        FileManager fileManager = new FileManager();
        fileManager.editFileName(TEST_FILE_NAME, TEST_CHAT_ID2, TEST_NEW_FILE_NAME);
        assertFalse(Files.exists(getFilePath(TEST_NEW_FILE_NAME)));
        assertFalse(Files.exists(getFilePath(TEST_FILE_NAME)));
    }

    @Test
    public void testExistsFile()
    {
        FileManager fileManager = new FileManager();
        assertFalse(fileManager.existsFile(TEST_FILE_NAME, TEST_CHAT_ID2));
    }

    @Test
    public void testIsValidFileNameWithValidName()
    {
        FileManager fileManager = new FileManager();
        assertTrue(fileManager.isValidFileName("test.txt"));
    }

    @Test
    public void testIsValidFileNameWithInvalidName()
    {
        FileManager fileManager = new FileManager();
        assertFalse(fileManager.isValidFileName("test.jpg"));
    }

    @Test(expected = NoSuchFileException.class)
    public void testWriteToFile() throws IOException
    {
        FileManager fileManager = new FileManager();
        String message = "Message to append";
        fileManager.writeToFile(TEST_FILE_NAME, TEST_CHAT_ID2, message);
        assertTrue(Files.readString(getFilePath(TEST_FILE_NAME)).contains(message));
    }

    private Path getFilePath(String fileName)
    {
        return Paths.get(ConstantManager.USER_DATA_DIRECTORY + "user_" + FileManagerTest.TEST_CHAT_ID2, fileName);
    }

}
