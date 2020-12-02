package org.innovateuk.ifs.project.yourorganisation.viewmodel;

import org.innovateuk.ifs.application.forms.sections.yourorganisation.viewmodel.YourOrganisationViewModel;
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
                                            String competitionName,
                                            boolean showStateAidAgreement,
                                            boolean showOrganisationSizeAlert,
                                            boolean h2020,
                                            long projectId,
                                            String projectName,
                                            long organisationId,
                                            boolean readOnly,
                                            boolean showHints,
                                            boolean procurementCompetition,
                                            UserResource loggedInUser,
                                            boolean isAllEligibilityAndViabilityInReview) {
        super(applicationId, competitionName, showStateAidAgreement, showOrganisationSizeAlert, h2020, procurementCompetition);
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
