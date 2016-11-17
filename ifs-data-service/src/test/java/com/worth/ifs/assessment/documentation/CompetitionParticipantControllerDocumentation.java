package com.worth.ifs.assessment.documentation;

import com.worth.ifs.BaseControllerMockMVCTest;
import com.worth.ifs.assessment.controller.CompetitionParticipantController;
import com.worth.ifs.assessment.resource.AssessmentStates;
import com.worth.ifs.invite.resource.CompetitionParticipantResource;
import com.worth.ifs.invite.resource.CompetitionParticipantRoleResource;
import com.worth.ifs.invite.resource.ParticipantStatusResource;
import com.worth.ifs.user.resource.UserRoleType;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;

import java.util.List;

import static com.worth.ifs.assessment.resource.AssessmentStates.ACCEPTED;
import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;
import static com.worth.ifs.documentation.CompetitionParticipantResourceDocs.competitionParticipantResourceBuilder;
import static com.worth.ifs.documentation.CompetitionParticipantResourceDocs.competitionParticipantResourceFields;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class CompetitionParticipantControllerDocumentation extends BaseControllerMockMVCTest<CompetitionParticipantController> {

    private RestDocumentationResultHandler document;

    @Override
    protected CompetitionParticipantController supplyControllerUnderTest() {
        return new CompetitionParticipantController();
    }

    @Before
    public void setup() {
        this.document = document("competitionparticipant/{method-name}",
                preprocessResponse(prettyPrint()));
    }

    @Test
    public void getParticipants() throws Exception {
        Long userId = 1L;
        CompetitionParticipantRoleResource role = CompetitionParticipantRoleResource.ASSESSOR;
        ParticipantStatusResource status = ParticipantStatusResource.ACCEPTED;

        List<CompetitionParticipantResource> competitionParticipants = competitionParticipantResourceBuilder.build(2);

        when(competitionParticipantServiceMock.getCompetitionParticipants(userId, role, status)).thenReturn(serviceSuccess(competitionParticipants));

        mockMvc.perform(get("/competitionparticipant/user/{userId}/role/{role}/status/{status}", userId, role, status)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(this.document.snippets(
                        pathParameters(
                                parameterWithName("userId").description("User id of the competition participant"),
                                parameterWithName("role").description("Role of the user"),
                                parameterWithName("status").description("Invite status for the competition")
                        ),
                        responseFields(fieldWithPath("[]").description("List of competition participants the user is allowed to see"))
                ));
    }
}
