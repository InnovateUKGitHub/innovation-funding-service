package org.innovateuk.ifs.starters.cache.cfg;


import io.lettuce.core.ClientOptions;
import io.lettuce.core.ClientOptions.DisconnectedBehavior;
import io.lettuce.core.cluster.ClusterClientOptions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.redis.LettuceClientConfigurationBuilderCustomizer;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.cache.Cache;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.cache.interceptor.SimpleCacheErrorHandler;
import org.springframework.context.annotation.Bean;

import javax.annotation.PostConstruct;

@Slf4j
public class RedisConfiguration {//extends CachingConfigurerSupport {
//
//    @Autowired
//    private RedisProperties redisProperties;

    /*
        Modify the redis properties. We set the redis connection with env variables. We want to use env variables
        to configure either an standalone or cluster configuration. The default autoconfigurer will assume we're using
        a cluster configuration if the cluster nodes property is defined (Even if its empty!).

        Here we are setting the cluster configuration to be null if the nodes property is empty.
     */
//    @PostConstruct
//    public void redisConfiguration() {
//        if (redisProperties.getCluster() != null && redisProperties.getCluster().getNodes().isEmpty()) {
//            redisProperties.setCluster(null);
//        }
//    }

//    @Bean
//    public LettuceClientConfigurationBuilderCustomizer lettuceClientConfigurationBuilderCustomizer() {
//        final ClientOptions.Builder options;
//        if (redisProperties.getCluster() != null) {
//            options = ClusterClientOptions.builder()
//                    .validateClusterNodeMembership(false);
//        } else {
//            options = ClientOptions.builder();
//        }
//        return builder -> builder.clientOptions(options
//                .disconnectedBehavior(DisconnectedBehavior.REJECT_COMMANDS).build());
//    }

}
