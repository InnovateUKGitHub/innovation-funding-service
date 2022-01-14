package org.innovateuk.ifs.supporter.service;

import org.innovateuk.ifs.BaseRestServiceUnitTest;
import org.innovateuk.ifs.supporter.resource.ApplicationsForCofundingPageResource;
import org.innovateuk.ifs.supporter.resource.SupporterAssignmentResource;
import org.innovateuk.ifs.supporter.resource.SupporterDecisionResource;
import org.innovateuk.ifs.supporter.resource.SupportersAvailableForApplicationPageResource;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static java.lang.String.format;
import static org.innovateuk.ifs.commons.service.ParameterizedTypeReferences.longsListType;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.springframework.http.HttpStatus.OK;

public class SupporterAssignmentRestServiceImplTest extends BaseRestServiceUnitTest<SupporterAssignmentRestServiceImpl> {

    private String supporterRestUrl = "/supporter";

    @Override
    protected SupporterAssignmentRestServiceImpl registerRestServiceUnderTest() {
        return new SupporterAssignmentRestServiceImpl();
    }

    @Test
    public void getAssignment() {
        long userId = 1L;
        long applicationId = 2L;
        SupporterAssignmentResource expected = new SupporterAssignmentResource();

        setupGetWithRestResultExpectations(format("%s/assignment/user/%d/application/%d", supporterRestUrl, userId, applicationId), SupporterAssignmentResource.class, expected);
        assertSame(expected, service.getAssignment(userId, applicationId).getSuccess());
    }

    @Test
    public void assign() {
        long userId = 1L;
        long applicationId = 2L;
        SupporterAssignmentResource expected = new SupporterAssignmentResource();

        setupPostWithRestResultExpectations(format("%s/user/%d/application/%d", supporterRestUrl, userId, applicationId), SupporterAssignmentResource.class, null, expected, OK);
        assertSame(expected, service.assign(userId, applicationId).getSuccess());
    }

    @Test
    public void removeAssignment() {
        long userId = 1L;
        long applicationId = 2L;

        setupDeleteWithRestResultExpectations(format("%s/user/%d/application/%d", supporterRestUrl, userId, applicationId), OK);
        assertTrue(service.removeAssignment(userId, applicationId).isSuccess());
    }

    @Test
    public void decision() {
        long assignmentId = 1L;
        SupporterDecisionResource decision = new SupporterDecisionResource();

        setupPostWithRestResultExpectations(format("%s/assignment/%d/decision", supporterRestUrl, assignmentId), Void.class, decision, null, OK);
        assertTrue(service.decision(assignmentId, decision).isSuccess());
    }

    @Test
    public void edit() {
        long assignmentId = 1L;

        setupPostWithRestResultExpectations(format("%s/assignment/%d/edit", supporterRestUrl, assignmentId), OK);
        assertTrue(service.edit(assignmentId).isSuccess());
    }

    @Test
    public void findApplicationsNeedingSupporters() {
        long competitionId = 1L;
        String filter = "filter";
        int page = 1;
        ApplicationsForCofundingPageResource expected = new ApplicationsForCofundingPageResource();

        setupGetWithRestResultExpectations(format("%s/competition/%d?page=%d&filter=%s", supporterRestUrl, competitionId, page, filter), ApplicationsForCofundingPageResource.class, expected);
        assertSame(expected, service.findApplicationsNeedingSupporters(competitionId, filter, page).getSuccess());
    }

    @Test
    public void findAvailableSupportersForApplication() {
        long applicationId = 1L;
        String filter = "filter";
        int page = 1;
        SupportersAvailableForApplicationPageResource expected = new SupportersAvailableForApplicationPageResource();

        setupGetWithRestResultExpectations(format("%s/application/%d?page=%d&filter=%s", supporterRestUrl, applicationId, page, filter), SupportersAvailableForApplicationPageResource.class, expected);
        assertSame(expected, service.findAvailableSupportersForApplication(applicationId, filter, page).getSuccess());
    }

    @Test
    public void findAvailableSupportersUserIdsForApplication() {
        long applicationId = 1L;
        String filter = "filter";
        List<Long> expected = Arrays.asList(1L, 2L, 3L);

        setupGetWithRestResultExpectations(format("%s/application/%d/userIds?filter=%s", supporterRestUrl, applicationId, filter), longsListType(), expected);
        assertSame(expected, service.findAllAvailableSupporterUserIdsForApplication(applicationId, filter).getSuccess());
    }
}
