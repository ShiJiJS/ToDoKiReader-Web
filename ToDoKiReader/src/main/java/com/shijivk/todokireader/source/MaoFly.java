package com.shijivk.todokireader.source;


import com.shijivk.todokireader.config.MQCode;
import com.shijivk.todokireader.config.ThreadPoolConfig;
import com.shijivk.todokireader.config.WebDriverPoolConfig;
import com.shijivk.todokireader.pojo.CacheInfo;
import com.shijivk.todokireader.pojo.Menu;
import com.shijivk.todokireader.pojo.SearchResult;
import com.shijivk.todokireader.utils.ImgDownloder;
import com.shijivk.todokireader.utils.PathUtil;
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

//@Component("maoFly")
public class MaoFly implements MangaSource{


    private static final String sourceName = "maoFly";

    public static class ImgUrlGetter implements Runnable{

        private Map<String, CacheInfo> messageQueue;
        private String chapterUrl;
        private String cachePath;
        private int titleNumber;
        private int chapterNumber;


        public void setParam(Map<String,CacheInfo> messageQueue,String url, String cachePath, int titleNumber, int chapterNumber){
            this.messageQueue = messageQueue;
            this.chapterUrl = url;
            this.cachePath = cachePath;
            this.titleNumber = titleNumber;
            this.chapterNumber = chapterNumber;
        }

        @Override
        public void run() {
            //从池中获取驱动，根据输入结果查询
            GenericObjectPool<WebDriver> pool = WebDriverPoolConfig.getPool();
            WebDriver driver = null;
            try {
                driver = pool.borrowObject();
            } catch (Exception e) {
                e.printStackTrace();
            }
            driver.get(chapterUrl);
            //点击下拉式按钮
            driver.findElement(By.className("btn-def")).click();

            //获取页脚位置，检查页脚处是否有正在加载的标志。如果超过一定时间没有出现，则说明已经加载完了。

            WebElement loading = driver.findElement(By.id("pull-load")); //加载提示信息
            WebElement footer = driver.findElement(By.className("footer"));//页脚，定位用

            boolean isLoading = true;//是否正在加载
            long lastChange = System.currentTimeMillis();//设置上次获取到加载标志的时间


            //存储已经交给downloder的图片
            //两个参数分别为url和图片名
            HashSet<String> imageUrls = new HashSet<>();
            String extension = null;//图片扩展名

            int i = 1;//给文件名计数用的变量
            while(isLoading){
                String style = loading.getAttribute("style");
                if(!style.equals("display: none;")){
                    lastChange = System.currentTimeMillis();//设置上次获取到加载标志的时间
                }
                //判断距离上次获取到加载标志是否经过了足够长的时间
                if(System.currentTimeMillis() - lastChange > 500){
                    isLoading = false;
                }

                //查看是否有新的图片被加载了出来，如果有就放入map并交给downloder处理
                WebElement imgContent = driver.findElement(By.className("img-content"));
                List<WebElement> imgs = imgContent.findElements(By.cssSelector("img"));

                for (WebElement img : imgs) {
                    String imgUrl = img.getAttribute("src");
                    if (!imageUrls.contains(imgUrl)){
                        //放入map
                        imageUrls.add(imgUrl);
                        //获得文件存放路径
                        extension = FilenameUtils.getExtension(imgUrl);
                        File image = new File(cachePath + File.separator + titleNumber
                                + File.separator + chapterNumber + File.separator + i++ + "." + extension);
                        ImgDownloder imgDownloder = new ImgDownloder();
                        imgDownloder.setParam(imgUrl,image,messageQueue);
                        ThreadPoolConfig.execute(imgDownloder);
                    }
                }

                //滚动页面至底部
                int deltaY = footer.getRect().y;
                new Actions(driver)
                        .scrollByAmount(0, deltaY)
                        .perform();
            }
            //放置结束标记
            //获取格式形如100/100/1的key  /标题序号/章节序号/图片序号
            String key = PathUtil.getPathKey(titleNumber,chapterNumber,i);
            messageQueue.put(key, new CacheInfo(MQCode.CHAPTER_OVER,extension));

            //归还driver
            pool.returnObject(driver);
        }
    }

    @Override
    public List<SearchResult> search(String content) {
        //从池中获取驱动，根据输入结果查询
        GenericObjectPool<WebDriver> pool = WebDriverPoolConfig.getPool();
        WebDriver driver = null;
        try {
            driver = pool.borrowObject();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }


        driver.get("https://www.maofly.com/search.html?q=" + content);
        //拿到外部div
        List<WebElement> elements = driver.findElements(By.className("comicbook-index"));
        //搜索结果的List
        List<SearchResult> searchResults = new ArrayList<>();


        //等待图片加载完成，依次遍历装入List
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(3));
        for (WebElement element : elements) {
            //获取图片Url
            WebElement img = element.findElement(By.cssSelector("img"));
            wait.until(ExpectedConditions.attributeToBeNotEmpty(img, "src"));
            String imgUrl = img.getAttribute("src");
            //获取href
            WebElement hrefA = element.findElement(By.cssSelector("a"));
            String href = hrefA.getAttribute("href");
            //获取title
            String title = hrefA.getAttribute("title");
            //获取author
            String author = element.findElement(By.className("comic-author"))
                                    .findElement(By.cssSelector("a")).getText();

            searchResults.add(new SearchResult(imgUrl,title,author,href,sourceName));
        }
        //归还driver
        pool.returnObject(driver);
        return searchResults;
    }

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
        //获取标题
        String title = driver.findElement(By.className("comic-title")).getText();
        //获取作者
        String author = driver.findElement(By.className("pub-duration"))
                                .findElement(By.cssSelector("a"))
                                .getText();
        //获取发行状态和上次更新时间
        String status = "";
        String lastUpdateTime = "";
        List<WebElement> trs = driver.findElements(By.cssSelector("tbody tr"));
        for (WebElement tr : trs) {
            WebElement th = tr.findElement(By.cssSelector("th"));
            if(th.getText().equals("发行状态")){
                status = tr.findElement(By.cssSelector("a")).getText();
            }
            if(th.getText().equals("上次更新")){
                lastUpdateTime = tr.findElement(By.cssSelector("td")).getText();
            }
        }

        //获取简介
        String synopsis = driver.findElement(By.className("comic_story")).getText();
        //等待url更新后,获取封面Url
        WebElement cover = driver.findElement(By.className("comic-cover")).findElement(By.cssSelector("img"));
        String coverUrl = cover.getAttribute("src");
        //获取章节列表
        HashMap<String, String> chapters = new LinkedHashMap<>();
        List<WebElement> tables = driver.findElements(By.className("links-of-books"));

        for (WebElement table : tables) {
            List<WebElement> chapterElements = table.findElements(By.cssSelector("a"));
            for (WebElement chapter : chapterElements) {
                chapters.put(chapter.getAttribute("title"),chapter.getAttribute("href"));
            }
        }

        //归还driver
        pool.returnObject(driver);
        return new Menu(coverUrl,title,author,synopsis,status,lastUpdateTime,chapters);
    }

    @Override
    public int getAmountAndStartDownload(Map<String,CacheInfo> messageQueue, String url, String cachePath, int titleNumber, int chapterNumber) {
        //拿图片，放到指定的位置
        ImgUrlGetter imgUrlGetter = new ImgUrlGetter();
        imgUrlGetter.setParam(messageQueue,url,cachePath,titleNumber,chapterNumber);
        ThreadPoolConfig.execute(imgUrlGetter);

        //拿不到amount
        return -1;
    }




    @Override
    public LinkedHashMap<String, String> getImages(String url) {
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
        //点击下拉式按钮
        driver.findElement(By.className("btn-def")).click();

        //获取页脚位置，检查页脚处是否有正在加载的标志。如果超过一定时间没有出现，则说明已经加载完了。

        WebElement loading = driver.findElement(By.id("pull-load")); //加载提示信息
        WebElement footer = driver.findElement(By.className("footer"));//页脚，定位用

        boolean isLoading = true;//是否正在加载
        long lastChange = System.currentTimeMillis();//设置上次获取到加载标志的时间

        while(isLoading){
            String style = loading.getAttribute("style");
            if(!style.equals("display: none;")){
                lastChange = System.currentTimeMillis();//设置上次获取到加载标志的时间
            }
            //判断距离上次获取到加载标志是否经过了足够长的时间
            if(System.currentTimeMillis() - lastChange > 500){
                isLoading = false;
            }
            //滚动页面至底部
            int deltaY = footer.getRect().y;
            new Actions(driver)
                    .scrollByAmount(0, deltaY)
                    .perform();
        }

        //获取图片链接
        LinkedHashMap<String, String> linkAndImages = new LinkedHashMap<>();
        WebElement imgContent = driver.findElement(By.className("img-content"));
        List<WebElement> imgs = imgContent.findElements(By.cssSelector("img"));
        for (WebElement img : imgs) {
            String src = img.getAttribute("src");
            int i;
            for(i = src.length() - 1;i >= 0;i --){
                if(src.charAt(i) == '/')break;
            }
            String imgName = src.substring(i + 1);
            linkAndImages.put(src,imgName);
        }

        //归还driver
        pool.returnObject(driver);
        return linkAndImages;
    }


}
