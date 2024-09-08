package com.example.SpringSpringBot.service;

import com.example.SpringSpringBot.config.AppConfig;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
public class TelegramBot extends TelegramLongPollingBot {

    private final MonobankApiService monobankApiService;
    private final CatApiService catApiService;
    private final AppConfig config;

    public TelegramBot(MonobankApiService monobankApiService, CatApiService catApiService, AppConfig config){

        this.monobankApiService = monobankApiService;
        this.catApiService = catApiService;
        this.config=config;
    }

    @Override
    public void onUpdateReceived(Update update) {

        if(update.hasMessage() &&
                update.getMessage().hasText()){
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();

            switch (messageText){
                case "start":
                    try {
                        startCommandReceived(chatId,update.getMessage().getChat().getFirstName());
                    } catch (TelegramApiException e) {
                        throw new RuntimeException(e);
                    }
                    break;
                case "cat":
                    try {
                        sendCatImage(chatId);
                    } catch (TelegramApiException e) {
                        throw new RuntimeException(e);
                    }
                    break;
                case "mono":
                    var res = monobankApiService.checkMyProfil();
                    try {
                        sendMessage(chatId,res);
                    } catch (TelegramApiException e) {
                        throw new RuntimeException(e);
                    }

                    break;
            }
        }
    }
    private void sendCatImage(long chatId) throws TelegramApiException {
        String imageUrl = catApiService.getRandomCatImage();
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(imageUrl);
        execute(message);
    }
    private void startCommandReceived(long chatId, String firstName) throws TelegramApiException {
        String answer = "Zdorov, "+ firstName;

        sendMessage(chatId, answer);
    }

    private void sendMessage(long chatId, String message) throws TelegramApiException  {
        SendMessage m = new SendMessage();
        m.setChatId(chatId);
        m.setText(message);

        try {
            execute(m);
        }catch (Exception ex){

        }
    }

    @Override
    public String getBotUsername() {
        return config.getBotName();
    }
    @Override
    public String getBotToken() {
     return  config.getToken();
    }

}
