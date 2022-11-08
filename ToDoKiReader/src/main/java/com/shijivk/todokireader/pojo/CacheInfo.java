package com.shijivk.todokireader.pojo;

public class CacheInfo {
    private Integer statusCode;
    private String fileExtension;

    public CacheInfo(Integer statusCode, String fileExtension) {
        this.statusCode = statusCode;
        this.fileExtension = fileExtension;
    }

    public Integer getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(Integer statusCode) {
        this.statusCode = statusCode;
    }

    public String getFileExtension() {
        return fileExtension;
    }

    public void setFileExtension(String fileExtension) {
        this.fileExtension = fileExtension;
    }
}
