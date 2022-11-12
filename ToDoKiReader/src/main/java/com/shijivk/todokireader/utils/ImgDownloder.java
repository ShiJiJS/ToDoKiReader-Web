package com.shijivk.todokireader.utils;


import com.shijivk.todokireader.config.MQCode;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;

public class ImgDownloder implements Runnable{
    private String imageUrl;
    private File image;
    private Map<String,Integer> messageQueue;

    public void setParam(String url,File image,Map<String,Integer> messageQueue){
        this.imageUrl = url;
        this.image = image;
        this.messageQueue = messageQueue;
    }

    @Override
    public void run() {
        //获取扩展名
        String extension = FilenameUtils.getExtension(imageUrl);
        //获取格式形如/标题序号/章节序号/图片序号 的key
        String key = PathUtil.getPathKey(image);

        try {
            System.out.println("cacheImageURL       " + imageUrl);
            System.out.println("cacheImagePath     " + image.getPath());

            URL url = new URL(imageUrl);
            URLConnection conn = url.openConnection();
            conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
            conn.setRequestProperty("referer", "https://www.maofly.com/");
            conn.connect();
            //FileUtils.copyURLToFile(url,image,20000,20000);
            FileUtils.copyInputStreamToFile(conn.getInputStream(),image);

        } catch (IOException e) {
            //超时
            messageQueue.put(PathUtil.getPathKey(image), MQCode.IMG_GET_OVERTIME);
            e.printStackTrace();
        }
    }
}
