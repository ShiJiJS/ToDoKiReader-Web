package com.shijivk.todokireader;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

public class Test {
    public static void main(String[] args) throws IOException {
        TestYaml testYaml = new TestYaml();
        testYaml.test();
    }

}


class TestYaml{
    public void test(){
        Yaml yaml = new Yaml();
        InputStream stream = this.getClass().getClassLoader().getResourceAsStream("application.yaml");

        Map<String, Object> ret = yaml.load(stream);
        String classPath = (String)ret.get("cachePath");
        System.out.println(ret);
        System.out.println(classPath);
    }
}