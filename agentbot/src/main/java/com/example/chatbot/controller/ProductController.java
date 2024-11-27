package com.example.chatbot.controller;

import com.example.chatbot.model.Card;
import com.example.chatbot.model.Fund;
import com.example.chatbot.model.Product;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class ProductController {

    @GetMapping("/simple-financial-products")
    public String showSimpleFinancialProducts(Model model) {
        List<String> products = List.of(
                "우리 WON 적금: 월 1만원부터 자유롭게 저축할 수 있는 적금 상품으로, 높은 금리를 제공합니다.",
                "우리 Super 주택청약종합저축: 주택 청약을 준비하는 고객을 위한 상품으로, 정부 지원과 다양한 혜택이 포함됩니다.",
                "우리 e-정기예금: 인터넷 뱅킹을 통해 간편하게 가입할 수 있는 정기 예금 상품입니다.",
                "우리 스마트 정기적금: 스마트폰 앱으로 간편하게 관리할 수 있는 정기적금 상품으로, 편리함과 높은 금리를 제공합니다."
        );
        model.addAttribute("products", products);
        return "financial-products";
    }

    @GetMapping("/product-list")
    public String showDetailedFinancialProducts(Model model) {
        List<Product> products = readCSVFile("products.csv");
        model.addAttribute("products", products);
        return "financial-products";
    }

    @GetMapping("/product-list/card")
    public String showCards(Model model) {
        List<Card> cards = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new InputStreamReader(
                new ClassPathResource("cards.csv").getInputStream(), StandardCharsets.UTF_8))) {
            cards = br.lines().skip(1).map(line -> {
                String[] fields = line.split(",", -1); // 쉼표로 분리
                return new Card(
                        fields.length > 0 ? fields[0] : "N/A", // 카드명
                        fields.length > 1 ? fields[1] : "N/A", // 카드 종류
                        fields.length > 2 ? fields[2] : "N/A", // 연회비
                        fields.length > 3 ? fields[3] : "N/A"  // 상세 설명
                );
            }).collect(Collectors.toList());
        } catch (Exception e) {
            e.printStackTrace();
        }

        model.addAttribute("products", cards);
        return "cards";
    }





    @GetMapping("/product-list/fund")
    public String showFunds(Model model) {
        List<Fund> funds = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new InputStreamReader(
                new ClassPathResource("funds.csv").getInputStream(), StandardCharsets.UTF_8))) {
            funds = br.lines().skip(1).map(line -> {
                String[] fields = line.split(",", -1); // 쉼표로 분리
                return new Fund(
                        fields[0], // 상품명
                        fields[1], // 상품 유형
                        fields[2], // 위험도
                        fields[3], // 1개월 수익률
                        fields[4], // 3개월 수익률
                        fields[5], // 6개월 수익률
                        fields[6], // 12개월 수익률
                        fields[7], // 총 보수비용
                        fields[8], // 운용사
                        fields[9], // 상품 요약
                        fields[10] // 클러스터
                );
            }).collect(Collectors.toList());
        } catch (Exception e) {
            e.printStackTrace();
        }

        model.addAttribute("products", funds);
        return "funds";
    }





    private List<Product> readCSVFile(String fileName) {
        List<Product> products = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(
                new ClassPathResource(fileName).getInputStream(), StandardCharsets.UTF_8))) {
            products = br.lines()
                    .skip(1) // 헤더 스킵
                    .map(line -> {
                        String[] fields = line.split(",", -1); // 빈 필드 포함
                        return new Product(
                                fields.length > 0 ? fields[0].replace("\"", "") : "N/A", // title
                                fields.length > 1 ? fields[1].replace("\"", "") : "N/A", // type
                                fields.length > 2 ? fields[2].replace("\"", "") : "N/A", // annualFee
                                fields.length > 3 ? fields[3].replace("\"", "") : "N/A", // details
                                fields.length > 4 ? fields[4].replace("\"", "") : "N/A"  // extra (if exists)
                        );
                    })
                    .collect(Collectors.toList());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return products;
    }
}
