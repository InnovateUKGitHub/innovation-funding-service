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

    public ProjectYourOrganisationViewModel(boolean showStateAidAgreement,
                                            boolean fundingSectionComplete,
                                            boolean h2020,
                                            long projectId,
                                            String projectName,
                                            long organisationId,
                                            boolean readOnly,
                                            boolean showHints,
                                            UserResource loggedInUser) {
        super(showStateAidAgreement, fundingSectionComplete, h2020);
        this.projectId = projectId;
        this.projectName = projectName;
        this.organisationId = organisationId;
        this.readOnly = readOnly;
        this.showHints = showHints;
        this.loggedInUser = loggedInUser;
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

//    public boolean isAllEligibilityAndViabilityInReview() {
//        return isAllEligibilityAndViabilityInReview;
//    }

    public boolean isAllowedToEditOrganisationSize() {
        return getLoggedInUser().hasAnyRoles(Role.IFS_ADMINISTRATOR, Role.PROJECT_FINANCE);
    }
}
