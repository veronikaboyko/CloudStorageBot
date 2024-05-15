package org.example.bot.user;

import java.io.File;

/**
 * Сущность, возникающая, когда пользователь присылает файл
 */
public class FileMessage implements UserMessage<File>
{
    /**
     * Файл, который передает пользователь
     */
    private final File file;

    public FileMessage(File file)
    {
        this.file = file;
    }

    @Override
    public File getContent()
    {
        return file;
    }
}
