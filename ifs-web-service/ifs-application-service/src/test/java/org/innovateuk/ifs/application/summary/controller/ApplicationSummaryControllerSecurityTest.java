package org.innovateuk.ifs.application.summary.controller;

import org.innovateuk.ifs.security.BaseControllerSecurityTest;
import org.innovateuk.ifs.user.resource.Role;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class ApplicationSummaryControllerSecurityTest extends BaseControllerSecurityTest<ApplicationSummaryController> {

    @Override
    protected Class<? extends ApplicationSummaryController> getClassUnderTest() {
        return ApplicationSummaryController.class;
    }

    @Test
    public void applicationSummary() {
        List<Role> roles = new ArrayList<>();
        roles.add(Role.APPLICANT);
        roles.add(Role.ASSESSOR);
        roles.add(Role.MONITORING_OFFICER);
        roles.add(Role.KNOWLEDGE_TRANSFER_ADVISER);

        assertRolesCanPerform(() -> classUnderTest.applicationSummary(null, null, null,null, 0L, null), roles);
    }
}
