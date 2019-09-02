package org.innovateuk.ifs.project.finance.resource;

import java.util.EnumSet;

/**
 * Enumeration for the possible Viability values.
 */
public enum Viability {
    REVIEW,
    APPROVED,
    NOT_APPLICABLE,
    COMPLETED_OFFLINE;

    private static final EnumSet<Viability> NOT_APPLICABLE_STATES = EnumSet.of(NOT_APPLICABLE, COMPLETED_OFFLINE);

    public boolean isNotApplicable() {
        return NOT_APPLICABLE_STATES.contains(this);
    }
}