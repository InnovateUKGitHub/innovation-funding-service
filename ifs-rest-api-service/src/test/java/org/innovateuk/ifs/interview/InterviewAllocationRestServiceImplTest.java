package org.innovateuk.ifs.interview;

import org.innovateuk.ifs.BaseRestServiceUnitTest;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.ParameterizedTypeReferences;
import org.innovateuk.ifs.interview.resource.InterviewAcceptedAssessorsPageResource;
import org.innovateuk.ifs.interview.resource.InterviewApplicationPageResource;
import org.innovateuk.ifs.interview.resource.InterviewApplicationResource;
import org.innovateuk.ifs.interview.service.InterviewAllocationRestServiceImpl;
import org.junit.Test;

import java.util.List;

import static java.lang.String.format;
import static java.util.Arrays.asList;
import static org.innovateuk.ifs.commons.service.ParameterizedTypeReferences.interviewApplicationsResourceListType;
import static org.innovateuk.ifs.interview.builder.InterviewAcceptedAssessorsPageResourceBuilder.newInterviewAcceptedAssessorsPageResource;
import static org.innovateuk.ifs.interview.builder.InterviewApplicationPageResourceBuilder.newInterviewApplicationPageResource;
import static org.innovateuk.ifs.interview.builder.InterviewApplicationResourceBuilder.newInterviewApplicationResource;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.springframework.http.HttpStatus.OK;

public class InterviewAllocationRestServiceImplTest extends BaseRestServiceUnitTest<InterviewAllocationRestServiceImpl> {

    private static final String restUrl = "/interview-panel";

    @Override
    protected InterviewAllocationRestServiceImpl registerRestServiceUnderTest() {
        InterviewAllocationRestServiceImpl InterviewAllocateRestService = new InterviewAllocationRestServiceImpl();
        return InterviewAllocateRestService;
    }

    @Test
    public void getAllocateApplicationsOverview() throws Exception {
        long competitionId = 1L;
        int page = 1;

        InterviewAcceptedAssessorsPageResource expected = newInterviewAcceptedAssessorsPageResource().build();

        String expectedUrl = format("%s/%s/%s?page=1", restUrl, "allocate-assessors", competitionId);

        setupGetWithRestResultExpectations(expectedUrl, InterviewAcceptedAssessorsPageResource.class, expected);

        InterviewAcceptedAssessorsPageResource actual = service.getInterviewAcceptedAssessors(competitionId, page)
                .getSuccess();

        assertEquals(expected, actual);
    }

    @Test
    public void getAllocatedApplications() {
        long competitionId = 1L;
        long userId = 1L;
        int page = 1;

        InterviewApplicationPageResource expected = newInterviewApplicationPageResource().build();

        String expectedUrl = format("%s/%s/%s/%s?page=1", restUrl, competitionId, "allocated-applications", userId);

        setupGetWithRestResultExpectations(expectedUrl, InterviewApplicationPageResource.class, expected);

        InterviewApplicationPageResource actual = service.getAllocatedApplications(competitionId, userId, page)
                .getSuccess();

        assertEquals(expected, actual);
    }

    @Test
    public void getAllAllocatedApplications() {
        long competitionId = 1L;
        List<Long> applicationIds = asList(1L, 2L, 3L);

        List<InterviewApplicationResource> expected = newInterviewApplicationResource().build(3);

        String expectedUrl = format("%s/%s/%s/%s/%s", restUrl, competitionId, "unallocated-applications", "all", "1,2,3");

        setupGetWithRestResultExpectations(expectedUrl, interviewApplicationsResourceListType(), expected);

        List<InterviewApplicationResource> actual = service.getUnallocatedApplicationsById(competitionId, applicationIds).getSuccess();

        assertEquals(expected, actual);
    }

    @Test
    public void getUnallocatedApplications() {
        long competitionId = 1L;
        long userId = 1L;
        int page = 1;

        InterviewApplicationPageResource expected = newInterviewApplicationPageResource().build();

        String expectedUrl = format("%s/%s/%s/%s?page=1", restUrl, competitionId, "unallocated-applications", userId);

        setupGetWithRestResultExpectations(expectedUrl, InterviewApplicationPageResource.class, expected);

        InterviewApplicationPageResource actual = service.getUnallocatedApplications(competitionId, userId, page)
                .getSuccess();

        assertEquals(expected, actual);
    }

    @Test
    public void getUnallocatedApplicationIds() {
        long competitionId = 1L;
        long userId = 1L;

        List<Long> expected = asList(1L);

        String expectedUrl = format("%s/%s/%s/%s", restUrl, competitionId, "unallocated-application-ids", userId);

        setupGetWithRestResultExpectations(expectedUrl, ParameterizedTypeReferences.longsListType(), expected);

        List<Long> actual = service.getUnallocatedApplicationIds(competitionId, userId)
                .getSuccess();

        assertEquals(expected, actual);
    }

    @Test
    public void unallocateApplications() {
        long applicationId = 1L;
        long assessorId = 1L;

        setupPostWithRestResultExpectations(format("%s/%s/%d/%s/%d", restUrl, "allocated-applications", assessorId, "unallocate", applicationId), OK);

        RestResult<Void> restResult = service.unallocateApplication(assessorId, applicationId);
        assertTrue(restResult.isSuccess());
    }
}