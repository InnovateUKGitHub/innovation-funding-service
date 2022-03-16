package org.innovateuk.ifs.starter.cache.cfg;

import com.google.common.collect.ObjectArrays;
import org.innovateuk.ifs.starter.common.util.ProfileUtils;
import org.innovateuk.ifs.starters.cache.cfg.IfsCacheAutoConfiguration;
import org.innovateuk.ifs.starters.cache.cfg.IfsCacheContextInitializer;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.cache.CacheAutoConfiguration;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.boot.autoconfigure.cache.CacheType;
import org.springframework.boot.autoconfigure.data.redis.LettuceClientConfigurationBuilderCustomizer;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.data.redis.core.StringRedisTemplate;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.innovateuk.ifs.IfsProfileConstants.REDIS_STANDALONE_CACHE;

public class IfsRedisStandaloneCacheAutoConfigurationTest {

    /** Expected from the environment i.e. k8s config-maps/secrets */
    protected static final String[] ENVIRONMENT_CACHE_CONFIG_COMMON = new String[] {
        "SPRING_CACHE_TYPE=redis",
        "SPRING_CACHE_REDIS_KEY_PREFIX=nn:",
        "SPRING_REDIS_SSL=false",
        "SPRING_REDIS_PASSWORD=",
        "SPRING_CACHE_REDIS_TIME_TO_LIVE=18000000",
        "SPRING_REDIS_LETTUCE_POOL_MAX_ACTIVE=8"
    };

    /** For standalone mode */
    private static final String[] ENVIRONMENT_CACHE_CONFIG_STANDALONE = new String[]{
        ProfileUtils.activeProfilesString(REDIS_STANDALONE_CACHE),
        "SPRING_REDIS_CLUSTER_NODES=",
        "SPRING_REDIS_HOST=cache-provider",
        "SPRING_REDIS_PORT=6379"
    };

    @Test
    public void testConfigWithSimpleCacheProfile() {

        new ApplicationContextRunner()
            .withSystemProperties(
                ObjectArrays.concat(ENVIRONMENT_CACHE_CONFIG_COMMON, ENVIRONMENT_CACHE_CONFIG_STANDALONE, String.class)
            )
            .withInitializer(new IfsCacheContextInitializer())
            .withConfiguration(
                AutoConfigurations.of(IfsCacheAutoConfiguration.class, CacheAutoConfiguration.class, RedisAutoConfiguration.class)
            ).run((context) -> {
                assertThat(context.getBean(CacheErrorHandler.class), is(notNullValue()));
                assertThat(context.getBean(StringRedisTemplate.class), is(notNullValue()));
                assertThat(context.getBean(LettuceClientConfigurationBuilderCustomizer.class), is(notNullValue()));
                assertThat(context.getBean(CacheProperties.class).getType(), equalTo(CacheType.REDIS));
                assertThat(context.getBean(RedisProperties.class).getClientType(), equalTo(RedisProperties.ClientType.LETTUCE));
                assertThat(context.getBean(RedisProperties.class).getCluster(), nullValue());
            });
    }
}
