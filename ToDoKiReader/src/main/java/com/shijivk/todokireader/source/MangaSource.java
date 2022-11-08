package com.shijivk.todokireader.source;


import com.shijivk.todokireader.pojo.CacheInfo;
import com.shijivk.todokireader.pojo.Menu;
import com.shijivk.todokireader.pojo.SearchResult;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public interface MangaSource {
    public List<SearchResult> search(String content);
    public Menu getMenu(String url);
    //返回图片数量,同时开始缓存图片
    public int getAmountAndStartDownload(Map<String, CacheInfo> messageQueue, String url, String cachePath, int titleNumber, int chapterNumber);


    //暂时用不到的方法
    //返回图片名，图片链接
    public LinkedHashMap<String,String> getImages(String url);
}
