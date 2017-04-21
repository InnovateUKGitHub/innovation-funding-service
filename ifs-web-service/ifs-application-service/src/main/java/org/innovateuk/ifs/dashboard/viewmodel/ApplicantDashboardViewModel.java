package org.innovateuk.ifs.dashboard.viewmodel;

import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.ApplicationState;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.util.TimeZoneUtil;

import java.time.Duration;
import java.time.LocalDate;
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
    private Map<Long, ApplicationState> applicationStates;

    public ApplicantDashboardViewModel() {
    }

    public ApplicantDashboardViewModel(Map<Long, Integer> applicationProgress, List<ApplicationResource> applicationsInProgress,
                                       List<Long> applicationsAssigned, List<ApplicationResource> applicationsFinished,
                                       List<ProjectResource> projectsInSetup, Map<Long, CompetitionResource> competitions,
                                       Map<Long, ApplicationState> applicationStates) {
        this.applicationProgress = applicationProgress;
        this.applicationsInProgress = applicationsInProgress;
        this.applicationsAssigned = applicationsAssigned;
        this.applicationsFinished = applicationsFinished;
        this.projectsInSetup = projectsInSetup;
        this.competitions = competitions;
        this.applicationStates = applicationStates;
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

    public Map<Long, ApplicationState> getApplicationStates() {
        return applicationStates;
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
        return ApplicationState.SUBMITTED.equals(getApplicationState(applicationId)) ||
                ApplicationState.INELIGIBLE.equals(getApplicationState(applicationId));
    }

    public boolean applicationIsInformedIneligible(Long applicationId) {
        return ApplicationState.INELIGIBLE_INFORMED.equals(getApplicationState(applicationId));
    }

    public boolean applicationIsCreated(Long applicationId) {
        return ApplicationState.CREATED.equals(getApplicationState(applicationId));
    }

    public boolean applicationIsApproved(Long applicationId) {
        return ApplicationState.APPROVED.equals(getApplicationState(applicationId));
    }

    public boolean applicationIsRejected(Long applicationId) {
        return ApplicationState.REJECTED.equals(getApplicationState(applicationId));
    }

    public boolean applicationIsOpen(Long applicationId) {
        return ApplicationState.OPEN.equals(getApplicationState(applicationId));
    }

    public boolean applicationIsCreatedOrOpen(Long applicationId) {
        return applicationIsCreated(applicationId) || applicationIsOpen(applicationId);
    }

    public ApplicationState getApplicationState(Long applicationId) {
        if(applicationStates.containsKey(applicationId)) {
            return applicationStates.get(applicationId);
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
        LocalDate endDay = TimeZoneUtil.toUkTimeZone(endDate).toLocalDate();
        LocalDate today = TimeZoneUtil.toUkTimeZone(ZonedDateTime.now()).toLocalDate();

        return today.equals(endDay);
    }
}
