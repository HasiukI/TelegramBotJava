package com.example.SpringSpringBot.repository;

import com.example.SpringSpringBot.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository  extends JpaRepository<User,Long> {
}
