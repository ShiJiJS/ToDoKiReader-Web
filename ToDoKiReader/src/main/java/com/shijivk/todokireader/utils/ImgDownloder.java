package com.shijivk.todokireader.utils;


import com.shijivk.todokireader.config.MQCode;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;

public class ImgDownloder implements Runnable{
    private String imageUrl;
    private File image;
    private Map<File,Integer> messageQueue;

    public void setParam(String url,File image,Map<File,Integer> messageQueue){
        this.imageUrl = url;
        this.image = image;
        this.messageQueue = messageQueue;
    }

    @Override
    public void run() {
        try {
            System.out.println("imageURL       " + imageUrl);
            System.out.println("imagePath     " + image.getPath());


            URL url = new URL(imageUrl);
            URLConnection conn = url.openConnection();
            conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
            conn.setRequestProperty("referer", "https://www.maofly.com/");
            conn.connect();
            //FileUtils.copyURLToFile(url,image,20000,20000);
            FileUtils.copyInputStreamToFile(conn.getInputStream(),image);
            messageQueue.put(image,MQCode.IMG_GET_OK);


        } catch (IOException e) {
            messageQueue.put(image, MQCode.IMG_GET_OVERTIME);
            e.printStackTrace();
        }
    }
}
