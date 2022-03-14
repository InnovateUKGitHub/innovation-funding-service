package org.innovateuk.ifs.starters.cache.cfg;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.cache.CacheAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.cache.Cache;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.cache.interceptor.SimpleCacheErrorHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Light touch on top of the spring AutoConfiguration migrated out of data-service into a starter -:
 *
 * Required to override the presence of redis on the classpath.
 *
 * Applies the existing RedisConfiguration based on the cache type setting.
 * Pre-processes some startup configuration based on the profile.
 * @see SimpleCachePropertiesPostProcessor
 *
 */
@Configuration
@Slf4j
@EnableCaching
@AutoConfigureBefore({CacheAutoConfiguration.class, RedisAutoConfiguration.class})
public class IfsCacheAutoConfiguration {

    @Bean
    @ConditionalOnProperty(name = "spring.cache.type", havingValue = "redis", matchIfMissing = true)
    public RedisConfiguration redisConfiguration() {
        return new RedisConfiguration();
    }

    @Bean
    public CacheErrorHandler errorHandler() {
        return new SimpleCacheErrorHandler() {
            @Override
            public void handleCacheGetError(RuntimeException exception, Cache cache, Object key) {
                log.debug("Failed to get cache item with key " + key, exception);
            }

            @Override
            public void handleCachePutError(RuntimeException exception, Cache cache, Object key, Object value) {
                log.error("Failed to put cache item with key " + key, exception);
            }

            @Override
            public void handleCacheEvictError(RuntimeException exception, Cache cache, Object key) {
                log.error("Failed to evict cache item with key " + key, exception);
            }
        };
    }

}
