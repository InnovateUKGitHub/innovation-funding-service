package org.innovateuk.ifs.assessment.documentation;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.assessment.controller.AssessorController;
import org.innovateuk.ifs.assessment.resource.AssessorProfileResource;
import org.innovateuk.ifs.assessment.transactional.AssessorService;
import org.innovateuk.ifs.competition.transactional.CompetitionService;
import org.innovateuk.ifs.documentation.*;
import org.innovateuk.ifs.registration.resource.UserRegistrationResource;
import org.junit.Test;
import org.mockito.Mock;

import static java.lang.Boolean.TRUE;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.documentation.AssessorProfileResourceDocs.assessorProfileResourceBuilder;
import static org.innovateuk.ifs.documentation.AssessorProfileResourceDocs.assessorProfileResourceFields;
import static org.innovateuk.ifs.documentation.UserRegistrationResourceDocs.userRegistrationResourceBuilder;
import static org.innovateuk.ifs.documentation.UserRegistrationResourceDocs.userRegistrationResourceFields;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AssessorControllerDocumentation extends BaseControllerMockMVCTest<AssessorController> {

    @Mock
    private AssessorService assessorServiceMock;

    @Mock
    private CompetitionService competitionServiceMock;

    @Override
    protected AssessorController supplyControllerUnderTest() {
        return new AssessorController();
    }

    @Test
    public void registerAssessorByHash() throws Exception {
        String hash = "testhash";

        UserRegistrationResource userRegistrationResource = userRegistrationResourceBuilder.build();

        when(assessorServiceMock.registerAssessorByHash(hash, userRegistrationResource)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/assessor/register/{hash}", hash)
                .header("IFS_AUTH_TOKEN", "123abc")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userRegistrationResource)))
                .andExpect(status().isOk())
                .andDo(document("assessor/register-assessor-by-hash",
                        pathParameters(
                                parameterWithName("hash").description("hash of the invite being accepted")
                        ),
                        requestFields(userRegistrationResourceFields)
                        .andWithPrefix("address.", AddressDocs.addressResourceFields)
                ));

        verify(assessorServiceMock).registerAssessorByHash(hash, userRegistrationResource);
    }

    @Test
    public void getAssessorProfile() throws Exception {
        Long assessorId = 1L;

        AssessorProfileResource assessorProfileResource = assessorProfileResourceBuilder.build();

        when(assessorServiceMock.getAssessorProfile(assessorId)).thenReturn(serviceSuccess(assessorProfileResource));

        mockMvc.perform(get("/assessor/profile/{assessorId}", assessorId)
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andExpect(status().isOk())
                .andDo(document("assessor/get-assessor-profile",
                        pathParameters(
                                parameterWithName("assessorId").description("Id of the assessor")
                        ),
                        responseFields(assessorProfileResourceFields)
                        .andWithPrefix("profile.", ProfileResourceDocs.profileResourceFields)
                                .andWithPrefix("profile.innovationAreas[].", InnovationAreaResourceDocs.innovationAreaResourceFields)
                                .andWithPrefix("profile.affiliations[].", AffiliationDocs.affiliationResourceFields)
                                .andWithPrefix("profile.address.", AddressDocs.addressResourceFields)
                ));
    }

    @Test
    public void notifyAssessors() throws Exception {
        final Long competitionId = 1L;

        when(competitionServiceMock.notifyAssessors(competitionId)).thenReturn(serviceSuccess());
        when(assessorServiceMock.notifyAssessorsByCompetition(competitionId)).thenReturn(serviceSuccess());

        mockMvc.perform(put("/assessor/notify-assessors/competition/{id}", competitionId)
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andExpect(status().isOk())
                .andDo(document(
                        "assessor/{method-name}",
                        pathParameters(
                                parameterWithName("id").description("id of the competition for the notifications")
                        ))
                );
    }

    @Test
    public void hasApplicationsAssigned() throws Exception {

        long userId = 1l;

        when(assessorServiceMock.hasApplicationsAssigned(1L)).thenReturn(serviceSuccess(TRUE));

        mockMvc.perform(get("/assessor/has-applications-assigned/{id}", userId)
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"))
                .andDo(document(
                        "assessor/{method-name}",
                        pathParameters(
                                parameterWithName("id").description("id of the assessor")
                        ))
                );
    }

}