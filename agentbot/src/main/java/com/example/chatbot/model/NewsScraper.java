package com.example.chatbot.model;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class NewsScraper {

    public List<News> scrapeNews() {
        List<News> newsList = new ArrayList<>();
        try {
            // User-Agent 설정
            Document doc = Jsoup.connect("https://news.google.com/rss/search?q=경제&hl=ko&gl=KR&ceid=KR:ko\n")
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.3")
                    .get();

            Elements newsHeadlines = doc.select("item"); // RSS의 item 태그 선택

            for (Element headline : newsHeadlines) {
                String title = headline.select("title").text();
                String link = headline.select("link").text();

                newsList.add(new News(title, "", link));

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return newsList;
    }
}
