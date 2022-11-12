package com.shijivk.todokireader.source;

import com.shijivk.todokireader.config.MQCode;
import com.shijivk.todokireader.config.ThreadPoolConfig;
import com.shijivk.todokireader.config.WebDriverPoolConfig;
import com.shijivk.todokireader.pojo.Menu;
import com.shijivk.todokireader.pojo.SearchResult;
import com.shijivk.todokireader.utils.ImgDownloder;
import com.shijivk.todokireader.utils.PathUtil;
import javafx.util.Pair;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class CopyManga implements MangaSource{

    private static final String baseUrl = "https://copymanga.site";

    private static final String sourceName = "copyManga";


    @Override
    public List<SearchResult> search(String content, Integer pageNumber) {
        //从池中获取驱动，根据输入结果查询
        GenericObjectPool<WebDriver> pool = WebDriverPoolConfig.getPool();
        WebDriver driver = null;
        try {
            driver = pool.borrowObject();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        //进行搜索
        driver.get(baseUrl + "/search?q=" + content);

        //页码不为1时，切换到对应的页面
        if(pageNumber != 1){
            //抓取一下切换之前的第一个漫画内容。便于后面判断是否切换完了
            //先要等待它加载完
            WebElement firstManga = new WebDriverWait(driver, Duration.ofSeconds(10)).until(ExpectedConditions.elementToBeClickable(By.cssSelector(".exemptComic_Item-img:first-of-type")));
            String href = firstManga.getAttribute("href");


            //拿到页码输入框的元素
            WebElement pageNumInput = driver.findElement(By.className("page-skip")).findElement(By.cssSelector("input"));
            //等待输入框加载完毕
            new WebDriverWait(driver, Duration.ofSeconds(10)).until(ExpectedConditions.elementToBeClickable(pageNumInput));
            //传入页码
            pageNumInput.sendKeys(pageNumber.toString());
            //点击跳转按钮
            WebElement goButton = driver.findElement(By.className("go"));
            goButton.click();

            //等待加载完毕
            new WebDriverWait(driver, Duration.ofSeconds(10)).until(ExpectedConditions.not(ExpectedConditions.textToBe(By.cssSelector(".exemptComic_Item-img:first-of-type"),href)));
        }else {
            //页码为1，只要等待加载完了就行
            new WebDriverWait(driver, Duration.ofSeconds(10)).until(ExpectedConditions.elementToBeClickable(By.cssSelector(".exemptComic_Item-img:first-of-type")));
        }

        //抓取外部div
        List<WebElement> elements = driver.findElements(By.className("exemptComic_Item"));
        List<SearchResult> searchResults = new ArrayList<>();

        for (WebElement element : elements) {
            //获取imgUrl
            String imgUrl = element.findElement(By.cssSelector("img")).getAttribute("data-src");
            //获取标题
            String title = element.findElement(By.cssSelector("p")).getText();
            //获取作者
            String author = element.findElement(By.cssSelector("span")).getText();
            //获取href
            String href =element.findElement(By.cssSelector(".exemptComicItem-txt-box a")).getAttribute("href");
            //加入搜索结果列表
            searchResults.add(new SearchResult(imgUrl,title,author,href,sourceName));
        }

        //归还driver
        pool.returnObject(driver);
        return searchResults;
    }

    @Override
    public Menu getMenu(String url) {
        //从池中获取驱动，根据输入结果查询
        GenericObjectPool<WebDriver> pool = WebDriverPoolConfig.getPool();
        WebDriver driver = null;
        try {
            driver = pool.borrowObject();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        driver.get(url);
        //等待信息加载完，这里用题材字段和章节字段来判断
        new WebDriverWait(driver, Duration.ofSeconds(10)).until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".comicParticulars-title-right ul li:nth-child(6) span:last-of-type")));
        new WebDriverWait(driver, Duration.ofSeconds(10)).until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".comicParticulars-left-img img")));
        new WebDriverWait(driver, Duration.ofSeconds(10)).until(ExpectedConditions.not(ExpectedConditions.textToBe(By.cssSelector(".upLoop span"),"章節加載中，請稍等")));

        //获取coverUrl
        String coverUrl = driver.findElement(By.cssSelector(".comicParticulars-left-img img")).getAttribute("data-src");
        //获取标题title
        String title = driver.findElement(By.cssSelector(".comicParticulars-title-right ul li:nth-child(1) h6")).getText();
        //获取作者author
        String author = driver.findElement(By.cssSelector(".comicParticulars-title-right ul li:nth-child(3) a:first-of-type")).getText();
        //获取发行状态status
        String status = driver.findElement(By.cssSelector(".comicParticulars-title-right ul li:nth-child(6) span:last-of-type")).getText();
        //获取上次更新时间lastUpdateTime
        String lastUpdateTime = driver.findElement(By.cssSelector(".comicParticulars-title-right ul li:nth-child(5) span:last-of-type")).getText();
        //获取简介synopsis
        String synopsis = driver.findElement(By.cssSelector(".intro")).getText();
        //获取章节chapters
        HashMap<String, String> chapters = new LinkedHashMap<>();

        List<WebElement> elements = driver.findElements(By.cssSelector("#default全部 ul:first-of-type a"));
        for (WebElement element : elements) {
            chapters.put(element.getAttribute("title"),element.getAttribute("href"));
        }


        //归还driver
        pool.returnObject(driver);
        return new Menu(coverUrl,title,author,synopsis,status,lastUpdateTime,chapters);
    }

    @Override
    public Pair<Integer,String> getAmountAndStartDownload(Map<String, Integer> messageQueue, String url, String cachePath, String title, String chapter) {

        //从池中获取驱动，根据输入结果查询
        GenericObjectPool<WebDriver> pool = WebDriverPoolConfig.getPool();
        WebDriver driver = null;
        try {
            driver = pool.borrowObject();
        } catch (Exception e) {
            e.printStackTrace();
        }

        driver.get(url);

        //等到页面序号加载出来
        new WebDriverWait(driver, Duration.ofSeconds(10)).until(ExpectedConditions.visibilityOfElementLocated(By.className("comicCount")));
        new WebDriverWait(driver, Duration.ofSeconds(10)).until(ExpectedConditions.not(ExpectedConditions.textToBe(By.className("comicCount"),"0")));
        WebElement comicIndexElement = driver.findElement(By.className("comicIndex"));
        WebElement comicCountElement = driver.findElement(By.className("comicCount"));
        int comicCount = Integer.parseInt(comicCountElement.getText());


        //下拉页面，让图片都加载出来
        //总数在60页以内的一般滚动五次即可全部加载出来。没必要滚动到底
        //该网站是按照滚动次数来进行懒加载的
        if(comicCount < 60){
            for (int i = 0; i < 5; i++) {
                new Actions(driver).scrollByAmount(0,300).perform();
            }
        }else {
            for (int i = 0; i < 10; i++) {
                new Actions(driver).scrollByAmount(0,300).perform();
            }
        }


        //在队列中放置开始标志
        //内容为章节名称/标题名称  START
        messageQueue.put(PathUtil.getPathKey(title,chapter), MQCode.CHAPTER_START);

        //抓取图片列表
        List<WebElement> onlineImages = driver.findElements(By.cssSelector(".comicContent-list img"));
        int i = 1;//用于图片计数
        String fileExtension = new String("");
        for (WebElement onlineImage : onlineImages) {
            //获取url和扩展名，创建本地对应的缓存图片位置
            String imgUrl = onlineImage.getAttribute("data-src");
            fileExtension = FilenameUtils.getExtension(imgUrl);
            File localImage = new File(cachePath + File.separator + title
                    + File.separator + chapter + File.separator + i++ + "." + fileExtension);
            //将图片交给imgDownloader异步处理
            ImgDownloder imgDownloder = new ImgDownloder();
            imgDownloder.setParam(imgUrl,localImage,messageQueue);
            ThreadPoolConfig.execute(imgDownloder);
            try {
                TimeUnit.MILLISECONDS.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        //在队列中放置结束标记
        //内容为章节名称/标题名称/结束的图片序号+1  CHAPTER_OVER
        messageQueue.put(PathUtil.getPathKey(title,chapter,i), MQCode.CHAPTER_OVER);

        //归还driver
        pool.returnObject(driver);

        return new Pair<>(comicCount,fileExtension);
    }
}
