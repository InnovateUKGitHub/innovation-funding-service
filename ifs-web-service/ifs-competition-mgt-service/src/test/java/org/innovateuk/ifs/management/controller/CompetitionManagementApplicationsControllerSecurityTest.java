package org.innovateuk.ifs.management.controller;

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
        assertAccessDenied(() -> classUnderTest.allApplications(null, competitionId, null, 0, null, null, null),
                () -> {
                });
    }

    @Test
    public void testSubmittedApplications() {
        assertAccessDenied(() -> classUnderTest.submittedApplications(null, competitionId, null, 0, null, null),
                () -> {
                });
    }

    @Test
    public void testIneligibleApplications() {
        assertAccessDenied(() -> classUnderTest.ineligibleApplications(null, null, competitionId, null, 0, null, null),
                () -> {
                });
    }

    @Test
    public void testUnsuccessfulApplications() {
        assertAccessDenied(() -> classUnderTest.unsuccessfulApplications(null, competitionId, null, 0, 0, null, null),
                () -> {
                });
    }

    @Test
    public void testMarkApplicationAsSuccessful() {
        assertAccessDenied(() -> classUnderTest.markApplicationAsSuccessful(competitionId, 0L),
                () -> {
                });
    }

}
