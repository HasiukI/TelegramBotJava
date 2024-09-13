package com.example.SpringSpringBot.service;

import com.example.SpringSpringBot.config.AppConfig;
import com.example.SpringSpringBot.model.Account;
import com.example.SpringSpringBot.model.User;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
public class MonobankApiService {

    private final RestTemplate restTemplate;
    private final AppConfig config;
    private final UserService userService;

    @Autowired
    public MonobankApiService(RestTemplateBuilder restTemplateBuilder, AppConfig config, UserService userService) {
        this.restTemplate = restTemplateBuilder.build();
        this.config = config;
        this.userService=userService;
    }

    public String checkMyBalance(String token){
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Token", token);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                config.getApiMono() + "personal/client-info",
                HttpMethod.GET,
                entity,
                String.class
        );

        JSONObject accountsJson = new JSONObject(response.getBody());
        JSONArray accountsArray = accountsJson.getJSONArray("accounts");

        String result = "На вашому рахунку:\n";

        for (int i = 0; i < accountsArray.length(); i++) {
            String balance = String.valueOf(accountsArray.getJSONObject(i).getInt("balance"));

            if (balance.length() >= 3) {
                int position = balance.length() - 2;
                balance = balance.substring(0, position) + "," + balance.substring(position);
            }

            result += i + 1 + ". Баланс: " + balance + " " + accountsArray.getJSONObject(i).getString("cashbackType") + ";\n";
        }

        return result;
    }




    public List<String> getAccounts(String token){
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Token",token);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                config.getApiMono() +"personal/client-info",
                HttpMethod.GET,
                entity,
                String.class
        );

        if(response.getStatusCode().value() != 200){
            return null;
        }
        JSONObject accountsJson = new JSONObject(response.getBody());
        JSONArray accountsArray = accountsJson.getJSONArray("accounts");

        List<String> accounts = new ArrayList<>();

        for(int i=0;i<accountsArray.length();i++){
            if(!accountsArray.getJSONObject(i).getString("type").equals("eAid")){
                String accountId = String.valueOf(accountsArray.getJSONObject(i).getString("id"));
                accounts.add(accountId);
            }
        }

        return accounts;
    }

    public Long getTimeLastAction(String token,String accountId){
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Token",token);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        long unixTo = Instant.now().getEpochSecond();
        Instant oneDayAgo =  Instant.now().minus(20, ChronoUnit.DAYS);
        long unixFrom = oneDayAgo.getEpochSecond();

        ResponseEntity<String> response = restTemplate.exchange(
                config.getApiMono() +"personal/statement/" + accountId + "/"+ unixFrom+ "/"+unixTo,
                HttpMethod.GET,
                entity,
                String.class
        );

        JSONArray accountsJson = new JSONArray(response.getBody());

        if(accountsJson.isEmpty()){
            return 0L;
        }
        return accountsJson.getJSONObject(0).getLong("time");
    }

    public Long getLastAction(String token, Account account){
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Token",token);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        long unixTo = Instant.now().getEpochSecond();

        ResponseEntity<String> response = restTemplate.exchange(
                config.getApiMono() +"personal/statement/" + account.getAccount() + "/"+ account.getLastAction()+ "/"+unixTo,
                HttpMethod.GET,
                entity,
                String.class
        );

        JSONArray accountsJson = new JSONArray(response.getBody());

        if(accountsJson.length()>1){
            return accountsJson.getJSONObject(0).getLong("amount");
        }
        return 0L;

    }
}
