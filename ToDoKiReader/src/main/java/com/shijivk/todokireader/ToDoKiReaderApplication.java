package com.shijivk.todokireader;

import com.shijivk.todokireader.config.CacheLoader;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication

public class ToDoKiReaderApplication {


    public static void main(String[] args) {
        SpringApplication.run(ToDoKiReaderApplication.class, args);
        CacheLoader cacheManager = new CacheLoader();
    }

}
