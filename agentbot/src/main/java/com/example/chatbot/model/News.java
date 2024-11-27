package com.example.chatbot.model;

public class News {
    private String title;
    private String description;
    private String link;

    // 생성자
    public News(String title, String description, String link) {
        this.title = title;
        this.description = description;
        this.link = link;
    }

    // getter 메서드
    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getLink() {
        return link;
    }

    // setter 메서드 (필요에 따라 추가)
    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setLink(String link) {
        this.link = link;
    }

    // toString 메서드 (디버깅에 유용)
    @Override
    public String toString() {
        return "News{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", link='" + link + '\'' +
                '}';
    }
}
