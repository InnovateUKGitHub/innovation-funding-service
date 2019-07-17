package org.innovateuk.ifs.project.status.security;

import org.innovateuk.ifs.project.status.resource.CompetitionProjectsStatusResource;
import org.innovateuk.ifs.project.status.resource.ProjectStatusResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.util.CollectionFunctions;

import java.util.Map;

public class StatusHelper {

    public static Map<Long, StatusPermission> projectStatusPermissions(UserResource user, CompetitionProjectsStatusResource competitionProjectsStatus) {
        return CollectionFunctions.simpleToLinkedMap(competitionProjectsStatus.getProjectStatusResources(),
                ProjectStatusResource::getApplicationNumber,
                projectStatus -> projectStatusPermission(new SetupSectionInternalUser(projectStatus), user));
    }

    private static StatusPermission projectStatusPermission(SetupSectionInternalUser internalUser, UserResource userResource) {
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
