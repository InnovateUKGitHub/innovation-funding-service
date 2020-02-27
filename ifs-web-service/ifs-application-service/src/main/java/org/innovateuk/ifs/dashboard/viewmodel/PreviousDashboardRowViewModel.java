package org.innovateuk.ifs.dashboard.viewmodel;

import org.innovateuk.ifs.applicant.resource.dashboard.DashboardPreviousRowResource;
import org.innovateuk.ifs.application.resource.ApplicationState;
import org.innovateuk.ifs.project.resource.ProjectState;

import java.time.LocalDate;

import static com.google.common.base.Strings.isNullOrEmpty;
import static org.innovateuk.ifs.application.resource.ApplicationState.*;

/**
 * View model for each application row in the 'Previous' section of the applicant dashboard.
 */
public class PreviousDashboardRowViewModel extends AbstractApplicantDashboardRowViewModel {

    private final ApplicationState applicationState;
    private final LocalDate startDate;
    private final ProjectState projectState;
    private final Long projectId;
    private final boolean leadApplicant;

    public PreviousDashboardRowViewModel(String title,
                                         long applicationId,
                                         Long projectId,
                                         String competitionTitle,
                                         ApplicationState applicationState,
                                         ProjectState projectState,
                                         LocalDate startDate,
                                         boolean leadApplicant) {
        super(title, applicationId, competitionTitle);
        this.applicationState = applicationState;
        this.projectState = projectState;
        this.projectId = projectId;
        this.startDate = startDate;
        this.leadApplicant = leadApplicant;
    }

    public PreviousDashboardRowViewModel(DashboardPreviousRowResource resource){
        super(resource.getTitle(), resource.getApplicationId(), resource.getCompetitionTitle());
        this.applicationState = resource.getApplicationState();
        this.projectState = resource.getProjectState();
        this.projectId = resource.getProjectId();
        this.startDate = resource.getStartDate();
        this.leadApplicant = resource.isLeadApplicant();
    }

    public ApplicationState getApplicationState() {
        return applicationState;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    private boolean hasProject() {
        return projectState != null;
    }

    public boolean isLeadApplicant() {
        return leadApplicant;
    }

    /* View logic */
    public boolean isRejected() {
        return REJECTED.equals(applicationState);
    }

    public boolean isApproved() {
        return APPROVED.equals(applicationState) && !hasProject();
    }

    public boolean isCreatedOrOpen() {
        return OPENED.equals(applicationState)
                ||  CREATED.equals(applicationState);
    }

    public boolean isInformedIneligible() {
        return INELIGIBLE_INFORMED.equals(applicationState);
    }

    public boolean isWithdrawn() {
        return hasProject() && projectState.isWithdrawn();
    }

    public boolean isLiveOrCompletedOffline() {
        return hasProject() && (projectState.isLive() || projectState.isCompletedOffline());
    }

    public boolean isUnsuccessful() {
        return hasProject() && projectState.isUnsuccessful();
    }

    public boolean canHideApplication() {
        return !leadApplicant && !submittedAndFinishedStates.contains(applicationState);
    }

    @Override
    public String getLinkUrl() {
        return hasProject()
                ? String.format("/project-setup/project/%d", projectId)
                : String.format("/application/%d/summary", getApplicationNumber());
    }

    @Override
    public String getTitle() {
        return !isNullOrEmpty(title) ? title : "Untitled application";
    }

}
