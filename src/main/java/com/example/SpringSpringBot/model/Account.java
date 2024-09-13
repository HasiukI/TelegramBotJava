package com.example.SpringSpringBot.model;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
@Table(name = "accounts")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private String account;

    @Column(nullable = false)
    private Long lastAction;

    public Account(Long userId, String account,Long lastAction){
        this.userId=userId;
        this.account=account;
        this.lastAction=lastAction;
    }
    public Account(){}
}
