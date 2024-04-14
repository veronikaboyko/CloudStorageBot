package org.example.internal;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Класс, который отвечает за создание файлов пользователя в его личной директории.
 */
public class FileManager {
    private final String USER_DATA_DIRECTORY = "src/main/java/org/example/usersData/";

    /**
     * Метод для создания директории пользователя, в которой будут храниться его личные файлы.
     *
     * @param chatId ID пользователя.
     * @throws IOException Исключение, если возникла ошибка при создании директории.
     */
    private void createUserDir(String chatId) throws IOException {
        Path directoryPath = Paths.get(USER_DATA_DIRECTORY, "user_" + chatId);
        if (!Files.exists(directoryPath)) {
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
    public void createFile(String fileName, String chatId) throws IOException {
        createUserDir(chatId);
        Path filePath = Paths.get(USER_DATA_DIRECTORY, "user_" + chatId, fileName);
        if (!fileName.endsWith(".txt") && !fileName.endsWith(".json") && !fileName.endsWith(".xml")) {
            throw new IllegalArgumentException("Неверное расширение файла. Допустимые расширения: txt, json, xml.");
        }
        if (Files.exists(filePath)) {
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
    public void deleteFile(String fileName, String chatId) throws IOException {
        Path filePath = Paths.get(USER_DATA_DIRECTORY, "user_" + chatId, fileName);
        if (!Files.exists(filePath)) {
            throw new IllegalArgumentException("Файл с таким именем не существует.");
        }
        Files.delete(filePath);
    }

}

