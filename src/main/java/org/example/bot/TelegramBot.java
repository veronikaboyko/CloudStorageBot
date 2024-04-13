package org.example.bot;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;


public class TelegramBot extends TelegramLongPollingBot {
    private final String botUsername;
    private final String botToken;


    public TelegramBot(String botUsername, String botToken) {
        this.botUsername = botUsername;
        this.botToken = botToken;
    }


    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public void onUpdateReceived(Update update) {
        String messageFromUser = update.getMessage().getText();
        String chatId = update.getMessage().getChatId().toString();
        BotApiMethod messageToSend = new MessageHandler().handleUserMessage(messageFromUser, chatId);
        send(messageToSend);
    }

    /**
     * Метод для запуска Telegram бота.
     */
    public void start() {
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(this);
        } catch (TelegramApiException e) {
            throw new RuntimeException("Не удалось запустить телеграм бота", e);
        }
    }


    /**
     * Метод для отправки сообщений в чат.
     *
     * @param message Сообщение (текст, документ и т.д.), которое нужно отправить.
     */
    private void send(BotApiMethod message) {
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
