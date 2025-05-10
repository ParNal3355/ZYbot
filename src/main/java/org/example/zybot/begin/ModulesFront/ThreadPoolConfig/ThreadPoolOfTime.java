package org.example.zybot.begin.ModulesFront.ThreadPoolConfig;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import java.util.concurrent.ThreadPoolExecutor;


//定时任务线程池
@Configuration
public class ThreadPoolOfTime {

    //线程池名称
    @Bean(name = "dbCleanupExecutor")
    public ThreadPoolTaskExecutor dbCleanupExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        // 核心线程数，根据CPU核心数和任务类型来设置
        int corePoolSize = Runtime.getRuntime().availableProcessors()/2;

        // 最大线程数，可以根据实际情况调整，但要避免设置过高
        int maxPoolSize = corePoolSize * 4;

        // 队列容量，可以根据任务的特性和内存限制来设置
        int queueCapacity = 100;

        // 线程池维护线程所允许的空闲时间
        int keepAliveSeconds = 60;

        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setKeepAliveSeconds(keepAliveSeconds);

        // 设置线程池中的线程的名称前缀，方便调试
        executor.setThreadNamePrefix("db-cleanup-thread-");

        // 拒绝策略：当池子中没有空闲线程时，新任务会交给此Handler处理
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());

        // 初始化
        executor.initialize();

        return executor;
    }
}