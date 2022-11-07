package com.shijivk.todokireader.controller;

import com.alibaba.fastjson2.JSONObject;
import com.shijivk.todokireader.config.SourceProps;
import com.shijivk.todokireader.pojo.Result;
import com.shijivk.todokireader.pojo.Source;
import com.shijivk.todokireader.source.MangaSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@RestController
public class SearchController{

    @Autowired
    //源的信息
    private SourceProps sourceProps;

//    @Value("${cachePath}")
//    private String cachePath;


    //controller和source之间的消息队列，用来确认图片是否下载完成
    private static final Map<File,Integer> messageQueue = new ConcurrentHashMap<>();
    //存储title和int之间的映射关系
    private static final Map<Integer,String> titleMap = new ConcurrentHashMap<>();
    //存储chapter和int之间的映射关系
    private static final Map<Integer,String> chapterMap = new ConcurrentHashMap<>();
    //存储缓存中title的序号
    private static final AtomicInteger titleNumber = new AtomicInteger(100);
    //存储缓存中chapter的序号
    private static final AtomicInteger chapterNumber = new AtomicInteger(100);

    @GetMapping("/api/sources")
    public Result sources(){
        List<Source> sourceList = sourceProps.getSourceList();
        return Result.success(sourceList);
    }

    @GetMapping("/api/debug")
    public Result debug(){
        System.out.println(messageQueue.toString());
        System.out.println(titleMap);
        System.out.println(chapterMap.toString());
        System.out.println(titleNumber);
        System.out.println(chapterNumber);
        return null;
    }

    //search/maoFly/邻家
    //        采用Spring容器的方式配置source
    //        WebApplicationContext ctx = RequestContextUtils.findWebApplicationContext(request);
    //        if (ctx != null) {
    //            MangaSource source = (MangaSource) ctx.getBean(sourceName);
    //            return Result.success(source.search(keyWord));
    //        }else{
    //            return Result.fail("获取错误，请检查参数是否正确");
    //        }
    @GetMapping("/api/search/{sourceName}/{keyWord}")
    public Result onlineSearch(@PathVariable("sourceName") String sourceName, @PathVariable("keyWord") String keyWord){
        //采用反射的方式来创建源对象
        Class<?> sourceClass = this.sourceProps.getSourceClass(sourceName);
        if(sourceClass == null)return Result.fail("获取类对象失败");
        try {
            MangaSource source = (MangaSource) sourceClass.newInstance();
            return Result.success(source.search(keyWord));
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return Result.fail("获取错误，请检查参数是否正确");
    }

    @PostMapping("/api/menu")
    public Result onlineMenu(@RequestBody JSONObject params){
        //采用反射的方式来创建源对象
        Class<?> sourceClass = this.sourceProps.getSourceClass(params.getString("sourceName"));
        if(sourceClass == null)return Result.fail("获取类对象失败");
        try {
            MangaSource source = (MangaSource) sourceClass.newInstance();
            return Result.success(source.getMenu(params.getString("url")));
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return Result.fail("获取错误，请检查参数是否正确");

    }

    @PostMapping("/api/amountOfImages")
    public Result amountOfImages(@RequestBody JSONObject params){
        //获取标题、章节名称和url
        String title = params.getString("title");
        String chapter = params.getString("chapter");
        String url = params.getString("url");

        //存储title,chapter和Integer的映射关系
        int currentTitleNumber = titleNumber.getAndIncrement();
        int currentChapterNumber = chapterNumber.getAndIncrement();
        titleMap.put(currentTitleNumber,title);
        chapterMap.put(currentChapterNumber,chapter);

        Class<?> sourceClass = this.sourceProps.getSourceClass(params.getString("sourceName"));
        if(sourceClass == null)return Result.fail("获取类对象失败");
        try {
            MangaSource source = (MangaSource) sourceClass.newInstance();
            return Result.success(source.getAmountAndStartDownload(messageQueue,url,"D:\\temp",currentTitleNumber,currentChapterNumber));
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return Result.fail("获取错误，请检查参数是否正确");
    }

    @PostMapping("/api/images")
    public Result images(@RequestBody JSONObject params){
        //采用反射的方式来创建源对象
        Class<?> sourceClass = this.sourceProps.getSourceClass(params.getString("sourceName"));
        if(sourceClass == null)return Result.fail("获取类对象失败");
        try {
            MangaSource source = (MangaSource) sourceClass.newInstance();
            return Result.success(source.getImages(params.getString("url")));
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return Result.fail("获取错误，请检查参数是否正确");
    }

}
