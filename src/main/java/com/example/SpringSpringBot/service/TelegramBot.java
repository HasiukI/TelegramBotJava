package com.example.SpringSpringBot.service;

import com.example.SpringSpringBot.config.AppConfig;
import com.example.SpringSpringBot.model.Account;
import com.example.SpringSpringBot.model.User;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;

@Component
public class TelegramBot extends TelegramLongPollingBot {

    private final MonobankApiService monobankApiService;
    private final CatApiService catApiService;
    private final AppConfig config;
    private final UserService userService;

    public TelegramBot(MonobankApiService monobankApiService, CatApiService catApiService,UserService userService, AppConfig config){

        this.monobankApiService = monobankApiService;
        this.catApiService = catApiService;
        this.config=config;
        this.userService=userService;
    }

    @SneakyThrows
    @Override
    public void onUpdateReceived(Update update) {

        if(update.hasMessage() && update.getMessage().hasText()){

            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();

            if(messageText.contains("monobank")){
                monobankResponce(messageText,chatId,update.getMessage().getChat().getFirstName());
            }

//            switch (messageText){
//                case "start":
//                    try {
//                        startCommandReceived(chatId,update.getMessage().getChat().getFirstName());
//                    } catch (TelegramApiException e) {
//                        throw new RuntimeException(e);
//                    }
//                    break;
//                case "cat":
//                    try {
//                        sendCatImage(chatId);
//                    } catch (TelegramApiException e) {
//                        throw new RuntimeException(e);
//                    }
//                    break;
//                case "monobank":
//
//
//                    var res = monobankApiService.checkMyProfil();
//                    try {
//                        sendMessage(chatId,res);
//                    } catch (TelegramApiException e) {
//                        throw new RuntimeException(e);
//                    }
//
//                    break;
//            }
        }
    }

    private void monobankResponce(String messageText, long chatId, String name) throws TelegramApiException{
        if(messageText.contains("monobankStart")){
            String[] spl = messageText.split(" ");

            List<String> accaounts = monobankApiService.getAccounts(spl[1]);

            if(accaounts == null){
                String errorMessage = "Не вірний токен... Спробуйте ще раз";
                sendMessage(chatId,errorMessage);
            }
            else
            {
                User user = new User(
                        name,
                        chatId,
                        spl[1]);

                User savedUser = userService.saveUser(user);

                accaounts.forEach(acc->{
                    userService.saveAccaunts(new Account(chatId,acc));
                });
                String okMessage = "Успішно, вітаю Вас";
                sendMessage(chatId,okMessage);
            }
        }
        else
        {

            User user = userService.getUserByChaId(chatId);

            if(user == null){
                String messageLogin="Для того щоб скористатись можливостями monobank,\n" +
                        "Перейдіть за посиланням\n https://api.monobank.ua/index.html \n" +
                        "Відскануйте і надішліть повідомлення:\n 'monobankStart Ваш токен'";

                sendMessage(chatId,messageLogin);
            }
            else
            {
                switch (messageText){
                    case "monobank balance":
                        var res = monobankApiService.checkMyBalance(user.getToken());
                        sendMessage(chatId,res);
                        break;
                }
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
