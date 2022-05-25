package org.innovateuk.ifs.starters.feign.cfg;

import feign.Logger;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class IfsFeignAutoConfiguration {

    @Bean
    Logger.Level feignLoggerLevel() {
        return Logger.Level.BASIC;
    }

    @Bean
    OkHttpClient okHttpClient() {
        return new OkHttpClient.Builder()
                .build();
    }

    @Bean
    ErrorDecoder errorDecoder() {
        return new IfsErrorDecoder();
    }

}
