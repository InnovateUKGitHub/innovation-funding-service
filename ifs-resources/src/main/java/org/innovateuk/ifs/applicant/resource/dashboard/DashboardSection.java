package org.innovateuk.ifs.applicant.resource.dashboard;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.Arrays.stream;
import static java.util.Collections.unmodifiableMap;
import static java.util.stream.Collectors.toMap;

/**
 * An enum representing the a logical grouping of applications and projects in the applicant dashboard into sections
 */
public enum DashboardSection {

    IN_SETUP(Constant.IN_SETUP),
    EU_GRANT_TRANSFER(Constant.EU_GRANT_TRANSFER),
    IN_PROGRESS(Constant.IN_PROGRESS),
    PREVIOUS(Constant.PREVIOUS);

    private static final Map<String,DashboardSection> ENUM_MAP;
    private String name;

    DashboardSection (String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    static {
        Map<String,DashboardSection> map = stream(values()).collect(toMap(DashboardSection::getName, instance -> instance, (a, b) -> b, ConcurrentHashMap::new));
        ENUM_MAP = unmodifiableMap(map);
    }

    public static DashboardSection get (String name) {
        return ENUM_MAP.get(name);
    }

    public static class Constant {
        private Constant() {}
        public static final String IN_SETUP = "in-setup";
        public static final String IN_PROGRESS = "in-progress";
        public static final String PREVIOUS = "previous";
    }}
