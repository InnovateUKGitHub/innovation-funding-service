package org.innovateuk.ifs.config.redis;

import io.lettuce.core.ClientOptions;
import io.lettuce.core.ClientOptions.DisconnectedBehavior;
import io.lettuce.core.resource.ClientResources;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.data.redis.LettuceClientConfigurationBuilderCustomizer;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties.Pool;
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
import org.springframework.util.StringUtils;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.List;

@Configuration
public class RedisConfiguration extends CachingConfigurerSupport {
    private static final Log LOG = LogFactory.getLog(RedisConfiguration.class);

    private final RedisProperties properties;
    private final RedisClusterConfiguration clusterConfiguration;
    private final List<LettuceClientConfigurationBuilderCustomizer> builderCustomizers;

    public RedisConfiguration(RedisProperties properties,
                              ObjectProvider<RedisClusterConfiguration> clusterConfigurationProvider,
			ObjectProvider<List<LettuceClientConfigurationBuilderCustomizer>> builderCustomizers) {
        this.properties = properties;
        this.builderCustomizers = builderCustomizers
                .getIfAvailable(Collections::emptyList);
        this.clusterConfiguration = clusterConfigurationProvider.getIfAvailable();
    }

    @Bean
    public LettuceConnectionFactory redisConnectionFactory(
            ClientResources clientResources) throws UnknownHostException {
        LettuceClientConfiguration clientConfig = getLettuceClientConfiguration(
                clientResources, this.properties.getLettuce().getPool());
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
            ClientResources clientResources, Pool pool) {
        LettuceClientConfigurationBuilder builder = createBuilder(pool);
        applyProperties(builder);
        builder.clientOptions(ClientOptions.builder()
                .disconnectedBehavior(DisconnectedBehavior.REJECT_COMMANDS)
                    .build());
        if (StringUtils.hasText(this.properties.getUrl())) {
            customizeConfigurationFromUrl(builder);
        }
        builder.clientResources(clientResources);
        customize(builder);
        return builder.build();
    }
    private LettuceClientConfigurationBuilder createBuilder(Pool pool) {
        if (pool == null) {
            return LettuceClientConfiguration.builder();
        }
        return new RedisConfiguration.PoolBuilderFactory().createBuilder(pool);
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

    private void customizeConfigurationFromUrl(
            LettuceClientConfiguration.LettuceClientConfigurationBuilder builder) {
        ConnectionInfo connectionInfo = parseUrl(this.properties.getUrl());
        if (connectionInfo.isUseSsl()) {
            builder.useSsl();
        }
    }

    private void customize(
            LettuceClientConfiguration.LettuceClientConfigurationBuilder builder) {
        for (LettuceClientConfigurationBuilderCustomizer customizer : this.builderCustomizers) {
            customizer.customize(builder);
        }
    }
    protected final RedisStandaloneConfiguration getStandaloneConfig() {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
        if (StringUtils.hasText(this.properties.getUrl())) {
            ConnectionInfo connectionInfo = parseUrl(this.properties.getUrl());
            config.setHostName(connectionInfo.getHostName());
            config.setPort(connectionInfo.getPort());
            config.setPassword(RedisPassword.of(connectionInfo.getPassword()));
        }
        else {
            config.setHostName(this.properties.getHost());
            config.setPort(this.properties.getPort());
            config.setPassword(RedisPassword.of(this.properties.getPassword()));
        }
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

    /**
     * Inner class to allow optional commons-pool2 dependency.
     */
    private static class PoolBuilderFactory {

        public LettuceClientConfigurationBuilder createBuilder(Pool properties) {
            return LettucePoolingClientConfiguration.builder()
                    .poolConfig(getPoolConfig(properties));
        }

        private GenericObjectPoolConfig getPoolConfig(Pool properties) {
            GenericObjectPoolConfig config = new GenericObjectPoolConfig();
            config.setMaxTotal(properties.getMaxActive());
            config.setMaxIdle(properties.getMaxIdle());
            config.setMinIdle(properties.getMinIdle());
            if (properties.getMaxWait() != null) {
                config.setMaxWaitMillis(properties.getMaxWait().toMillis());
            }
            return config;
        }

    }

    protected RedisConfiguration.ConnectionInfo parseUrl(String url) {
        try {
            URI uri = new URI(url);
            boolean useSsl = (url.startsWith("rediss://"));
            String password = null;
            if (uri.getUserInfo() != null) {
                password = uri.getUserInfo();
                int index = password.indexOf(':');
                if (index >= 0) {
                    password = password.substring(index + 1);
                }
            }
            return new RedisConfiguration.ConnectionInfo(uri, useSsl, password);
        }
        catch (URISyntaxException ex) {
            throw new IllegalArgumentException("Malformed url '" + url + "'", ex);
        }
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
    protected static class ConnectionInfo {

        private final URI uri;

        private final boolean useSsl;

        private final String password;

        public ConnectionInfo(URI uri, boolean useSsl, String password) {
            this.uri = uri;
            this.useSsl = useSsl;
            this.password = password;
        }

        public boolean isUseSsl() {
            return this.useSsl;
        }

        public String getHostName() {
            return this.uri.getHost();
        }

        public int getPort() {
            return this.uri.getPort();
        }

        public String getPassword() {
            return this.password;
        }

    }
}
