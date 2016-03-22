package com.worth.ifs.competition.documentation;

import com.worth.ifs.BaseControllerMockMVCTest;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.competition.controller.CompetitionController;
import com.worth.ifs.competition.transactional.CompetitionService;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;

import static com.worth.ifs.documentation.CompetitionResourceDocs.competitionResourceBuilder;
import static com.worth.ifs.documentation.CompetitionResourceDocs.competitionResourceFields;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;

public class CompetitionControllerDocumentation extends BaseControllerMockMVCTest<CompetitionController> {
    private RestDocumentationResultHandler document;

    @Override
    protected CompetitionController supplyControllerUnderTest() {
        return new CompetitionController();
    }

    @Mock
    CompetitionService competitionService;

    @Before
    public void setup(){
        this.document = document("question/{method-name}",
                preprocessResponse(prettyPrint()));
    }

    @Test
    public void findOne() throws Exception {
        final Long competitionId = 1L;

        when(competitionService.getCompetitionById(competitionId)).thenReturn(ServiceResult.serviceSuccess(competitionResourceBuilder.build()));

        mockMvc.perform(get("/competition/{id}", competitionId))
                .andDo(this.document.snippets(
                        pathParameters(
                                parameterWithName("id").description("id of the competition to be retrieved")
                        ),
                        responseFields(competitionResourceFields)
                ));
    }

    @Test
    public void findAll() throws Exception {

        when(competitionService.findAll()).thenReturn(ServiceResult.serviceSuccess(competitionResourceBuilder.build(2)));

        mockMvc.perform(get("/competition/findAll"))
                .andDo(this.document.snippets(
                        responseFields(
                                fieldWithPath("[]").description("list of Competitions the authenticated user has access to")
                        )
                ));
    }
}