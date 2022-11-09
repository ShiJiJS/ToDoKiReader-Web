package com.shijivk.todokireader.controller;

import com.alibaba.fastjson2.JSONObject;
import com.shijivk.todokireader.config.MQCode;
import com.shijivk.todokireader.config.SourceProps;
import com.shijivk.todokireader.pojo.CacheInfo;
import com.shijivk.todokireader.pojo.Result;
import com.shijivk.todokireader.pojo.Source;
import com.shijivk.todokireader.source.MangaSource;
import com.shijivk.todokireader.utils.PathUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@RestController
public class SearchController{

    @Autowired
    //源的信息
    private SourceProps sourceProps;

    @Value("${cachePath}")
    private String cachePath;


    //controller和source之间的消息队列，用来确认图片是否下载完成
    private static final Map<String, CacheInfo> messageQueue = new ConcurrentHashMap<>();
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
        String sourceName = params.getString("sourceName");

        //存储title,chapter和Integer的映射关系
        int currentTitleNumber = titleNumber.getAndIncrement();
        int currentChapterNumber = chapterNumber.getAndIncrement();
        titleMap.put(currentTitleNumber,title);
        chapterMap.put(currentChapterNumber,chapter);

        Class<?> sourceClass = this.sourceProps.getSourceClass(sourceName);
        if(sourceClass == null)return Result.fail("获取类对象失败");

        try {
            MangaSource source = (MangaSource) sourceClass.newInstance();
            int amount = source.getAmountAndStartDownload(messageQueue, url, this.cachePath, currentTitleNumber, currentChapterNumber);
            JSONObject result = new JSONObject();
            result.put("amount",amount);
            result.put("titleNumber",currentTitleNumber);
            result.put("chapterNumber",currentChapterNumber);
            return Result.success(result);
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return Result.fail("获取错误，请检查参数是否正确");
    }

    @GetMapping("/api/checkImgStatus/{titleNum}/{chapterNum}/{imgNum}")
    //前端查询缓存中是否已经缓存好了该图片，或者在未知数量时，是否存在该图片。
    public Result checkImgStatus(@PathVariable("titleNum")Integer titleNum,
                                 @PathVariable("chapterNum") Integer chapterNum,
                                 @PathVariable("imgNum") Integer imgNum){
        long startWaitingTime = System.currentTimeMillis();
        CacheInfo cacheInfo = messageQueue.get(PathUtil.getPathKey(titleNum, chapterNum, imgNum));
        while(cacheInfo==null){
            try {
                //等待十秒
                if (System.currentTimeMillis() - startWaitingTime > 10000){
                    //虽然请求失败了，但是为了将参数写在data里面，用success返回
                    JSONObject overTimeResult = new JSONObject();
                    overTimeResult.put("code",MQCode.IMG_GET_OVERTIME);
                    overTimeResult.put("fileExtension","");//超时的情况扩展名留空
                    return Result.success(overTimeResult);
                }
                TimeUnit.MILLISECONDS.sleep(100);
                cacheInfo = messageQueue.get(PathUtil.getPathKey(titleNum, chapterNum, imgNum));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        //如果缓存map中，值为成功则返回ok，否则返回章节结束
        if(cacheInfo.getStatusCode().equals(MQCode.IMG_GET_OK)){
            //返回成功的code和文件扩展名
            JSONObject successResult = new JSONObject();
            successResult.put("code",MQCode.IMG_GET_OK);
            successResult.put("fileExtension",cacheInfo.getFileExtension());
            return Result.success(successResult);

        }else if (cacheInfo.getStatusCode().equals(MQCode.CHAPTER_OVER)){
            //返回章节结束的code，扩展名留空
            JSONObject successResult = new JSONObject();
            successResult.put("code",MQCode.CHAPTER_OVER);
            successResult.put("fileExtension","");
            return Result.success(successResult);
        }
        return null;
    }



    //暂时用不到的方法
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
