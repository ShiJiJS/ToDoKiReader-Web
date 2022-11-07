package com.shijivk.todokireader;

import org.apache.commons.pool2.impl.GenericObjectPool;
import org.checkerframework.checker.units.qual.A;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication

public class ToDoKiReaderApplication {

    public static void main(String[] args) {
        SpringApplication.run(ToDoKiReaderApplication.class, args);
    }

}
