package org.example.internal;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.*;
import java.util.Scanner;
import java.util.Set;

/**
 * Класс, который отвечает за операции с файлами пользователя.
 */
public class FileManager
{
    private final Set<String> ALLOWED_EXTENSIONS = Set.of(".txt", ".json", ".xml");
    private String directoryToWork = ConstantManager.USER_DATA_DIRECTORY; //по умолчанию
    public FileManager(String directoryToWork)
    {
        this.directoryToWork = directoryToWork;
    }
    public FileManager(){}

    /**
     * Метод для создания директории пользователя, в которой будут храниться его личные файлы.
     *
     * @param chatId ID пользователя.
     * @throws IOException Исключение, если возникла ошибка при создании директории.
     */
    private void createUserDir(String chatId) throws IOException
    {
        Path directoryPath = getPathToUserDir(chatId);
        if (!Files.exists(directoryPath))
        {
            Files.createDirectories(directoryPath);
        }
    }

    /**
     * Получить путь к директории пользователя
     */
    private Path getPathToUserDir(String chatId)
    {
        return Paths.get(directoryToWork, "user_" + chatId);
    }

    /**
     * Получить путь к файлу
     */
    private Path getPathToFile(String chatId, String fileName)
    {
        return Paths.get(directoryToWork, "user_" + chatId, fileName);
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
        Path filePath = getPathToFile(chatId, fileName);

        if (!isValidFileName(fileName))
        {
            throw new IOException("Неверное расширение файла. Допустимые расширения: txt, json, xml.");
        }
        if (Files.exists(filePath))
        {
            throw new IOException("Файл с таким именем уже существует.");
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
        Path filePath = getPathToFile(chatId, fileName);
        if (!Files.exists(filePath))
        {
            throw new IOException(ConstantManager.NO_SUCH_FILE_EXISTS);
        }
        Files.delete(filePath);
    }

    /**
     * Переписываем содержимое файла
     */
    public void editFile(String fileName, String chatId, String newText) throws IOException
    {
        try
        {
            Files.write(getPathToFile(chatId, fileName), newText.getBytes());
        }
        catch (NoSuchFileException exception)
        {
            throw new NoSuchFileException(ConstantManager.NO_SUCH_FILE_EXISTS);
        }
    }

    /**
     * Поменять имя файла
     */
    public boolean editFileName(String oldName, String chatId, String newName) throws IOException
    {
        String currUserDir = getFileNameByID(chatId);
        if (new File(currUserDir, oldName).renameTo(new File(currUserDir, newName)))
            return true;
        throw new IOException("Ошибка в переименовывании файла %s".formatted(oldName));
    }

    private String getFileNameByID(String chatId)
    {
        return directoryToWork + "user_" + chatId;
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
     * Проверяет, что валидно название файла (что у файла допустимое расширение)
     */
    public boolean isValidFileName(String fileName)
    {
        String fileExtension = getFileExtension(fileName);
        return ALLOWED_EXTENSIONS.contains(fileExtension);
    }

    /**
     * Пишет/Дописывает информацию в файл.
     */
    public void writeToFile(String fileToWrite, String chatId, String messageFromUser) throws IOException
    {
        try
        {
            Files.write(Paths.get(getFileNameByID(chatId), fileToWrite), messageFromUser.getBytes(), StandardOpenOption.APPEND);
        }
        catch (NoSuchFileException ioException)
        {
            throw new NoSuchFileException(ConstantManager.NO_SUCH_FILE_EXISTS);
        }
    }

    /**
     * Получить сожержимое файла
     */
    public String getFileContent(String fileName, String chatId) throws FileNotFoundException
    {
        StringBuilder content = new StringBuilder();
        try (Scanner scanner = new Scanner(new File(String.valueOf(Paths.get(getFileNameByID(chatId), fileName)))))
        {
            while (scanner.hasNextLine())
            {
                content.append(scanner.nextLine()).append("\n");
            }
        }
        return content.toString();
    }

    /**
     * Получить расширение файла
     *
     * @param fileName Имя файла
     * @return Расширение
     */
    private String getFileExtension(String fileName)
    {
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex == -1 || dotIndex == fileName.length() - 1)
        {
            return "";
        }
        return fileName.substring(dotIndex);
    }

    /**
     * Возвращает список файлов пользователя
     *
     * @param chatId Идентификатор пользователя
     * @return Список всех файлов пользователя в виде строки
     */
    public String getListFiles(final String chatId) throws IOException
    {
        final StringBuilder userFileList = new StringBuilder();
        File currentUserDirectory = new File(getFileNameByID(chatId));
        if (currentUserDirectory.isDirectory())
        {
            File[] files = currentUserDirectory.listFiles();
            if (files == null)
            {
                throw new IOException(ConstantManager.NO_USER_FILES_FOUND);
            }
            for (File file : files)
            {
                if (file.isFile())
                {
                    userFileList.append(file.getName()).append("\n");
                }
            }
            return userFileList.toString();
        } else
        {
            throw new IOException(ConstantManager.NO_USER_FILES_FOUND);
        }
    }
}

