package com.example.chatbot.controller;

import com.example.chatbot.model.User;
import com.example.chatbot.model.UserProfile;
import com.example.chatbot.repository.UserProfileRepository;
import com.example.chatbot.service.PythonModelService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

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
}

