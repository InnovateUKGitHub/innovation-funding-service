package org.innovateuk.ifs.starter.common.util;

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.core.env.ConfigurableEnvironment;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ProfileUtils {

    /**
     * Builds the ENV equivalent for enabling spring profiles.
     *
     * i.e. SPRING_PROFILES_ACTIVE=one,two,three
     *
     * @param profiles the active profiles
     * @return the env variable representation
     */
    public static String activeProfilesString(String... profiles) {
        return activeProfilesString(Arrays.asList(profiles));
    }

    public static String activeProfilesString(List<String> profiles) {
        return AbstractEnvironment.ACTIVE_PROFILES_PROPERTY_NAME
                + "=" +
                profiles.stream().collect(Collectors.joining(","));
    }

    /**
     * Are any of the profiles specified active in the current spring environment.
     * @param environment the spring environment
     * @param profiles the profiles to match
     * @return true if a one or more matches are found
     */
    public static boolean profileMatches(ConfigurableEnvironment environment, List<String> profiles) {
        for (String profile : environment.getActiveProfiles()) {
            if (profiles.contains(profile)) {
                return true;
            }
        }
        return false;
    }

    public static boolean profileMatches(ConfigurableEnvironment environment, String... profiles) {
        return profileMatches(environment, Arrays.asList(profiles));
    }

    public static boolean profileMatches(ConfigurableApplicationContext applicationContext, List<String> profiles) {
        return profileMatches(applicationContext.getEnvironment(), profiles);
    }

    public static boolean profileMatches(ConfigurableApplicationContext applicationContext, String... profiles) {
        return profileMatches(applicationContext.getEnvironment(), Arrays.asList(profiles));
    }
}
