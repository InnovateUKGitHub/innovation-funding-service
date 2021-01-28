package org.innovateuk.ifs.project.core.transactional;

import org.innovateuk.ifs.BaseServiceSecurityTest;
import org.junit.Test;

import static org.innovateuk.ifs.user.resource.Role.*;

public class KtpProjectNotificationServiceSecurityTest extends BaseServiceSecurityTest<KtpProjectNotificationService> {

    @Override
    protected Class<? extends KtpProjectNotificationService> getClassUnderTest() {
        return KtpProjectNotificationServiceImpl.class;
    }

    @Test
    public void sendProjectSetupNotification() {
        testOnlyAUserWithOneOfTheGlobalRolesCan(
                () -> classUnderTest.sendProjectSetupNotification(1L), SYSTEM_MAINTAINER);
    }
}
