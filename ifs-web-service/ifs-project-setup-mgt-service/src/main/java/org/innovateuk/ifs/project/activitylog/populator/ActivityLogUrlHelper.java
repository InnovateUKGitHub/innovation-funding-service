package org.innovateuk.ifs.project.activitylog.populator;

import org.innovateuk.ifs.activitylog.resource.ActivityLogResource;
import org.innovateuk.ifs.activitylog.resource.ActivityType;
import org.innovateuk.ifs.project.resource.ProjectResource;

import static java.lang.String.format;

public class ActivityLogUrlHelper {

    public static String url(ActivityLogResource log, ProjectResource project) {
        switch (log.getActivityType()) {
            case APPLICATION_SUBMITTED:
            case APPLICATION_REOPENED:
                return format("/management/competition/%d/application/%d", project.getCompetition(), project.getApplication());
            case APPLICATION_INTO_PROJECT_SETUP:
                return format("/project-setup-management/competition/%d/status/all?applicationSearchString=%d", project.getCompetition(), project.getApplication());
            case PROJECT_DETAILS_COMPLETE:
            case FINANCE_REVIEWER_ADDED:
            case MANAGED_OFFLINE:
            case COMPLETE_OFFLINE:
            case WITHDRAWN:
            case ON_HOLD:
            case RESUMED_FROM_ON_HOLD:
            case MARKED_PROJECT_AS_SUCCESSFUL:
            case MARKED_PROJECT_AS_UNSUCCESSFUL:
                return format("/project-setup-management/competition/%d/project/%d/details", project.getCompetition(), project.getId());
            case ORGANISATION_REMOVED:
            case ORGANISATION_ADDED:
            case PROJECT_MANAGER_NOMINATED:
            case FINANCE_CONTACT_NOMINATED:
                return format("/project-setup-management/competition/%d/project/%d/team", project.getCompetition(), project.getId());
            case DOCUMENT_UPLOADED:
            case DOCUMENT_APPROVED:
            case DOCUMENT_REJECTED:
                return format("/project-setup-management/project/%d/document/config/%d", project.getId(), log.getDocumentConfig());
            case MONITORING_OFFICER_ASSIGNED:
                return format("/project-setup-management/project/%d/monitoring-officer", project.getId());
            case BANK_DETAILS_SUBMITTED:
            case BANK_DETAILS_APPROVED:
            case BANK_DETAILS_EDITED:
                return format("/project-setup-management/project/%d/organisation/%d/review-bank-details", project.getId(), log.getOrganisation());
            case VIABILITY_APPROVED:
                return format("/project-setup-management/project/%d/finance-check/organisation/%d/viability", project.getId(), log.getOrganisation());
            case ELIGIBILITY_APPROVED:
                return format("/project-setup-management/project/%d/finance-check/organisation/%d/eligibility", project.getId(), log.getOrganisation());
            case VIABILITY_RESET:
                return format("/project-setup-management/project/%d/finance-check", project.getId());
            case ELIGIBILITY_RESET:
                return format("/project-setup-management/project/%d/finance-check", project.getId());
            case FINANCE_CHECKS_RESET:
                return format("/project-setup-management/project/%d/finance-check", project.getId());
            case FINANCE_QUERY:
            case FINANCE_QUERY_RESPONDED:
                return log.getQueryType() != null ? format("/project-setup-management/project/%d/finance-check/organisation/%d/query?query_section=%s", project.getId(), log.getOrganisation(), log.getQueryType().name()) : null;
            case SPEND_PROFILE_GENERATED:
                return format("/project-setup-management/project/%d/finance-check", project.getId());
            case SPEND_PROFILE_SENT:
            case SPEND_PROFILE_APPROVED:
            case SPEND_PROFILE_REJECTED:
                return format("/project-setup-management/project/%d/spend-profile/approval", project.getId());
            case SPEND_PROFILE_DELETED:
            case GRANT_OFFER_LETTER_UPLOADED:
            case GRANT_OFFER_LETTER_PUBLISHED:
            case GRANT_OFFER_LETTER_SIGNED:
            case GRANT_OFFER_LETTER_APPROVED:
            case GRANT_OFFER_LETTER_REJECTED:
                return format("/project-setup-management/project/%d/grant-offer-letter/send", project.getId());
            default:
                return null;
        }
    }

    /**
     * Determines whether the link is invalid once an organisation is removed.
     * @param log
     * @return
     */
    public static boolean linkInvalidIfOrganisationRemoved(ActivityLogResource log) {
        return log.getOrganisation() != null
                && log.getActivityType() != ActivityType.ORGANISATION_REMOVED
                && log.getActivityType() != ActivityType.ORGANISATION_ADDED;
    }
}
