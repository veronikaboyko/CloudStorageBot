package org.example.bot;

import org.example.bot.user.FileMessage;
import org.example.bot.user.StringMessage;
import org.example.bot.user.UserMessage;
import org.example.internal.ConstantManager;
import org.example.internal.FileManager;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Document;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.io.File;


/**
 * Telegram bot.
 */
public class TelegramBot extends TelegramLongPollingBot
{
    private final String botUsername;
    private final MessageHandler messageHandler;


    public TelegramBot(String botUsername, String botToken)
    {
        super(botToken);
        this.botUsername = botUsername;
        this.messageHandler = new MessageHandler(new FileManager());
    }


    @Override
    public String getBotUsername()
    {
        return botUsername;
    }


    @Override
    public void onUpdateReceived(Update update)
    {
        String chatId = update.getMessage().getChatId().toString();
        UserMessage<?> userMessage;
        if (update.getMessage().hasDocument())
        {
            String docId = update.getMessage().getDocument().getFileId();
            String docName = update.getMessage().getDocument().getFileName();
            String docMine = update.getMessage().getDocument().getMimeType();
            long docSize = update.getMessage().getDocument().getFileSize();
            Document document = new Document();
            document.setMimeType(docMine);
            document.setFileName(docName);
            document.setFileSize(docSize);
            document.setFileId(docId);
            GetFile getFile = new GetFile();
            getFile.setFileId(document.getFileId());
            try
            {
                org.telegram.telegrambots.meta.api.objects.File file = execute(getFile);

                final File systemFile = new File(ConstantManager.USER_DATA_DIRECTORY + "user_" + chatId + "/" + docName);
                downloadFile(file, systemFile);
                userMessage = new FileMessage(systemFile);
            }
            catch (TelegramApiException e)
            {
                e.printStackTrace();
                return;
            }
        } else
        {
            String messageFromUser = update.getMessage().getText();
            userMessage = new StringMessage(messageFromUser);
        }
        PartialBotApiMethod<?> messageToSend = messageHandler.handleUserMessage(userMessage, chatId);
        send(messageToSend);
    }

    /**
     * Метод для запуска Telegram бота.
     */
    public void start()
    {
        try
        {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(this);
            System.out.println("Telegram bot запущен.");
        }
        catch (TelegramApiException e)
        {
            throw new RuntimeException("Не удалось запустить телеграм бота", e);
        }
    }


    /**
     * Метод для отправки сообщений в чат.
     *
     * @param message Сообщение (текст, документ и т.д.), которое нужно отправить.
     */
    private void send(PartialBotApiMethod<?> message)
    {
        try
        {
            if (message instanceof SendMessage)
                execute((SendMessage) message);
            else
                execute((SendDocument) message);
        }
        catch (TelegramApiException e)
        {
            System.out.println("Не удалось отправить сообщение. " + e.getMessage());
        }
    }
}
