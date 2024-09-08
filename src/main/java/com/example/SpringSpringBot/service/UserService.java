package com.example.SpringSpringBot.service;

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

    public User getUser(long chatId){
        userRepository.findById(chatId);
        return new User();
    }

    public List<User> getAllUsers(){
        return  userRepository.findAll();
    }
}
