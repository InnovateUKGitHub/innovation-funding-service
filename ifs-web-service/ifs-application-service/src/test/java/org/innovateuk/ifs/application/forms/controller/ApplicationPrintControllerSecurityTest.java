package org.innovateuk.ifs.application.forms.controller;

import org.innovateuk.ifs.security.BaseControllerSecurityTest;
import org.innovateuk.ifs.user.resource.Role;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class ApplicationPrintControllerSecurityTest extends BaseControllerSecurityTest<ApplicationPrintController> {

    @Override
    protected Class<? extends ApplicationPrintController> getClassUnderTest() {
        return ApplicationPrintController.class;
    }

    @Test
    public void printApplication() {
        List<Role> roles = new ArrayList<>();
        roles.add(Role.APPLICANT);
        roles.add(Role.ASSESSOR);
        roles.add(Role.SUPPORTER);
        roles.add(Role.MONITORING_OFFICER);

        assertRolesCanPerform(() -> classUnderTest.printApplication(0L, null, null), roles);
    }
}
