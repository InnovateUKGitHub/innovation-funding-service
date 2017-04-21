package org.innovateuk.ifs.project.monitoringofficer.controller;

import org.innovateuk.ifs.project.BaseProjectSetupControllerSecurityTest;
import org.innovateuk.ifs.project.status.security.ProjectSetupSectionsPermissionRules;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Test;

import java.util.function.Consumer;

import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;

public class ProjectMonitoringOfficerControllerSecurityTest extends BaseProjectSetupControllerSecurityTest<ProjectMonitoringOfficerController> {

    @Override
    protected Class<? extends ProjectMonitoringOfficerController> getClassUnderTest() {
        return ProjectMonitoringOfficerController.class;
    }

    @Test
    public void testViewMonitoringOfficer() {
        assertSecured(() -> classUnderTest.viewMonitoringOfficer(123L, null));
    }

    @Override
    protected Consumer<ProjectSetupSectionsPermissionRules> getVerification() {
        return permissionRules -> permissionRules.partnerCanAccessMonitoringOfficerSection(eq(123L), isA(UserResource.class));
    }
}
