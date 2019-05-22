package org.innovateuk.ifs.project.status.populator;

import org.apache.commons.lang3.StringUtils;
import org.innovateuk.ifs.competition.service.CompetitionPostSubmissionRestService;
import org.innovateuk.ifs.project.status.resource.CompetitionProjectsStatusResource;
import org.innovateuk.ifs.project.status.resource.ProjectStatusResource;
import org.innovateuk.ifs.project.status.security.SetupSectionInternalUser;
import org.innovateuk.ifs.project.status.security.StatusPermission;
import org.innovateuk.ifs.project.status.service.StatusRestService;
import org.innovateuk.ifs.project.status.viewmodel.CompetitionStatusViewModel;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.util.CollectionFunctions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

import static org.innovateuk.ifs.user.resource.Role.PROJECT_FINANCE;

/**
 * This class represents a populated CompetitionStatusViewModel.
 */
@Component
public class CompetitionStatusViewModelPopulator {

    private CompetitionPostSubmissionRestService competitionPostSubmissionRestService;
    private StatusRestService statusRestService;

    private CompetitionStatusViewModelPopulator() {}

    @Autowired
    public CompetitionStatusViewModelPopulator(CompetitionPostSubmissionRestService competitionPostSubmissionRestService,
                                               StatusRestService statusRestService) {
        this.competitionPostSubmissionRestService = competitionPostSubmissionRestService;
        this.statusRestService = statusRestService;
    }

    public CompetitionStatusViewModel populate(UserResource user, Long competitionId, String applicationSearchString) {
        final boolean hasProjectFinanceRole = user.hasRole(PROJECT_FINANCE);
        long openQueryCount = hasProjectFinanceRole ? competitionPostSubmissionRestService.getCompetitionOpenQueriesCount(competitionId).getSuccess() : 0L;
        long pendingSpendProfilesCount = hasProjectFinanceRole ? competitionPostSubmissionRestService.countPendingSpendProfiles(competitionId).getSuccess() : 0;
        CompetitionProjectsStatusResource competitionProjectsStatus = statusRestService.getCompetitionStatus(competitionId, StringUtils.trim(applicationSearchString)).getSuccess();

        return new CompetitionStatusViewModel(competitionProjectsStatus, hasProjectFinanceRole, projectStatusPermissions(user, competitionProjectsStatus), openQueryCount, pendingSpendProfilesCount);
    }

    private Map<Long, StatusPermission> projectStatusPermissions(UserResource user, CompetitionProjectsStatusResource competitionProjectsStatus) {
        return CollectionFunctions.simpleToLinkedMap(competitionProjectsStatus.getProjectStatusResources(),
                ProjectStatusResource::getApplicationNumber,
                projectStatus -> projectStatusPermission(new SetupSectionInternalUser(projectStatus), user));
    }

    private StatusPermission projectStatusPermission(SetupSectionInternalUser internalUser, UserResource userResource) {
        return new StatusPermission(
                internalUser.canAccessCompaniesHouseSection().isAccessibleOrNotRequired(),
                internalUser.canAccessProjectDetailsSection(userResource).isAccessibleOrNotRequired(),
                internalUser.canAccessMonitoringOfficerSection(userResource).isAccessibleOrNotRequired(),
                internalUser.canAccessBankDetailsSection(userResource).isAccessibleOrNotRequired(),
                internalUser.canAccessFinanceChecksSection(userResource).isAccessibleOrNotRequired(),
                internalUser.canAccessSpendProfileSection(userResource).isAccessibleOrNotRequired(),
                internalUser.canAccessDocumentsSection(userResource).isAccessibleOrNotRequired(),
                internalUser.canAccessGrantOfferLetterSection(userResource).isAccessibleOrNotRequired(),
                internalUser.canAccessGrantOfferLetterSendSection(userResource).isAccessibleOrNotRequired(),
                internalUser.grantOfferLetterActivityStatus(userResource));
    }
}
