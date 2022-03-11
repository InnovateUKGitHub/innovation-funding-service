package org.innovateuk.ifs.starters.cache.cfg;

import org.innovateuk.ifs.IfsProfileConstants;
import org.springframework.boot.autoconfigure.AutoConfigurationImportFilter;
import org.springframework.boot.autoconfigure.AutoConfigurationMetadata;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.core.env.AbstractEnvironment;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.innovateuk.ifs.ProfileUtils.isProfileActive;

/**
 * Cleanest way I can see to disable redis configuration while it is still on the classpath.
 *
 * If STUBDEV or DEV profile is active do not allow RedisAutoConfiguration
 */
public class RedisAutoConfigurationExclusionFilter implements AutoConfigurationImportFilter {

    private static final Set<String> SHOULD_SKIP = new HashSet<>(
            Arrays.asList(RedisAutoConfiguration.class.getCanonicalName()));

    @Override
    public boolean[] match(String[] classNames, AutoConfigurationMetadata metadata) {
        boolean[] matches = new boolean[classNames.length];
        for(int i = 0; i < classNames.length; i++) {
            matches[i] = shouldSkip(classNames[i]);
        }
        return matches;
    }

    private boolean shouldSkip(String className) {
        if (isProfileActive(IfsProfileConstants.STUBDEV, IfsProfileConstants.DEV)) {
            return !SHOULD_SKIP.contains(className);
        }
        return false;
    }
}
