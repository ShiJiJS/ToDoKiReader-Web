package com.shijivk.todokireader.pojo;

import java.util.Map;

public class Menu {
    private String coverUrl;//封面Url
    private String title;//标题
    private String author;//作者
    private String synopsis;//简介
    private String status;//发行状态
    private String lastUpdateTime;//上次更新时间
    private Map<String,String> chapters;//章节,对应链接

    public Menu(String coverUrl, String title, String author, String synopsis, String status, String lastUpdateTime, Map<String, String> chapters) {
        this.coverUrl = coverUrl;
        this.title = title;
        this.author = author;
        this.synopsis = synopsis;
        this.status = status;
        this.lastUpdateTime = lastUpdateTime;
        this.chapters = chapters;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
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

    public String getSynopsis() {
        return synopsis;
    }

    public void setSynopsis(String synopsis) {
        this.synopsis = synopsis;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(String lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    public Map<String, String> getChapters() {
        return chapters;
    }

    public void setChapters(Map<String, String> chapters) {
        this.chapters = chapters;
    }

    @Override
    public String toString() {
        return "Menu{" +
                "coverUrl='" + coverUrl + '\'' +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", synopsis='" + synopsis + '\'' +
                ", status='" + status + '\'' +
                ", lastUpdateTime='" + lastUpdateTime + '\'' +
                ", chapters=" + chapters +
                '}';
    }
}
