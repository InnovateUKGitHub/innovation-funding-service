package org.innovateuk.ifs.starter.cache.cfg;

import org.innovateuk.ifs.starter.common.util.ProfileUtils;
import org.innovateuk.ifs.starters.cache.cfg.IfsCacheAutoConfiguration;
import org.innovateuk.ifs.starters.cache.cfg.IfsCacheContextInitializer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.ResourceLock;
import org.springframework.beans.BeansException;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.cache.CacheAutoConfiguration;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.boot.autoconfigure.cache.CacheType;
import org.springframework.boot.autoconfigure.data.redis.LettuceClientConfigurationBuilderCustomizer;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.data.redis.core.RedisTemplate;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.innovateuk.ifs.IfsProfileConstants.SIMPLE_CACHE;
import static org.junit.jupiter.api.Assertions.assertThrows;

class IfsSimpleCacheAutoConfigurationTest {

    @Test
    @ResourceLock("ApplicationContextRunner")
    void testConfigWithSimpleCacheProfile() {
        new ApplicationContextRunner()
            .withSystemProperties(
                ProfileUtils.activeProfilesString(SIMPLE_CACHE)
            )
            .withInitializer(new IfsCacheContextInitializer())
            .withConfiguration(
                AutoConfigurations.of(IfsCacheAutoConfiguration.class, CacheAutoConfiguration.class, RedisAutoConfiguration.class)
            ).run((context) -> {
                assertThat(context.getBean(CacheErrorHandler.class), is(notNullValue()));
                assertThrows(BeansException.class, () -> context.getBean(RedisTemplate.class));
                assertThrows(BeansException.class, () -> context.getBean(LettuceClientConfigurationBuilderCustomizer.class));
                assertThat(context.getBean(CacheProperties.class).getType(), equalTo(CacheType.SIMPLE));
            });
    }
}
