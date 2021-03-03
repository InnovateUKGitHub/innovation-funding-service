package org.innovateuk.ifs.application.forms.controller;

import org.innovateuk.ifs.security.BaseControllerSecurityTest;
import org.innovateuk.ifs.user.resource.Role;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class ApplicationSectionControllerSecurityTest extends BaseControllerSecurityTest<ApplicationSectionController> {

    @Override
    protected Class<? extends ApplicationSectionController> getClassUnderTest() {
        return ApplicationSectionController.class;
    }

    @Test
    public void redirectToSectionManagement() {
        List<Role> roles = new ArrayList<>(Role.internalRoles());
        roles.add(Role.STAKEHOLDER);
        roles.add(Role.EXTERNAL_FINANCE);
        roles.add(Role.KNOWLEDGE_TRANSFER_ADVISER);
        roles.add(Role.APPLICANT);
        roles.add(Role.SUPPORTER);
        roles.add(Role.ASSESSOR);
        roles.add(Role.SYSTEM_MAINTAINER);

        assertRolesCanPerform(() -> classUnderTest.redirectToSectionManagement(null, 0L, 0L), roles);
    }
}
