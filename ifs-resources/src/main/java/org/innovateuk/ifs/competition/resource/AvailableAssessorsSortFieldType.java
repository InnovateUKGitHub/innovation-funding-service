package org.innovateuk.ifs.competition.resource;

public enum AvailableAssessorsSortFieldType {

    TITLE ("title"),
    SKILLS ("skills"),
    TOTAL_APPLICATIONS ("totalApplications"),
    ASSIGNED_APPLICATIONS ("assignedApplications"),
    SUBMITTED_APPLICATIONS ("submittedApplications");

    String value;

    AvailableAssessorsSortFieldType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static AvailableAssessorsSortFieldType fromString(String text) {
        if (text != null) {
            for (AvailableAssessorsSortFieldType field : AvailableAssessorsSortFieldType.values()) {
                if (text.equalsIgnoreCase(field.getValue())) {
                    return field;
                }
            }
        }
        return null;
    }
}
