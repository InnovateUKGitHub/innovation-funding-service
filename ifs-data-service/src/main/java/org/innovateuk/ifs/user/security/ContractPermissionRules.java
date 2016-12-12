package org.innovateuk.ifs.user.security;

import org.innovateuk.ifs.commons.security.PermissionRule;
import org.innovateuk.ifs.security.BasePermissionRules;
import org.innovateuk.ifs.security.SecurityRuleUtil;
import org.innovateuk.ifs.user.resource.UserResource;

/**
 * Permission rules that determines who can perform CRUD operations based around Contracts.
 */
public class ContractPermissionRules extends BasePermissionRules {

    @PermissionRule(value = "READ", description = "Any assessor user can view the current contract")
    public boolean anyAssessorCanViewTheCurrentContract(UserResource user) {
        return SecurityRuleUtil.isAssessor(user);
    }
}
