package org.innovateuk.ifs.competition.resource;

public enum AvailableApplicationsSortFieldType {
    APP_NUMBER ("Application number"),
    TITLE ("Title"),
    LEAD_ORG ("Lead organisation"),
    ASSIGNED_APPLICATIONS ("Assigned applications"),
    ACCEPTED_APPLICATIONS ("Accepted applications"),
    SUBMITTED_APPLICATIONS ("Submitted applications");

    String label;

    AvailableApplicationsSortFieldType(String value) {
        this.label = value;
    }

    public String getLabel() {
        return label;
    }
}
