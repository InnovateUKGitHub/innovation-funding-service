package org.innovateuk.ifs.config.cache;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

import static java.time.temporal.ChronoUnit.MINUTES;

@Configuration
public class CacheConfiguration {

    @Bean
    public ServiceResultWrappingSerializer serviceResultWrappingSerializer() {
        return new ServiceResultWrappingSerializer();
    }

    @Bean
    public RedisCacheConfiguration defaultCacheConfig(ServiceResultWrappingSerializer serviceResultWrappingSerializer) {
        return RedisCacheConfiguration.defaultCacheConfig()
                .serializeKeysWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(
                                new StringRedisSerializer()
                        )
                )
                .serializeValuesWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(
                                serviceResultWrappingSerializer
                        )
                )
                .entryTtl(Duration.of(30, MINUTES));
    }
}
