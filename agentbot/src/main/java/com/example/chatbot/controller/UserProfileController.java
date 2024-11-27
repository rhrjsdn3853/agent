package com.example.chatbot.controller;

import com.example.chatbot.model.User;
import com.example.chatbot.model.UserProfile;
import com.example.chatbot.repository.UserProfileRepository;
import com.example.chatbot.service.PythonModelService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
public class UserProfileController {

    @Autowired
    private UserProfileRepository userProfileRepository;

    @Autowired
    private PythonModelService pythonModelService;

    @GetMapping("/asset-status")
    public String showAssetStatus(Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        model.addAttribute("user", user);
        return "asset-status";
    }

    @GetMapping("/asset-data")
    @ResponseBody
    public Map<String, Double> getAssetData(HttpSession session) {
        User user = (User) session.getAttribute("user");
        Map<String, Double> assetData = new HashMap<>();

        if (user != null) {
            UserProfile userProfile = userProfileRepository.findByProfileUserId(user.getUserId());
            if (userProfile != null) {
                assetData.put("depositHoldings", userProfile.getDepositHoldings());
                assetData.put("savingsHoldings", userProfile.getSavingsHoldings());
                assetData.put("fundHoldings", userProfile.getFundHoldings());
                assetData.put("debtAmount", userProfile.getDebtAmount());
                assetData.put("totalFinancialAssets", userProfile.getTotalFinancialAssets());
            }
        }
        return assetData;
    }

    @GetMapping("/analyze-user-type")
    public String analyzeUserType(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        // PythonModelService를 통해 사용자 유형을 가져옵니다.
        String userType = pythonModelService.getUserCluster(user.getUserId());
        model.addAttribute("userType", userType);
        return "asset-status";  // asset-status.html 템플릿으로 반환
    }

    @GetMapping("/user/profile-data")
    @ResponseBody
    public UserProfile getUserProfileData(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not logged in");
        }
        return userProfileRepository.findByProfileUserId(user.getUserId());
    }


    /**
     * 나이대별 평균 자산 데이터를 반환하는 API
     */
    @GetMapping("/api/average-assets-by-age")
    @ResponseBody
    public Map<String, Object> getAverageAssetsByAge(@RequestParam Integer userAge) {
        // 나이대 계산
        int ageGroup;
        if (userAge < 30) {
            ageGroup = 20;
        } else if (userAge < 40) {
            ageGroup = 30;
        } else if (userAge < 50) {
            ageGroup = 40;
        } else if (userAge < 60) {
            ageGroup = 50;
        } else {
            ageGroup = 60;
        }

        // 나이대에 해당하는 사용자 데이터 가져오기
        List<UserProfile> profiles = userProfileRepository.findByAgeBetween(ageGroup, ageGroup + 9);

        System.out.println("Fetched profiles count: " + profiles.size());

        // 평균 계산
        double avgIncome = profiles.stream().mapToDouble(UserProfile::getMonthlyIncome).average().orElse(0.0);
        double avgDeposit = profiles.stream().mapToDouble(UserProfile::getDepositHoldings).average().orElse(0.0);
        double avgSavings = profiles.stream().mapToDouble(UserProfile::getSavingsHoldings).average().orElse(0.0);
        double avgFund = profiles.stream().mapToDouble(UserProfile::getFundHoldings).average().orElse(0.0);

        System.out.println("Average Income: " + avgIncome);
        System.out.println("Average Deposit: " + avgDeposit);
        System.out.println("Average Savings: " + avgSavings);
        System.out.println("Average Fund: " + avgFund);

        // 데이터 반환
        Map<String, Object> response = new HashMap<>();
        response.put("avgIncome", avgIncome);
        response.put("avgDeposit", avgDeposit);
        response.put("avgSavings", avgSavings);
        response.put("avgFund", avgFund);
        response.put("ageRange", ageGroup + "대");

        return response;
    }



}
