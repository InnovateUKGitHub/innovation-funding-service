package org.innovateuk.ifs.project.monitoringofficer.controller;

import org.innovateuk.ifs.project.BaseProjectSetupControllerSecurityTest;
import org.innovateuk.ifs.project.status.security.SetupSectionsPermissionRules;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Test;

import java.util.function.Consumer;

import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;

public class MonitoringOfficerControllerSecurityTest extends BaseProjectSetupControllerSecurityTest<MonitoringOfficerController> {

    @Override
    protected Class<? extends MonitoringOfficerController> getClassUnderTest() {
        return MonitoringOfficerController.class;
    }

    @Test
    public void testViewMonitoringOfficer() {
        assertSecured(() -> classUnderTest.viewMonitoringOfficer(null, 123L, null));
    }

    @Override
    protected Consumer<SetupSectionsPermissionRules> getVerification() {
        return permissionRules -> permissionRules.internalCanAccessMonitoringOfficerSection(eq(123L), isA(UserResource.class));
    }
}
