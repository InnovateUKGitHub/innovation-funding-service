package org.innovateuk.ifs.application.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.QuestionApplicationCompositeId;
import org.innovateuk.ifs.application.transactional.ApplicationResearchCategoryService;
import org.innovateuk.ifs.application.transactional.QuestionStatusService;
import org.innovateuk.ifs.form.resource.QuestionResource;
import org.innovateuk.ifs.form.transactional.QuestionService;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.springframework.http.MediaType;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.form.builder.QuestionResourceBuilder.newQuestionResource;
import static org.innovateuk.ifs.question.resource.QuestionSetupType.RESEARCH_CATEGORY;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ApplicationResearchCategoryControllerTest extends
        BaseControllerMockMVCTest<ApplicationResearchCategoryController> {

    @Mock
    private ApplicationResearchCategoryService applicationResearchCategoryServiceMock;

    @Mock
    private QuestionService questionService;

    @Mock
    private QuestionStatusService questionStatusService;

    @Override
    protected ApplicationResearchCategoryController supplyControllerUnderTest() {
        return new ApplicationResearchCategoryController(applicationResearchCategoryServiceMock,
                questionService, questionStatusService);
    }

    @Test
    public void setResearchCategory() throws Exception {
        long researchCategoryId = 1L;

        ApplicationResource applicationResource = newApplicationResource().build();

        when(applicationResearchCategoryServiceMock.setResearchCategory(applicationResource.getId(),
                researchCategoryId))
                .thenReturn(serviceSuccess(applicationResource));

        mockMvc.perform(post("/applicationResearchCategory/researchCategory/{applicationId}", applicationResource
                .getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(researchCategoryId)))
                .andExpect(status().isOk());

        verify(applicationResearchCategoryServiceMock, only()).setResearchCategory(applicationResource.getId(),
                researchCategoryId);
    }

    @Test
    public void markResearchCategoryComplete() throws Exception {
        long competitionId = 1L;
        long researchCategoryId = 2L;
        long markedAsCompleteById = 3L;

        ApplicationResource applicationResource = newApplicationResource()
                .withCompetition(competitionId)
                .build();

        QuestionResource questionResource = newQuestionResource().build();

        when(applicationResearchCategoryServiceMock.setResearchCategory(applicationResource.getId(),
                researchCategoryId))
                .thenReturn(serviceSuccess(applicationResource));

        when(questionService.getQuestionByCompetitionIdAndQuestionSetupType(competitionId, RESEARCH_CATEGORY))
                .thenReturn(serviceSuccess(questionResource));

        QuestionApplicationCompositeId ids = new QuestionApplicationCompositeId(questionResource.getId(),
                applicationResource.getId());
        when(questionStatusService.markAsComplete(ids, markedAsCompleteById)).thenReturn(serviceSuccess(emptyList()));

        mockMvc.perform(post("/applicationResearchCategory/markResearchCategoryComplete/{applicationId}" +
                        "/{markedAsCompleteById}",
                applicationResource.getId(), markedAsCompleteById)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(researchCategoryId)))
                .andExpect(status().isOk());

        InOrder inOrder = inOrder(applicationResearchCategoryServiceMock, questionService, questionStatusService);

        inOrder.verify(applicationResearchCategoryServiceMock).setResearchCategory(applicationResource.getId(),
                researchCategoryId);
        inOrder.verify(questionService).getQuestionByCompetitionIdAndQuestionSetupType(competitionId,
                RESEARCH_CATEGORY);
        inOrder.verify(questionStatusService).markAsComplete(ids, markedAsCompleteById);
        inOrder.verifyNoMoreInteractions();
    }
}