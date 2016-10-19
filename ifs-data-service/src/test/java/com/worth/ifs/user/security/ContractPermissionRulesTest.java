package com.worth.ifs.user.security;

import com.worth.ifs.BasePermissionRulesTest;
import com.worth.ifs.user.resource.UserResource;
import com.worth.ifs.user.resource.UserRoleType;
import org.junit.Test;

import static com.worth.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


public class ContractPermissionRulesTest extends BasePermissionRulesTest<ContractPermissionRules> {

    @Override
    protected ContractPermissionRules supplyPermissionRulesUnderTest() {
        return new ContractPermissionRules();
    }

    @Test
    public void anyAssessorCanViewTheCurrentContract() throws Exception {
        UserResource userWithAssessorRole = getUserWithRole(UserRoleType.ASSESSOR);

        assertTrue(rules.anyAssessorCanViewTheCurrentContract(userWithAssessorRole));
    }

    @Test
    public void anyAssessorCanViewTheCurrentContract_notAnAssessor() throws Exception {
        UserResource userWithoutAssessorRole = newUserResource().build();

        assertFalse(rules.anyAssessorCanViewTheCurrentContract(userWithoutAssessorRole));
    }
}