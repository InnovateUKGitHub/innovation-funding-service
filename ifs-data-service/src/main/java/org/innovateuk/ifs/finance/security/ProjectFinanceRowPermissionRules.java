package org.innovateuk.ifs.finance.security;

import org.innovateuk.ifs.commons.security.PermissionRule;
import org.innovateuk.ifs.commons.security.PermissionRules;
import org.innovateuk.ifs.user.resource.OrganisationResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.stereotype.Component;

import static org.innovateuk.ifs.security.SecurityRuleUtil.isCompAdmin;
import static org.innovateuk.ifs.security.SecurityRuleUtil.isProjectFinanceUser;
import static org.innovateuk.ifs.user.resource.UserRoleType.FINANCE_CONTACT;

@Component
@PermissionRules
public class ProjectFinanceRowPermissionRules {

    @PermissionRule(value = "PROJECT_FINANCE_READ", description = "The finance contact is able to view the project finance")
    public boolean financeContactCanGetProjectFinance(OrganisationResource organisation, UserResource user) {
        return isOrganisationFinanceContact(organisation, user) || isCompAdmin(user) || isProjectFinanceUser(user);
    }

    private boolean isOrganisationFinanceContact(OrganisationResource organisation, UserResource user) {
        return organisation.getUsers() != null && organisation.getUsers().contains(user.getId()) && user.hasRole(FINANCE_CONTACT);
    }
}
