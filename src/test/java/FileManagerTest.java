import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import org.example.internal.FileManager;

public class FileManagerTest {

    private FileManager fileManager;
    private final String TEST_CHAT_ID = "123";

    @Before
    public void setUp() {
        fileManager = new FileManager();
    }

    @Test
    public void testCreateFile() throws IOException {
        fileManager.createFile("testCreateFile.txt", TEST_CHAT_ID);
        Path filePath = Paths.get("src/main/java/org/example/usersData/user_" + TEST_CHAT_ID, "testCreateFile.txt");
        assertTrue(Files.exists(filePath));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateFileWithInvalidExtension() throws IOException {
        fileManager.createFile("testFile.jpg", TEST_CHAT_ID);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateFileThatAlreadyExists() throws IOException {
        fileManager.createFile("testFile.txt", TEST_CHAT_ID);
        fileManager.createFile("testFile.txt", TEST_CHAT_ID);
    }

    @Test
    public void testDeleteFile() throws IOException {
        fileManager.createFile("testDeleteFile.txt", TEST_CHAT_ID);
        Path filePath = Paths.get("src/main/java/org/example/usersData/user_" + TEST_CHAT_ID,
                "testDeleteFile.txt");
        assertTrue(Files.exists(filePath));
        fileManager.deleteFile("testDeleteFile.txt", TEST_CHAT_ID);
        assertFalse(Files.exists(filePath));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDeleteFileThatAlreadyExists() throws IOException {
        fileManager.createFile("testFile.txt", TEST_CHAT_ID);
        fileManager.deleteFile("testFile.txt", TEST_CHAT_ID);
        fileManager.deleteFile("testFile.txt", TEST_CHAT_ID);
    }

}
