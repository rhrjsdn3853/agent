package com.example.chatbot.controller;

import com.example.chatbot.model.News;
import com.example.chatbot.model.NewsScraper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class NewsController {

    @GetMapping("/news")
    public String showAboutPage(Model model) {
        NewsScraper scraper = new NewsScraper();
        List<News> newsList = scraper.scrapeNews();
        model.addAttribute("newsList", newsList);
        return "about";
    }

}
