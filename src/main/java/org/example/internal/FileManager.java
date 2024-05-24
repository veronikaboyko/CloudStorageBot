package org.example.internal;


import javassist.NotFoundException;
import org.apache.commons.lang3.StringUtils;
import org.example.bot.TelegramBot;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.objects.Document;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.io.*;


/**
 * Класс, который отвечает за операции с файлами пользователя.
 */
public class FileManager
{
    private String directoryToWork = ConstantManager.USER_DATA_DIRECTORY; //по умолчанию

    public FileManager(String directoryToWork)
    {
        this.directoryToWork = directoryToWork;
    }

    public FileManager()
    {
    }

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
        return ConstantManager.ALLOWED_EXTENSIONS.contains(fileExtension);
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
     * Возвращает все файлы пользователя из его директории
     *
     * @param chatId Идентификатор пользователя
     * @return массив, содержащий все пользовательские файлы
     * @throws NotFoundException если никаких файлов нет
     */
    private File[] getAllUserFiles(final String chatId) throws NotFoundException
    {
        File currentUserDirectory = new File(getFileNameByID(chatId));
        File[] files;
        if (currentUserDirectory.isDirectory())
        {
            files = currentUserDirectory.listFiles();
            if (files == null)
            {
                throw new NotFoundException(ConstantManager.NO_USER_FILES_FOUND);
            }
            return files;
        } else
        {
            throw new NotFoundException(ConstantManager.NO_USER_FILES_FOUND);
        }
    }


    /**
     * Возвращает список всех файлов пользователя
     *
     * @param chatId Идентификатор пользователя
     * @return Список всех файлов пользователя в виде строки
     * @throws NotFoundException если никаких файлов нет
     */
    public String getListFiles(final String chatId) throws NotFoundException
    {
        List<String> listFiles = new ArrayList<>();
        File[] files = getAllUserFiles(chatId);
        for (File file : files)
        {
            if (file.isFile())
                listFiles.add(file.getName() + "\n");
        }
        Collections.sort(listFiles);
        return StringUtils.join(listFiles, "");
    }


    /**
     * Возвращает список всех файлов пользователя, найденный по искомой строке
     *
     * @param chatId          Идентификатор пользователя
     * @param searchString    Искомая строка
     * @param searchInContent флаг, который нужно выставить, чтобы искать в содержимом файла, иначе поиск будет по названию
     * @return Список всех файлов пользователя в виде строки
     * @throws NotFoundException если нет таких файлов
     * @throws IOException       если ошибка во время чтения файла
     */
    public String findFilesBySearchString(final String chatId, String searchString, boolean searchInContent) throws NotFoundException, IOException
    {
        List<String> listFiles = new ArrayList<>();
        final boolean searchInName = !searchInContent;
        File[] files = getAllUserFiles(chatId);
        for (File file : files)
        {
            if (file.isFile() && (searchInContent && fileContainsString(file, searchString) || searchInName && file.getName().contains(searchString)))
                listFiles.add(file.getName() + "\n");
        }
        if (listFiles.isEmpty())
        {
            throw new NotFoundException("По запросу “%s” не найдено файлов.".formatted(searchString));
        }
        Collections.sort(listFiles);
        return StringUtils.join(listFiles, "");
    }


    /**
     * Проверяет, содержит ли файл искомую строку
     *
     * @param file         Файл, содержимое которого проверяется
     * @param searchString Искомая строка
     * @return true, если содержит, иначе false
     * @throws IOException если ошибка во время чтения файла
     */
    private boolean fileContainsString(File file, String searchString) throws IOException
    {
        try (BufferedReader reader = new BufferedReader(new FileReader(file)))
        {
            return reader.lines().anyMatch(line -> line.contains(searchString));
        }
    }


    /**
     * Получить файл
     *
     * @param fileName Имя файла, который нужно получить
     * @param chatId   Идентификатор пользователя
     * @return Нужный файл
     * @throws NotFoundException если нет такого файла
     */
    public File getFile(String fileName, String chatId) throws NotFoundException
    {
        Path filePath = getPathToFile(chatId, fileName);
        if (!Files.exists(filePath))
        {
            throw new NotFoundException(ConstantManager.NO_SUCH_FILE_EXISTS);
        }
        return new File(getFileNameByID(chatId), fileName);
    }

    /**
     * Создать файл, присланный пользователем из телеграма
     */
    public void createFile(Document userDocument, TelegramBot telegramBot, String chatId) throws Exception
    {
        Document document = new Document();
        final String fileName = userDocument.getFileName();
        document.setFileName(fileName);
        document.setFileSize(userDocument.getFileSize());
        document.setFileId(userDocument.getFileId());
        GetFile getFile = new GetFile();
        getFile.setFileId(document.getFileId());
        createUserDir(chatId);
        org.telegram.telegrambots.meta.api.objects.File file = telegramBot.execute(getFile);
        telegramBot.downloadFile(file, new File(String.valueOf(getPathToFile(chatId, fileName))));
    }
}

