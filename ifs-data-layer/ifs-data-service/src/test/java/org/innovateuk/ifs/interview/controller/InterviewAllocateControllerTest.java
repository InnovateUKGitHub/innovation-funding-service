package org.innovateuk.ifs.interview.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.interview.resource.InterviewAllocateOverviewPageResource;
import org.junit.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.invite.builder.InterviewAllocateOverviewPageResourceBuilder.newInterviewAssessorAllocateApplicationsPageResource;
import static org.innovateuk.ifs.invite.builder.InterviewAllocateOverviewResourceBuilder.newInterviewAssessorAllocateApplicationsResource;
import static org.innovateuk.ifs.util.JsonMappingUtil.toJson;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class InterviewAllocateControllerTest extends BaseControllerMockMVCTest<InterviewAllocateController> {

    @Override
    protected InterviewAllocateController supplyControllerUnderTest() {
        return new InterviewAllocateController();
    }

    @Test
    public void getAllocateApplicationsOverview() throws Exception {
        long competitionId = 1L;
        int page = 2;
        int size = 10;

        InterviewAllocateOverviewPageResource expectedPageResource = newInterviewAssessorAllocateApplicationsPageResource()
                .withContent(newInterviewAssessorAllocateApplicationsResource().build(2))
                .build();

        Pageable pageable = new PageRequest(page, size, new Sort(Sort.Direction.ASC, "invite.email"));

        when(interviewAllocateServiceMock.getAllocateApplicationsOverview(competitionId, pageable))
                .thenReturn(serviceSuccess(expectedPageResource));

        mockMvc.perform(get("/interview-panel/allocate-overview/{competitionId}", competitionId)
                .param("page", "2")
                .param("size", "10")
                .param("sort", "invite.email"))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(expectedPageResource)));

        verify(interviewAllocateServiceMock, only()).getAllocateApplicationsOverview(competitionId, pageable);
    }
}