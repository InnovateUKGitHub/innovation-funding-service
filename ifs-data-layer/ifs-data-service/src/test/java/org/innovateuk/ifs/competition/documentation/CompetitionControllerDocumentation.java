package org.innovateuk.ifs.competition.documentation;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.competition.controller.CompetitionController;
import org.innovateuk.ifs.competition.transactional.CompetitionService;
import org.innovateuk.ifs.documentation.CompetitionResourceDocs;
import org.innovateuk.ifs.documentation.TermsAndConditionsResourceDocs;
import org.junit.Test;
import org.mockito.Mock;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.documentation.CompetitionResourceDocs.competitionResourceBuilder;
import static org.innovateuk.ifs.documentation.CompetitionResourceDocs.competitionResourceFields;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class CompetitionControllerDocumentation extends BaseControllerMockMVCTest<CompetitionController> {
    @Mock
    private CompetitionService competitionService;

    @Override
    protected CompetitionController supplyControllerUnderTest() {
        return new CompetitionController();
    }

    @Test
    public void findOne() throws Exception {
        final long competitionId = 1L;

        when(competitionService.getCompetitionById(competitionId)).thenReturn(serviceSuccess(competitionResourceBuilder.build()));

        mockMvc.perform(get("/competition/{id}", competitionId)
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andExpect(status().isOk())
                .andDo(document(
                        "competition/{method-name}",
                        pathParameters(
                                parameterWithName("id").description("id of the competition to be retrieved")
                        ),
                        responseFields(competitionResourceFields)
                                .andWithPrefix("termsAndConditions.", TermsAndConditionsResourceDocs.termsAndConditionsResourceFields)
                        )
                );
    }

    @Test
    public void findAll() throws Exception {

        when(competitionService.findAll()).thenReturn(serviceSuccess(competitionResourceBuilder.build(2)));

        mockMvc.perform(get("/competition/find-all")
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andExpect(status().isOk())
                .andDo(document(
                        "competition/{method-name}",
                        relaxedResponseFields(
                                fieldWithPath("[]").description("list of Competitions the authenticated user has access to")
                        ).andWithPrefix("[].", CompetitionResourceDocs.competitionResourceFields)
                        .andWithPrefix("[].termsAndConditions.", TermsAndConditionsResourceDocs.termsAndConditionsResourceFields)
                ));
    }

    @Test
    public void updateTermsAndConditions() throws Exception {
        final long competitionId = 1L;
        final long termsAndConditionsId = 2L;

        when(competitionService.updateTermsAndConditionsForCompetition(competitionId, termsAndConditionsId)).thenReturn(serviceSuccess());

        mockMvc.perform(put("/competition/{id}/update-terms-and-conditions/{tcId}", competitionId, termsAndConditionsId)
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andExpect(status().isOk())
                .andDo(document(
                        "competition/{method-name}",
                        pathParameters(
                                parameterWithName("id").description("The competition for which the terms and conditions need to be updated"),
                                parameterWithName("tcId").description("The terms and conditions id to update it to")
                        )
                ));

        verify(competitionService, only()).updateTermsAndConditionsForCompetition(competitionId, termsAndConditionsId);
    }
}
