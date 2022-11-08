package com.shijivk.todokireader.utils;

import org.apache.commons.io.FilenameUtils;

import java.io.File;

public class PathUtil {
    public static String getPathKey(File file){
        //获取格式形如100/100/1的key  /标题序号/章节序号/图片序号
        String filePath = file.getPath();
        String temp = filePath.substring(0, FilenameUtils.indexOfLastSeparator(filePath));
        temp = temp.substring(0,FilenameUtils.indexOfLastSeparator(temp));
        int startIndex = FilenameUtils.indexOfLastSeparator(temp) + 1;
        int endIndex = FilenameUtils.indexOfExtension(filePath);
        return filePath.substring(startIndex, endIndex);
    }

    public static String getPathKey(Integer titleNumber,Integer chapterNumber,Integer imageIndex){
        return new String(titleNumber + File.separator + chapterNumber + File.separator + imageIndex);
    }
}
