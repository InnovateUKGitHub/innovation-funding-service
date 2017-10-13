package org.innovateuk.ifs.project.status.populator;

import org.innovateuk.ifs.project.status.security.SetupSectionInternalUser;
import org.innovateuk.ifs.project.status.security.StatusPermission;
import org.innovateuk.ifs.project.status.resource.CompetitionProjectsStatusResource;
import org.innovateuk.ifs.project.status.resource.ProjectStatusResource;
import org.innovateuk.ifs.project.status.viewmodel.CompetitionStatusViewModel;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.util.CollectionFunctions;

import java.util.List;
import java.util.Map;

import static org.innovateuk.ifs.user.resource.UserRoleType.PROJECT_FINANCE;

/**
 * This class represents a populated CompetitionStatusViewModel.
 */
public class PopulatedCompetitionStatusViewModel {
    private final UserResource user;
    private final CompetitionProjectsStatusResource competitionProjectsStatus;
    private final CompetitionStatusViewModel viewModel;

    public PopulatedCompetitionStatusViewModel(CompetitionProjectsStatusResource competitionProjectsStatus, UserResource user, long openQueryCount) {
        this.user = user;
        this.competitionProjectsStatus = competitionProjectsStatus;
        final boolean canExportBankDetails = user.hasRole(PROJECT_FINANCE);
        this.viewModel = new CompetitionStatusViewModel(competitionProjectsStatus, canExportBankDetails, projectStatusPermissions(), openQueryCount);
    }

    public CompetitionStatusViewModel get() {
        return viewModel;
    }

    private Map<Long, StatusPermission> projectStatusPermissions() {
        return projectStatusPermissions(user, competitionProjectsStatus.getProjectStatusResources());
    }

    private Map<Long, StatusPermission> projectStatusPermissions(UserResource user, List<ProjectStatusResource> projectStatuses) {
        return CollectionFunctions.simpleToLinkedMap(projectStatuses,
                ProjectStatusResource::getApplicationNumber,
                projectStatus -> projectStatusPermission(new SetupSectionInternalUser(projectStatus), user));
    }

    private StatusPermission projectStatusPermission(SetupSectionInternalUser internalUser, UserResource userResource) {
        return new StatusPermission(
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
