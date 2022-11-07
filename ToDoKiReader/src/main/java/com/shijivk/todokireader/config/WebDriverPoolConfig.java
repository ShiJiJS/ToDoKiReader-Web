package com.shijivk.todokireader.config;

import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.openqa.selenium.WebDriver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


public class WebDriverPoolConfig {

//    @Bean
//    public GenericObjectPool<WebDriver> genericObjectPool(){
//        // 创建池对象工厂
//        PooledObjectFactory<WebDriver> factory = new WebDriverFactory();
//
//        GenericObjectPoolConfig<WebDriver> poolConfig = new GenericObjectPoolConfig<WebDriver>();
//        // 最大空闲数
//        poolConfig.setMaxIdle(3);
//        // 最小空闲数, 池中只有一个空闲对象的时候，池会在创建一个对象，并借出一个对象，从而保证池中最小空闲数为1
//        poolConfig.setMinIdle(1);
//        // 最大池对象总数
//        poolConfig.setMaxTotal(3);
//        // 在获取对象的时候检查有效性, 默认false
//        poolConfig.setTestOnBorrow(true);
//        // 在归还对象的时候检查有效性, 默认false
//        poolConfig.setTestOnReturn(true);
//        // 是否启用后进先出, 默认true
//        poolConfig.setLifo(true);
//        // 连接耗尽时是否阻塞, false报异常,ture阻塞直到超时, 默认true
//        poolConfig.setBlockWhenExhausted(true);
//        // 每次逐出检查时 逐出的最大数目 默认3
//        poolConfig.setNumTestsPerEvictionRun(3);
//
//        // 创建对象池
//        return new GenericObjectPool<WebDriver>(factory, poolConfig){};
//    }

    private static GenericObjectPool<WebDriver> pool;

    //当类加载时初始化池
    static {
        // 创建池对象工厂
        PooledObjectFactory<WebDriver> factory = new WebDriverFactory();

        GenericObjectPoolConfig<WebDriver> poolConfig = new GenericObjectPoolConfig<WebDriver>();
        // 最大空闲数
        poolConfig.setMaxIdle(3);
        // 最小空闲数, 池中只有一个空闲对象的时候，池会在创建一个对象，并借出一个对象，从而保证池中最小空闲数为1
        poolConfig.setMinIdle(1);
        // 最大池对象总数
        poolConfig.setMaxTotal(3);
        // 在获取对象的时候检查有效性, 默认false
        poolConfig.setTestOnBorrow(true);
        // 在归还对象的时候检查有效性, 默认false
        poolConfig.setTestOnReturn(true);
        // 是否启用后进先出, 默认true
        poolConfig.setLifo(true);
        // 连接耗尽时是否阻塞, false报异常,ture阻塞直到超时, 默认true
        poolConfig.setBlockWhenExhausted(true);
        // 每次逐出检查时 逐出的最大数目 默认3
        poolConfig.setNumTestsPerEvictionRun(3);

        // 创建对象池
        pool = new GenericObjectPool<WebDriver>(factory, poolConfig);
    }

    public static GenericObjectPool<WebDriver> getPool(){
        return pool;
    }

}
