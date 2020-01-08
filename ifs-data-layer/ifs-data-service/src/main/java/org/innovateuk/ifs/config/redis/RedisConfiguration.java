package org.innovateuk.ifs.config.redis;

import io.lettuce.core.ClientOptions;
import io.lettuce.core.ClientOptions.DisconnectedBehavior;
import io.lettuce.core.cluster.ClusterClientOptions;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.autoconfigure.data.redis.LettuceClientConfigurationBuilderCustomizer;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.cache.Cache;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.cache.interceptor.SimpleCacheErrorHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedisConfiguration extends CachingConfigurerSupport {
    private static final Log LOG = LogFactory.getLog(RedisConfiguration.class);
    private RedisProperties redisProperties;

    /*
        Modify the redis properties. We set the redis connection with env variables. We want to use env variables
        to configure either an standalone or cluster configuration. The default autoconfigurer will assume we're using
        a cluster configuration if the cluster nodes property is defined (Even if its empty!).

        Here we are setting the cluster configuration to be null if the nodes property is empty.
     */
    public RedisConfiguration(RedisProperties properties) {
        if (properties.getCluster() != null && properties.getCluster().getNodes().isEmpty()) {
            properties.setCluster(null);
        }
        this.redisProperties = properties;
    }

    @Bean
    public LettuceClientConfigurationBuilderCustomizer lettuceClientConfigurationBuilderCustomizer() {
        final ClientOptions.Builder options;
        if (redisProperties.getCluster() != null) {
            options = ClusterClientOptions.builder()
                    .validateClusterNodeMembership(false);
        } else {
            options = ClientOptions.builder();
        }
        return builder -> builder.clientOptions(options
                .disconnectedBehavior(DisconnectedBehavior.REJECT_COMMANDS).build());
    }

    @Override
    public CacheErrorHandler errorHandler() {
        return new SimpleCacheErrorHandler() {
            @Override
            public void handleCacheGetError(RuntimeException exception, Cache cache, Object key) {
                LOG.debug("Failed to get cache item with key " + key.toString(), exception);
            }

            @Override
            public void handleCachePutError(RuntimeException exception, Cache cache, Object key, Object value) {
                LOG.error("Failed to put cache item with key " + key.toString(), exception);
            }

            @Override
            public void handleCacheEvictError(RuntimeException exception, Cache cache, Object key) {
                LOG.error("Failed to evict cache item with key " + key.toString(), exception);

            }
        };
    }
}
