package org.innovateuk.ifs.testdata;

import org.innovateuk.ifs.Application;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * TODO DW - document this class
 */
@EnableAsync
@Configuration
public class TestApplication extends Application {

    @Bean
    public ThreadPoolTaskExecutor threadPoolTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(1);
        executor.setMaxPoolSize(50);
        executor.setQueueCapacity(Integer.MAX_VALUE);
        executor.setAllowCoreThreadTimeOut(true);
        executor.setThreadNamePrefix("IFS-Test-Executor-");
        executor.initialize();
        return executor;
    }
}
