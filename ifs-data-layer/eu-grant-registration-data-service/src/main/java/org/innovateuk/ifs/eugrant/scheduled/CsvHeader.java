package org.innovateuk.ifs.eugrant.scheduled;

/**
 * TODO DW - document this class
 */
enum CsvHeader {

    ORGANISATION_TYPE("Organisation type"),
    ORGANISATION_NAME("Organisation name"),
    COMPANIES_HOUSE_REGISTRATION_NUMBER("Registration number"),
    CONTACT_FULL_NAME("Full name"),
    CONTACT_JOB_TITLE("Job title"),
    CONTACT_EMAIL_ADDRESS("Email"),
    CONTACT_TELEPHONE_NUMBER("Telephone"),
    GRANT_AGREEMENT_NUMBER("Grant agreement number"),
    PIC("Participant identification code"),
    ACTION_TYPE("Type of action"),
    PROJECT_NAME("Project name"),
    PROJECT_START_DATE("Project start date"),
    PROJECT_END_DATE("Project end date"),
    PROJECT_EU_FUNDING_CONTRIBUTION("EU funding contribution"),
    PROJECT_COORDINATOR("Is your organisation the project co-ordinator");

    private String headerText;

    CsvHeader(String headerText) {
        this.headerText = headerText;
    }

    public String getHeaderText() {
        return headerText;
    }
}