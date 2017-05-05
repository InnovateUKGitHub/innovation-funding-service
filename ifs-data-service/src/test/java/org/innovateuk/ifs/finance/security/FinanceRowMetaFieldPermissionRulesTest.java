package org.innovateuk.ifs.finance.security;

import org.innovateuk.ifs.BasePermissionRulesTest;
import org.innovateuk.ifs.finance.builder.FinanceRowMetaFieldResourceBuilder;
import org.innovateuk.ifs.finance.resource.FinanceRowMetaFieldResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Before;
import org.junit.Test;

import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.id;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class FinanceRowMetaFieldPermissionRulesTest extends BasePermissionRulesTest<FinanceRowMetaFieldPermissionsRules> {

    private FinanceRowMetaFieldResource financeRowMetaFieldResource;
    private UserResource user1;

    @Override
    protected FinanceRowMetaFieldPermissionsRules supplyPermissionRulesUnderTest() {
        return new FinanceRowMetaFieldPermissionsRules();
    }

    @Before
    public void setup() throws Exception {
        user1 = newUserResource().build();
        financeRowMetaFieldResource = FinanceRowMetaFieldResourceBuilder.newFinanceRowMetaFieldResource().with(id(1L)).build();
    }

    @Test
    public void loggedInUsersCanSeeCostFields() {
        assertTrue(rules.loggedInUsersCanReadCostFieldReferenceData(financeRowMetaFieldResource, user1));
    }

    @Test
    public void nonLoggedInUserCannotSeeCostFields() {
        assertFalse(rules.loggedInUsersCanReadCostFieldReferenceData(financeRowMetaFieldResource, anonymousUser()));
    }
}
