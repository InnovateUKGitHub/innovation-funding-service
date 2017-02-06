package org.innovateuk.ifs.assessment.controller.dashboard;

import org.innovateuk.ifs.security.BaseControllerSecurityTest;
import org.junit.Test;


public class AssessorDashboardControllerSecurityTest extends BaseControllerSecurityTest<AssessorDashboardController> {
    @Override
    protected Class<? extends AssessorDashboardController> getClassUnderTest() {
        return AssessorDashboardController.class;
    }

    @Test
    public void dashboard() {
        assertAccessDenied(() -> classUnderTest.dashboard(null,null),
                () -> {

                });
    }

}
