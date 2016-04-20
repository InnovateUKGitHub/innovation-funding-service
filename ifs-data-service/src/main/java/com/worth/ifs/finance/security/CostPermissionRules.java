package com.worth.ifs.finance.security;

import com.worth.ifs.finance.domain.Cost;
import com.worth.ifs.finance.mapper.ApplicationFinanceMapper;
import com.worth.ifs.finance.repository.ApplicationFinanceRepository;
import com.worth.ifs.finance.resource.ApplicationFinanceResource;
import com.worth.ifs.security.PermissionRule;
import com.worth.ifs.security.PermissionRules;
import com.worth.ifs.user.repository.ProcessRoleRepository;
import com.worth.ifs.user.repository.RoleRepository;
import com.worth.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.worth.ifs.finance.resource.cost.CostItem;

import static com.worth.ifs.security.SecurityRuleUtil.checkRole;
import static com.worth.ifs.user.domain.UserRoleType.COLLABORATOR;
import static com.worth.ifs.user.domain.UserRoleType.LEADAPPLICANT;


/**
 * Permission rules for {@link Cost} and {@link CostItem} for permissioning
 */
@Component
@PermissionRules
public class CostPermissionRules {

    @Autowired
    private ProcessRoleRepository processRoleRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private ApplicationFinanceRepository applicationFinanceRepository;

    @Autowired
    private ApplicationFinanceMapper applicationMapper;

    @PermissionRule(value = "UPDATE", description = "The consortium can update the cost for their application and organisation")
    public boolean consortiumCanUpdateACostForTheirApplicationAndOrganisation(final Cost cost, final UserResource user) {
        return isCollaborator(cost, user);
    }

    @PermissionRule(value = "DELETE", description = "The consortium can update the cost for their application and organisation")
    public boolean consortiumCanDeleteACostForTheirApplicationAndOrganisation(final Cost cost, final UserResource user) {
        return isCollaborator(cost, user);
    }

    private boolean isCollaborator(final Cost cost, final UserResource user) {
        final ApplicationFinanceResource applicationFinance = applicationMapper.mapToResource(applicationFinanceRepository.findOne(cost.getApplicationFinance().getId()));
        final boolean isLead = checkRole(user, applicationFinance.getApplication(), applicationFinance.getOrganisation(), LEADAPPLICANT, roleRepository, processRoleRepository);
        final boolean isCollaborator = checkRole(user, applicationFinance.getApplication(), applicationFinance.getOrganisation(), COLLABORATOR, roleRepository, processRoleRepository);
        return isLead || isCollaborator;
    }

}
