package org.example.command.stateful;

import java.util.HashMap;
import java.util.Map;

/**
 * Кэширует имена файлов
 */
public class FilesDataCasher implements Casher<String>
{
    private final Map<String, String> usersFileNamesKeeper = new HashMap<>();

    @Override
    public void add(String chatID, String data)
    {
        usersFileNamesKeeper.put(chatID, data);
    }

    @Override
    public String getData(String chatID)
    {
        return usersFileNamesKeeper.get(chatID);
    }

    @Override
    public void clearUserCash(String chatID)
    {
        usersFileNamesKeeper.remove(chatID);
    }
}
