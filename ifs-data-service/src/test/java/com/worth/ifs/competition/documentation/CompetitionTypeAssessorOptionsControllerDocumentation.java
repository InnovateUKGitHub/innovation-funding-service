package com.worth.ifs.competition.documentation;

import com.worth.ifs.BaseControllerMockMVCTest;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.competition.controller.CompetitionTypeAssessorOptionsController;
import com.worth.ifs.competition.fixtures.CompetitionTypeAssessorOptionFixture;
import com.worth.ifs.competition.transactional.CompetitionTypeAssessorOptionService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;

import static com.worth.ifs.documentation.CompetitionTypeAssessorOptionResourceDocs.competitionTypeResourceOptionResourceFields;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class CompetitionTypeAssessorOptionsControllerDocumentation extends BaseControllerMockMVCTest<CompetitionTypeAssessorOptionsController> {

    @Mock
    private CompetitionTypeAssessorOptionService competitionTypeAssessorOptionService;

    private RestDocumentationResultHandler document;

    @Override
    protected CompetitionTypeAssessorOptionsController supplyControllerUnderTest() {
        return new CompetitionTypeAssessorOptionsController();
    }

    @Before
    public void setup() {
        this.document = document("competition-type-assessor-options/{method-name}",
                preprocessResponse(prettyPrint()));
    }

    @Test
    public void getAllByCompetitionType() throws Exception {
        when(competitionTypeAssessorOptionService.findAllByCompetitionType(anyLong())).thenReturn(ServiceResult.serviceSuccess(CompetitionTypeAssessorOptionFixture.programmeAssessorOptionResourcesList()));

        mockMvc.perform(get("/competition-type-assessor-options/{id}", 1L))
                .andExpect(status().isOk())
                .andDo(this.document.snippets(
                        pathParameters(
                                parameterWithName("id").description("id of the competition type")
                        ),
                        responseFields(competitionTypeResourceOptionResourceFields)
                ));
    }
}