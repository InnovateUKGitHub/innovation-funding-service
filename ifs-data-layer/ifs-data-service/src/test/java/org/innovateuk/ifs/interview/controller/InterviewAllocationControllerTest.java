package org.innovateuk.ifs.interview.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.interview.resource.InterviewAcceptedAssessorsPageResource;
import org.innovateuk.ifs.interview.resource.InterviewApplicationPageResource;
import org.junit.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;

import static java.util.Arrays.asList;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.interview.builder.InterviewAcceptedAssessorsPageResourceBuilder.newInterviewAcceptedAssessorsPageResource;
import static org.innovateuk.ifs.interview.builder.InterviewAcceptedAssessorsResourceBuilder.newInterviewAcceptedAssessorsResource;
import static org.innovateuk.ifs.interview.builder.InterviewApplicationPageResourceBuilder.newInterviewApplicationPageResource;
import static org.innovateuk.ifs.interview.builder.InterviewApplicationResourceBuilder.newInterviewApplicationResource;
import static org.innovateuk.ifs.util.JsonMappingUtil.toJson;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class InterviewAllocationControllerTest extends BaseControllerMockMVCTest<InterviewAllocationController> {

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


    @Test
    public void getAllocatedApplications() throws Exception {
        long competitionId = 1L;
        long userId = 2L;
        int page = 2;
        int size = 10;

        InterviewApplicationPageResource expectedPageResource = newInterviewApplicationPageResource()
                .withContent(newInterviewApplicationResource().build(2))
                .build();

        Pageable pageable = new PageRequest(page, size, new Sort(Sort.Direction.ASC, "target.id"));

        when(interviewAllocationServiceMock.getAllocatedApplications(competitionId, userId, pageable))
                .thenReturn(serviceSuccess(expectedPageResource));

        mockMvc.perform(get("/interview-panel/{competitionId}/allocated-applications/{userId}", competitionId, userId)
                .param("page", "2")
                .param("size", "10")
                .param("sort", "target.id"))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(expectedPageResource)));

        verify(interviewAllocationServiceMock, only()).getAllocatedApplications(competitionId, userId, pageable);
    }

    @Test
    public void getUnallocatedApplications() throws Exception {
        long competitionId = 1L;
        long userId = 2L;
        int page = 2;
        int size = 10;

        InterviewApplicationPageResource expectedPageResource = newInterviewApplicationPageResource()
                .withContent(newInterviewApplicationResource().build(2))
                .build();

        Pageable pageable = new PageRequest(page, size, new Sort(Sort.Direction.ASC, "target.id"));

        when(interviewAllocationServiceMock.getUnallocatedApplications(competitionId, userId, pageable))
                .thenReturn(serviceSuccess(expectedPageResource));

        mockMvc.perform(get("/interview-panel/{competitionId}/unallocated-applications/{userId}", competitionId, userId)
                .param("page", "2")
                .param("size", "10")
                .param("sort", "target.id"))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(expectedPageResource)));

        verify(interviewAllocationServiceMock, only()).getUnallocatedApplications(competitionId, userId, pageable);
    }

    @Test
    public void getUnallocatedApplicationIds() throws Exception {
        long competitionId = 1L;
        long userId = 2L;

        List<Long> ids = asList(1L);

        when(interviewAllocationServiceMock.getUnallocatedApplicationIds(competitionId, userId))
                .thenReturn(serviceSuccess(ids));

        mockMvc.perform(get("/interview-panel/{competitionId}/unallocated-application-ids/{userId}", competitionId, userId)
                .param("page", "2")
                .param("size", "10")
                .param("sort", "target.id"))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(ids)));

        verify(interviewAllocationServiceMock, only()).getUnallocatedApplicationIds(competitionId, userId);
    }
}