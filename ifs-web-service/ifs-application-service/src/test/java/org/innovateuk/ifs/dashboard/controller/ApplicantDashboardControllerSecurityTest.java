package org.innovateuk.ifs.dashboard.controller;

import org.innovateuk.ifs.security.BaseControllerSecurityTest;
import org.innovateuk.ifs.user.resource.Role;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletResponse;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.mock;

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

        HttpServletRequest request = mock(HttpServletRequest.class);
        MockHttpServletResponse response = new MockHttpServletResponse();

        assertRolesCanPerform(() -> classUnderTest.dashboard(null, null), roles);
    }
}
