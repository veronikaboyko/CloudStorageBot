package org.example.internal;

import java.util.List;

/**
 * Класс для проверки количества аргументов, проверки аргументов на соотвествие командам.
 */
public class ArgumentChecker
{
    public final String fileNameParameter = "В качестве параметра укажите название файла.";
    private final List<String> commands = List.of(
            "/help", "/create", "/delete",
            "/writeToFile",
            "/listFiles",
            "/viewFileContent",
            "/editFile",
            "/editFileName"
    );

    /**
     * Метод для проверки количества параметров, введенных пользователем.
     *
     * @param wordCount Ожидаемое количество параметров.
     * @param message   Сообщение, полученное от пользователя.
     * @return True - если количество параметров соответствует ожидаемому, False - иначе.
     */
    public Boolean checkArguments(int wordCount, String message)
    {
        String[] words = message.split("\\s+");
        return words.length == wordCount;
    }

    /**
     * Проверяет, является ли сообщение от пользователя командой
     *
     * @param userMessage - пользовательская строка
     * @return
     */
    public boolean isCommand(String userMessage)
    {
        final String potentialCommand = userMessage.split(" ")[0];
        return commands.contains(potentialCommand);
    }
}
