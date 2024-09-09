package com.example.SpringSpringBot.service;

import com.example.SpringSpringBot.model.Account;
import com.example.SpringSpringBot.model.User;
import com.example.SpringSpringBot.repository.AccountRepository;
import com.example.SpringSpringBot.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AccountRepository accountRepository;

    public User getUserByChaId(long chatId){
        return userRepository.findByChatId(chatId).orElse(null);
    }

    public User saveUser(User user){
        return  userRepository.save(user);
    }

    public List<User> getAllUsers(){
        return  userRepository.findAll();
    }

    public void saveAccaunts(Account acc){
        accountRepository.save(acc);
    }
}
