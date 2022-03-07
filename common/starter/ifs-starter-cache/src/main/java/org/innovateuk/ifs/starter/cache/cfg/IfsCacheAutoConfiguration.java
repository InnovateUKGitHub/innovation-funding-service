package org.innovateuk.ifs.starter.cache.cfg;

@Configuration
@EnableCaching
@AutoConfigureBefore(CacheAutoConfiguration.class)
public class IfsCacheAutoConfiguration {
}
