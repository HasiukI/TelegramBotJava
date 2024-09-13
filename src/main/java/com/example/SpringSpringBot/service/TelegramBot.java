package com.example.SpringSpringBot.service;

import com.example.SpringSpringBot.config.AppConfig;
import com.example.SpringSpringBot.model.Account;
import com.example.SpringSpringBot.model.User;
import jakarta.annotation.PostConstruct;
import lombok.SneakyThrows;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScope;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class TelegramBot extends TelegramLongPollingBot {

    private final MonobankApiService monobankApiService;
    private final CatApiService catApiService;
    private final AppConfig config;
    private final UserService userService;
    private final WeatherService weatherService;

    public TelegramBot(WeatherService weatherService, MonobankApiService monobankApiService, CatApiService catApiService,UserService userService, AppConfig config){
        this.weatherService=weatherService;
        this.monobankApiService = monobankApiService;
        this.catApiService = catApiService;
        this.config=config;
        this.userService=userService;
    }

    @PostConstruct
    public void init(){
        List<BotCommand> listOfCommands = new ArrayList<>();
        listOfCommands.add(new BotCommand("/help","Документація"));
        listOfCommands.add(new BotCommand("/monobalance","Переглянути баланс"));
        listOfCommands.add(new BotCommand("/monocoach","Включити або виключити мотіватора"));
        listOfCommands.add(new BotCommand("/cat","Для релаксу"));
        listOfCommands.add(new BotCommand("/weather","Дізнатись погоду"));

        try {
            this.execute(new SetMyCommands(listOfCommands, new BotCommandScopeDefault(), null));
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    @SneakyThrows
    @Override
    public void onUpdateReceived(Update update) {

        if(update.hasMessage() && update.getMessage().hasText()){

            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();

            if(messageText.equals("/help")){
                sendMessage(chatId,"/weather - погода у тернополі\n" +
                        "/weather Місто - погода у заданому місті\n" +
                        "/monobalance - дізнатись баланс\n" +
                        "/monocoach - включає або виключає нагадування за витратами\n" +
                        "Для того щоб скористатись можливостями monobank,\n" +
                        "Перейдіть за посиланням\n https://api.monobank.ua/index.html \n" +
                        "Відскануйте і надішліть повідомлення:\n '/monoset Ваш токен'");
            }

            if(messageText.contains("mono")){
                monobankResponce(messageText,chatId,update.getMessage().getChat().getFirstName());
            }

            if(messageText.equals("/cat")){
                sendCatImage(chatId);
            }

            if(messageText.contains("/weather")){
                if(messageText.equals("/weather")){
                    sendWeather(chatId, "Ternopil");
                }else{
                    String[] spl = messageText.split(" ");
                    if (spl.length > 1) {
                        String city = spl[1];
                        sendWeather(chatId, city);
                    }
                }

            }

        }
    }

    private void sendWeather(long chatId, String city) throws TelegramApiException {
        Map<String, String> weatherInfo = weatherService.getWeatherWithForecast(city);

        String description = weatherInfo.get("description");
        String temperature = weatherInfo.get("temperature");
        String imageFileName = weatherInfo.get("imageFileName"); // Локальний файл
        String advice = weatherInfo.get("advice");
        String warnings = weatherInfo.get("warnings");  // Попередження

        // Формуємо текст повідомлення з попередженням
        String messageText = String.format(
                "Погода в %s: %s, Температура: %s°C\n%s\n\n%s",
                city, description, temperature, advice, warnings
        );

        try {
            InputStream imageStream = getClass().getClassLoader().getResourceAsStream("static/weather_images/" + imageFileName);
            if (imageStream == null) {
                throw new RuntimeException("Зображення не знайдено: " + imageFileName);
            }
            InputFile inputFile = new InputFile(imageStream, imageFileName);

            SendPhoto sendPhoto = new SendPhoto();
            sendPhoto.setChatId(chatId);
            sendPhoto.setPhoto(inputFile);
            sendPhoto.setCaption(messageText);
            execute(sendPhoto);
        } catch (Exception e) {

            sendMessage(chatId, messageText);
        }
    }

    private void monobankResponce(String messageText, long chatId, String name) throws TelegramApiException{
        if(messageText.contains("/monoset")){
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
                        spl[1],
                        false);

                User savedUser = userService.saveUser(user);


                accaounts.forEach(acc->{
                    var last = monobankApiService.getTimeLastAction(user.getToken(),acc);
                    userService.saveAccaunts(new Account(savedUser.getId(),acc,last));
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
                        "Відскануйте і надішліть повідомлення:\n '/monoset Ваш токен'";

                sendMessage(chatId,messageLogin);
            }
            else
            {
                switch (messageText){
                    case "/monobalance":
                        var res = monobankApiService.checkMyBalance(user.getToken());
                        sendMessage(chatId,res);
                        break;
                    case "/monocoach":
                            String mess="";
                            if(userService.updateUserIsActiveCoach(chatId)){
                                mess="Ви включили мотіватора";
                            }else{
                                mess="Ви виключили мотіватора";
                            }
                            sendMessage(chatId,mess);
                        break;
                }
            }
        }
    }

    @Scheduled(cron = "0 * * * * ?")
    public void screenCheck() throws TelegramApiException {
       List<User> users = userService.getAllUsers();

       users.forEach(user -> {
           if(user.getIsActiveCoach()){
               List<Account> accounts = userService.GetAccounts(user.getId());
               accounts.forEach(acc->{
                   String message;
                   Long price = monobankApiService.getLastAction(user.getToken(),acc);

                   String balance = price.toString();
                   if (balance.length() >= 3) {
                       int position = balance.length() - 2;
                       balance = balance.substring(0, position) + "," + balance.substring(position);
                   }

                   if(price <0){
                       message="Єбать, нема куди гроші діти???\n" + balance +
                               " грн. Піздец. Блять. Нахуя???";
                   }else{
                       message="Та ти що красава "+balance+" грн в копілку.";
                   }

                   try {
                       sendMessage(user.getChatId(),message);
                   } catch (TelegramApiException e) {
                       throw new RuntimeException(e);
                   }
               });
           }

       });
    }

    private void sendCatImage(long chatId) throws TelegramApiException {
        String imageUrl = catApiService.getRandomCatImage();
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(imageUrl);
        execute(message);
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
