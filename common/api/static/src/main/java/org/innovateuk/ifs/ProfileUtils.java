package org.innovateuk.ifs;

import java.util.*;
import java.util.stream.Stream;

public class ProfileUtils {

    protected static final String PROFILE_CONSTANT = "spring.profiles.active";
    protected static final String PROFILE_CONSTANT_ENV = "SPRING_PROFILES_ACTIVE";

    /**
     * Is any arg profile active?
     * @param profiles list of profiles to match
     * @return true if any supplied profiles are active
     */
    public static boolean isProfileActive(String... profiles) {
        return !Collections.disjoint(Arrays.asList(profiles), getProfiles());
    }

    /**
     * Retrieve a list of active profiles from either of the possible ENV variables
     * spring.profiles.active or SPRING_PROFILES_ACTIVE.
     * This can run before context init so supports the merging of results from both sources
     * as we cannot guarantee values are exactly the same at point of call.
     * @return the list of profiles
     */
    public static Set<String> getProfiles() {
        Set<String> allProfiles = new HashSet<>();
        if (System.getenv().containsKey(PROFILE_CONSTANT) && !System.getenv(PROFILE_CONSTANT).isEmpty()) {
            allProfiles.addAll(asList(System.getenv(PROFILE_CONSTANT)));
        }
        if (System.getenv().containsKey(PROFILE_CONSTANT_ENV) && !System.getenv(PROFILE_CONSTANT_ENV).isEmpty()) {
            allProfiles.addAll(asList(System.getenv(PROFILE_CONSTANT_ENV)));
        }
        return allProfiles;
    }

    private static List<String> asList(String profiles) {
        List<String> profs = new ArrayList<>();
        for (String profile : profiles.split(",")) {
            profs.add(profile.trim());
        }
        return profs;
    }


}
