package org.innovateuk.ifs.interview.documentation;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.interview.controller.InterviewAllocationController;
import org.innovateuk.ifs.interview.resource.*;
import org.innovateuk.ifs.interview.transactional.InterviewAllocationService;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;

import java.util.Collections;
import java.util.List;

import static java.util.Arrays.asList;
import static org.apache.commons.lang3.StringUtils.join;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.documentation.AssessorInviteToSendDocs.ASSESSOR_INVITES_TO_SEND_FIELDS;
import static org.innovateuk.ifs.documentation.InterviewAcceptedAssessorsPageResourceDocs.interviewAssessorAllocateApplicationsPageResourceFields;
import static org.innovateuk.ifs.documentation.InterviewAcceptedAssessorsResourceDocs.interviewAcceptedAssessorsResourceFields;
import static org.innovateuk.ifs.documentation.InterviewApplicationPageResourceDocs.InterviewApplicationPageResourceFields;
import static org.innovateuk.ifs.documentation.InterviewApplicationResourceDocs.InterviewApplicationResourceFields;
import static org.innovateuk.ifs.documentation.InterviewNotifyAllocationResourceDocs.INTERVIEW_ALLOCATION_RESOURCE_FIELDS;
import static org.innovateuk.ifs.interview.builder.InterviewAcceptedAssessorsPageResourceBuilder.newInterviewAcceptedAssessorsPageResource;
import static org.innovateuk.ifs.interview.builder.InterviewAcceptedAssessorsResourceBuilder.newInterviewAcceptedAssessorsResource;
import static org.innovateuk.ifs.interview.builder.InterviewApplicationPageResourceBuilder.newInterviewApplicationPageResource;
import static org.innovateuk.ifs.interview.builder.InterviewApplicationResourceBuilder.newInterviewApplicationResource;
import static org.innovateuk.ifs.interview.builder.InterviewNotifyAllocationResourceBuilder.newInterviewNotifyAllocationResource;
import static org.innovateuk.ifs.interview.builder.InterviewResourceBuilder.newInterviewResource;
import static org.innovateuk.ifs.invite.builder.AssessorInvitesToSendResourceBuilder.newAssessorInvitesToSendResource;
import static org.innovateuk.ifs.util.JsonMappingUtil.toJson;
import static org.mockito.Mockito.*;
import static org.springframework.data.domain.Sort.Direction.ASC;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
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

        when(interviewAllocationServiceMock.getAllocatedApplications(competitionId, userId, pageable))
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

        verify(interviewAllocationServiceMock, only()).getAllocatedApplications(competitionId, userId, pageable);
    }

    @Test
    public void getAllocatedApplicationsByAssessorId() throws Exception {
        long competitionId = 1L;
        long userId = 2L;

        InterviewResource interviewResource = newInterviewResource()
                .build();

        when(interviewAllocationServiceMock.getAllocatedApplicationsByAssessorId(competitionId, userId))
                .thenReturn(serviceSuccess(Collections.singletonList(interviewResource)));

        mockMvc.perform(get("/interview-panel/{competitionId}/allocated-applications-assessorId/{userId}", competitionId, userId))
                .andExpect(status().isOk())
                .andDo(document("interview-panel/{method-name}",
                        pathParameters(
                                parameterWithName("competitionId").description("Id of the competition"),
                                parameterWithName("userId").description("Id of the assessor")
                        ),
                        responseFields(fieldWithPath("[]").description("List of interviews"))
                ));

        verify(interviewAllocationServiceMock, only()).getAllocatedApplicationsByAssessorId(competitionId, userId);
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

    @Test
    public void unallocateApplication() throws Exception {
        long assessorId = 1L;
        long applicationId = 2L;

        when(interviewAllocationServiceMock.unallocateApplication(assessorId, applicationId)).thenReturn(serviceSuccess());
        mockMvc.perform(post("/interview-panel/allocated-applications/{assessorId}/unallocate/{applicationId}", assessorId, applicationId))
                .andExpect(status().isOk())
                .andDo(document("interview-panel/{method-name}",
                        pathParameters(
                                parameterWithName("assessorId").description("Id of the assessor"),
                                parameterWithName("applicationId").description("Id of the application to unassign from interview panel")
                                )));

        verify(interviewAllocationServiceMock, only()).unallocateApplication(assessorId, applicationId);
    }

    @Test
    public void getInviteToSend() throws Exception {
        long competitionId = 1L;
        long userId = 2L;

        when(interviewAllocationServiceMock.getInviteToSend(competitionId, userId))
                .thenReturn(serviceSuccess(newAssessorInvitesToSendResource().build()));

        mockMvc.perform(get("/interview-panel/{competitionId}/allocated-applications/{userId}/invite-to-send", competitionId, userId))
                .andExpect(status().isOk())
                .andDo(document("interview-panel/{method-name}",
                        pathParameters(
                                parameterWithName("competitionId").description("Id of the competition"),
                                parameterWithName("userId").description("Id of the assessor")
                        ),
                        responseFields(ASSESSOR_INVITES_TO_SEND_FIELDS)
                ));

        verify(interviewAllocationServiceMock, only()).getInviteToSend(competitionId, userId);
    }

    @Test
    public void sendInvite() throws Exception {
        long competitionId = 1L;
        long userId = 2L;

        InterviewNotifyAllocationResource interviewNotifyAllocationResource = newInterviewNotifyAllocationResource().build();

        when(interviewAllocationServiceMock.notifyAllocation(interviewNotifyAllocationResource))
                .thenReturn(serviceSuccess());

        mockMvc.perform(post("/interview-panel/{competitionId}/allocated-applications/{userId}/send-invite", competitionId, userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(interviewNotifyAllocationResource)))
                .andExpect(status().isOk())
                .andDo(document("interview-panel/{method-name}",
                        pathParameters(
                                parameterWithName("competitionId").description("Id of the competition"),
                                parameterWithName("userId").description("Id of the assessor")
                        ),
                        requestFields(INTERVIEW_ALLOCATION_RESOURCE_FIELDS)
                ));

        verify(interviewAllocationServiceMock, only()).notifyAllocation(interviewNotifyAllocationResource);
    }

    @Test
    public void getUnallocatedApplicationsById() throws Exception {
        long competitionId = 1L;
        List<Long> applicationIds = asList(3L, 5L);

        List<InterviewApplicationResource> interviewApplicationResources = newInterviewApplicationResource().build(2);

        when(interviewAllocationServiceMock.getUnallocatedApplicationsById(applicationIds))
                .thenReturn(serviceSuccess(interviewApplicationResources));

        mockMvc.perform(get("/interview-panel/{competitionId}/unallocated-applications/all/{applicationIds}", competitionId, join(applicationIds, ',')))
                .andExpect(status().isOk())
                .andDo(document("interview-panel/{method-name}",
                        pathParameters(
                                parameterWithName("competitionId").description("Id of the competition"),
                                parameterWithName("applicationIds").description("Ids of applications")
                        ),
                        responseFields(fieldWithPath("[]").description("List of unallocated applications"))
                ));

        verify(interviewAllocationServiceMock, only()).getUnallocatedApplicationsById(applicationIds);

    }
}