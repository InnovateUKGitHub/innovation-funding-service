package org.innovateuk.ifs.interview.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.interview.resource.InterviewAcceptedAssessorsPageResource;
import org.innovateuk.ifs.interview.transactional.InterviewAllocationService;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.invite.builder.InterviewAcceptedAssessorsPageResourceBuilder.newInterviewAcceptedAssessorsPageResource;
import static org.innovateuk.ifs.invite.builder.InterviewAcceptedAssessorsResourceBuilder.newInterviewAcceptedAssessorsResource;
import static org.innovateuk.ifs.util.JsonMappingUtil.toJson;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class InterviewAllocationControllerTest extends BaseControllerMockMVCTest<InterviewAllocationController> {

    @Mock
    private InterviewAllocationService interviewAllocationServiceMock;


    @Override
    protected InterviewAllocationController supplyControllerUnderTest() {
        return new InterviewAllocationController();
    }

    @Test
    public void getInterviewAcceptedAssessors() throws Exception {
        long competitionId = 1L;
        int page = 2;
        int size = 10;

        InterviewAcceptedAssessorsPageResource expectedPageResource = newInterviewAcceptedAssessorsPageResource()
                .withContent(newInterviewAcceptedAssessorsResource().build(2))
                .build();

        Pageable pageable = new PageRequest(page, size, new Sort(Sort.Direction.ASC, "invite.email"));

        when(interviewAllocationServiceMock.getInterviewAcceptedAssessors(competitionId, pageable))
                .thenReturn(serviceSuccess(expectedPageResource));

        mockMvc.perform(get("/interview-panel/allocate-assessors/{competitionId}", competitionId)
                .param("page", "2")
                .param("size", "10")
                .param("sort", "invite.email"))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(expectedPageResource)));

        verify(interviewAllocationServiceMock, only()).getInterviewAcceptedAssessors(competitionId, pageable);
    }
}