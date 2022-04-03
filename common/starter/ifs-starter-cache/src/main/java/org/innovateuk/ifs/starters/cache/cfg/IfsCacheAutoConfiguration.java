package org.innovateuk.ifs.starters.cache.cfg;

import io.lettuce.core.ClientOptions;
import io.lettuce.core.cluster.ClusterClientOptions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.cache.CacheAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.data.redis.LettuceClientConfigurationBuilderCustomizer;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.Cache;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.cache.interceptor.SimpleCacheErrorHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Wrapper around the spring AutoConfiguration migrated out of data-service into a starter.
 *
 * Required to override the presence of redis on the classpath and support cluster and non-cluster modes
 * while being able to read this config in via environment variables.
 *
 * Sets up a cache error handler.
 * Detects cluster and non cluster mode and alters lettuce config to accommodate this.
 *
 * This all follows on from the config pre-processor
 * @see IfsCacheContextInitializer
 *
 */
@Slf4j
@Configuration
@EnableCaching
@EnableConfigurationProperties({RedisProperties.class})
@AutoConfigureBefore({CacheAutoConfiguration.class, RedisAutoConfiguration.class})
public class IfsCacheAutoConfiguration {

    @Autowired
    private RedisProperties redisProperties;

    /**
     * Setting the properties via k8s config maps means we always have an empty string for spring.redis.cluster.nodes.
     *
     * Detect this and override the Lettuce configuration to force standalone mode, otherwise go with the defaults.
     *
     * @return LettuceClientConfigurationBuilderCustomizer
     */
    @Bean
    @ConditionalOnProperty(name = "spring.redis.clientType", havingValue = "LETTUCE")
    public LettuceClientConfigurationBuilderCustomizer lettuceClientConfigurationBuilderCustomizer() {
        final ClientOptions.Builder options;
        if (redisProperties.getCluster() == null
                || redisProperties.getCluster().getNodes() == null
                || redisProperties.getCluster().getNodes().isEmpty()) {
            redisProperties.setCluster(null); // RedisConnectionConfiguration:L106!!
            options = ClusterClientOptions.builder().validateClusterNodeMembership(false);
        } else {
            options = ClientOptions.builder();
        }
        return builder -> builder.clientOptions(options.disconnectedBehavior(ClientOptions.DisconnectedBehavior.REJECT_COMMANDS).build());
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
