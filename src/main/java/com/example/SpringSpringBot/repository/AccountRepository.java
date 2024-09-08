package com.example.SpringSpringBot.repository;

import com.example.SpringSpringBot.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, Long> {
}
