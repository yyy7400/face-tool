package com.yang.face.config;

import cn.hutool.core.thread.NamedThreadFactory;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Async;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 线程池管理器
 *
 * @author yangyuyang
 * @date 2020/3/12 16:01
 */
@Configuration
@Async
public class TaskPollConfig {

    private final AtomicInteger threadNumber = new AtomicInteger(1);

    @Bean("taskExcutor")
    public Executor taskExcutor() {

        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(5, 10, 200, TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(5), new NamedThreadFactory("taskExcutor", false));
        return threadPoolExecutor;
    }
}
