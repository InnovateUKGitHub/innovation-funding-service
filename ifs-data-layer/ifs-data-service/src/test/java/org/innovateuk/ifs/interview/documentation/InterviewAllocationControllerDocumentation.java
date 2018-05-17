package org.innovateuk.ifs.interview.documentation;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.interview.controller.InterviewAllocationController;
import org.innovateuk.ifs.interview.resource.InterviewAcceptedAssessorsPageResource;
import org.innovateuk.ifs.interview.resource.InterviewAcceptedAssessorsResource;
import org.innovateuk.ifs.interview.resource.InterviewApplicationPageResource;
import org.innovateuk.ifs.interview.resource.InterviewApplicationResource;
import org.innovateuk.ifs.interview.transactional.InterviewAllocationService;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;

import static java.util.Arrays.asList;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.documentation.InterviewAcceptedAssessorsPageResourceDocs.interviewAssessorAllocateApplicationsPageResourceFields;
import static org.innovateuk.ifs.documentation.InterviewAcceptedAssessorsResourceDocs.interviewAcceptedAssessorsResourceFields;
import static org.innovateuk.ifs.documentation.InterviewApplicationPageResourceDocs.InterviewApplicationPageResourceFields;
import static org.innovateuk.ifs.documentation.InterviewApplicationResourceDocs.InterviewApplicationResourceFields;
import static org.innovateuk.ifs.interview.builder.InterviewAcceptedAssessorsPageResourceBuilder.newInterviewAcceptedAssessorsPageResource;
import static org.innovateuk.ifs.interview.builder.InterviewAcceptedAssessorsResourceBuilder.newInterviewAcceptedAssessorsResource;
import static org.innovateuk.ifs.interview.builder.InterviewApplicationPageResourceBuilder.newInterviewApplicationPageResource;
import static org.innovateuk.ifs.interview.builder.InterviewApplicationResourceBuilder.newInterviewApplicationResource;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.data.domain.Sort.Direction.ASC;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class InterviewAllocationControllerDocumentation extends BaseControllerMockMVCTest<InterviewAllocationController> {

    @Mock
    private InterviewAllocationService interviewAllocationServiceMock;

    @Override
    protected InterviewAllocationController supplyControllerUnderTest() {
        return new InterviewAllocationController();
    }

    @Test
    public void getInterviewAcceptedAssessors() throws Exception {
        long competitionId = 1L;

        Pageable pageable = new PageRequest(0, 20, new Sort(ASC, "invite.name"));

        List<InterviewAcceptedAssessorsResource> content = newInterviewAcceptedAssessorsResource().build(2);

        InterviewAcceptedAssessorsPageResource expectedPageResource = newInterviewAcceptedAssessorsPageResource()
                .withContent(content)
                .build();

        when(interviewAllocationServiceMock.getInterviewAcceptedAssessors(competitionId, pageable))
                .thenReturn(serviceSuccess(expectedPageResource));

        mockMvc.perform(get("/interview-panel/allocate-assessors/{competitionId}", 1L)
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
                                .andWithPrefix("content[].", interviewAcceptedAssessorsResourceFields)
                ));

        verify(interviewAllocationServiceMock, only()).getInterviewAcceptedAssessors(competitionId, pageable);
    }

    @Test
    public void getAllocatedApplications() throws Exception {
        long competitionId = 1L;
        long userId = 2L;

        Pageable pageable = new PageRequest(0, 20, new Sort(ASC, "target.id"));

        List<InterviewApplicationResource> content = newInterviewApplicationResource().build(2);

        InterviewApplicationPageResource expectedPageResource = newInterviewApplicationPageResource()
                .withContent(content)
                .build();

        when(interviewAllocationServiceMock.getAllocatedApplicationsById(competitionId, userId, pageable))
                .thenReturn(serviceSuccess(expectedPageResource));

        mockMvc.perform(get("/interview-panel/{competitionId}/allocated-applications/{userId}", competitionId, userId)
                .param("size", "20")
                .param("page", "0")
                .param("sort", "target.id,asc"))
                .andExpect(status().isOk())
                .andDo(document("interview-panel/{method-name}",
                        pathParameters(
                                parameterWithName("competitionId").description("Id of the competition"),
                                parameterWithName("userId").description("Id of the assessor")
                        ),
                        requestParameters(
                                parameterWithName("size").optional()
                                        .description("Maximum number of elements in a single page. Defaults to 20."),
                                parameterWithName("page").optional()
                                        .description("Page number of the paginated data. Starts at 0. Defaults to 0."),
                                parameterWithName("sort").optional()
                                        .description("The property to sort the elements on. For example `sort=invite.name,asc`. Defaults to `invite.name,asc`")
                        ),
                        responseFields(InterviewApplicationPageResourceFields)
                                .andWithPrefix("content[].", InterviewApplicationResourceFields)
                ));

        verify(interviewAllocationServiceMock, only()).getAllocatedApplicationsById(competitionId, userId, pageable);
    }

    @Test
    public void getUnallocatedApplications() throws Exception {
        long competitionId = 1L;
        long userId = 2L;

        Pageable pageable = new PageRequest(0, 20, new Sort(ASC, "target.id"));

        List<InterviewApplicationResource> content = newInterviewApplicationResource().build(2);

        InterviewApplicationPageResource expectedPageResource = newInterviewApplicationPageResource()
                .withContent(content)
                .build();

        when(interviewAllocationServiceMock.getUnallocatedApplications(competitionId, userId, pageable))
                .thenReturn(serviceSuccess(expectedPageResource));

        mockMvc.perform(get("/interview-panel/{competitionId}/unallocated-applications/{userId}", competitionId, userId)
                .param("size", "20")
                .param("page", "0")
                .param("sort", "target.id,asc"))
                .andExpect(status().isOk())
                .andDo(document("interview-panel/{method-name}",
                        pathParameters(
                                parameterWithName("competitionId").description("Id of the competition"),
                                parameterWithName("userId").description("Id of the assessor")
                        ),
                        requestParameters(
                                parameterWithName("size").optional()
                                        .description("Maximum number of elements in a single page. Defaults to 20."),
                                parameterWithName("page").optional()
                                        .description("Page number of the paginated data. Starts at 0. Defaults to 0."),
                                parameterWithName("sort").optional()
                                        .description("The property to sort the elements on. For example `sort=invite.name,asc`. Defaults to `invite.name,asc`")
                        ),
                        responseFields(InterviewApplicationPageResourceFields)
                                .andWithPrefix("content[].", InterviewApplicationResourceFields)
                ));

        verify(interviewAllocationServiceMock, only()).getUnallocatedApplications(competitionId, userId, pageable);
    }

    @Test
    public void getUnallocatedApplicationIds() throws Exception {
        long competitionId = 1L;
        long userId = 2L;

        List<Long> expectedIds = asList(1L);

        when(interviewAllocationServiceMock.getUnallocatedApplicationIds(competitionId, userId))
                .thenReturn(serviceSuccess(expectedIds));

        mockMvc.perform(get("/interview-panel/{competitionId}/unallocated-application-ids/{userId}", competitionId, userId))
                .andExpect(status().isOk())
                .andDo(document("interview-panel/{method-name}",
                        pathParameters(
                                parameterWithName("competitionId").description("Id of the competition"),
                                parameterWithName("userId").description("Id of the assessor")
                        ),
                        responseFields(fieldWithPath("[]").description("List of ids of applications"))
                ));

        verify(interviewAllocationServiceMock, only()).getUnallocatedApplicationIds(competitionId, userId);
    }
}
