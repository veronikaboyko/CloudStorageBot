package org.example.internal;

import org.junit.Before;
import org.junit.Test;

import javax.validation.constraints.AssertTrue;
import java.io.IOException;

import static org.junit.Assert.*;

public class DirectoryManagerTest
{
    private static final String chatId = "JurexProductionID";
    private FileManager fileManager;
    private DirectoryManager directoryManager;
    @Before
    public void setUp()
    {
        fileManager = new FileManager();
        directoryManager = new DirectoryManager();
    }
    @Test
    public void listFiles() throws IOException
    {
        try
        {
            fileManager.createFile("1.txt",chatId);
            fileManager.createFile("2.json",chatId);
            fileManager.createFile("3.xml",chatId);
            String result = directoryManager.listFiles(chatId);
            assertTrue(result.contains("1.txt"));
            assertTrue(result.contains("2.json"));
            assertTrue(result.contains("3.xml"));
            assertEquals(4, result.split("\n").length);
        }
        catch (IllegalArgumentException exception)
        {
            assertSame("Файл с таким именем уже существует.", exception.getMessage());
        }

    }
}