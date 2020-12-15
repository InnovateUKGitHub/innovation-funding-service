package org.innovateuk.ifs.project.yourorganisation.viewmodel;

import org.innovateuk.ifs.application.forms.sections.yourorganisation.viewmodel.YourOrganisationViewModel;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserResource;

public class ProjectYourOrganisationViewModel extends YourOrganisationViewModel {
    private final long projectId;
    private final String projectName;
    private final long organisationId;
    private final boolean readOnly;
    private final boolean showHints;
    private final UserResource loggedInUser;
    private final boolean isAllEligibilityAndViabilityInReview;

    public ProjectYourOrganisationViewModel(long applicationId,
                                            CompetitionResource competitionResource,
                                            boolean showStateAidAgreement,
                                            boolean showOrganisationSizeAlert,
                                            long projectId,
                                            String projectName,
                                            long organisationId,
                                            boolean readOnly,
                                            boolean showHints,
                                            UserResource loggedInUser,
                                            boolean isAllEligibilityAndViabilityInReview) {
        super(applicationId, competitionResource, showStateAidAgreement, showOrganisationSizeAlert);
        this.projectId = projectId;
        this.projectName = projectName;
        this.organisationId = organisationId;
        this.readOnly = readOnly;
        this.showHints = showHints;
        this.loggedInUser = loggedInUser;
        this.isAllEligibilityAndViabilityInReview = isAllEligibilityAndViabilityInReview;
    }

    public long getProjectId() {
        return projectId;
    }

    public String getProjectName() {
        return projectName;
    }

    public long getOrganisationId() {
        return organisationId;
    }

    public boolean isShowHints() {
        return showHints;
    }

    public boolean isReadOnly() {
        return readOnly;
    }

    public UserResource getLoggedInUser() {
        return loggedInUser;
    }

    public boolean isAllowedToEditOrganisationSize() {
        return getLoggedInUser().hasRole(Role.PROJECT_FINANCE) && isAllEligibilityAndViabilityInReview;
    }
}
