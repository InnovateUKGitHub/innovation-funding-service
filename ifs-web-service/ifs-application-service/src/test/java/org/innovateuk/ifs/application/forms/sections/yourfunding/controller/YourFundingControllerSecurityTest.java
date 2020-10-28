package org.innovateuk.ifs.application.forms.sections.yourfunding.controller;

import org.innovateuk.ifs.security.BaseControllerSecurityTest;
import org.innovateuk.ifs.user.resource.Role;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class YourFundingControllerSecurityTest extends BaseControllerSecurityTest<YourFundingController> {

    @Override
    protected Class<? extends YourFundingController> getClassUnderTest() {
        return YourFundingController.class;
    }

    @Test
    public void viewYourFunding() {
        List<Role> roles = new ArrayList<>(Role.internalRoles());
        roles.add(Role.APPLICANT);
        roles.add(Role.STAKEHOLDER);
        roles.add(Role.EXTERNAL_FINANCE);
        roles.add(Role.KNOWLEDGE_TRANSFER_ADVISER);
        roles.add(Role.COFUNDER);
        roles.add(Role.ASSESSOR);
        roles.add(Role.SYSTEM_MAINTAINER);

        assertRolesCanPerform(() -> classUnderTest.viewYourFunding(null, null, null, 0L, 0L,0L), roles);
    }
}
