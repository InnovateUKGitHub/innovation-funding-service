package org.innovateuk.ifs.starters.cache.cfg;


import io.lettuce.core.ClientOptions;
import io.lettuce.core.ClientOptions.DisconnectedBehavior;
import io.lettuce.core.cluster.ClusterClientOptions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.redis.LettuceClientConfigurationBuilderCustomizer;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.context.annotation.Bean;

@Slf4j
public class RedisConfiguration extends CachingConfigurerSupport {

    @Autowired
    private RedisProperties redisProperties;

    @Bean
    public LettuceClientConfigurationBuilderCustomizer lettuceClientConfigurationBuilderCustomizer() {
        final ClientOptions.Builder options = ClusterClientOptions.builder()
                    .validateClusterNodeMembership(false)
                .disconnectedBehavior(DisconnectedBehavior.REJECT_COMMANDS);
        return builder -> builder.clientOptions(options.build());
    }

}
