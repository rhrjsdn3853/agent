package com.example.chatbot.service;

import com.example.chatbot.dto.TargetRateRequest;
import com.example.chatbot.model.UserForeignCurrency;
import com.example.chatbot.model.UserProfile;
import com.example.chatbot.repository.UserForeignCurrencyRepository;
import com.example.chatbot.repository.UserProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ForeignCurrencyService {

    @Autowired
    private UserForeignCurrencyRepository userForeignCurrencyRepository;

    @Autowired
    private UserProfileRepository userProfileRepository;

    // 외화 보유 정보 조회
    public List<UserForeignCurrency> getUserForeignHoldings(String userId) {
        return userForeignCurrencyRepository.findByUserId(userId);
    }

    // 구매/판매 처리 로직
    public String processTransaction(TargetRateRequest request) {
        UserProfile userProfile = userProfileRepository.findById(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("사용자 정보를 찾을 수 없습니다."));

        UserForeignCurrency foreignCurrency = userForeignCurrencyRepository
                .findByUserIdAndCurrency(request.getUserId(), request.getCurrency())
                .orElse(new UserForeignCurrency(request.getUserId(), request.getCurrency(), 0.0, 0.0, 0.0));

        double transactionAmountKrw = request.getAmount() * request.getTargetRate();

        if ("buy".equalsIgnoreCase(request.getAction())) {
            // 구매 처리
            if (userProfile.getDepositHoldings() < transactionAmountKrw) {
                throw new IllegalArgumentException("예금 보유액이 부족합니다.");
            }
            userProfile.setDepositHoldings(userProfile.getDepositHoldings() - transactionAmountKrw);
            foreignCurrency.setBalance(foreignCurrency.getBalance() + request.getAmount());
        } else if ("sell".equalsIgnoreCase(request.getAction())) {
            // 판매 처리
            if (foreignCurrency.getBalance() < request.getAmount()) {
                throw new IllegalArgumentException("외화 보유액이 부족합니다.");
            }
            foreignCurrency.setBalance(foreignCurrency.getBalance() - request.getAmount());
            userProfile.setDepositHoldings(userProfile.getDepositHoldings() + transactionAmountKrw);
        } else {
            throw new IllegalArgumentException("유효하지 않은 거래 유형입니다.");
        }

        userProfileRepository.save(userProfile);
        userForeignCurrencyRepository.save(foreignCurrency);

        return String.format("거래 완료: %s %s %,.2f, 잔액 업데이트 완료.", request.getAction(), request.getCurrency(), request.getAmount());
    }
}
