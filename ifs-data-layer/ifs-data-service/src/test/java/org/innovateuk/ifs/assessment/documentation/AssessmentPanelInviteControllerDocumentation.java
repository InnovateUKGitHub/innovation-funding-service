package org.innovateuk.ifs.assessment.documentation;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.assessment.controller.AssessmentPanelInviteController;
import org.innovateuk.ifs.invite.resource.ExistingUserStagedInviteListResource;
import org.innovateuk.ifs.invite.resource.ExistingUserStagedInviteResource;
import org.junit.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;

import java.util.List;

import static com.google.common.primitives.Longs.asList;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.documentation.AssessorCreatedInvitePageResourceDocs.assessorCreatedInvitePageResourceBuilder;
import static org.innovateuk.ifs.documentation.AssessorCreatedInvitePageResourceDocs.assessorCreatedInvitePageResourceFields;
import static org.innovateuk.ifs.documentation.AssessorCreatedInviteResourceDocs.assessorCreatedInviteResourceFields;
import static org.innovateuk.ifs.documentation.AvailableAssessorPageResourceDocs.availableAssessorPageResourceBuilder;
import static org.innovateuk.ifs.documentation.AvailableAssessorPageResourceDocs.availableAssessorPageResourceFields;
import static org.innovateuk.ifs.documentation.AvailableAssessorResourceDocs.availableAssessorResourceFields;
import static org.innovateuk.ifs.documentation.CompetitionInviteDocs.existingUserStagedInviteListResourceBuilder;
import static org.innovateuk.ifs.documentation.CompetitionInviteDocs.existingUserStagedInviteResourceFields;
import static org.innovateuk.ifs.util.JsonMappingUtil.toJson;
import static org.mockito.Mockito.*;
import static org.springframework.data.domain.Sort.Direction.ASC;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AssessmentPanelInviteControllerDocumentation extends BaseControllerMockMVCTest<AssessmentPanelInviteController> {

    @Override
    protected AssessmentPanelInviteController supplyControllerUnderTest() {
        return new AssessmentPanelInviteController();
    }

    @Test
    public void getAvailableAssessors() throws Exception {
        long competitionId = 1L;

        Pageable pageable = new PageRequest(0, 20, new Sort(ASC, "firstName"));

        when(assessmentPanelInviteServiceMock.getAvailableAssessors(competitionId, pageable))
                .thenReturn(serviceSuccess(availableAssessorPageResourceBuilder.build()));

        mockMvc.perform(get("/assessmentpanelinvite/getAvailableAssessors/{competitionId}", competitionId)
                .param("size", "20")
                .param("page", "0")
                .param("sort", "firstName,asc"))
                .andExpect(status().isOk())
                .andDo(document("assessmentpanelinvite/{method-name}",
                        pathParameters(
                                parameterWithName("competitionId").description("Id of the competition")
                        ),
                        requestParameters(
                                parameterWithName("size").optional()
                                        .description("Maximum number of elements in a single page. Defaults to 20."),
                                parameterWithName("page").optional()
                                        .description("Page number of the paginated data. Starts at 0. Defaults to 0."),
                                parameterWithName("sort").optional()
                                        .description("The property to sort the elements on. For example `sort=firstName,asc`. Defaults to `firstName,asc`")
                        ),
                        responseFields(availableAssessorPageResourceFields)
                                .andWithPrefix("content[].", availableAssessorResourceFields)
                ));

        verify(assessmentPanelInviteServiceMock, only()).getAvailableAssessors(competitionId, pageable);
    }

    @Test
    public void getAvailableAssessorIds() throws Exception {
        long competitionId = 1L;

        when(assessmentPanelInviteServiceMock.getAvailableAssessorIds(competitionId))
                .thenReturn(serviceSuccess(asList(1L, 2L)));

        mockMvc.perform(get("/assessmentpanelinvite/getAvailableAssessorIds/{competitionId}", competitionId))
                .andExpect(status().isOk())
                .andDo(document("assessmentpanelinvite/{method-name}",
                        pathParameters(
                                parameterWithName("competitionId").description("Id of the competition")
                        ),
                        responseFields(fieldWithPath("[]").description("List of available assessor ids "))
                ));

        verify(assessmentPanelInviteServiceMock, only()).getAvailableAssessorIds(competitionId);
    }

    @Test
    public void getCreatedInvites() throws Exception {
        long competitionId = 1L;

        Pageable pageable = new PageRequest(0, 20, new Sort(ASC, "name"));

        when(assessmentPanelInviteServiceMock.getCreatedInvites(competitionId, pageable)).thenReturn(serviceSuccess(assessorCreatedInvitePageResourceBuilder.build()));

        mockMvc.perform(get("/assessmentpanelinvite/getCreatedInvites/{competitionId}", 1L)
                .param("size", "20")
                .param("page", "0")
                .param("sort", "name,asc"))
                .andExpect(status().isOk())
                .andDo(document("assessmentpanelinvite/{method-name}",
                        pathParameters(
                                parameterWithName("competitionId").description("Id of the competition")
                        ),
                        requestParameters(
                                parameterWithName("size").optional()
                                        .description("Maximum number of elements in a single page. Defaults to 20."),
                                parameterWithName("page").optional()
                                        .description("Page number of the paginated data. Starts at 0. Defaults to 0."),
                                parameterWithName("sort").optional()
                                        .description("The property to sort the elements on. For example `sort=name,asc`. Defaults to `name,asc`")
                        ),
                        responseFields(assessorCreatedInvitePageResourceFields)
                                .andWithPrefix("content[]", assessorCreatedInviteResourceFields)
                ));

        verify(assessmentPanelInviteServiceMock, only()).getCreatedInvites(competitionId, pageable);
    }

    @Test
    public void inviteUsers() throws Exception {
        ExistingUserStagedInviteListResource existingUserStagedInviteListResource = existingUserStagedInviteListResourceBuilder.build();
        List<ExistingUserStagedInviteResource> existingUserStagedInviteResources = existingUserStagedInviteListResource.getInvites();

        when(assessmentPanelInviteServiceMock.inviteUsers(existingUserStagedInviteResources)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/assessmentpanelinvite/inviteUsers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(existingUserStagedInviteListResource)))
                .andExpect(status().isOk())
                .andDo(document("assessmentpanelinvite/{method-name}",
                        requestFields(
                                fieldWithPath("invites[]").description("List of existing users to be invited to the assessment panel")
                        ).andWithPrefix("invites[].", existingUserStagedInviteResourceFields)
                ));

        verify(assessmentPanelInviteServiceMock, only()).inviteUsers(existingUserStagedInviteResources);
    }
}
