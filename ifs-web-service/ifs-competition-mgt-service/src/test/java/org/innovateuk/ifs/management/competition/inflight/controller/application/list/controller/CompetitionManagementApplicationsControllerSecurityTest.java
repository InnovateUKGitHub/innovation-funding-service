package org.innovateuk.ifs.management.competition.inflight.controller.application.list.controller;

import org.innovateuk.ifs.management.application.list.controller.CompetitionManagementApplicationsController;
import org.innovateuk.ifs.security.BaseControllerSecurityTest;
import org.junit.Test;

public class CompetitionManagementApplicationsControllerSecurityTest extends BaseControllerSecurityTest<CompetitionManagementApplicationsController> {

    @Override
    protected Class<? extends CompetitionManagementApplicationsController> getClassUnderTest() {
        return CompetitionManagementApplicationsController.class;
    }

    long competitionId = 1L;

    @Test
    public void testApplicationsMenu() {
        assertAccessDenied(() -> classUnderTest.applicationsMenu(null, competitionId, null),
        () -> {
        });
    }

    @Test
    public void testAllApplications() {
        assertAccessDenied(() -> classUnderTest.allApplications(null, competitionId, 0, null, null, null),
                () -> {
                });
    }

    @Test
    public void testSubmittedApplications() {
        assertAccessDenied(() -> classUnderTest.submittedApplications(null, competitionId, 0, null, null),
                () -> {
                });
    }

    @Test
    public void testIneligibleApplications() {
        assertAccessDenied(() -> classUnderTest.ineligibleApplications(null, null, competitionId, 0, null, null),
                () -> {
                });
    }

}
