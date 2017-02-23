package org.innovateuk.ifs.competition.resource;

public enum AvailableAssessorsSortFieldType {

    TITLE ("Assessor name"),
    SKILLS ("Skill areas"),
    TOTAL_APPLICATIONS ("Total applications"),
    ASSIGNED_APPLICATIONS ("Assigned applications"),
    SUBMITTED_APPLICATIONS ("Submitted applications");

    String label;

    AvailableAssessorsSortFieldType(String value) {
        this.label = value;
    }

    public String getLabel() {
        return label;
    }
}
