package com.worth.ifs.project.monitoringofficer;

import com.worth.ifs.monitoringofficer.controller.ProjectMonitoringOfficerController;
import com.worth.ifs.project.BaseProjectSetupControllerSecurityTest;
import com.worth.ifs.project.ProjectSetupSectionsPermissionRules;
import com.worth.ifs.user.resource.UserResource;
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
