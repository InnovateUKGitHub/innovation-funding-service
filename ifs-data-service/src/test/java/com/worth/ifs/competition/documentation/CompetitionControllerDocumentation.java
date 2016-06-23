package com.worth.ifs.competition.documentation;

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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;

import com.worth.ifs.BaseControllerMockMVCTest;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.competition.controller.CompetitionController;
import com.worth.ifs.competition.resource.CompetitionSetupSection;
import com.worth.ifs.competition.transactional.CompetitionService;
import com.worth.ifs.competition.transactional.CompetitionSetupService;

public class CompetitionControllerDocumentation extends BaseControllerMockMVCTest<CompetitionController> {
    @Mock
    CompetitionService competitionService;
    @Mock
    CompetitionSetupService competitionSetupService;
    private RestDocumentationResultHandler document;

    @Override
    protected CompetitionController supplyControllerUnderTest() {
        return new CompetitionController();
    }

    @Before
    public void setup() {
        this.document = document("competition/{method-name}",
                preprocessResponse(prettyPrint()));
    }

    @Test
    public void findOne() throws Exception {
        final Long competitionId = 1L;

        when(competitionService.getCompetitionById(competitionId)).thenReturn(ServiceResult.serviceSuccess(competitionResourceBuilder.build()));

        mockMvc.perform(get("/competition/{id}", competitionId))
                .andExpect(status().isOk())
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
                .andExpect(status().isOk())
                .andDo(this.document.snippets(
                        responseFields(
                                fieldWithPath("[]").description("list of Competitions the authenticated user has access to")
                        )
                ));
    }

    @Test
    public void markAsComplete() throws Exception {
        Long competitionId = 2L;
        CompetitionSetupSection section = CompetitionSetupSection.INITIAL_DETAILS;
        when(competitionSetupService.markSectionComplete(competitionId, section)).thenReturn(ServiceResult.serviceSuccess());

        mockMvc.perform(get("/competition/sectionStatus/complete/{competitionId}/{section}", competitionId, section))
                .andExpect(status().isOk())
                .andDo(this.document.snippets(
                        pathParameters(
                                parameterWithName("competitionId").description("id of the competition on what the section should be marked as complete"),
                                parameterWithName("section").description("the section to mark as complete")
                        )
                ));
    }

    @Test
    public void markAsInComplete() throws Exception {
        Long competitionId = 2L;
        CompetitionSetupSection section = CompetitionSetupSection.INITIAL_DETAILS;
        when(competitionSetupService.markSectionInComplete(competitionId, section)).thenReturn(ServiceResult.serviceSuccess());

        mockMvc.perform(get("/competition/sectionStatus/incomplete/{competitionId}/{section}", competitionId, section))
                .andExpect(status().isOk())
                .andDo(this.document.snippets(
                        pathParameters(
                                parameterWithName("competitionId").description("id of the competition on what the section should be marked as incomplete"),
                                parameterWithName("section").description("the section to mark as incomplete")
                        )
                ));
    }


}