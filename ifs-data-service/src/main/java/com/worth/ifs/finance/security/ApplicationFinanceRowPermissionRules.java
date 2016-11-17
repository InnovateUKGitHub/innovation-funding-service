package com.worth.ifs.finance.security;

import com.worth.ifs.commons.security.PermissionRule;
import com.worth.ifs.commons.security.PermissionRules;
import com.worth.ifs.finance.domain.ApplicationFinance;
import com.worth.ifs.finance.domain.FinanceRow;
import com.worth.ifs.finance.repository.ApplicationFinanceRowRepository;
import com.worth.ifs.finance.resource.FinanceRowMetaValueResource;
import com.worth.ifs.finance.resource.cost.FinanceRowItem;
import com.worth.ifs.project.resource.ProjectResource;
import com.worth.ifs.security.BasePermissionRules;
import com.worth.ifs.user.repository.ProcessRoleRepository;
import com.worth.ifs.user.repository.RoleRepository;
import com.worth.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.worth.ifs.security.SecurityRuleUtil.*;
import static com.worth.ifs.user.resource.UserRoleType.COLLABORATOR;
import static com.worth.ifs.user.resource.UserRoleType.LEADAPPLICANT;


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
        final FinanceRow cost = financeRowRepository.findOne(financeRowMetaValueResource.getFinanceRow());
        return isCollaborator(cost, user);
    }

    @PermissionRule(value = "READ_ORGANISATION_FUNDING_STATUS", description = "Any partner can check if any of the other partners are seeking funding")
    public boolean projectPartnersCanCheckFundingStatusOfTeam(final ProjectResource project, final UserResource user) {
        return isPartner(project.getId(), user.getId());
    }

    @PermissionRule(value = "READ_ORGANISATION_FUNDING_STATUS", description = "Any project finance user can check if any of partners are seeking funding")
    public boolean projectFinanceUsersCanCheckFundingStatusOfTeam(final ProjectResource project, final UserResource user) {
        return isProjectFinanceUser(user);
    }

    @PermissionRule(value = "READ_ORGANISATION_FUNDING_STATUS", description = "Any competition administrator can check if any of the partners are seeking funding")
    public boolean compAdminsCanCheckFundingStatusOfTeam(final ProjectResource project, final UserResource user) {
        return isCompAdmin(user);
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
