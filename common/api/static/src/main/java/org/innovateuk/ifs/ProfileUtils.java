package org.innovateuk.ifs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ProfileUtils {

    private static final String PROFILE_CONSTANT = "spring.profiles.active";
    private static final String PROFILE_CONSTANT_ENV = "SPRING_PROFILES_ACTIVE";

    public static boolean isProfileActive(String profile) {
        return getProfiles().stream().anyMatch(p -> p.equals(profile));
    }

    /**
     * Retrieve a list of active profiles from either of the possible ENV variables
     * spring.profiles.active or SPRING_PROFILES_ACTIVE
     * @return the list of profiles
     */
    public static List<String> getProfiles() {
        if (System.getenv().containsKey(PROFILE_CONSTANT) && !System.getenv(PROFILE_CONSTANT).isEmpty()) {
            return asList(System.getenv(PROFILE_CONSTANT));
        } else if (System.getenv().containsKey(PROFILE_CONSTANT_ENV) && !System.getenv(PROFILE_CONSTANT_ENV).isEmpty()) {
            return asList(System.getenv(PROFILE_CONSTANT_ENV));
        }
        return Collections.emptyList();
    }

    private static List<String> asList(String profiles) {
        ArrayList<String> profs = new ArrayList();
        for (String profile : profiles.split(",")) {
            profs.add(profile.trim());
        }
        return profs;
    }


}
