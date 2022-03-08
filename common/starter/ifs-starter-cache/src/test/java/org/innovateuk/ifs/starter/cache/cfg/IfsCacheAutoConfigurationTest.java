package org.innovateuk.ifs.starter.cache.cfg;

import org.innovateuk.ifs.IfsProfileConstants;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeansException;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.cache.CacheAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.cache.support.NoOpCacheManager;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;

class IfsCacheAutoConfigurationTest {

    private static final String PROFILE_PROP = AbstractEnvironment.ACTIVE_PROFILES_PROPERTY_NAME + "=" + IfsProfileConstants.STUBDEV;

    @Test
    public void testConfigWithStubProfile() {
        new ApplicationContextRunner()
            .withSystemProperties(
                    PROFILE_PROP
            )
            .withConfiguration(
                    AutoConfigurations.of(IfsCacheAutoConfiguration.class)
            ).run((context) -> {
                assertFound(context, Collections.singletonList(NoOpCacheManager.class));
                assertNotFound(context, Collections.singletonList(RedisTemplate.class));
            });
    }

    @Test
    public void testConfigWithOtherProfile() {
        new ApplicationContextRunner()
            .withSystemProperties(
                    AbstractEnvironment.ACTIVE_PROFILES_PROPERTY_NAME + "=ANYTHING_ELSE"
            )
            .withConfiguration(
                    AutoConfigurations.of(IfsCacheAutoConfiguration.class, CacheAutoConfiguration.class, RedisAutoConfiguration.class)
            ).run((context) -> {
                assertFound(context, Collections.singletonList(StringRedisTemplate.class));
                assertNotFound(context, Collections.singletonList(NoOpCacheManager.class));
            });
    }

    private static void assertNotFound(ApplicationContext context, List<Class> clzs) {
        clzs.stream().forEach(clz -> assertThrows(BeansException.class, () -> context.getBean(clz)));
    }

    private static void assertFound(ApplicationContext context, List<Class> clzs) {
        clzs.stream().forEach(clz -> assertThat(context.getBean(clz), is(notNullValue())));
    }

}