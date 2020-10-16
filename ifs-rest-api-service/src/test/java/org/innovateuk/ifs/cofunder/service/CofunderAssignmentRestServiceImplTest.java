package org.innovateuk.ifs.cofunder.service;

import org.innovateuk.ifs.BaseRestServiceUnitTest;
import org.innovateuk.ifs.cofunder.resource.ApplicationsForCofundingPageResource;
import org.innovateuk.ifs.cofunder.resource.CofunderAssignmentResource;
import org.innovateuk.ifs.cofunder.resource.CofunderDecisionResource;
import org.innovateuk.ifs.cofunder.resource.CofundersAvailableForApplicationPageResource;
import org.junit.Test;

import static java.lang.String.format;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.springframework.http.HttpStatus.OK;

public class CofunderAssignmentRestServiceImplTest extends BaseRestServiceUnitTest<CofunderAssignmentRestServiceImpl> {

    private String cofunderRestUrl = "/cofunder";

    @Override
    protected CofunderAssignmentRestServiceImpl registerRestServiceUnderTest() {
        return new CofunderAssignmentRestServiceImpl();
    }

    @Test
    public void getAssignment() {
        long userId = 1L;
        long applicationId = 2L;
        CofunderAssignmentResource expected = new CofunderAssignmentResource();

        setupGetWithRestResultExpectations(format("%s/assignment/user/%d/application/%d", cofunderRestUrl, userId, applicationId), CofunderAssignmentResource.class, expected);
        assertSame(expected, service.getAssignment(userId, applicationId).getSuccess());
    }

    @Test
    public void assign() {
        long userId = 1L;
        long applicationId = 2L;
        CofunderAssignmentResource expected = new CofunderAssignmentResource();

        setupPostWithRestResultExpectations(format("%s/user/%d/application/%d", cofunderRestUrl, userId, applicationId), CofunderAssignmentResource.class, null, expected, OK);
        assertSame(expected, service.assign(userId, applicationId).getSuccess());
    }

    @Test
    public void removeAssignment() {
        long userId = 1L;
        long applicationId = 2L;

        setupDeleteWithRestResultExpectations(format("%s/user/%d/application/%d", cofunderRestUrl, userId, applicationId), OK);
        assertTrue(service.removeAssignment(userId, applicationId).isSuccess());
    }

    @Test
    public void decision() {
        long assignmentId = 1L;
        CofunderDecisionResource decision = new CofunderDecisionResource();

        setupPostWithRestResultExpectations(format("%s/assignment/%d/decision", cofunderRestUrl, assignmentId), Void.class, decision, null, OK);
        assertTrue(service.decision(assignmentId, decision).isSuccess());
    }

    @Test
    public void edit() {
        long assignmentId = 1L;

        setupPostWithRestResultExpectations(format("%s/assignment/%d/edit", cofunderRestUrl, assignmentId), OK);
        assertTrue(service.edit(assignmentId).isSuccess());
    }

    @Test
    public void findApplicationsNeedingCofunders() {
        long competitionId = 1L;
        String filter = "filter";
        int page = 1;
        ApplicationsForCofundingPageResource expected = new ApplicationsForCofundingPageResource();

        setupGetWithRestResultExpectations(format("%s/competition/%d?page=%d&filter=%s", cofunderRestUrl, competitionId, page, filter), ApplicationsForCofundingPageResource.class, expected);
        assertSame(expected, service.findApplicationsNeedingCofunders(competitionId, filter, page).getSuccess());
    }

    @Test
    public void findAvailableCofundersForApplication() {
        long applicationId = 1L;
        String filter = "filter";
        int page = 1;
        CofundersAvailableForApplicationPageResource expected = new CofundersAvailableForApplicationPageResource();

        setupGetWithRestResultExpectations(format("%s/application/%d?page=%d&filter=%s", cofunderRestUrl, applicationId, page, filter), CofundersAvailableForApplicationPageResource.class, expected);
        assertSame(expected, service.findAvailableCofundersForApplication(applicationId, filter, page).getSuccess());
    }
}
