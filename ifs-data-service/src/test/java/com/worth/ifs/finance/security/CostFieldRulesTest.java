package com.worth.ifs.finance.security;

import com.worth.ifs.BaseUnitTestMocksTest;
import com.worth.ifs.finance.builder.CostFieldResourceBuilder;
import com.worth.ifs.finance.resource.CostFieldResource;
import com.worth.ifs.security.CustomPermissionEvaluator;
import com.worth.ifs.user.resource.UserResource;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.springframework.test.util.ReflectionTestUtils;

import static com.worth.ifs.BuilderAmendFunctions.id;
import static com.worth.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class CostFieldRulesTest extends BaseUnitTestMocksTest {

    @InjectMocks
    private CostFieldPermissionsRules costFieldRules;

    private CostFieldResource costFieldResource;
    private UserResource user1;
    private UserResource anonymousUser;

    @Before
    public void setup() throws Exception {
        user1 = newUserResource().build();
        anonymousUser = (UserResource)ReflectionTestUtils.getField(new CustomPermissionEvaluator(), "ANONYMOUS_USER");
        costFieldResource = CostFieldResourceBuilder.newCostFieldResource().with(id(1L)).build();
    }

    @Test
    public void loggedInUsersCanSeeCostFields() {
        assertTrue(costFieldRules.loggedInUsersCanReadCostFieldReferenceData(costFieldResource, user1));
    }

    @Test
    public void nonLoggedInUserCannotSeeCostFields() {
        assertFalse(costFieldRules.loggedInUsersCanReadCostFieldReferenceData(costFieldResource, anonymousUser));
    }
}
