package com.example.android.worldlynews;

/**
 * Created by masus on 3/15/2018.
 */

public class Story {
    private String title;
    private String section;
    private String webUrl;
    private String author; //optional
    private String publishDate; //optional

    public Story(String title, String section, String webUrl) {
        this.title = title;
        this.section = section;
        this.webUrl = webUrl;
    }

    public Story(String title, String section, String webUrl, String author, String publishDate) {
        this.title = title;
        this.section = section;
        this.webUrl = webUrl;
        this.author = author;
        this.publishDate = publishDate;
    }

    public String getTitle() {
        return title;
    }

    public String getSection() {
        return section;
    }

    public String getWebUrl() {
        return webUrl;
    }

    public String getAuthor() {
        return author;
    }

    public String getPublishDate() {
        return publishDate;
    }
}
