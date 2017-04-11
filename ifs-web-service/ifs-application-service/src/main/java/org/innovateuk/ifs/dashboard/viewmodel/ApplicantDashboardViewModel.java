package org.innovateuk.ifs.dashboard.viewmodel;

import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.ApplicationStatus;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.util.TimeZoneUtil;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

/**
 * Applicant dashboard view model
 */
public class ApplicantDashboardViewModel {
    private Map<Long, Integer> applicationProgress;
    private List<ApplicationResource> applicationsInProgress;
    private List<Long> applicationsAssigned;
    private List<ApplicationResource> applicationsFinished;
    private List<ProjectResource> projectsInSetup;
    private Map<Long, CompetitionResource> competitions;
    private Map<Long, ApplicationStatus> applicationStatuses;

    public ApplicantDashboardViewModel() {
    }

    public ApplicantDashboardViewModel(Map<Long, Integer> applicationProgress, List<ApplicationResource> applicationsInProgress,
                                       List<Long> applicationsAssigned, List<ApplicationResource> applicationsFinished,
                                       List<ProjectResource> projectsInSetup, Map<Long, CompetitionResource> competitions,
                                       Map<Long, ApplicationStatus> applicationStatuses) {
        this.applicationProgress = applicationProgress;
        this.applicationsInProgress = applicationsInProgress;
        this.applicationsAssigned = applicationsAssigned;
        this.applicationsFinished = applicationsFinished;
        this.projectsInSetup = projectsInSetup;
        this.competitions = competitions;
        this.applicationStatuses = applicationStatuses;
    }

    public Map<Long, Integer> getApplicationProgress() {
        return applicationProgress;
    }

    public List<ApplicationResource> getApplicationsInProgress() {
        return applicationsInProgress;
    }

    public List<Long> getApplicationsAssigned() {
        return applicationsAssigned;
    }

    public List<ApplicationResource> getApplicationsFinished() {
        return applicationsFinished;
    }

    public List<ProjectResource> getProjectsInSetup() {
        return projectsInSetup;
    }

    public Map<Long, CompetitionResource> getCompetitions() {
        return competitions;
    }

    public Map<Long, ApplicationStatus> getApplicationStatuses() {
        return applicationStatuses;
    }

    public boolean getProjectsInSetupNotEmpty() {
        return null != projectsInSetup && !projectsInSetup.isEmpty();
    }

    public boolean getApplicationsInProgressNotEmpty() {
        return null != applicationsInProgress && !applicationsInProgress.isEmpty();
    }

    public boolean getApplicationsInFinishedNotEmpty() {
        return null != applicationsFinished && !applicationsFinished.isEmpty();
    }

    public String getApplicationInProgressText() {
        if(applicationsInProgress.size() > 1) {
            return "Applications in progress";
        } else {
            return "Application in progress";
        }
    }

    public boolean applicationIsAssignedToMe(Long applicationId) {
        return applicationsAssigned.contains(applicationId);
    }

    public boolean applicationIsSubmitted(Long applicationId) {
        return ApplicationStatus.SUBMITTED.equals(getApplicationStatus(applicationId));
    }

    public boolean applicationIsCreated(Long applicationId) {
        return ApplicationStatus.CREATED.equals(getApplicationStatus(applicationId));
    }

    public boolean applicationIsApproved(Long applicationId) {
        return ApplicationStatus.APPROVED.equals(getApplicationStatus(applicationId));
    }

    public boolean applicationIsRejected(Long applicationId) {
        return ApplicationStatus.REJECTED.equals(getApplicationStatus(applicationId));
    }

    public boolean applicationIsOpen(Long applicationId) {
        return ApplicationStatus.OPEN.equals(getApplicationStatus(applicationId));
    }

    public boolean applicationIsCreatedOrOpen(Long applicationId) {
        return applicationIsCreated(applicationId) || applicationIsOpen(applicationId);
    }

    public ApplicationStatus getApplicationStatus(Long applicationId) {
        if(applicationStatuses.containsKey(applicationId)) {
            return applicationStatuses.get(applicationId);
        }

        return null;
    }

    public Long getHoursLeftBeforeSubmit(Long applicationId) {
        ZonedDateTime endDate = competitions.get(applicationId).getEndDate();

        return Duration.between(ZonedDateTime.now(), endDate).toHours();
    }

    public boolean isApplicationWithin24Hours(Long applicationId) {
        Long hoursLeft = getHoursLeftBeforeSubmit(applicationId);
        return hoursLeft >= 0 && hoursLeft < 24;
    }

    public boolean isClosingToday(Long applicationId) {
        ZonedDateTime endDate = competitions.get(applicationId).getEndDate();

        return TimeZoneUtil.toUkTimeZone(ZonedDateTime.now()).toLocalDate()
                .equals(TimeZoneUtil.toUkTimeZone(endDate).toLocalDate());
    }
}
