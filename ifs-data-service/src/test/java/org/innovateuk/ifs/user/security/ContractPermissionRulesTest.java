package org.innovateuk.ifs.user.security;

import org.innovateuk.ifs.BasePermissionRulesTest;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.resource.UserRoleType;
import org.junit.Test;

import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
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
