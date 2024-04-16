package org.example.internal;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 * Класс, который отвечает за операции с файлами пользователя.
 */
public class FileManager
{
    private final String USER_DATA_DIRECTORY = ConstantManager.USER_DATA_DIRECTORY;

    /**
     * Метод для создания директории пользователя, в которой будут храниться его личные файлы.
     *
     * @param chatId ID пользователя.
     * @throws IOException Исключение, если возникла ошибка при создании директории.
     */
    private void createUserDir(String chatId) throws IOException
    {
        Path directoryPath = Paths.get(USER_DATA_DIRECTORY, "user_" + chatId);
        if (!Files.exists(directoryPath))
        {
            Files.createDirectories(directoryPath);
        }
    }

    /**
     * Метод для создания файла с именем, указанным пользователем.
     *
     * @param fileName Имя файла.
     * @param chatId   ID пользователя.
     * @throws IOException Исключение, если возникла ошибка при создании файла.
     */
    public void createFile(String fileName, String chatId) throws IOException
    {
        createUserDir(chatId);
        Path filePath = Paths.get(USER_DATA_DIRECTORY, "user_" + chatId, fileName);
        if (!fileName.endsWith(".txt") && !fileName.endsWith(".json") && !fileName.endsWith(".xml"))
        {
            throw new IllegalArgumentException("Неверное расширение файла. Допустимые расширения: txt, json, xml.");
        }
        if (Files.exists(filePath))
        {
            throw new IllegalArgumentException("Файл с таким именем уже существует.");
        }
        Files.createFile(filePath);
    }

    /**
     * Метод для удаления файла с именем, указанным пользователем.
     *
     * @param fileName Имя файла.
     * @param chatId   ID пользователя.
     * @throws IOException Исключение, если возникла ошибка при удалении файла.
     */
    public void deleteFile(String fileName, String chatId) throws IOException
    {
        Path filePath = Paths.get(USER_DATA_DIRECTORY, "user_" + chatId, fileName);
        if (!Files.exists(filePath))
        {
            throw new IllegalArgumentException("Файл с таким именем не существует.");
        }
        Files.delete(filePath);
    }

    /**
     * Переписываем содержимое файла
     */
    public void editFile(String fileName, String chatId, String newText) throws IOException
    {
        Files.write(Paths.get(ConstantManager.USER_DATA_DIRECTORY + "user_" + chatId, fileName), newText.getBytes());
    }

    /**
     * Поменять имя файла
     */
    public void editFileName(String oldName, String chatId, String newName) throws IOException
    {
        new File(String.valueOf(Paths.get(getFileNameByID(chatId), oldName)))
                .renameTo(new File(String.valueOf(Paths.get(getFileNameByID(chatId), newName))));
    }

    private String getFileNameByID(String chatId)
    {
        return USER_DATA_DIRECTORY + "user_" + chatId;
    }

    /**
     * Проверяет существование файла
     */
    public boolean existsFile(String fileName, String chatId)
    {
        Path filePath = Paths.get(getFileNameByID(chatId), fileName);
        return Files.exists(filePath);
    }

    /**
     * Проверяет, что валидно название файла
     */
    public boolean isValidFileName(String fileName)
    {
        return fileName.endsWith(".txt") || fileName.endsWith(".json") || fileName.endsWith(".xml");
    }

    /**
     * Пишет/Дописывает информацию в файл.
     */
    public void writeToFile(String fileToWrite, String chatId, String messageFromUser) throws IOException
    {
        Files.write(Paths.get(getFileNameByID(chatId), fileToWrite), messageFromUser.getBytes(), StandardOpenOption.APPEND);
    }

}

