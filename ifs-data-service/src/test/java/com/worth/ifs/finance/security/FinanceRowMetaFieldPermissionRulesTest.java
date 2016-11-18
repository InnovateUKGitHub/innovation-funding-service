package com.worth.ifs.finance.security;

import com.worth.ifs.BasePermissionRulesTest;
import com.worth.ifs.finance.builder.FinanceRowMetaFieldResourceBuilder;
import com.worth.ifs.finance.resource.FinanceRowMetaFieldResource;
import com.worth.ifs.commons.security.CustomPermissionEvaluator;
import com.worth.ifs.user.resource.UserResource;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static com.worth.ifs.base.amend.BaseBuilderAmendFunctions.id;
import static com.worth.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class FinanceRowMetaFieldPermissionRulesTest extends BasePermissionRulesTest<FinanceRowMetaFieldPermissionsRules> {

    private FinanceRowMetaFieldResource financeRowMetaFieldResource;
    private UserResource user1;
    private UserResource anonymousUser;

    @Override
    protected FinanceRowMetaFieldPermissionsRules supplyPermissionRulesUnderTest() {
        return new FinanceRowMetaFieldPermissionsRules();
    }

    @Before
    public void setup() throws Exception {
        user1 = newUserResource().build();
        anonymousUser = (UserResource)ReflectionTestUtils.getField(new CustomPermissionEvaluator(), "ANONYMOUS_USER");
        financeRowMetaFieldResource = FinanceRowMetaFieldResourceBuilder.newFinanceRowMetaFieldResource().with(id(1L)).build();
    }

    @Test
    public void loggedInUsersCanSeeCostFields() {
        assertTrue(rules.loggedInUsersCanReadCostFieldReferenceData(financeRowMetaFieldResource, user1));
    }

    @Test
    public void nonLoggedInUserCannotSeeCostFields() {
        assertFalse(rules.loggedInUsersCanReadCostFieldReferenceData(financeRowMetaFieldResource, anonymousUser));
    }
}
