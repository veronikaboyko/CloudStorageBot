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
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;
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
            final Document userDocument = update.getMessage().getDocument();
            if (!(userDocument.getFileSize() / 1048576 <= 1))
                send(new SendMessage(chatId, ConstantManager.FILE_SIZE_OVERFLOW));
            final File file = handleFileReceived(userDocument, chatId);
            userMessage = new FileMessage(file);
        } else
        {
            String messageFromUser = update.getMessage().getText();
            userMessage = new StringMessage(messageFromUser);
        }
        PartialBotApiMethod<?> messageToSend = messageHandler.handleUserMessage(userMessage, chatId);
        send(messageToSend);
    }

    /**
     * Обработать, если прислали файл
     */
    private File handleFileReceived(Document userDocument, String chatId)
    {
        File systemFile = null;
        Document document = new Document();
        document.setFileName(userDocument.getFileName());
        document.setFileSize(userDocument.getFileSize());
        document.setFileId(userDocument.getFileId());
        GetFile getFile = new GetFile();
        getFile.setFileId(document.getFileId());
        try
        {
            org.telegram.telegrambots.meta.api.objects.File file = execute(getFile);
            systemFile = new File(ConstantManager.USER_DATA_DIRECTORY + "user_" + chatId + "/" + userDocument.getFileName());
            downloadFile(file, systemFile);
            //Здесь приходится загружать файл куда-то в память, иначе файл не передать дальше.
            //Я конечно могу передавать org.telegram.telegrambots.meta.api.objects.File, но как потом вызвать
            //метод downloadFile?
        }
        catch (TelegramApiException e)
        {
            e.printStackTrace();
            send(new SendMessage(chatId, ConstantManager.BOT_BROKEN_INSIDE_MESSAGE));
        }
        return systemFile;
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
            if (e.getMessage().equals("Error executing org.telegram.telegrambots.meta.api.methods.send.SendMessage query: [400] Bad Request: message is too long"))
            {
                SendMessage answer = (SendMessage) message;
                answer.setText("Файл слишком большой для показа.");
                try
                {
                    execute(answer);
                }
                catch (TelegramApiException ex)
                {
                    System.out.println("Не удалось отправить сообщение. " + ex.getMessage());
                }
            }

        }
    }
}
