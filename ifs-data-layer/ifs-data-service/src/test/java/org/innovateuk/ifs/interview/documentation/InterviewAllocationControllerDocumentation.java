package org.innovateuk.ifs.interview.documentation;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.interview.controller.InterviewAllocationController;
import org.innovateuk.ifs.interview.resource.InterviewAcceptedAssessorsPageResource;
import org.innovateuk.ifs.interview.resource.InterviewAcceptedAssessorsResource;
import org.junit.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.documentation.InterviewAcceptedAssessorsPageResourceDocs.interviewAssessorAllocateApplicationsPageResourceFields;
import static org.innovateuk.ifs.documentation.InterviewAcceptedAssessorsResourceDocs.interviewAssessorAllocateApplicationsResourceFields;
import static org.innovateuk.ifs.invite.builder.InterviewAcceptedAssessorsPageResourceBuilder.newInterviewAssessorAllocateApplicationsPageResource;
import static org.innovateuk.ifs.invite.builder.InterviewAcceptedAssessorsResourceBuilder.newInterviewAssessorAllocateApplicationsResource;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.data.domain.Sort.Direction.ASC;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class InterviewAllocationControllerDocumentation extends BaseControllerMockMVCTest<InterviewAllocationController> {

    @Override
    protected InterviewAllocationController supplyControllerUnderTest() {
        return new InterviewAllocationController();
    }

    @Test
    public void getAllocateApplicationsOverview() throws Exception {
        long competitionId = 1L;

        Pageable pageable = new PageRequest(0, 20, new Sort(ASC, "invite.name"));

        List<InterviewAcceptedAssessorsResource> content = newInterviewAssessorAllocateApplicationsResource().build(2);

        InterviewAcceptedAssessorsPageResource expectedPageResource = newInterviewAssessorAllocateApplicationsPageResource()
                .withContent(content)
                .build();

        when(interviewAllocationServiceMock.getInterviewAcceptedAssessors(competitionId, pageable))
                .thenReturn(serviceSuccess(expectedPageResource));

        mockMvc.perform(get("/interview-panel/allocate-overview/{competitionId}", 1L)
                .param("size", "20")
                .param("page", "0")
                .param("sort", "invite.name,asc"))
                .andExpect(status().isOk())
                .andDo(document("interview-panel/{method-name}",
                        pathParameters(
                                parameterWithName("competitionId").description("Id of the competition")
                        ),
                        requestParameters(
                                parameterWithName("size").optional()
                                        .description("Maximum number of elements in a single page. Defaults to 20."),
                                parameterWithName("page").optional()
                                        .description("Page number of the paginated data. Starts at 0. Defaults to 0."),
                                parameterWithName("sort").optional()
                                        .description("The property to sort the elements on. For example `sort=invite.name,asc`. Defaults to `invite.name,asc`")
                        ),
                        responseFields(interviewAssessorAllocateApplicationsPageResourceFields)
                                .andWithPrefix("content[].", interviewAssessorAllocateApplicationsResourceFields)
                ));

        verify(interviewAllocationServiceMock, only()).getInterviewAcceptedAssessors(competitionId, pageable);
    }
}
