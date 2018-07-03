package org.innovateuk.ifs.application.documentation;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.application.controller.ApplicationResearchCategoryController;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.QuestionApplicationCompositeId;
import org.innovateuk.ifs.application.transactional.ApplicationResearchCategoryService;
import org.innovateuk.ifs.application.transactional.QuestionStatusService;
import org.innovateuk.ifs.form.resource.QuestionResource;
import org.innovateuk.ifs.form.transactional.QuestionService;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.http.MediaType;

import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.form.builder.QuestionResourceBuilder.newQuestionResource;
import static org.innovateuk.ifs.question.resource.QuestionSetupType.RESEARCH_CATEGORY;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;

public class ApplicationResearchCategoryControllerDocumentation extends BaseControllerMockMVCTest<ApplicationResearchCategoryController> {

    @Mock
    private ApplicationResearchCategoryService applicationResearchCategoryService;

    @Mock
    private QuestionService questionService;

    @Mock
    private QuestionStatusService questionStatusService;

    private static String baseUrl = "/applicationResearchCategory";

    @Override
    protected ApplicationResearchCategoryController supplyControllerUnderTest() {
        return new ApplicationResearchCategoryController(applicationResearchCategoryService, questionService,
                questionStatusService);
    }

    @Test
    public void setResearchCategory() throws Exception {
        Long applicationId = 1L;
        Long researchCategoryId = 2L;

        when(applicationResearchCategoryService.setResearchCategory(applicationId, researchCategoryId))
                .thenReturn(serviceSuccess(newApplicationResource().build()));

        mockMvc.perform(post(baseUrl + "/researchCategory/{applicationId}", applicationId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(researchCategoryId)))
                .andExpect(status().isOk())
                .andDo(document("application-research-category/{method-name}",
                        pathParameters(
                                parameterWithName("applicationId").description("id of the application for which the research category should be marked as complete")
                        )
                ));
    }

    @Test
    public void setResearchCategoryAndMarkAsComplete() throws Exception {
        Long markedAsCompleteById = 2L;
        Long researchCategoryId = 3L;

        ApplicationResource application = newApplicationResource().withCompetition(4L).build();
        QuestionResource question = newQuestionResource().build();
        QuestionApplicationCompositeId questionApplicationCompositeId = new QuestionApplicationCompositeId(question.getId(), application.getId());

        when(applicationResearchCategoryService.setResearchCategory(application.getId(), researchCategoryId))
                .thenReturn(serviceSuccess(application));
        when(questionService.getQuestionByCompetitionIdAndQuestionSetupType(application.getCompetition(), RESEARCH_CATEGORY))
                .thenReturn(serviceSuccess(question));
        when(questionStatusService.markAsComplete(questionApplicationCompositeId, markedAsCompleteById))
                .thenReturn(serviceSuccess(null));


        mockMvc.perform(post(baseUrl + "/markResearchCategoryComplete/{applicationId}/{markedAsCompleteById}", application.getId(), markedAsCompleteById)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(researchCategoryId)))
                .andExpect(status().isOk())
                .andDo(document("application-research-category/{method-name}",
                        pathParameters(
                                parameterWithName("applicationId").description("id of the application for which the research category should be marked as complete"),
                                parameterWithName("markedAsCompleteById").description("Id of the user that marked the application as complete")
                        )
                ));
    }
}
