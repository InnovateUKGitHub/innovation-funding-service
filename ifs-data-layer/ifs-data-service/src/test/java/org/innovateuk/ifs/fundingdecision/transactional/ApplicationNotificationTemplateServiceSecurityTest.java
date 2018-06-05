package org.innovateuk.ifs.fundingdecision.transactional;

import org.innovateuk.ifs.BaseServiceSecurityTest;
import org.junit.Test;

import static org.innovateuk.ifs.user.resource.Role.COMP_ADMIN;
import static org.innovateuk.ifs.user.resource.Role.PROJECT_FINANCE;

public class ApplicationNotificationTemplateServiceSecurityTest extends BaseServiceSecurityTest<ApplicationNotificationTemplateService> {

    @Test
    public void getSuccessfulNotificationTemplate() {
        testOnlyAUserWithOneOfTheGlobalRolesCan(() -> classUnderTest.getSuccessfulNotificationTemplate(1L),
                COMP_ADMIN, PROJECT_FINANCE);
    }

    @Test
    public void getUnsuccessfulNotificationTemplate() {
        testOnlyAUserWithOneOfTheGlobalRolesCan(() -> classUnderTest.getUnsuccessfulNotificationTemplate(1L),
                COMP_ADMIN, PROJECT_FINANCE);
    }

    @Test
    public void getIneligibleNotificationTemplate() {
        testOnlyAUserWithOneOfTheGlobalRolesCan(() -> classUnderTest.getIneligibleNotificationTemplate(1L),
                COMP_ADMIN, PROJECT_FINANCE);
    }

    @Override
    protected Class<? extends ApplicationNotificationTemplateService> getClassUnderTest() {
        return ApplicationNotificationTemplateServiceImpl.class;
    }
}
