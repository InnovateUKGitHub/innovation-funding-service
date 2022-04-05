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
import static org.innovateuk.ifs.starter.cache.cfg.IfsRedisStandaloneCacheAutoConfigurationTest.ENVIRONMENT_CACHE_CONFIG_COMMON;

public class IfsRedisClusterCacheAutoConfigurationTest {

    /** For cluster mode */
    private static final String[] ENVIRONMENT_CACHE_CONFIG_CLUSTER = new String[] {
        ProfileUtils.activeProfilesString("ANYTHINGELSE"),
        "SPRING_REDIS_CLUSTER_NODES=somewhere.probably.cache.aws.com:6379,a.n.other:6379",
        "SPRING_REDIS_HOST=",
        "SPRING_REDIS_PORT=0"
    };

    @Test
    public void testConfigClusterMode() {
        new ApplicationContextRunner()
            .withSystemProperties(
                ObjectArrays.concat(ENVIRONMENT_CACHE_CONFIG_COMMON, ENVIRONMENT_CACHE_CONFIG_CLUSTER, String.class)
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
                assertThat(context.getBean(RedisProperties.class).getCluster().getNodes().size(), equalTo(2));
            });
    }

}