package com.example.chatbot.controller;

import com.example.chatbot.model.Product;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.BufferedReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class ProductController {

    // 기존의 간단한 상품 설명을 위한 메서드
    @GetMapping("/simple-financial-products")
    public String showSimpleFinancialProducts(Model model) {
        List<String> products = List.of(
                "우리 WON 적금: 월 1만원부터 자유롭게 저축할 수 있는 적금 상품으로, 높은 금리를 제공합니다.",
                "우리 Super 주택청약종합저축: 주택 청약을 준비하는 고객을 위한 상품으로, 정부 지원과 다양한 혜택이 포함됩니다.",
                "우리 e-정기예금: 인터넷 뱅킹을 통해 간편하게 가입할 수 있는 정기 예금 상품입니다.",
                "우리 스마트 정기적금: 스마트폰 앱으로 간편하게 관리할 수 있는 정기적금 상품으로, 편리함과 높은 금리를 제공합니다."
        );
        model.addAttribute("products", products);
        return "financial-products"; // financial-products.html 페이지로 이동
    }

    // CSV 파일로부터 상품 정보를 읽어오는 메서드
    @GetMapping("/product-list")
    public String showDetailedFinancialProducts(Model model) {
        List<Product> products = new ArrayList<>();

        try (BufferedReader br = Files.newBufferedReader(Paths.get("src/main/resources/products.csv"))) {
            products = br.lines().skip(1).map(line -> {
                String[] fields = line.split(",");
                return new Product(fields[0], fields[1], fields[2], fields[3], fields[4]);
            }).collect(Collectors.toList());
        } catch (Exception e) {
            e.printStackTrace();
        }

        model.addAttribute("products", products);
        return "financial-products"; // financial-products.html 페이지로 이동
    }
}
