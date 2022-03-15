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
public class IfsRedisConfiguration extends CachingConfigurerSupport {

    @Autowired
    private RedisProperties redisProperties;

    /**
     * Setting the properties via k8s config maps means we always have an empty string for spring.redis.cluster.nodes.
     *
     * Detect this and override the Lettuce configuration to force standalone mode, otherwise go with the defaults.
     *
     * @return LettuceClientConfigurationBuilderCustomizer
     */
    @Bean
    public LettuceClientConfigurationBuilderCustomizer lettuceClientConfigurationBuilderCustomizer() {
        final ClientOptions.Builder options;
        if (redisProperties.getCluster() != null && redisProperties.getCluster().getNodes().isEmpty()) {
            options = ClusterClientOptions.builder().validateClusterNodeMembership(false);
        } else {
            options = ClientOptions.builder();
        }
        return builder -> builder.clientOptions(options.disconnectedBehavior(DisconnectedBehavior.REJECT_COMMANDS).build());
    }

}
