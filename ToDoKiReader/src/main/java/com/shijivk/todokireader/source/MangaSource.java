package com.shijivk.todokireader.source;


import com.shijivk.todokireader.pojo.Menu;
import com.shijivk.todokireader.pojo.SearchResult;
import javafx.util.Pair;

import java.util.List;
import java.util.Map;

public interface MangaSource {
    public List<SearchResult> search(String content,Integer pageNumber);
    public Menu getMenu(String url);
    //返回图片数量,同时开始缓存图片
    public Pair<Integer,String> getAmountAndStartDownload(Map<String, Integer> messageQueue, String url, String cachePath, String title, String chapter);


    //暂时用不到的方法
    //返回图片名，图片链接
    //public LinkedHashMap<String,String> getImages(String url);
}
