package com.worth.ifs.project.sections;


/**
 * An enum representing the status of a section
 */
public enum SectionStatus {
    EMPTY(""),
    HOURGLASS("waiting"),
    FLAG("require-action"),
    TICK("complete"),
    CROSS("rejected");

    private final String status;

    SectionStatus(String value) {
        status = value;
    }

    public String getSectionStatus() { return this.status; }
}
