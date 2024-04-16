package org.example.internal;

import java.io.File;

/**
 * Класс, отвечающий за работу с директорией пользователя
 */
public class DirectoryManager
{
    /**
     * Возвращает список файлов пользователя
     *
     * @param chatId Идентификатор пользователя
     * @return Список всех файлов пользователя в виде строки
     */
    public String listFiles(final String chatId)
    {
        final StringBuilder userFileList = new StringBuilder("Список ваших файлов:\n");
        File currentUserDirectory = new File(ConstantManager.USER_DATA_DIRECTORY + "user_"+chatId);
        if (currentUserDirectory.isDirectory())
        {
            File[] files = currentUserDirectory.listFiles();
            if (files == null)
            {
                return ConstantManager.NO_USER_FILES_FOUND;
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
            return ConstantManager.NO_USER_FILES_FOUND;
        }
    }
}
