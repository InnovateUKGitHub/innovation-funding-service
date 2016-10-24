package com.worth.ifs.user.security;

import com.worth.ifs.commons.security.PermissionRule;
import com.worth.ifs.security.BasePermissionRules;
import com.worth.ifs.security.SecurityRuleUtil;
import com.worth.ifs.user.resource.UserResource;

/**
 * Permission rules that determines who can perform CRUD operations based around Contracts.
 */
public class ContractPermissionRules extends BasePermissionRules {

    @PermissionRule(value = "READ", description = "Any assessor user can view the current contract")
    public boolean anyAssessorCanViewTheCurrentContract(UserResource user) {
        return SecurityRuleUtil.isAssessor(user);
    }
}