package org.innovateuk.ifs.procurement.milestone.security;

import org.innovateuk.ifs.commons.security.PermissionRule;
import org.innovateuk.ifs.commons.security.PermissionRules;
import org.innovateuk.ifs.procurement.milestone.resource.PaymentMilestoneResource;
import org.innovateuk.ifs.security.BasePermissionRules;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.stereotype.Component;

import static org.innovateuk.ifs.user.resource.Role.EXTERNAL_FINANCE;
import static org.innovateuk.ifs.user.resource.Role.STAKEHOLDER;
import static org.innovateuk.ifs.util.SecurityRuleUtil.isInternal;
import static org.innovateuk.ifs.util.SecurityRuleUtil.isProjectFinanceUser;

/**
 * Provides the permissions around CRUD for ApplicationProcurementMilestones
 */
@Component
@PermissionRules
public class ProjectProcurementMilestonePermissionRules extends BasePermissionRules {

    @PermissionRule(value = "EDIT", description = "Project finance users users can update the project finances of a project")
    public boolean internalUsersCanUpdateProjectFinance(final PaymentMilestoneResource financeResource, final UserResource user) {
        return isProjectFinanceUser(user);
    }

    @PermissionRule(value = "VIEW", description = "Project partners can see the project finances of their own project")
    public boolean partnersCanSeeTheProjectFinancesForTheirOrganisation(final PaymentMilestoneResource projectFinanceResource, final UserResource user) {
        return isPartner(projectFinanceResource.getProjectId(), user.getId());
    }

    @PermissionRule(value = "VIEW", description = "An internal user can see project finances for organisations")
    public boolean internalUserCanSeeProjectFinancesForOrganisations(final PaymentMilestoneResource projectFinanceResource, final UserResource user) {
        return isInternal(user);
    }

    @PermissionRule(value = "VIEW", description = "A stakeholder user can see project finances for organisations")
    public boolean stakeholderUserCanSeeProjectFinancesForOrganisations(final PaymentMilestoneResource projectFinanceResource, final UserResource user) {
        return user.hasRole(STAKEHOLDER);
    }

    @PermissionRule(value = "VIEW", description = "A Competition finance user can see project finances for organisations")
    public boolean competitionFinanceUserCanSeeProjectFinancesForOrganisations(final PaymentMilestoneResource projectFinanceResource, final UserResource user) {
        return user.hasRole(EXTERNAL_FINANCE);
    }
}