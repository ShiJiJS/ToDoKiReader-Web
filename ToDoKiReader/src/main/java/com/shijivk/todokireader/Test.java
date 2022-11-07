package com.shijivk.todokireader;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

public class Test {
    public static void main(String[] args) throws IOException {



        URL url=new URL("https://mao.mhtupian.com/uploads/img/34291/466738/01.jpg");
        URLConnection urlConnection = url.openConnection();
        HttpURLConnection conn = (HttpURLConnection) urlConnection;
        conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/107.0.0.0 Safari/537.36");
        conn.setRequestProperty("referer", "https://www.maofly.com/");

        conn.connect();

        FileUtils.copyInputStreamToFile(conn.getInputStream(), new File("D:\\temp\\1.jpg"));
    }

//    public static void main(String[] args) {
//        String url =
//                "https://mao.mhtupian.com/uploads/img/46132/663011/" + URLEncoder.encode("BZNQDZXSV01-001A.jpg");
//        String path="d:/temp/1.jpg";
//        downloadPicture(url,path);
//    }


    private static void downloadPicture(String urlList,String path) {

        try {
            URL url = new URL(urlList);
            DataInputStream dataInputStream = new DataInputStream(url.openStream());
            FileOutputStream fileOutputStream = new FileOutputStream(new File(path));
            ByteArrayOutputStream output = new ByteArrayOutputStream();

            byte[] buffer = new byte[1024];
            int length;

            while ((length = dataInputStream.read(buffer)) > 0) {
                output.write(buffer, 0, length);
            }
            fileOutputStream.write(output.toByteArray());
            dataInputStream.close();
            fileOutputStream.close();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }






}
