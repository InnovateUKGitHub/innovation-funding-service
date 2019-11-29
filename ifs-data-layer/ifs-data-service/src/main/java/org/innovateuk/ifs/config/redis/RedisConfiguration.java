package org.innovateuk.ifs.config.redis;

import io.lettuce.core.ClientOptions;
import io.lettuce.core.ClientOptions.DisconnectedBehavior;
import io.lettuce.core.resource.ClientResources;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.cache.Cache;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.cache.interceptor.SimpleCacheErrorHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration.LettuceClientConfigurationBuilder;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;

@Configuration
public class RedisConfiguration extends CachingConfigurerSupport {
    private static final Log LOG = LogFactory.getLog(RedisConfiguration.class);

    private final RedisProperties properties;
    private final RedisClusterConfiguration clusterConfiguration;

    public RedisConfiguration(RedisProperties properties,
                              ObjectProvider<RedisClusterConfiguration> clusterConfigurationProvider) {
        this.properties = properties;
        this.clusterConfiguration = clusterConfigurationProvider.getIfAvailable();
    }

    @Bean
    public LettuceConnectionFactory redisConnectionFactory(
            ClientResources clientResources) {
        LettuceClientConfiguration clientConfig = getLettuceClientConfiguration(
                clientResources);
        return createLettuceConnectionFactory(clientConfig);
    }

    private LettuceConnectionFactory createLettuceConnectionFactory(
            LettuceClientConfiguration clientConfiguration) {
        if (getClusterConfiguration() != null) {
            return new LettuceConnectionFactory(getClusterConfiguration(),
                    clientConfiguration);
        }
        return new LettuceConnectionFactory(getStandaloneConfig(), clientConfiguration);
    }
    private LettuceClientConfiguration getLettuceClientConfiguration(
            ClientResources clientResources) {
        LettuceClientConfigurationBuilder builder = createBuilder();
        applyProperties(builder);
        builder.clientOptions(ClientOptions.builder()
                .disconnectedBehavior(DisconnectedBehavior.REJECT_COMMANDS)
                    .build());
        builder.clientResources(clientResources);
        return builder.build();
    }
    private LettuceClientConfigurationBuilder createBuilder() {
        GenericObjectPoolConfig config = new GenericObjectPoolConfig();
        config.setMaxTotal(properties.getLettuce().getPool().getMaxActive());
        config.setMaxIdle(properties.getLettuce().getPool().getMaxIdle());
        config.setMinIdle(properties.getLettuce().getPool().getMinIdle());
        if (properties.getLettuce().getPool().getMaxWait() != null) {
            config.setMaxWaitMillis(properties.getLettuce().getPool().getMaxWait().toMillis());
        }
        return LettucePoolingClientConfiguration.builder()
                .poolConfig(config);
    }

    private LettuceClientConfigurationBuilder applyProperties(
            LettuceClientConfiguration.LettuceClientConfigurationBuilder builder) {
        if (this.properties.isSsl()) {
            builder.useSsl();
        }
        if (this.properties.getTimeout() != null) {
            builder.commandTimeout(this.properties.getTimeout());
        }
        if (this.properties.getLettuce() != null) {
            RedisProperties.Lettuce lettuce = this.properties.getLettuce();
            if (lettuce.getShutdownTimeout() != null
                    && !lettuce.getShutdownTimeout().isZero()) {
                builder.shutdownTimeout(
                        this.properties.getLettuce().getShutdownTimeout());
            }
        }
        return builder;
    }

    protected final RedisStandaloneConfiguration getStandaloneConfig() {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
        config.setHostName(this.properties.getHost());
        config.setPort(this.properties.getPort());
        config.setPassword(RedisPassword.of(this.properties.getPassword()));
        config.setDatabase(this.properties.getDatabase());
        return config;
    }

    /**
     * Create a {@link RedisClusterConfiguration} if necessary.
     * @return {@literal null} if no cluster settings are set.
     */
    protected final RedisClusterConfiguration getClusterConfiguration() {
        if (this.clusterConfiguration != null) {
            return this.clusterConfiguration;
        }
        if (this.properties.getCluster() == null || this.properties.getCluster().getNodes().isEmpty()) {
            return null;
        }
        RedisProperties.Cluster clusterProperties = this.properties.getCluster();
        RedisClusterConfiguration config = new RedisClusterConfiguration(
                clusterProperties.getNodes());
        if (clusterProperties.getMaxRedirects() != null) {
            config.setMaxRedirects(clusterProperties.getMaxRedirects());
        }
        if (this.properties.getPassword() != null) {
            config.setPassword(RedisPassword.of(this.properties.getPassword()));
        }
        return config;
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
