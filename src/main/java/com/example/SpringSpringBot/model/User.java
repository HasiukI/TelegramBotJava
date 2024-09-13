package com.example.SpringSpringBot.model;

import com.mysql.cj.protocol.x.StatementExecuteOk;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import org.hibernate.annotations.ColumnDefault;


@Entity
@Getter
@Data
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(name = "chatId")
    @ColumnDefault("0")
    private Long chatId;

    @Column(nullable = false)
    private String token;

    @Column(nullable = false)
    private Boolean isActiveCoach;

    public User(String name, long chatId, String token, Boolean isActiveCoach) {
        this.name=name;
        this.chatId=chatId;
        this.token= token;
        this.isActiveCoach=isActiveCoach;
    }

    public User(){}
}
