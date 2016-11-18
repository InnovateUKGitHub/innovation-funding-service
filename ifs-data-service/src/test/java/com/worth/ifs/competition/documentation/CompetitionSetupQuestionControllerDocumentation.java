package com.worth.ifs.competition.documentation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.worth.ifs.*;
import com.worth.ifs.competition.controller.*;
import com.worth.ifs.competition.resource.*;
import com.worth.ifs.competition.transactional.*;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;

import static com.worth.ifs.commons.service.ServiceResult.*;
import static com.worth.ifs.documentation.CompetitionSetupQuestionResourceDocs.*;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class CompetitionSetupQuestionControllerDocumentation extends BaseControllerMockMVCTest<CompetitionSetupQuestionController> {

    @Mock
    CompetitionSetupQuestionService competitionSetupQuestionService;
    private RestDocumentationResultHandler document;
    private static String baseUrl = "/competition-setup-question";

    @Override
    protected CompetitionSetupQuestionController supplyControllerUnderTest() {
        return new CompetitionSetupQuestionController();
    }

    @Before
    public void setup() {
        this.document = document("competition-setup-question/{method-name}",
                preprocessResponse(prettyPrint()));
    }

    @Test
    public void getByQuestionId() throws Exception {
        final Long questionId = 1L;

        when(competitionSetupQuestionService.getByQuestionId(questionId)).thenReturn(serviceSuccess(competitionSetupQuestionResourceBuilder.build()));

        mockMvc.perform(get(baseUrl + "/{id}", questionId))
                .andExpect(status().isOk())
                .andDo(this.document.snippets(
                        pathParameters(
                                parameterWithName("id").description("id of the question to be retrieved")
                        ),
                        responseFields(competitionSetupQuestionResourceFields)
                ));
    }

    @Test
    public void save() throws Exception {
        final Long questionId = 1L;
        CompetitionSetupQuestionResource resource = competitionSetupQuestionResourceBuilder.build();
        when(competitionSetupQuestionService.save(resource)).thenReturn(serviceSuccess(resource));
        ObjectMapper mapper = new ObjectMapper();

        mockMvc.perform(put(baseUrl + "/{id}", questionId)

                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(resource)))
                .andExpect(status().isOk())
                .andDo(this.document.snippets(
                        pathParameters(
                                parameterWithName("id").description("id of the question to be saved")
                        )
                ));
    }
}
