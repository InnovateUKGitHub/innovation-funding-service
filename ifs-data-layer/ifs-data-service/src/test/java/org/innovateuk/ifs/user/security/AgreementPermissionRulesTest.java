package org.innovateuk.ifs.user.security;

import org.innovateuk.ifs.BasePermissionRulesTest;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.resource.UserRoleType;
import org.junit.Test;

import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


public class AgreementPermissionRulesTest extends BasePermissionRulesTest<AgreementPermissionRules> {

    @Override
    protected AgreementPermissionRules supplyPermissionRulesUnderTest() {
        return new AgreementPermissionRules();
    }

    @Test
    public void anyAssessorCanViewTheCurrentAgreement() throws Exception {
        UserResource userWithAssessorRole = getUserWithRole(UserRoleType.ASSESSOR);
        assertTrue(rules.anyAssessorCanViewTheCurrentAgreement(userWithAssessorRole));
    }

    @Test
    public void anyAssessorCanViewTheCurrentAgreement_notAnAssessor() throws Exception {
        UserResource userWithoutAssessorRole = newUserResource().build();
        assertFalse(rules.anyAssessorCanViewTheCurrentAgreement(userWithoutAssessorRole));
    }
}
