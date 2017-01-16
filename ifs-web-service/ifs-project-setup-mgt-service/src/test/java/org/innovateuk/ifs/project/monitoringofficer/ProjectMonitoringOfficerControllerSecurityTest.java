package org.innovateuk.ifs.project.monitoringofficer;

import org.innovateuk.ifs.monitoringofficer.controller.ProjectMonitoringOfficerController;
import org.innovateuk.ifs.project.BaseProjectSetupControllerSecurityTest;
import org.innovateuk.ifs.project.ProjectSetupSectionsPermissionRules;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Ignore;
import org.junit.Test;

import java.util.function.Consumer;

import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;

@Ignore("Ignoring for now, as these will be fixed when dev is merged in prior to PR")
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
