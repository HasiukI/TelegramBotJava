package com.example.SpringSpringBot.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class WeatherService {

    @Value("${weather.api.key}")
    private String apiKey;
    private static final String WEATHER_API_URL = "https://api.openweathermap.org/data/2.5/weather";
    private static final String FORECAST_API_URL = "https://api.openweathermap.org/data/2.5/forecast";
    private static final Map<String, String> weatherImages = new HashMap<>();
    private static final Map<String, String> weatherAdvices = new HashMap<>();
    private static final Map<String, String> weatherWarnings = new HashMap<>();

    static {
        weatherImages.put("ясно", "clean.jpg");
        weatherImages.put("чисте небо", "clean.jpg");
        weatherImages.put("трохи хмарно", "clean.jpg");
        weatherImages.put("розсіяні хмари", "clean.jpg");
        weatherImages.put("хмарно", "hma.jpg");
        weatherImages.put("короткочасний дощ", "clean.jpg");
        weatherImages.put("дощ", "clean.jpg");
        weatherImages.put("гроза", "clean.jpg");
        weatherImages.put("сніг", "clean.jpg");
        weatherImages.put("рвані хмари", "rv.jpg");
        weatherImages.put("похмуро", "clean.jpg");
        weatherImages.put("легкий дощ", "clean.jpg");
        weatherImages.put("помірний дощ", "clean.jpg");
        weatherImages.put("сильний дощ", "clean.jpg");
        weatherImages.put("дуже сильний дощ", "clean.jpg");
        weatherImages.put("екстремальний дощ", "clean.jpg");
        weatherImages.put("легкий сніг", "clean.jpg");
        weatherImages.put("сильний сніг", "clean.jpg");
        weatherImages.put("мряка", "clean.jpg");
        weatherImages.put("серпанок", "clean.jpg");
        weatherImages.put("туман", "clean.jpg");
        weatherImages.put("пісок", "clean.jpg");
        weatherImages.put("пил", "clean.jpg");
        weatherImages.put("вулканічний попіл", "clean.jpg");
        weatherImages.put("шквали", "clean.jpg");
        weatherImages.put("торнадо", "clean.jpg");


        weatherAdvices.put("ясно", "Сьогодні сонячно! Не забудьте окуляри від сонця.");
        weatherAdvices.put("чисте небо", "Чудове небо! Не забудьте окуляри від сонця.");
        weatherAdvices.put("трохи хмарно", "Трішки хмарно, але переважно сонячно. Можна вийти на прогулянку.");
        weatherAdvices.put("розсіяні хмари", "Розкидані хмари на небі. Приємний день для прогулянки.");
        weatherAdvices.put("хмарно", "Троха хмарно. Можливе короткочасне сонце.");
        weatherAdvices.put("короткочасний дощ", "Очікується короткочасний дощ. Можливо, знадобиться парасолька.");
        weatherAdvices.put("дощ", "Іде дощ. Візьміть парасольку і не забудьте дощовик.");
        weatherAdvices.put("гроза", "Гроза на горизонті. Залишайтеся в безпечному місці.");
        weatherAdvices.put("сніг", "Сніг на вулиці. Одягніть теплі речі і взуйте зимове взуття.");
        weatherAdvices.put("рвані хмари", "Можливо, сьогодні буде як сонце, так і хмари. Візьміть із собою легку куртку, про всяк випадок!");
        weatherAdvices.put("похмуро", "Похмуро. Можливо, сонце сьогодні не вийде.");
        weatherAdvices.put("легкий дощ", "Легкий дощ. Парасолька може знадобитися.");
        weatherAdvices.put("помірний дощ", "Помірний дощ. Парасолька і дощовик знадобляться.");
        weatherAdvices.put("сильний дощ", "Сильний дощ. Будьте готові до зливи.");
        weatherAdvices.put("дуже сильний дощ", "Дуже сильний дощ. Краще залишитися вдома.");
        weatherAdvices.put("екстремальний дощ", "Екстремальний дощ. Будьте дуже обережні!");
        weatherAdvices.put("легкий сніг", "Легкий сніг. Гарний день для зимових розваг.");
        weatherAdvices.put("сильний сніг", "Сильний снігопад. Обережно на дорогах!");
        weatherAdvices.put("мряка", "Дощ зі снігом. Будьте обережні на вулиці.");
        weatherAdvices.put("серпанок", "Серпанок. Погана видимість.");
        weatherAdvices.put("туман", "Туман. Водіям слід бути обережними.");
        weatherAdvices.put("пісок", "Пісок у повітрі. Захистіть очі та органи дихання.");
        weatherAdvices.put("пил", "Пил у повітрі. Рекомендується носити маску.");
        weatherAdvices.put("вулканічний попіл", "Вулканічний попіл. Уникайте виходів на вулицю.");
        weatherAdvices.put("шквали", "Шквали. Будьте обережні.");
        weatherAdvices.put("торнадо", "Торнадо! Знайдіть найближче укриття.");

        weatherWarnings.put("дощ", "Скоро очікується дощ. Візьміть парасольку!");
        weatherWarnings.put("сніг", "Невдовзі піде сніг. Одягніть теплі речі.");
        weatherWarnings.put("гроза", "Гроза на горизонті. Уникайте виходу на вулицю.");
        weatherWarnings.put("шторм", "Очікується шторм. Будьте обережні!");
        weatherWarnings.put("туман", "На вулиці буде туман. Погана видимість!");
    }

    public Map<String, String> getWeatherWithForecast(String city) {
        try {
            String encodedCity = URLEncoder.encode(city, StandardCharsets.UTF_8.toString());

            String weatherUrl = UriComponentsBuilder.fromHttpUrl(WEATHER_API_URL)
                    .queryParam("q", encodedCity)  // Використовуємо лише закодовану назву міста
                    .queryParam("units", "metric")
                    .queryParam("appid", apiKey)
                    .queryParam("lang", "uk")
                    .toUriString();

            String forecastUrl = UriComponentsBuilder.fromHttpUrl(FORECAST_API_URL)
                    .queryParam("q", encodedCity)  // Використовуємо лише закодовану назву міста
                    .queryParam("units", "metric")
                    .queryParam("appid", apiKey)
                    .queryParam("lang", "uk")
                    .toUriString();

            RestTemplate restTemplate = new RestTemplate();

            Map<String, Object> currentWeatherResponse = restTemplate.getForObject(weatherUrl, Map.class);
            Map<String, Object> forecastResponse = restTemplate.getForObject(forecastUrl, Map.class);

            if (currentWeatherResponse != null && currentWeatherResponse.containsKey("main")) {
                Map<String, Object> main = (Map<String, Object>) currentWeatherResponse.get("main");
                int roundedTemperature = (int) Math.round((Double) main.get("temp"));

                List<Map<String, Object>> weatherList = (List<Map<String, Object>>) currentWeatherResponse.get("weather");
                String weatherDescription = weatherList.get(0).get("description").toString();

                String imageFileName = weatherImages.getOrDefault(weatherDescription, "default.jpg");
                String advice = weatherAdvices.getOrDefault(weatherDescription, "Стан погоди незвичний. Будьте обережні!");

                StringBuilder warnings = new StringBuilder();
                boolean isStable = true;  // прапор стабільності погоди

                if (forecastResponse != null && forecastResponse.containsKey("list")) {
                    List<Map<String, Object>> forecastList = (List<Map<String, Object>>) forecastResponse.get("list");

                    for (int i = 0; i < 2; i++) {  // Перевіряємо 2 періоди по 3 години
                        Map<String, Object> forecastData = forecastList.get(i);
                        List<Map<String, Object>> futureWeatherList = (List<Map<String, Object>>) forecastData.get("weather");
                        String futureWeatherDescription = futureWeatherList.get(0).get("description").toString().toLowerCase();

                        int futureTemperature = (int) Math.round((Double) ((Map<String, Object>) forecastData.get("main")).get("temp"));

                        // Додаємо попередження для кожного погодного типу, якщо є
                        String warning = getWeatherWarning(futureWeatherDescription);
                        if (!warning.isEmpty() && !warnings.toString().contains(warning)) {  // Перевірка на дублювання
                            warnings.append(warning).append("\n");
                            isStable = false;  // Погода нестабільна, якщо є попередження
                        }

                        // Перевірка значних змін температури або погоди
                        if (Math.abs(roundedTemperature - futureTemperature) > 3) {
                            isStable = false;
                        }

                        // Перевірка змін у погодних умовах (наприклад, з "ясно" на "дощ")
                        if (!futureWeatherDescription.equals(weatherDescription.toLowerCase()) && !warnings.toString().contains("Очікується зміна погодних умов")) {
                            warnings.append("Очікується зміна погодних умов: з ").append(weatherDescription).append(" на ").append(futureWeatherDescription).append(".\n");
                            isStable = false;
                        }
                    }
                }

                if (isStable) {
                    warnings.append("На найближчі години погода буде стабільною.");  // Виведення повідомлення про стабільність погоди
                }

                Map<String, String> result = new HashMap<>();
                result.put("temperature", String.valueOf(roundedTemperature));
                result.put("description", weatherDescription);
                result.put("imageFileName", imageFileName);
                result.put("advice", advice);
                result.put("warnings", warnings.toString());

                return result;

            } else if (currentWeatherResponse != null && currentWeatherResponse.containsKey("message")) {
                throw new RuntimeException("Помилка від API: " + currentWeatherResponse.get("message"));
            } else {
                throw new RuntimeException("Не вдалося отримати прогноз погоди для міста " + city + ". Перевірте правильність введеної назви.");
            }
        } catch (Exception e) {
            throw new RuntimeException("Виникла помилка при отриманні прогнозу погоди: " + e.getMessage());
        }
    }

    private String getWeatherWarning(String weatherDescription) {
        return weatherWarnings.getOrDefault(weatherDescription, "");
    }
}
