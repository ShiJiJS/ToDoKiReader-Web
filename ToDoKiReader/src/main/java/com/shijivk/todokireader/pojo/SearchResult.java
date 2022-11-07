package com.shijivk.todokireader.pojo;

public class SearchResult {
    private String imgUrl;
    private String title;
    private String author;
    private String href;
    private String sourceName;


    public SearchResult(String imgUrl, String title, String author, String href, String sourceName) {
        this.imgUrl = imgUrl;
        this.title = title;
        this.author = author;
        this.href = href;
        this.sourceName = sourceName;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public String getSourceName() {
        return sourceName;
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }
}
