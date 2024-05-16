package org.example.bot.user;

/**
 * Сущность, возникающая, когда пользователь присылает строку
 */
public class StringMessage implements UserMessage<String>
{
    /**
     * Строковый контент от пользователя
     */
    private final String userContent;

    public StringMessage(String userContent)
    {
        this.userContent = userContent;
    }

    @Override
    public String getContent()
    {
        return this.userContent;
    }
}
