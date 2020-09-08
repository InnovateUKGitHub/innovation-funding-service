package org.innovateuk.ifs.application.forms.sections.yourprojectcosts.controller;

import org.innovateuk.ifs.security.BaseControllerSecurityTest;
import org.innovateuk.ifs.user.resource.Role;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class YourProjectCostsControllerSecurityTest extends BaseControllerSecurityTest<YourProjectCostsController> {

    @Override
    protected Class<? extends YourProjectCostsController> getClassUnderTest() {
        return YourProjectCostsController.class;
    }

    @Test
    public void YourProjectCostsController() {
        List<Role> roles = new ArrayList<>(Role.internalRoles());
        roles.add(Role.APPLICANT);
        roles.add(Role.STAKEHOLDER);
        roles.add(Role.EXTERNAL_FINANCE);
        roles.add(Role.KNOWLEDGE_TRANSFER_ADVISER);

        assertRolesCanPerform(() -> classUnderTest.viewYourProjectCosts(null, null, 0L, 0L, 0L), roles);
    }
}
