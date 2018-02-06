package org.innovateuk.ifs.testdata;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * TODO DW - document this class
 */
@TestConfiguration
public class TestApplication {

    @Bean
    public ThreadPoolTaskExecutor threadPoolTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(150);
        executor.setMaxPoolSize(150);
        executor.setQueueCapacity(Integer.MAX_VALUE);
        executor.setAllowCoreThreadTimeOut(true);
        executor.setThreadNamePrefix("IFS-Test-Executor-");
        executor.initialize();
        return executor;
    }
}
