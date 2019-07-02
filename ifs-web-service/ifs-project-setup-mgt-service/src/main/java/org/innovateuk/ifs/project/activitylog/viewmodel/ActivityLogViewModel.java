package org.innovateuk.ifs.project.activitylog.viewmodel;

import org.innovateuk.ifs.activitylog.resource.ActivityLogResource;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.user.resource.Role;

import java.util.List;

import static java.lang.String.format;

public class ActivityLogViewModel {

    private final long competitionId;
    private final long applicationId;
    private final long projectId;
    private final List<ActivityLogResource> activities;

    public ActivityLogViewModel(ProjectResource project, List<ActivityLogResource> activities) {
        this.competitionId = project.getCompetition();
        this.applicationId = project.getApplication();
        this.projectId = project.getId();
        this.activities = activities;
    }

    public List<ActivityLogResource> getActivities() {
        return activities;
    }

    public String userText(ActivityLogResource log) {
        if (log.getCreatedByRoles().stream().anyMatch(role -> Role.internalRoles().contains(role))) {
            return log.getCreatedByName() + ", " + log.getCreatedByRoles().iterator().next().getDisplayName();
        } else {
            return log.getCreatedByName();
        }
    }

    public String url(ActivityLogResource log) {
        switch (log.getActivityType()) {
            case APPLICATION_SUBMITTED:
                return format("/management/competition/%d/application/%d?origin=ACTIVITY_LOG", competitionId, applicationId);
            case APPLICATION_INTO_PROJECT_SETUP:
                return format("/project-setup-management/competition/%d/status/all", competitionId);
            case PROJECT_DETAILS_COMPLETE:
            case FINANCE_REVIEWER_ADDED:
            case MANAGED_OFFLINE:
            case COMPLETE_OFFLINE:
            case WITHDRAWN:
            case ON_HOLD:
            case RESUMED_FROM_ON_HOLD:
                return format("/project-setup-management/competition/%d/project/%d/details", competitionId, projectId);
            case PROJECT_MANAGER_NOMINATED:
            case FINANCE_CONTACT_NOMINATED:
                return format("/project-setup-management/competition/%d/project/%d/team", competitionId, projectId);
            case DOCUMENT_UPLOADED:
            case DOCUMENT_APPROVED:
                return format("/project-setup-management/project/%d/document/config/%d", projectId, log.getDocumentConfig());
            case MONITORING_OFFICER_ASSIGNED:
                return format("/project-setup-management/project/%d/monitoring-officer", projectId);
            case BANK_DETAILS_SUBMITTED:
            case BANK_DETAILS_APPROVED:
                return format("/project-setup-management/project/%d/organisation/%d/review-bank-details", projectId, log.getOrganisation());
            case VIABILITY_APPROVED:
                return format("/project-setup-management/project/%d/finance-check/organisation/%d/viability", projectId, log.getOrganisation());
            case ELIGIBILITY_APPROVED:
                return format("/project-setup-management/project/%d/finance-check/organisation/%d/eligibility", projectId, log.getOrganisation());
            case FINANCE_QUERY:
            case FINANCE_QUERY_RESPONDED:
                return format("/project-setup-management/project/%d/finance-check/organisation/%d/query?query_section=%s", projectId, log.getOrganisation(), log.getQueryType().name());
            case SPEND_PROFILE_GENERATED:
            case SPEND_PROFILE_EDIT:
            case SPEND_PROFILE_COMPLETE:
                return format("/project-setup-management/project/%d/finance-check", projectId);
            case SPEND_PROFILE_SENT:
            case SPEND_PROFILE_APPROVED:
                return format("/project-setup-management/project/%d/spend-profile/approval", projectId);
            case GRANT_OFFER_LETTER_UPLOADED:
            case GRANT_OFFER_LETTER_PUBLISHED:
            case GRANT_OFFER_LETTER_SIGNED:
            case GRANT_OFFER_LETTER_APPROVED:
                return format("/project-setup-management/project/%d/grant-offer-letter/send", projectId);
            default:
                return "";
        }
    }
}
