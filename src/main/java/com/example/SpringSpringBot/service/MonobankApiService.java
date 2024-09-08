package com.example.SpringSpringBot.service;

import com.example.SpringSpringBot.config.AppConfig;
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

import java.util.List;

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

    public String checkMyProfil(){
//        HttpHeaders headers = new HttpHeaders();
//        headers.set("X-Token","");
//
//        HttpEntity<String> entity = new HttpEntity<>(headers);
//
//        ResponseEntity<String> response = restTemplate.exchange(
//                config.getApiMono() +"personal/client-info",
//                HttpMethod.GET,
//                entity,
//                String.class
//        );
//
//        JSONObject accountsJson = new JSONObject(response.getBody());
//        JSONArray accountsArray = accountsJson.getJSONArray("accounts");
//
//        String result = "На вашому рахунку:\n";
//
//        for(int i=0;i<accountsArray.length();i++){
//            String balance = String.valueOf(accountsArray.getJSONObject(i).getInt("balance"));
//
//            if(balance.length()>=3){
//                int position = balance.length() - 2;
//                balance = balance.substring(0, position) + "," + balance.substring(position);
//            }
//
//            result += i+1+". Баланс: " + balance + " "+ accountsArray.getJSONObject(i).getString("cashbackType") + ";\n";
//        }
//
//        return result;

        List<User> users= userService.getAllUsers();
        return "";
    }

    public String GetTransaction(){
        List<User> users= userService.getAllUsers();
//        HttpHeaders headers = new HttpHeaders();
//        headers.set("X-Token","");
//
//        HttpEntity<String> entity = new HttpEntity<>(headers);
//
//        ResponseEntity<String> response = restTemplate.exchange(
//                config.getApiMono() +"personal/client-info",
//                HttpMethod.GET,
//                entity,
//                String.class
//        );
//
//        JSONObject accountsJson = new JSONObject(response.getBody());
//        JSONArray accountsArray = accountsJson.getJSONArray("accounts");



        return "";
    }
}
