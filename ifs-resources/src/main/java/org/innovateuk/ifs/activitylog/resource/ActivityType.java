package org.innovateuk.ifs.activitylog.resource;

public enum ActivityType {

    APPLICATION_SUBMITTED,
    APPLICATION_REOPENED,
    APPLICATION_INTO_PROJECT_SETUP,
    PROJECT_DETAILS_COMPLETE,
    PROJECT_MANAGER_NOMINATED,
    FINANCE_CONTACT_NOMINATED,
    DOCUMENT_UPLOADED,
    DOCUMENT_APPROVED,
    DOCUMENT_REJECTED,
    MONITORING_OFFICER_ASSIGNED,
    BANK_DETAILS_SUBMITTED,
    BANK_DETAILS_EDITED,
    BANK_DETAILS_APPROVED,
    VIABILITY_APPROVED,
    ELIGIBILITY_APPROVED,
    VIABILITY_RESET,
    ELIGIBILITY_RESET,
    FINANCE_CHECKS_RESET,
    FINANCE_QUERY,
    FINANCE_QUERY_RESPONDED,
    //PROJECT_FINANCE_EDITED, Not yet implemented but exists in database enum.
    SPEND_PROFILE_GENERATED,
    SPEND_PROFILE_SENT,
    SPEND_PROFILE_APPROVED,
    SPEND_PROFILE_REJECTED,
    SPEND_PROFILE_DELETED,
    FINANCE_REVIEWER_ADDED,
    GRANT_OFFER_LETTER_UPLOADED,
    GRANT_OFFER_LETTER_PUBLISHED,
    GRANT_OFFER_LETTER_SIGNED,
    GRANT_OFFER_LETTER_APPROVED,
    GRANT_OFFER_LETTER_REJECTED,
    GRANT_OFFER_LETTER_DELETED,
    MANAGED_OFFLINE,
    COMPLETE_OFFLINE,
    WITHDRAWN,
    ON_HOLD,
    RESUMED_FROM_ON_HOLD,
    MARKED_PROJECT_AS_SUCCESSFUL,
    MARKED_PROJECT_AS_UNSUCCESSFUL,
    ORGANISATION_REMOVED,
    ORGANISATION_ADDED,
    GRANTS_PROJECT_MANAGER_INVITED,
    GRANTS_FINANCE_CONTACT_INVITED,
    GRANTS_MONITORING_OFFICER_INVITED,

    NONE //Will not be persisted to database. Only gives default value to annotation.

}
