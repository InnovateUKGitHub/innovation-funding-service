package org.innovateuk.ifs.application.documentation;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.application.controller.QuestionSetupStatusController;
import org.innovateuk.ifs.application.transactional.QuestionSetupService;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSection;
import org.junit.Test;
import org.mockito.Mock;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.documentation.SetupStatusResourceDocs.setupStatusResourceBuilder;
import static org.innovateuk.ifs.util.MapFunctions.asMap;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class QuestionSetupStatusControllerDocumentation extends BaseControllerMockMVCTest<QuestionSetupStatusController> {

    @Mock
    private QuestionSetupService questionSetupService;

    @Override
    protected QuestionSetupStatusController supplyControllerUnderTest() {
        return new QuestionSetupStatusController();
    }

    @Test
    public void markQuestionSetupAsComplete() throws Exception {
        Long competitionId = 2L;
        Long questionId = 5L;
        CompetitionSetupSection parentSection = CompetitionSetupSection.APPLICATION_FORM;
        when(questionSetupService.markQuestionInSetupAsComplete(questionId, competitionId, parentSection)).thenReturn(serviceSuccess(setupStatusResourceBuilder.build()));

        mockMvc.perform(put("/question/setup/markAsComplete/{competitionId}/{parentSection}/{questionId}", competitionId, parentSection, questionId))
                .andExpect(status().isOk())
                .andDo(document(
                        "question/setup/{method-name}",
                        pathParameters(
                                parameterWithName("competitionId").description("id of the competition on what the section should be marked as complete"),
                                parameterWithName("questionId").description("the id of the question to mark as complete"),
                                parameterWithName("parentSection").description("the parent section of the section that needs to be marked as complete")
                        )
                ));
    }

    @Test
    public void markQuestionSetupAsIncomplete() throws Exception {
        Long competitionId = 2L;
        Long questionId = 5L;
        CompetitionSetupSection parentSection = CompetitionSetupSection.APPLICATION_FORM;
        when(questionSetupService.markQuestionInSetupAsIncomplete(questionId, competitionId, parentSection)).thenReturn(serviceSuccess(setupStatusResourceBuilder.build()));

        mockMvc.perform(put("/question/setup/markAsIncomplete/{competitionId}/{parentSection}/{questionId}", competitionId, parentSection, questionId))
                .andExpect(status().isOk())
                .andDo(document(
                        "question/setup/{method-name}",
                        pathParameters(
                                parameterWithName("competitionId").description("id of the competition on what the section should be marked as incomplete"),
                                parameterWithName("questionId").description("the id of the question to mark as incomplete"),
                                parameterWithName("parentSection").description("the parent section of the section that needs to be marked as complete")
                        )
                ));
    }

    @Test
    public void getQuestionStatuses() throws Exception {
        Long competitionId = 2L;
        CompetitionSetupSection parentSection = CompetitionSetupSection.APPLICATION_FORM;
        when(questionSetupService.getQuestionStatuses(competitionId, parentSection)).thenReturn(serviceSuccess(asMap(1L, Boolean.TRUE)));

        mockMvc.perform(get("/question/setup/getStatuses/{competitionId}/{parentSection}", competitionId, parentSection))
                .andExpect(status().isOk())
                .andDo(document(
                        "question/setup/{method-name}",
                        pathParameters(
                                parameterWithName("competitionId").description("id of the competition of the questions where status is from listed"),
                                parameterWithName("parentSection").description("the parent section of the questions where status is from listed")
                        )
                ));
    }
}
