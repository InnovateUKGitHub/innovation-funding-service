package org.innovateuk.ifs.finance.security;

import org.innovateuk.ifs.commons.security.PermissionRules;
import org.innovateuk.ifs.finance.domain.FinanceRow;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowItem;
import org.innovateuk.ifs.security.BasePermissionRules;
import org.springframework.stereotype.Component;


/**
 * Permission rules for {@link FinanceRow} and {@link FinanceRowItem} for permissioning
 */
@Component
@PermissionRules
public class ProjectFinanceRowPermissionRules extends BasePermissionRules {

    /*@Autowired
    private ProcessRoleRepository processRoleRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private ProjectFinanceRowRepository financeRowRepository;

    @PermissionRule(value = "READ", description = "The consortium can read the cost for their Project and organisation")
    public boolean projectPartnersCanReadACostForTheirProjectAndOrganisation(final FinanceRowItem cost, final UserResource user) {
        return isPartner(cost.getTarget().getProject().getId(), user.getId());
    }

    @PermissionRule(value = "READ_ORGANISATION_FUNDING_STATUS", description = "Any partner can check if any of the other partners are seeking funding")
    public boolean projectPartnersCanCheckFundingStatusOfTeam(final ProjectResource project, final UserResource user) {
        return isPartner(project.getId(), user.getId());
    }

    @PermissionRule(value = "READ_ORGANISATION_FUNDING_STATUS", description = "Any internal user can check if any of the partners are seeking funding")
    public boolean internalUsersCanCheckFundingStatusOfTeam(final ProjectResource project, final UserResource user) {
        return isInternal(user);
    }*/
}
