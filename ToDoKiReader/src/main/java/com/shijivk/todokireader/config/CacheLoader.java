package com.shijivk.todokireader.config;

import com.google.common.collect.HashBiMap;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;


public class CacheLoader {

    private static String cachePath;


    //存储title和int之间的映射关系
    private static HashBiMap<String,Integer> titleMap;
    //存储chapter和int之间的映射关系
    private static HashBiMap<String,Integer> chapterMap;
    //存储缓存中title的序号
    private static final AtomicInteger titleNumber = new AtomicInteger(100);
    //存储缓存中chapter的序号
    private static final AtomicInteger chapterNumber = new AtomicInteger(100);


    public CacheLoader() {
        cachePath = "D:\\temp";
        titleMap = HashBiMap.create();
        chapterMap = HashBiMap.create();

        try {
            this.loadCache();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Boolean hasCache(Integer titleNum,Integer chapterNum,String imgName){
        File image = new File(cachePath + File.separator + getTitle(titleNum)
                + File.separator + getChapter(chapterNum) + File.separator + imgName);
        return image.exists();

    }

    //setter
    public static Integer setNewTitle(String title){
        Integer currentTitleNumber = titleNumber.getAndIncrement();
        titleMap.put(title,currentTitleNumber);
        return currentTitleNumber;
    }

    public static Integer setNewChapter(String titleAndChapter){
        Integer currentChapterNumber = chapterNumber.getAndIncrement();
        chapterMap.put(titleAndChapter,currentChapterNumber);
        return currentChapterNumber;
    }

    //getter
    public static Integer getTitleNum(String title){
        if(titleMap.containsKey(title)){
            return titleMap.get(title);
        }
        return -1;
    }

    public static Integer getChapterNum(String titleAndChapter){
        if(chapterMap.containsKey(titleAndChapter)){
            return chapterMap.get(titleAndChapter);
        }
        return -1;
    }

    public static String getTitle(Integer titleNum){
        if(titleMap.inverse().containsKey(titleNum)){
            return titleMap.inverse().get(titleNum);
        }
        return null;
    }

    public static String getChapter(Integer chapterNum){
        if(chapterMap.inverse().containsKey(chapterNum)){
            String s = chapterMap.inverse().get(chapterNum);
            int i = FilenameUtils.indexOfLastSeparator(s);
            return s.substring(i+1);
        }
        return null;
    }



    private void loadCache() throws IOException {
        //获取cache目录下所有的文件夹
        File cachePathFile = new File(cachePath);
        List<File> titles = new ArrayList<>();
        File[] files = cachePathFile.listFiles();
        if(files != null){
            for (File file : files) {
                if(file.isDirectory()) titles.add(file);
            }
        }

        //创建titles的映射
        for (File title : titles) {
            titleMap.put(title.getName(),titleNumber.getAndIncrement());
        }

        //遍历获取到的文件夹，分别获取下面的每一个章节名称
        for (File title : titles) {
            files = title.listFiles();
            List<File> chapters = new ArrayList<>();
            if(files != null){
                for (File file : files) {
                    if(file.isDirectory()){
                        chapters.add(file);
                    }
                }
            }
            //创建章节名称的映射关系
            for (File chapter : chapters) {
                chapterMap.put(title.getName() + "\\" + chapter.getName(),chapterNumber.getAndIncrement());
            }
        }


    }

}
