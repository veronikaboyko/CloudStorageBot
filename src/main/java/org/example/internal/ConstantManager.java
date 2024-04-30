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
            "/editFileName [File.расширение] - Переименовать файл";
    /**
     * Директория, где лежат директории по каждому пользователю
     */
    public static final String USER_DATA_DIRECTORY = System.getProperty("user.home") + "/CloudBot/";
    /**
     * Когда не найдены файлы пользователя
     */
    public static final String NO_USER_FILES_FOUND = "У вас пока что нет файлов." +
            " Используйте команду /help, чтобы узнать, как работать с ботом";
    /**
     * Когда в параметр не передано имя файла
     */
    public static final String NO_FILE_NAME_FOUND = "В качестве параметра укажите название файла.";
    /**
     * Внутренняя ошибка бота - когда передается состояние которого не существует
     */
    public static final String BOT_BROKEN_INSIDE_MESSAGE = "Внутрення ошибка телеграм-бота." +
            " Если это сообщение возникло, напишите нам на почту: cloud_bot@yandex.ru";
}
