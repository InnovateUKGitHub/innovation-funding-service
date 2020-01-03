package org.innovateuk.ifs.sections;


/**
 * An enum representing the status of a section
 */
public enum SectionStatus {
    EMPTY(""),
    HOURGLASS("waiting"),
    FLAG("require-action"),
    LEAD_ACTION_FLAG("lead-action-required"),
    TICK("complete"),
    CROSS("rejected");

    private final String status;

    SectionStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

}