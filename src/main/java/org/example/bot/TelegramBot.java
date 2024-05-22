package org.example.bot;

import org.example.internal.ConstantManager;
import org.example.internal.FileManager;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;


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
        this.messageHandler = new MessageHandler(new FileManager(), this);
    }


    @Override
    public String getBotUsername()
    {
        return botUsername;
    }


    @Override
    public void onUpdateReceived(Update update)
    {
        final Message messageFromUser = update.getMessage();
        String chatId = messageFromUser.getChatId().toString();
        PartialBotApiMethod<?> messageToSend = messageHandler.handleUserMessage(messageFromUser, chatId);
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
            assert message instanceof SendMessage;
            SendMessage answer = (SendMessage) message;
            answer.setText(ConstantManager.BOT_BROKEN_INSIDE_MESSAGE);
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
