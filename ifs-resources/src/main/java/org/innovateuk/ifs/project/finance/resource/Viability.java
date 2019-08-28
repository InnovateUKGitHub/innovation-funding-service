package org.innovateuk.ifs.project.finance.resource;

/**
 * Enumeration for the possible Viability values.
 */
public enum Viability {
    REVIEW,
    APPROVED,
    NOT_APPLICABLE,
    COMPLETED_OFFLINE;

    public boolean isNotApplicable() {
        return this == NOT_APPLICABLE;
    }
}