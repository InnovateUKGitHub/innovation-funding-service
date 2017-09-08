package org.innovateuk.ifs.finance.security;

import org.innovateuk.ifs.commons.security.PermissionRule;
import org.innovateuk.ifs.commons.security.PermissionRules;
import org.innovateuk.ifs.finance.domain.ApplicationFinance;
import org.innovateuk.ifs.finance.domain.FinanceRow;
import org.innovateuk.ifs.finance.repository.ApplicationFinanceRowRepository;
import org.innovateuk.ifs.finance.resource.FinanceRowMetaValueResource;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowItem;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.security.BasePermissionRules;
import org.innovateuk.ifs.user.repository.ProcessRoleRepository;
import org.innovateuk.ifs.user.repository.RoleRepository;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.innovateuk.ifs.security.SecurityRuleUtil.checkProcessRole;
import static org.innovateuk.ifs.user.resource.UserRoleType.COLLABORATOR;
import static org.innovateuk.ifs.user.resource.UserRoleType.LEADAPPLICANT;
import static org.innovateuk.ifs.util.SecurityRuleUtil.isInternal;


/**
 * Permission rules for {@link FinanceRow} and {@link FinanceRowItem} for permissioning
 */
@Component
@PermissionRules
public class ApplicationFinanceRowPermissionRules extends BasePermissionRules {

    @Autowired
    private ProcessRoleRepository processRoleRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private ApplicationFinanceRowRepository financeRowRepository;

    @PermissionRule(value = "UPDATE", description = "The consortium can update the cost for their application and organisation")
    public boolean consortiumCanUpdateACostForTheirApplicationAndOrganisation(final FinanceRow cost, final UserResource user) {
        return isCollaborator(cost, user);
    }

    @PermissionRule(value = "DELETE", description = "The consortium can update the cost for their application and organisation")
    public boolean consortiumCanDeleteACostForTheirApplicationAndOrganisation(final FinanceRow cost, final UserResource user) {
        return isCollaborator(cost, user);
    }

    @PermissionRule(value = "READ", description = "The consortium can read the cost for their application and organisation")
    public boolean consortiumCanReadACostForTheirApplicationAndOrganisation(final FinanceRow cost, final UserResource user) {
        return isCollaborator(cost, user);
    }

    @PermissionRule(value = "READ", description = "The consortium can read the cost for their application and organisation")
    public boolean consortiumCanReadACostItemForTheirApplicationAndOrganisation(final FinanceRowItem costItem, final UserResource user) {
        return isCollaborator(financeRowRepository.findOne(costItem.getId()), user);
    }

    @PermissionRule(value = "READ", description = "The consortium can read the cost for their application and organisation")
    public boolean consortiumCanReadACostValueForTheirApplicationAndOrganisation(final FinanceRowMetaValueResource financeRowMetaValueResource, final UserResource user) {
        final FinanceRow cost = financeRowRepository.findOne(financeRowMetaValueResource.getFinanceRowId());
        return isCollaborator(cost, user);
    }

    @PermissionRule(value = "READ_ORGANISATION_FUNDING_STATUS", description = "Any partner can check if any of the other partners are seeking funding")
    public boolean projectPartnersCanCheckFundingStatusOfTeam(final ProjectResource project, final UserResource user) {
        return isPartner(project.getId(), user.getId());
    }

    @PermissionRule(value = "READ_ORGANISATION_FUNDING_STATUS", description = "Any internal user can check if any of the partners are seeking funding")
    public boolean internalUsersCanCheckFundingStatusOfTeam(final ProjectResource project, final UserResource user) {
        return isInternal(user);
    }

    private boolean isCollaborator(final FinanceRow cost, final UserResource user) {
        final ApplicationFinance applicationFinance = (ApplicationFinance) cost.getTarget();
        final Long applicationId = applicationFinance.getApplication().getId();
        final Long organisationId = applicationFinance.getOrganisation().getId();
        final boolean isLead = checkProcessRole(user, applicationId, organisationId, LEADAPPLICANT, roleRepository, processRoleRepository);
        final boolean isCollaborator = checkProcessRole(user, applicationId, organisationId, COLLABORATOR, roleRepository, processRoleRepository);
        return isLead || isCollaborator;
    }
}
