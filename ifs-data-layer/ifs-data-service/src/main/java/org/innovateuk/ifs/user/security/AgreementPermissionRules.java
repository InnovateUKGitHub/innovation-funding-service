package org.innovateuk.ifs.user.security;

import org.innovateuk.ifs.commons.security.PermissionRule;
import org.innovateuk.ifs.security.BasePermissionRules;
import org.innovateuk.ifs.util.SecurityRuleUtil;
import org.innovateuk.ifs.user.resource.UserResource;

/**
 * Permission rules that determines who can perform CRUD operations based around Agreements.
 */
public class AgreementPermissionRules extends BasePermissionRules {

    @PermissionRule(value = "READ", description = "Any assessor user can view the current agreement")
    public boolean anyAssessorCanViewTheCurrentAgreement(UserResource user) {
        return SecurityRuleUtil.isAssessor(user);
    }
}
