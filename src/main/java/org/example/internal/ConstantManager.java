package org.example.internal;

/**
 * Хранит в себе константы проекта
 */
public class ConstantManager
{
    /**
     * Справка по боту
     */
    public static final String HELP_MESSAGE = "Привет! Это бот, который поможет Вам хранить," +
            " создавать и просматривать файлы с расширениями .txt, .json, .xml.\n\n" +
            "Доступны следующие команды:\n" +
            "/help - Справка\n" +
            "/create [Filename.расширение] – Создать новый файл\n" +
            "/delete [File.расширение] – Удалить файл\n" +
            "/writeToFile [File.расширение] – Записать(дописать) в файл\n" +
            "/listFiles – Посмотреть список файлов из хранилища\n" +
            "/viewFileContent [File.расширение] - Просмотреть содержимое файла\n" +
            "/editFile [File.расширение] - Редактировать содержимое файла\n" +
            "/editFileName [File.расширение] - Переименовать файл\n" +
            "/getFile [FileName.расширение] - Получить файл\n" +
            "/findFileName [name] - Просмотреть список файлов, в названии которых встречается [name]\n" +
            "/findFile [content] - Просмотреть список файлов, в содержании которых есть [content]\n" +
            "/sendFile - Добавить в хранилище пользовательский файл\n";
    /**
     * Директория, где лежат директории по каждому пользователю
     */
    public static final String USER_DATA_DIRECTORY = System.getProperty("user.home") + "/CloudBot/";
    /**
     * Для исключений, когда файла с таким названием не существует
     */
    public static final String NOT_UNDERSTAND = "Не понимаю вас! Вызовите /help для получения справки по боту.";
    public static final String NO_SUCH_FILE_EXISTS = "Файла с таким названием не существует.";
    /**
     * Для исключений, когда некорректно название файла
     */
    public static final String FILE_ALREADY_EXISTS = "Файл с таким именем уже существует.";
    public static final String ALLOWED_EXTENSIONS_MISTAKE = "Неверное расширение файла. Допустимые расширения: txt, json, xml.";
    public static final String INCORRECT_FILE_NAME = "Некорректное название файла.";
    /**
     * Для SendMessage, сообщение о том что пользователю нужно ввести новое содержимое
     */
    public static final String INPUT_NEW_FILE_CONTENT = "Введите новое содержимое файла.";
    /**
     * Когда не найдены файлы пользователя
     */
    public static final String NO_USER_FILES_FOUND = "У вас пока что нет файлов.";
    /**
     * Когда в параметр не передано имя файла
     */
    public static final String NO_FILE_NAME_FOUND = "В качестве параметра укажите название файла.";
    /**
     * Внутренняя ошибка бота - когда передается состояние которого не существует
     */
    public static final String BOT_BROKEN_INSIDE_MESSAGE = "Внутрення ошибка телеграм-бота." +
            " Если это сообщение возникло, напишите нам на почту: cloud_bot@yandex.ru";
    public static final String FILE_SIZE_OVERFLOW = "Размер файла превышает 1МБ.";

    public static final String NO_SEARCH_STRING = "В качестве параметра введите искомую строку.";
    /**
     * Один мегабайт
     */
    public static final int ONE_MB = 1048576;

    public static final String SEND_FILE = "Отправьте файл с расширением .txt, .json или .xml";
    public static final String NOT_SUPPORT_FILE_FORMAT = "В этой команде не поддерживается формат отправки файлов.";

}
