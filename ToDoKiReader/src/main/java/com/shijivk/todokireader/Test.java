package com.shijivk.todokireader;



import com.shijivk.todokireader.pojo.Menu;
import com.shijivk.todokireader.source.CopyManga;
import org.apache.commons.io.FilenameUtils;

import java.io.*;


public class Test {
    public static void main(String[] args) throws IOException {
        String a = "《关于邻家的天使大人不知不觉把我惯成了废人这件事》\\第1.2话";

        int i = FilenameUtils.indexOfLastSeparator(a);
        System.out.println(a.substring(i + 1));


    }

}


