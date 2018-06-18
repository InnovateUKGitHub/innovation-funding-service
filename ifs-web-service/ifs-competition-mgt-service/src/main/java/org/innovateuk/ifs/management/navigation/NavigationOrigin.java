package org.innovateuk.ifs.management.navigation;

public enum NavigationOrigin {

    ALL_APPLICATIONS("/competition/{competitionId}/applications/all"),
    SUBMITTED_APPLICATIONS("/competition/{competitionId}/applications/submitted"),
    INELIGIBLE_APPLICATIONS("/competition/{competitionId}/applications/ineligible"),
    MANAGE_APPLICATIONS("/assessment/competition/{competitionId}/applications"),
    MANAGE_ASSESSORS("/assessment/competition/{competitionId}/assessors"),
    FUNDING_APPLICATIONS("/competition/{competitionId}/funding"),
    APPLICATION_PROGRESS("/assessment/competition/{competitionId}/application/{applicationId}/assessors"),
    MANAGE_ASSESSMENTS("/assessment/competition/{competitionId}"),
    ASSESSOR_PROGRESS("/assessment/competition/{competitionId}/assessors/{assessorId}"),
    PROJECT_SETUP_MANAGEMENT_STATUS("/project-setup-management/competition/{competitionId}/status"),
    UNSUCCESSFUL_APPLICATIONS("/competition/{competitionId}/applications/previous"),
    MANAGE_APPLICATIONS_PANEL("/assessment/panel/competition/{competitionId}/manage-applications"),
    INTERVIEW_PANEL_FIND("/assessment/interview/competition/{competitionId}/applications/find"),
    INTERVIEW_PANEL_INVITE("/assessment/interview/competition/{competitionId}/applications/invite"),
    INTERVIEW_PANEL_SEND("/assessment/interview/competition/{competitionId}/applications/invite/send"),
    INTERVIEW_PANEL_STATUS("/assessment/interview/competition/{competitionId}/applications/view-status"),
    INTERVIEW_PANEL_ALLOCATE("/assessment/interview/competition/{competitionId}/assessors/allocate-applications/{assessorId}"),
    INTERVIEW_APPLICATION_ALLOCATION("/assessment/interview/competition/{competitionId}/assessors/unallocated-applications/{assessorId}"),
    INTERVIEW_PANEL_ALLOCATED("/assessment/interview/competition/{competitionId}/assessors/allocated-applications/{assessorId}"),
    INTERVIEW_PANEL_VIEW_INVITE("/assessment/interview/competition/{competitionId}/applications/invite/{applicationId}/view");

    private String baseOriginUrl;

    NavigationOrigin(String baseOriginUrl) {
        this.baseOriginUrl = baseOriginUrl;
    }

    public String getBaseOriginUrl() {
        return baseOriginUrl;
    }
}
