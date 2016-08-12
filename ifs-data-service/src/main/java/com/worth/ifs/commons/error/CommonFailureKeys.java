package com.worth.ifs.commons.error;

import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

/**
 * A set of failure cases for Service code, including general catch-all errors and more specific use-case errors that potentially
 * span different services
 */
public enum CommonFailureKeys implements ErrorTemplate {

    /**
     * General
     */
    GENERAL_UNEXPECTED_ERROR("An unexpected error occurred", INTERNAL_SERVER_ERROR),
    GENERAL_NOT_FOUND("Unable to find entity", NOT_FOUND),
    GENERAL_INCORRECT_TYPE("Argument was of an incorrect type", BAD_REQUEST),
    GENERAL_FORBIDDEN("User is forbidden from performing requested action", FORBIDDEN),

    /**
     * Files
     */
    FILES_UNABLE_TO_FIND_FILE_ENTRY_ID_FROM_FILE("The file entry id could not be determined from the file", INTERNAL_SERVER_ERROR),
    FILES_UNABLE_TO_CREATE_FILE("The file could not be created", INTERNAL_SERVER_ERROR),
    FILES_NO_SUCH_FILE("The file could founs created", INTERNAL_SERVER_ERROR),
    FILES_UNABLE_TO_MOVE_FILE("The file could not be moved", INTERNAL_SERVER_ERROR),
    FILES_FILE_ALREADY_LINKED_TO_FORM_INPUT_RESPONSE("A file is already linked to this Form Input Response", CONFLICT),
    FILES_UNABLE_TO_UPDATE_FILE("The file could not be updated", INTERNAL_SERVER_ERROR),
    FILES_UNABLE_TO_DELETE_FILE("The file could not be deleted", INTERNAL_SERVER_ERROR),
    FILES_UNABLE_TO_CREATE_FOLDERS("Unable to create folders in order to store files", INTERNAL_SERVER_ERROR),
    FILES_DUPLICATE_FILE_CREATED("A matching file already exists", CONFLICT),
    FILES_DUPLICATE_FILE_MOVED("A matching file already exists", CONFLICT),
    FILES_MOVE_DESTINATION_EXIST_SOURCE_DOES_NOT("The destination exists and the source does not", CONFLICT),
    FILES_DUPLICATE_FILE_("The file has already been moved", CONFLICT),
    FILES_INCORRECTLY_REPORTED_MEDIA_TYPE("The actual file media type didn't match the reported media type", UNSUPPORTED_MEDIA_TYPE),
    FILES_INCORRECTLY_REPORTED_FILESIZE("The actual file size didn't match the reported file size", BAD_REQUEST),
    FILES_FILE_AWAITING_VIRUS_SCAN("The file is awaiting virus scanning", FORBIDDEN),
    FILES_FILE_QUARANTINED("The file has been quarantined by the virus scanner", FORBIDDEN),

    /**
     * Competitions
     */
    COMPETITION_NOT_EDITABLE("The competition is no longer editable", BAD_REQUEST),
    COMPETITION_NOT_OPEN("The competition this application belongs to is no longer open for application submissions", BAD_REQUEST),
    COMPETITION_NO_TEMPLATE("This competition type has no competition template available", CONFLICT),

    /**
     * Notifications
     */
    NOTIFICATIONS_UNABLE_TO_SEND_SINGLE("The notification could not be sent", INTERNAL_SERVER_ERROR),
    NOTIFICATIONS_UNABLE_TO_SEND_MULTIPLE("Unable to send the Notifications", INTERNAL_SERVER_ERROR),
    NOTIFICATIONS_UNABLE_TO_RENDER_TEMPLATE("Could not render Notification template", INTERNAL_SERVER_ERROR),

    /**
     * Emails
     */
    EMAILS_NOT_SENT_MULTIPLE("The emails could not be sent", INTERNAL_SERVER_ERROR),

    /**
     * Users
     */
    USERS_DUPLICATE_EMAIL_ADDRESS("This email address is already taken", CONFLICT),
    USERS_EMAIL_VERIFICATION_TOKEN_NOT_FOUND("E-mail verification token not found", NOT_FOUND),
    USERS_EMAIL_VERIFICATION_TOKEN_EXPIRED("E-mail verification token has expired", BAD_REQUEST),

    /**
     * Funding Panel
     */
    FUNDING_PANEL_DECISION_NOT_ALL_APPLICATIONS_REPRESENTED("Not all submitted applications are represented in the funding decision", BAD_REQUEST),
    FUNDING_PANEL_DECISION_NO_ASSESSOR_FEEDBACK_DATE_SET("An Assessor Feedback Date has not yet been set for this competition", BAD_REQUEST),
    FUNDING_PANEL_DECISION_WRONG_STATUS("competition not in correct status", BAD_REQUEST),

    /**
     * Project Setup
     */
    PROJECT_SETUP_DATE_MUST_START_ON_FIRST_DAY_OF_MONTH("The Project Start Date must start on the first day of the month", BAD_REQUEST),
    PROJECT_SETUP_DATE_MUST_BE_IN_THE_FUTURE("The Project Start Date must start in the future", BAD_REQUEST),
    PROJECT_SETUP_PROJECT_MANAGER_MUST_BE_LEAD_PARTNER("The Project Manager must be a member of the lead partner organisation", BAD_REQUEST),
    PROJECT_SETUP_FINANCE_CONTACT_MUST_BE_A_USER_ON_THE_PROJECT_FOR_THE_ORGANISATION("The organisation finance contact must be present on the project for the specified organisation", BAD_REQUEST),
    PROJECT_SETUP_FINANCE_CONTACT_MUST_BE_A_PARTNER_ON_THE_PROJECT_FOR_THE_ORGANISATION("The organisation finance contact must be a partner on the project for the specified organisation", BAD_REQUEST),
    PROJECT_SETUP_PROJECT_DETAILS_CANNOT_BE_SUBMITTED_IF_INCOMPLETE("All project details must be completed before submission", BAD_REQUEST),
    PROJECT_SETUP_OTHER_DOCUMENTS_CAN_ONLY_SUBMITTED_BY_PROJECT_MANAGER("Other documents can only be submitted by the project manager", BAD_REQUEST),
    PROJECT_SETUP_OTHER_DOCUMENTS_MUST_BE_UPLOADED_BEFORE_SUBMIT("Other documents must be uploaded before submit", BAD_REQUEST),
	PROJECT_SETUP_PROJECT_ID_IN_URL_MUST_MATCH_PROJECT_ID_IN_MONITORING_OFFICER_RESOURCE("The project id in the url must match the project id in the Monitoring Officer Resource request body", BAD_REQUEST),
    PROJECT_SETUP_PROJECT_DETAILS_CANNOT_BE_UPDATED_IF_ALREADY_SUBMITTED("Project details cannot be updated if they are already submitted", BAD_REQUEST),
    CANNOT_FIND_ORG_FOR_GIVEN_PROJECT_AND_USER("Cannot find organisation for given project and user", NOT_FOUND),

    /**
     * Project Bank details
     */
    BANK_DETAILS_CANNOT_BE_SUBMITTED_BEFORE_PROJECT_DETAILS("Project details must be submitted before bank details", BAD_REQUEST),
    BANK_DETAILS_CAN_ONLY_BE_SUBMITTED_ONCE("Bank details can only be submitted once", BAD_REQUEST),
    BANK_DETAILS_DONT_EXIST_FOR_GIVEN_PROJECT_AND_ORGANISATION("Bank details don't exist on project {0} for organisation {1}", NOT_FOUND),
    EXPERIAN_VALIDATION_FAILED("Bank details cannot be validated", BAD_REQUEST),
    EXPERIAN_VALIDATION_FAILED_WITH_INCORRECT_ACC_NO("Account number is incorrect, please check and try again", BAD_REQUEST),
    EXPERIAN_VALIDATION_FAILED_WITH_INCORRECT_BANK_DETAILS("Bank account details are incorrect, please check and try again", BAD_REQUEST),
    EXPERIAN_VERIFICATION_FAILED("Experian verification failed", BAD_REQUEST),

    /**
     * Project Monitoring Officer
     */
    PROJECT_SETUP_MONITORING_OFFICER_CANNOT_BE_ASSIGNED_UNTIL_PROJECT_DETAILS_SUBMITTED("A Monitoring Officer cannot be assigned to a Project until its Project Details have been submitted", BAD_REQUEST),

    /**
     * Assessment
     */
    ASSESSMENT_REJECTION_FAILED("Only assessments which are Open can be rejected.", BAD_REQUEST),
    ASSESSMENT_RECOMMENDATION_FAILED("Only assessments which are Open can be recommended.", BAD_REQUEST),

    /*
     * Forms
     */
    FORM_WORD_LIMIT_EXCEEDED("The form word limit has been exceeded",BAD_REQUEST)
    ;

    private ErrorTemplate errorTemplate;

    CommonFailureKeys(String errorMessage, HttpStatus category) {
        this.errorTemplate = new ErrorTemplateImpl(name(), errorMessage, category);
    }

    @Override
    public String getErrorKey() {
        return errorTemplate.getErrorKey();
    }

    @Override
    public String getErrorMessage() {
        return errorTemplate.getErrorMessage();
    }

    @Override
    public HttpStatus getCategory() {
        return errorTemplate.getCategory();
    }
}
