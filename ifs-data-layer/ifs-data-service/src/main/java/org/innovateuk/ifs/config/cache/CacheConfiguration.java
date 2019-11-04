package org.innovateuk.ifs.config.cache;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

import static java.time.temporal.ChronoUnit.SECONDS;

@Configuration
public class CacheConfiguration {

    @Value("${ifs.data.service.cache.ttl.seconds}")
    private int ttlSeconds;

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
                .entryTtl(Duration.of(ttlSeconds, SECONDS));
    }
}
