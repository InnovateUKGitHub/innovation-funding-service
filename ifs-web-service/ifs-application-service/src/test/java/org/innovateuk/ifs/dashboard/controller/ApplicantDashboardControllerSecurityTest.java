package org.innovateuk.ifs.dashboard.controller;

import org.innovateuk.ifs.security.BaseControllerSecurityTest;
import org.innovateuk.ifs.user.resource.Role;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class ApplicantDashboardControllerSecurityTest extends BaseControllerSecurityTest<ApplicantDashboardController> {

    @Override
    protected Class<? extends ApplicantDashboardController> getClassUnderTest() {
        return ApplicantDashboardController.class;
    }

    @Test
    public void dashboard() {
        List<Role> roles = new ArrayList<>();
        roles.add(Role.APPLICANT);
        roles.add(Role.KNOWLEDGE_TRANSFER_ADVISER);

        assertRolesCanPerform(() -> classUnderTest.dashboard(null, null), roles);
    }
}
