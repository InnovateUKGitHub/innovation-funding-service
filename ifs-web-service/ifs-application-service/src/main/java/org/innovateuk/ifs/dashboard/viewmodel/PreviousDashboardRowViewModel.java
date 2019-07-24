package org.innovateuk.ifs.dashboard.viewmodel;

import org.innovateuk.ifs.applicant.resource.dashboard.DashboardPreviousRowResource;
import org.innovateuk.ifs.application.resource.ApplicationState;
import org.innovateuk.ifs.project.resource.ProjectState;

import java.time.LocalDate;
import java.util.Optional;

import static com.google.common.base.Strings.isNullOrEmpty;
import static org.innovateuk.ifs.application.resource.ApplicationState.*;

/**
 * View model for each application row in the 'Previous' section of the applicant dashboard.
 */
public class PreviousDashboardRowViewModel extends AbstractApplicantDashboardRowViewModel {

    private final ApplicationState applicationState;
    private final LocalDate startDate;
    private final Optional<ProjectState> projectState;
    private final Long projectId;

    public PreviousDashboardRowViewModel(String title,
                                         long applicationId,
                                         Long projectId,
                                         String competitionTitle,
                                         ApplicationState applicationState,
                                         Optional<ProjectState> projectState,
                                         LocalDate startDate) {
        super(title, applicationId, competitionTitle);
        this.applicationState = applicationState;
        this.projectState = projectState;
        this.projectId = projectId;
        this.startDate = startDate;
    }

    public PreviousDashboardRowViewModel(DashboardPreviousRowResource resource){
        super(resource.getTitle(), resource.getApplicationId(), resource.getCompetitionTitle());
        this.applicationState = resource.getApplicationState();
        this.projectState = resource.getProjectState();
        this.projectId = resource.getProjectId();
        this.startDate = resource.getStartDate();
    }

    public ApplicationState getApplicationState() {
        return applicationState;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    private boolean hasProject() {
        return projectState.isPresent();
    }

    /* View logic */
    public boolean isRejected() {
        return REJECTED.equals(applicationState);
    }

    public boolean isApproved() {
        return APPROVED.equals(applicationState);
    }

    public boolean isCreatedOrOpen() {
        return OPENED.equals(applicationState)
                ||  CREATED.equals(applicationState);
    }

    public boolean isInformedIneligible() {
        return INELIGIBLE_INFORMED.equals(applicationState);
    }

    public boolean isWithdrawn() {
        return hasProject() && projectState.get().isWithdrawn();
    }

    public boolean isLive() {
        return hasProject() && (projectState.get().isLive() || projectState.get().isCompletedOffline());
    }

    @Override
    public String getLinkUrl() {
        return hasProject() ?
                String.format("/project-setup/project/%s", projectId) :
                String.format("/application/%s/summary", getApplicationNumber());
    }

    @Override
    public String getTitle() {
        return !isNullOrEmpty(title) ? title : "Untitled application";
    }

}
