package org.innovateuk.ifs.assessment.service;

import org.innovateuk.ifs.BaseRestServiceUnitTest;
import org.innovateuk.ifs.interview.resource.InterviewAcceptedAssessorsPageResource;
import org.innovateuk.ifs.interview.service.InterviewAllocationRestServiceImpl;
import org.junit.Test;

import static java.lang.String.format;
import static org.innovateuk.ifs.interview.builder.InterviewAcceptedAssessorsPageResourceBuilder.newInterviewAcceptedAssessorsPageResource;
import static org.junit.Assert.assertEquals;

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
}