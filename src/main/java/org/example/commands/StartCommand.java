package org.example.commands;

import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;


/**
 * Команда /start.
 */
public class StartCommand implements Command
{

    private final String START_MESSAGE = "Привет! Это бот, который поможет Вам хранить," +
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

    @Override
    public BotApiMethod handle(String messageFromUser, String chatId)
    {
        return new SendMessage(chatId, START_MESSAGE);
    }
}
