package com.shijivk.todokireader.controller;

import com.alibaba.fastjson2.JSONObject;
import com.shijivk.todokireader.config.CacheLoader;
import com.shijivk.todokireader.config.MQCode;
import com.shijivk.todokireader.config.SourceProps;
import com.shijivk.todokireader.pojo.Result;
import com.shijivk.todokireader.pojo.Source;
import com.shijivk.todokireader.source.MangaSource;
import com.shijivk.todokireader.utils.PathUtil;
import javafx.util.Pair;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@RestController
public class SearchController{

    @Autowired
    //源的信息
    private SourceProps sourceProps;

    @Value("${cachePath}")
    private String cachePath;

    //controller和source之间的消息队列，用来传递例如下载开始，下载超时，下载结束之类的消息
    private static final Map<String, Integer> messageQueue = new ConcurrentHashMap<>();



    @GetMapping("/api/sources")
    public Result sources(){
        List<Source> sourceList = sourceProps.getSourceList();
        return Result.success(sourceList);
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
    @GetMapping("/api/search/{sourceName}/{keyWord}/{pageNumber}")
    public Result onlineSearch(@PathVariable("sourceName") String sourceName, @PathVariable("keyWord") String keyWord,@PathVariable("pageNumber") Integer pageNumber){
        //采用反射的方式来创建源对象
        Class<?> sourceClass = this.sourceProps.getSourceClass(sourceName);
        if(sourceClass == null)return Result.fail("获取类对象失败");
        try {
            MangaSource source = (MangaSource) sourceClass.newInstance();
            return Result.success(source.search(keyWord,pageNumber));
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
        System.out.println("============================" + params+"============================");

        //获取标题、章节名称和url
        String title = params.getString("title");
        String chapter = params.getString("chapter");
        String url = params.getString("url");
        String sourceName = params.getString("sourceName");

        int currentTitleNumber = CacheLoader.getTitleNum(title);
        int currentChapterNumber = CacheLoader.getChapterNum(chapter);

        //缓存操作
        //查找map中是否已经有了该标题和章节的缓存，如果有可以直接返回
        if((currentTitleNumber != -1) && (currentChapterNumber != -1)){//标题和章节都有
            //拿到数量
            //获取文件夹下的所有图片（便于拿到扩展名），及图片的数量
            Collection<File> images = FileUtils.listFiles(new File(cachePath + File.separator + title + File.separator + chapter), null, true);
            int amount = images.size();
            int i = 0;
            //随便取一个图片，拿到扩展名
            String fileExtension = null;
            for (File image : images) {
                if(i++ >= 1)break;
                fileExtension = FilenameUtils.getExtension(image.getPath());
            }
            //返回结果
            JSONObject result = new JSONObject();
            result.put("amount",amount);
            result.put("titleNumber",currentTitleNumber);
            result.put("chapterNumber",currentChapterNumber);
            result.put("fileExtension",fileExtension);
            return Result.success(result);
        }else if(currentTitleNumber != -1){
            //有标题，没有章节
            //缓存章节的对应关系
            currentChapterNumber = CacheLoader.setNewChapter(title + "\\" +chapter);
        }else{
            //标题和章节都没有
            //存储title,chapter和Integer的映射关系
            currentTitleNumber = CacheLoader.setNewTitle(title);
            currentChapterNumber = CacheLoader.setNewChapter(title + "\\" +chapter);
        }


        Class<?> sourceClass = this.sourceProps.getSourceClass(sourceName);
        if(sourceClass == null)return Result.fail("获取类对象失败");


        try {
            MangaSource source = (MangaSource) sourceClass.newInstance();
            Pair<Integer, String> pair = source.getAmountAndStartDownload(messageQueue, url, this.cachePath, title, chapter);
            Integer amount = pair.getKey();
            String fileExtension = pair.getValue();

            JSONObject result = new JSONObject();
            result.put("amount",amount);
            result.put("titleNumber",currentTitleNumber);
            result.put("chapterNumber",currentChapterNumber);
            result.put("fileExtension",fileExtension);
            return Result.success(result);
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return Result.fail("获取错误，请检查参数是否正确");

    }

    @GetMapping("/api/checkImgStatus/{titleNum}/{chapterNum}/{imgName}")
    //前端查询缓存中是否已经缓存好了该图片，或者在未知数量时，是否存在该图片。
    public Result checkImgStatus(@PathVariable("titleNum")Integer titleNum,
                                 @PathVariable("chapterNum") Integer chapterNum,
                                 @PathVariable("imgName") String imgName){
        //查看本地缓存中是否有该图片，如果有直接返回
        if(CacheLoader.hasCache(titleNum, chapterNum, imgName)){
            JSONObject result = new JSONObject();
            result.put("code",MQCode.IMG_GET_OK);
            return Result.success(result);
        }

        //本地缓存中没有，等待消息队列
        Integer status = messageQueue.get(CacheLoader.getTitle(titleNum) + File.separator + CacheLoader.getChapter(chapterNum) + File.separator + FilenameUtils.getBaseName(imgName));
        long startWaitingTime = System.currentTimeMillis();
        while(status==null){
            try {
                //等待十秒
                if (System.currentTimeMillis() - startWaitingTime > 10000){//过了十秒，超时
                    //虽然请求失败了，但是为了将参数写在data里面，用success返回
                    JSONObject overTimeResult = new JSONObject();
                    overTimeResult.put("code",MQCode.IMG_GET_OVERTIME);
                    return Result.success(overTimeResult);
                }
                TimeUnit.MILLISECONDS.sleep(100);

                status = messageQueue.get(CacheLoader.getTitle(titleNum) + File.separator + CacheLoader.getChapter(chapterNum) + File.separator + FilenameUtils.getBaseName(imgName));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        //如果缓存map中，值为成功则返回ok，否则返回章节结束
        if(status.equals(MQCode.IMG_GET_OK)){
            //返回成功的code
            JSONObject successResult = new JSONObject();
            successResult.put("code",MQCode.IMG_GET_OK);
            return Result.success(successResult);

        }else if (status.equals(MQCode.CHAPTER_OVER)){
            //返回章节结束的code，扩展名留空
            JSONObject successResult = new JSONObject();
            successResult.put("code",MQCode.CHAPTER_OVER);
            return Result.success(successResult);
        }
        return null;
    }

    @GetMapping("/temp/{titleNum}/{chapterNum}/{imageName}")
    public void getImage(@PathVariable("titleNum") Integer titleNum,
                           @PathVariable("chapterNum") Integer chapterNum,
                           @PathVariable("imageName") String imageName,
                           HttpServletResponse response){
        String title = CacheLoader.getTitle(titleNum);
        String chapter = CacheLoader.getChapter(chapterNum);

        File image = new File(cachePath + File.separator + title +
                                File.separator + chapter + File.separator + imageName);
        //字节流形式读取图片
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(image);
            ServletOutputStream outputStream = response.getOutputStream();
            IOUtils.copy(fis,outputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }



//    //暂时用不到的方法
//    @PostMapping("/api/images")
//    public Result images(@RequestBody JSONObject params){
//        //采用反射的方式来创建源对象
//        Class<?> sourceClass = this.sourceProps.getSourceClass(params.getString("sourceName"));
//        if(sourceClass == null)return Result.fail("获取类对象失败");
//        try {
//            MangaSource source = (MangaSource) sourceClass.newInstance();
//            return Result.success(source.getImages(params.getString("url")));
//        } catch (InstantiationException | IllegalAccessException e) {
//            e.printStackTrace();
//        }
//        return Result.fail("获取错误，请检查参数是否正确");
//    }

}
