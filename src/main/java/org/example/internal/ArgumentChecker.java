package org.example.internal;

/**
 * Класс для проверки количества аргументов.
 */
public class ArgumentChecker {

    /**
     * Метод для проверки количества параметров, введенных пользователем.
     *
     * @param wordCount Ожидаемое количество параметров.
     * @param message   Сообщение, полученное от пользователя.
     * @return True - если количество параметров соответствует ожидаемому, False - иначе.
     */
    public Boolean checkArguments(int wordCount, String message) {
        String[] words = message.split("\\s+");
        return words.length == wordCount;
    }
}
