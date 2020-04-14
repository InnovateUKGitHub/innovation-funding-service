package org.innovateuk.ifs.finance.security;

import org.innovateuk.ifs.commons.security.PermissionRule;
import org.innovateuk.ifs.commons.security.PermissionRules;
import org.innovateuk.ifs.finance.domain.FinanceRow;
import org.innovateuk.ifs.finance.domain.ProjectFinance;
import org.innovateuk.ifs.finance.domain.ProjectFinanceRow;
import org.innovateuk.ifs.finance.repository.ProjectFinanceRepository;
import org.innovateuk.ifs.finance.resource.ProjectFinanceResource;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowItem;
import org.innovateuk.ifs.security.BasePermissionRules;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.innovateuk.ifs.user.resource.Role.PROJECT_FINANCE;


/**
 * Permission rules for {@link FinanceRow} and {@link FinanceRowItem} for permissioning
 */
@Component
@PermissionRules
public class ProjectFinanceRowPermissionRules extends BasePermissionRules {

    @Autowired
    private ProjectFinanceRepository projectFinanceRepository;

    @PermissionRule(value = "CRUD", description = "The consortium can update the cost for their application and organisation")
    public boolean teamMembersCanCrudFinanceRows(final ProjectFinanceRow cost, final UserResource user) {
        return isProjectTeamMember(cost.getTarget(), user);
    }

    @PermissionRule(value = "CRUD", description = "The consortium can update the cost for their application and organisation")
    public boolean projectFinanceCanCrudProjectFinanceRows(final ProjectFinanceRow cost, final UserResource user) {
        return user.hasRole(PROJECT_FINANCE);
    }

    @PermissionRule(value = "CRUD", description = "The comp finance user can edit finances")
    public boolean compFinanceCanCrudProjectFinanceRows(final ProjectFinanceRow cost, final UserResource user) {
        return userIsCompFinanceOnCompetitionForProject(cost.getTarget().getProject().getId(),  user.getId());
    }

    @PermissionRule(value = "ADD_ROW", description = "The consortium can update the cost for their application and organisation")
    public boolean teamMembersCanCrudFinanceRows(final ProjectFinanceResource financeResource, final UserResource user) {
        return isProjectTeamMember(projectFinanceRepository.findById(financeResource.getId()).get(), user);
    }

    @PermissionRule(value = "ADD_ROW", description = "The consortium can update the cost for their application and organisation")
    public boolean projectFinanceCanCrudProjectFinanceRows(final ProjectFinanceResource financeResource, final UserResource user) {
        return user.hasRole(PROJECT_FINANCE);
    }

    private boolean isProjectTeamMember(final ProjectFinance projectFinance, final UserResource user) {
        final long projectId = projectFinance.getProject().getId();
        final long organisationId = projectFinance.getOrganisation().getId();
        return partnerBelongsToOrganisation(projectId, user.getId(), organisationId);
    }
}
