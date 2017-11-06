package org.innovateuk.ifs.competition.documentation;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.competition.controller.CompetitionSetupQuestionController;
import org.innovateuk.ifs.competition.resource.CompetitionSetupQuestionResource;
import org.innovateuk.ifs.competition.transactional.CompetitionSetupQuestionService;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.http.MediaType;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.documentation.CompetitionSetupQuestionResourceDocs.competitionSetupQuestionResourceBuilder;
import static org.innovateuk.ifs.documentation.CompetitionSetupQuestionResourceDocs.competitionSetupQuestionResourceFields;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class CompetitionSetupQuestionControllerDocumentation extends BaseControllerMockMVCTest<CompetitionSetupQuestionController> {

    @Mock
    CompetitionSetupQuestionService competitionSetupQuestionService;

    private static String baseUrl = "/competition-setup-question";

    @Override
    protected CompetitionSetupQuestionController supplyControllerUnderTest() {
        return new CompetitionSetupQuestionController();
    }

    @Test
    public void getByQuestionId() throws Exception {
        final Long questionId = 1L;

        when(competitionSetupQuestionService.getByQuestionId(questionId)).thenReturn(serviceSuccess(competitionSetupQuestionResourceBuilder.build()));

        mockMvc.perform(get(baseUrl + "/getById/{id}", questionId))
                .andExpect(status().isOk())
                .andDo(document("competition-setup-question/{method-name}",
                        pathParameters(
                                parameterWithName("id").description("id of the question to be retrieved")
                        ),
                        responseFields(competitionSetupQuestionResourceFields)
                ));
    }

    @Test
    public void save() throws Exception {
        CompetitionSetupQuestionResource resource = competitionSetupQuestionResourceBuilder.build();
        when(competitionSetupQuestionService.update(resource)).thenReturn(serviceSuccess(resource));

        mockMvc.perform(put(baseUrl + "/save")

                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(resource)))
                .andExpect(status().isOk())
                .andDo(document("competition-setup-question/{method-name}",
                        requestFields(competitionSetupQuestionResourceFields)
                ));
    }

    @Test
    public void addDefaultToCompetition() throws Exception {
        final Long competitionId = 1L;
        CompetitionSetupQuestionResource resource = competitionSetupQuestionResourceBuilder.build();
        when(competitionSetupQuestionService.createByCompetitionId(competitionId)).thenReturn(serviceSuccess(resource));

        mockMvc.perform(post(baseUrl + "/addDefaultToCompetition/{id}", competitionId))
                .andExpect(status().isCreated())
                .andDo(document("competition-setup-question/{method-name}",
                        pathParameters(
                                parameterWithName("id").description("id of the competition to which the question will be added")
                        ),
                        responseFields(competitionSetupQuestionResourceFields)
                ));
    }

    @Test
    public void deleteById() throws Exception {
        final Long questionId = 1L;
        when(competitionSetupQuestionService.delete(questionId)).thenReturn(serviceSuccess());

        mockMvc.perform(delete(baseUrl + "/deleteById/{id}", questionId)).
                andExpect(status().isNoContent())
                .andDo(document("competition-setup-question/{method-name}",
                        pathParameters(
                                parameterWithName("id").description("id of the question to be removed")
                        )
                ));
    }
}
