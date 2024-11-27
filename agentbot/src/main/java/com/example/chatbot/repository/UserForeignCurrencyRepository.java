package com.example.chatbot.repository;

import com.example.chatbot.model.UserForeignCurrency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserForeignCurrencyRepository extends JpaRepository<UserForeignCurrency, Long> {
    List<UserForeignCurrency> findByUserId(String userId);

    Optional<UserForeignCurrency> findByUserIdAndCurrency(String userId, String currency);
}
