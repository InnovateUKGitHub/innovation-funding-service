package org.innovateuk.ifs.project.monitoringofficer;

import org.innovateuk.ifs.monitoringofficer.controller.ProjectMonitoringOfficerController;
import org.innovateuk.ifs.project.BaseProjectSetupControllerSecurityTest;
import org.innovateuk.ifs.project.sections.security.ProjectSetupSectionsPermissionRules;
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
        assertSecured(() -> classUnderTest.viewMonitoringOfficer(null, 123L, null));
    }

    @Override
    protected Consumer<ProjectSetupSectionsPermissionRules> getVerification() {
        return permissionRules -> permissionRules.internalCanAccessMonitoringOfficerSection(eq(123L), isA(UserResource.class));
    }
}
