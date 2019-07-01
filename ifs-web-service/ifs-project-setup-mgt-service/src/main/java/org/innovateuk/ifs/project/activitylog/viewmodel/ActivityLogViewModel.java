package org.innovateuk.ifs.project.activitylog.viewmodel;

import org.innovateuk.ifs.activitylog.resource.ActivityLogResource;

import java.util.List;

public class ActivityLogViewModel {

    private final List<ActivityLogResource> activities;

    public ActivityLogViewModel(List<ActivityLogResource> activities) {
        this.activities = activities;
    }

    public List<ActivityLogResource> getActivities() {
        return activities;
    }


    public String url(ActivityLogResource log) {
        switch (log.getActivityType()) {
            case APPLICATION_SUBMITTED:
                break;
            case APPLICATION_INTO_PROJECT_SETUP:
                break;
            case PROJECT_DETAILS_COMPLETE:
                break;
            case PROJECT_MANAGER_NOMINATED:
                break;
            case FINANCE_CONTACT_NOMINATED:
                break;
            case DOCUMENT_UPLOADED:
                break;
            case DOCUMENT_APPROVED:
                break;
            case MONITORING_OFFICER_ASSIGNED:
                break;
            case BANK_DETAILS_SUBMITTED:
                break;
            case BANK_DETAILS_APPROVED:
                break;
            case VIABILITY_APPROVED:
                break;
            case ELIGIBILITY_APPROVED:
                break;
            case FINANCE_QUERY:
                break;
            case FINANCE_QUERY_RESPONDED:
                break;
            case SPEND_PROFILE_GENERATED:
                break;
            case SPEND_PROFILE_EDIT:
                break;
            case SPEND_PROFILE_COMPLETE:
                break;
            case SPEND_PROFILE_SENT:
                break;
            case SPEND_PROFILE_APPROVED:
                break;
            case FINANCE_REVIEWER_ADDED:
                break;
            case GRANT_OFFER_LETTER_UPLOADED:
                break;
            case GRANT_OFFER_LETTER_PUBLISHED:
                break;
            case GRANT_OFFER_LETTER_SIGNED:
                break;
            case GRANT_OFFER_LETTER_APPROVED:
                break;
            case MANAGED_OFFLINE:
                break;
            case COMPLETE_OFFLINE:
                break;
            case WITHDRAWN:
                break;
            case ON_HOLD:
                break;
            case RESUMED_FROM_ON_HOLD:
                break;
        }
        return "";
    }
}
