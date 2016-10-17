package com.worth.ifs.project.sections;

/**
 * An enum representing whether a section is accessible yet, or not required
 */
public enum SectionAccess {

    ACCESSIBLE,
    NOT_ACCESSIBLE,
    NOT_REQUIRED;

    public boolean isAccessible() {
        return this == ACCESSIBLE;
    }

    public boolean isNotAccessible() {
        return this == NOT_ACCESSIBLE;
    }

    public boolean isNotRequired() {
        return this == NOT_REQUIRED;
    }

    public boolean isAccessibleOrNotRequired() {
        return isAccessible() || isNotRequired();
    }
}
