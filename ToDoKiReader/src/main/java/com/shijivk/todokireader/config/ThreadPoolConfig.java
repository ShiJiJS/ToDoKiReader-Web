package com.shijivk.todokireader.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Configuration
public class ThreadPoolConfig {

    private static ThreadPoolExecutor threadPoolExecutor;

    private static final int CORE_POOL_SIZE = 20;
    private static final int MAX_POOL_SIZE = 40;
    private static final int QUEUE_CAPACITY = 100;
    private static final Long KEEP_ALIVE_TIME = 1L;

    @Bean
    public ThreadPoolExecutor threadPoolExecutor(){
        threadPoolExecutor = new ThreadPoolExecutor(
                CORE_POOL_SIZE,
                MAX_POOL_SIZE,
                KEEP_ALIVE_TIME,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(QUEUE_CAPACITY),
                new ThreadPoolExecutor.CallerRunsPolicy());
        return threadPoolExecutor;
    }

    public static void execute(Runnable runnable){
        threadPoolExecutor.execute(runnable);
    }

    public void shutdownThreadPool(){
        threadPoolExecutor.shutdown();
    }

}
