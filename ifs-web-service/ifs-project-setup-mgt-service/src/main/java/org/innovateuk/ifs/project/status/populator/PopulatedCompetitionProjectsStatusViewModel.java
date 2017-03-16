package org.innovateuk.ifs.project.status.populator;

import org.innovateuk.ifs.project.sections.ProjectSetupSectionInternalUser;
import org.innovateuk.ifs.project.status.controller.ProjectStatusPermission;
import org.innovateuk.ifs.project.status.resource.CompetitionProjectsStatusResource;
import org.innovateuk.ifs.project.status.resource.ProjectStatusResource;
import org.innovateuk.ifs.project.status.viewmodel.CompetitionProjectStatusViewModel;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.util.CollectionFunctions;

import java.util.List;
import java.util.Map;

import static org.innovateuk.ifs.user.resource.UserRoleType.PROJECT_FINANCE;

/**
 * This class represents a populated CompetitionProjectStatusViewModel.
 */
public class PopulatedCompetitionProjectsStatusViewModel {
    private final UserResource user;
    private final CompetitionProjectsStatusResource competitionProjectsStatus;
    private final CompetitionProjectStatusViewModel viewModel;

    public PopulatedCompetitionProjectsStatusViewModel(CompetitionProjectsStatusResource competitionProjectsStatus, UserResource user) {
        this.user = user;
        this.competitionProjectsStatus = competitionProjectsStatus;
        final boolean canExportBankDetails = user.hasRole(PROJECT_FINANCE);
        this.viewModel = new CompetitionProjectStatusViewModel(competitionProjectsStatus, canExportBankDetails, projectStatusPermissions());
    }

    public CompetitionProjectStatusViewModel get() {
        return viewModel;
    }

    private Map<Long, ProjectStatusPermission> projectStatusPermissions() {
        return projectStatusPermissions(user, competitionProjectsStatus.getProjectStatusResources());
    }

    private Map<Long, ProjectStatusPermission> projectStatusPermissions(UserResource user, List<ProjectStatusResource> projectStatuses) {
        return CollectionFunctions.simpleToLinkedMap(projectStatuses,
                ProjectStatusResource::getApplicationNumber,
                projectStatus -> projectStatusPermission(new ProjectSetupSectionInternalUser(projectStatus), user));
    }

    private ProjectStatusPermission projectStatusPermission(ProjectSetupSectionInternalUser internalUser, UserResource userResource) {
        return new ProjectStatusPermission(
                internalUser.canAccessCompaniesHouseSection(userResource).isAccessibleOrNotRequired(),
                internalUser.canAccessProjectDetailsSection(userResource).isAccessibleOrNotRequired(),
                internalUser.canAccessMonitoringOfficerSection(userResource).isAccessibleOrNotRequired(),
                internalUser.canAccessBankDetailsSection(userResource).isAccessibleOrNotRequired(),
                internalUser.canAccessFinanceChecksSection(userResource).isAccessibleOrNotRequired(),
                internalUser.canAccessSpendProfileSection(userResource).isAccessibleOrNotRequired(),
                internalUser.canAccessOtherDocumentsSection(userResource).isAccessibleOrNotRequired(),
                internalUser.canAccessGrantOfferLetterSection(userResource).isAccessibleOrNotRequired(),
                internalUser.canAccessGrantOfferLetterSendSection(userResource).isAccessibleOrNotRequired(),
                internalUser.grantOfferLetterActivityStatus(userResource));
    }
}
