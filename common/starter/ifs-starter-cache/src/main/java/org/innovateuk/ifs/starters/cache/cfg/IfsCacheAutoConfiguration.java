package org.innovateuk.ifs.starters.cache.cfg;

import lombok.extern.slf4j.Slf4j;
import org.innovateuk.ifs.IfsProfileConstants;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.cache.CacheAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.cache.interceptor.SimpleCacheErrorHandler;
import org.springframework.cache.support.NoOpCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * Light touch on top of the spring AutoConfiguration
 *
 * In stub and dev mode we create NoOpCacheManager that stops any other cache managers being registered.
 *
 * This includes redis cache that is also on the classpath.
 */
@Configuration
@EnableCaching
@Slf4j
@AutoConfigureBefore({CacheAutoConfiguration.class, RedisAutoConfiguration.class})
public class IfsCacheAutoConfiguration {

    @Bean
    @Profile({IfsProfileConstants.STUBDEV + "|" + IfsProfileConstants.DEV})
    public CacheManager cacheManager() {
        log.info("Configuring NoOpCacheManager");
        return new NoOpCacheManager();
    }

//    @Bean
//    @Profile({IfsProfileConstants.NOT_STUBDEV + "&" + IfsProfileConstants.NOT_DEV})
//    public RedisConfiguration redisConfiguration() {
//        return new RedisConfiguration();
//    }

    @Bean
    public CacheErrorHandler errorHandler() {
        return new SimpleCacheErrorHandler() {
            @Override
            public void handleCacheGetError(RuntimeException exception, Cache cache, Object key) {
                log.debug("Failed to get cache item with key " + key.toString(), exception);
            }

            @Override
            public void handleCachePutError(RuntimeException exception, Cache cache, Object key, Object value) {
                log.error("Failed to put cache item with key " + key.toString(), exception);
            }

            @Override
            public void handleCacheEvictError(RuntimeException exception, Cache cache, Object key) {
                log.error("Failed to evict cache item with key " + key.toString(), exception);
            }
        };
    }

}
