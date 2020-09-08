package org.innovateuk.ifs.application.overview.controller;

import org.innovateuk.ifs.security.BaseControllerSecurityTest;
import org.innovateuk.ifs.user.resource.Role;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class ApplicationOverviewControllerSecurityTest extends BaseControllerSecurityTest<ApplicationOverviewController> {

    @Override
    protected Class<? extends ApplicationOverviewController> getClassUnderTest() {
        return ApplicationOverviewController.class;
    }

    @Test
    public void applicationOverview() {
        List<Role> roles = new ArrayList<>();
        roles.add(Role.APPLICANT);
        roles.add(Role.KNOWLEDGE_TRANSFER_ADVISER);

        assertRolesCanPerform(() -> classUnderTest.applicationOverview(null, 0L, null), roles);
    }

    @Test
    public void termsAndConditions() {
        List<Role> roles = new ArrayList<>();
        roles.add(Role.APPLICANT);
        roles.add(Role.KNOWLEDGE_TRANSFER_ADVISER);

        assertRolesCanPerform(() -> classUnderTest.termsAndConditions(), roles);
    }
}
