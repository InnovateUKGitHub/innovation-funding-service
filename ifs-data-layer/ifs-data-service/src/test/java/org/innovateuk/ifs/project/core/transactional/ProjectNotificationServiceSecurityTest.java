package org.innovateuk.ifs.project.core.transactional;

import org.innovateuk.ifs.BaseServiceSecurityTest;
import org.junit.Test;

import static org.innovateuk.ifs.user.resource.Role.*;

public class ProjectNotificationServiceSecurityTest extends BaseServiceSecurityTest<ProjectNotificationService> {

    @Override
    protected Class<? extends ProjectNotificationService> getClassUnderTest() {
        return ProjectNotificationServiceImpl.class;
    }

    @Test
    public void sendProjectSetupNotification() {
        testOnlyAUserWithOneOfTheGlobalRolesCan(
                () -> classUnderTest.sendProjectSetupNotification(1L), SYSTEM_MAINTAINER);
    }
}
