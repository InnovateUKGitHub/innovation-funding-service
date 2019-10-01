package org.innovateuk.ifs.sections;


/**
 * An enum representing the status of a section
 */
public enum SectionState {
    EMPTY(""),
    HOURGLASS("waiting"),
    FLAG("require-action"),
    TICK("complete"),
    CROSS("rejected");

    private final String status;

    SectionState(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

}