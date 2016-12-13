package org.innovateuk.ifs.finance.security;

import org.innovateuk.ifs.finance.resource.FinanceRowMetaFieldResource;
import org.innovateuk.ifs.commons.security.PermissionRule;
import org.innovateuk.ifs.commons.security.PermissionRules;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.stereotype.Component;

import static org.innovateuk.ifs.security.SecurityRuleUtil.isAnonymous;

@Component
@PermissionRules
public class FinanceRowMetaFieldPermissionsRules {

    @PermissionRule(value = "READ", description = "All logged in users can see the reference cost field reference data")
    public boolean loggedInUsersCanReadCostFieldReferenceData(final FinanceRowMetaFieldResource costFieldToRead, final UserResource user){
        return !isAnonymous(user);
    }

}
