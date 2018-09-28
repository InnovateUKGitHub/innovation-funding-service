package org.innovateuk.ifs.finance.security;

import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.repository.ApplicationRepository;
import org.innovateuk.ifs.commons.security.PermissionRule;
import org.innovateuk.ifs.commons.security.PermissionRules;
import org.innovateuk.ifs.finance.domain.ApplicationFinance;
import org.innovateuk.ifs.finance.domain.FinanceRow;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowItem;
import org.innovateuk.ifs.security.BasePermissionRules;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.innovateuk.ifs.security.SecurityRuleUtil.checkProcessRole;
import static org.innovateuk.ifs.user.resource.Role.*;
import static org.innovateuk.ifs.util.SecurityRuleUtil.isInternal;


/**
 * Permission rules for {@link FinanceRow} and {@link FinanceRowItem} for permissioning
 */
@Component
@PermissionRules
public class OverheadFilePermissionRules extends BasePermissionRules {

    @Autowired
    private ApplicationRepository applicationRepository;

    @PermissionRule(value = "CREATE_OVERHEAD_FILE", description = "The consortium can create the overhead file for their application and organisation")
    public boolean consortiumCanCreateAnOverheadsFileForTheirApplicationAndOrganisation(final FinanceRow overheads, final UserResource user) {
        return isCollaborator(overheads, user);
    }

    @PermissionRule(value = "UPDATE_OVERHEAD_FILE", description = "The consortium can update the overhead file for their application and organisation")
    public boolean consortiumCanUpdateAnOverheadsFileForTheirApplicationAndOrganisation(final FinanceRow overheads, final UserResource user) {
        return isCollaborator(overheads, user);
    }

    @PermissionRule(value = "DELETE_OVERHEAD_FILE", description = "The consortium can delete the overhead file for their application and organisation")
    public boolean consortiumCanDeleteAnOverheadsFileForTheirApplicationAndOrganisation(final FinanceRow overheads, final UserResource user) {
        return isCollaborator(overheads, user);
    }

    @PermissionRule(value = "READ_OVERHEAD_DETAILS", description = "The consortium can read the overhead file details for their application and organisation")
    public boolean consortiumCanReadDetailsAnOverheadsFileForTheirApplicationAndOrganisation(final FinanceRow overheads, final UserResource user) {
        return isCollaborator(overheads, user);
    }

    @PermissionRule(value = "READ_OVERHEAD_DETAILS", description = "The internal user can read the overhead file details for any application and organisation")
    public boolean internalUserCanReadDetailsOfAnOverheadsFileForTheirApplicationAndOrganisation(final FinanceRow overheads, final UserResource user) {
        return isInternal(user);
    }

    @PermissionRule(value = "READ_OVERHEAD_CONTENTS", description = "Support and Ifs Admin user can read the overhead file contents for any unsubmitted application and organisation")
    public boolean supportAndIfsAdminCanReadContentsOfAnOverheadsFileForANotSubmittedApplication(final FinanceRow overheads, final UserResource user) {
        return isDownloadableBeforeSubmission(overheads, user);
    }

    @PermissionRule(value = "READ_OVERHEAD_CONTENTS", description = "Internal users can read the overhead file contents for any submitted application and organisation")
    public boolean internalUsersCanReadContentsOfAnOverheadsFileForASubmittedApplication(final FinanceRow overheads, final UserResource user) {
        return isDownloadableAfterSubmission(overheads, user);
    }

    @PermissionRule(value = "READ_OVERHEAD_CONTENTS", description = "The consortium can read the overhead file contents for their application and organisation")
    public boolean consortiumCanReadContentsOfAnOverheadsFileForTheirApplicationAndOrganisation(final FinanceRow overheads, final UserResource user) {
        return isCollaborator(overheads, user);
    }

    private boolean isCollaborator(final FinanceRow overheads, final UserResource user) {
        final ApplicationFinance applicationFinance = (ApplicationFinance) overheads.getTarget();
        final Long applicationId = applicationFinance.getApplication().getId();
        final Long organisationId = applicationFinance.getOrganisation().getId();
        final boolean isLead = checkProcessRole(user, applicationId, organisationId, LEADAPPLICANT, processRoleRepository);
        final boolean isCollaborator = checkProcessRole(user, applicationId, organisationId, COLLABORATOR, processRoleRepository);
        return isLead || isCollaborator;
    }

    private boolean isDownloadableBeforeSubmission(final FinanceRow overheads, final UserResource user) {
        boolean isSupportOrAdmin = user.hasAnyRoles(SUPPORT, IFS_ADMINISTRATOR);
        return !isApplicationSubmitted(overheads) && isSupportOrAdmin;
    }

    private boolean isDownloadableAfterSubmission(final FinanceRow overheads, final UserResource user) {
        return isApplicationSubmitted(overheads) && isInternal(user);
    }

    private boolean isApplicationSubmitted(final FinanceRow overheads) {
        final ApplicationFinance applicationFinance = (ApplicationFinance) overheads.getTarget();
        final Long applicationId = applicationFinance.getApplication().getId();
        Application application = applicationRepository.findById(applicationId);

        return application.isSubmitted();
    }

}
