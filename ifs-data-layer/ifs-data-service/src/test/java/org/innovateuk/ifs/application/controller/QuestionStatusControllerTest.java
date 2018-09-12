package org.innovateuk.ifs.application.controller;

import com.google.common.primitives.Longs;
import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.application.builder.QuestionStatusResourceBuilder;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.QuestionApplicationCompositeId;
import org.innovateuk.ifs.application.resource.QuestionStatusResource;
import org.innovateuk.ifs.application.transactional.QuestionStatusService;
import org.junit.Test;
import org.mockito.Mock;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.application.builder.QuestionStatusResourceBuilder.newQuestionStatusResource;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class QuestionStatusControllerTest extends BaseControllerMockMVCTest<QuestionStatusController> {

    @Mock
    private QuestionStatusService questionStatusService;

    @Override
    protected QuestionStatusController supplyControllerUnderTest() {
        return new QuestionStatusController();
    }

    @Test
    public void testGetQuestionStatusByQuestionIdAndApplicationId() throws Exception {
        ApplicationResource applicationResource = newApplicationResource().withCompetition(newCompetitionResource()
                .build().getId()).build();
        QuestionStatusResource questionStatus = newQuestionStatusResource().withApplication(applicationResource)
                .build();
        List<QuestionStatusResource> questionStatuses = singletonList(questionStatus);

        when(questionStatusService.getQuestionStatusByQuestionIdAndApplicationId(1L, 2L)).thenReturn(serviceSuccess
                (questionStatuses));

        mockMvc.perform(get("/questionStatus/findByQuestionAndApplication/1/2"))
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(questionStatuses)));
    }

    @Test
    public void testGetQuestionStatusByApplicationIdAndAssigneeIdAndOrganisationId() throws Exception {
        Long questionId = 1L;
        Long applicationId = 2L;
        Long organisationId = 3L;
        List<QuestionStatusResource> questionStatuses = QuestionStatusResourceBuilder.newQuestionStatusResource()
                .build(1);

        when(questionStatusService.getQuestionStatusByApplicationIdAndAssigneeIdAndOrganisationId(questionId,
                applicationId, organisationId)).thenReturn(serviceSuccess(questionStatuses));

        mockMvc.perform(get("/questionStatus/findByQuestionAndApplicationAndOrganisation/" + questionId + "/" +
                applicationId + "/" + organisationId))
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(questionStatuses)));
    }

    @Test
    public void testGetQuestionStatusByQuestionIdsAndApplicationIdAndOrganisationId() throws Exception {
        List<Long> questionIdsList = Longs.asList(1L, 2L, 3L);
        Long[] questionIds = questionIdsList.toArray(new Long[questionIdsList.size()]);
        Long applicationId = 2L;
        Long organisationId = 3L;
        List<QuestionStatusResource> questionStatuses = QuestionStatusResourceBuilder.newQuestionStatusResource()
                .build(1);

        when(questionStatusService.getQuestionStatusByQuestionIdsAndApplicationIdAndOrganisationId(questionIds,
                applicationId, organisationId)).thenReturn(serviceSuccess(questionStatuses));

        mockMvc.perform(get("/questionStatus/findByQuestionIdsAndApplicationIdAndOrganisationId/" + questionIdsList
                .stream().map(id -> id.toString()).collect(Collectors.joining(",")) + "/" + applicationId + "/" +
                organisationId))
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(questionStatuses)));
    }

    @Test
    public void testFindByApplicationAndOrganisation() throws Exception {
        Long applicationId = 2L;
        Long organisationId = 3L;
        List<QuestionStatusResource> questionStatuses = QuestionStatusResourceBuilder.newQuestionStatusResource()
                .build(1);

        when(questionStatusService.findByApplicationAndOrganisation(applicationId, organisationId)).thenReturn
                (serviceSuccess(questionStatuses));

        mockMvc.perform(get("/questionStatus/findByApplicationAndOrganisation/" + applicationId + "/" + organisationId))
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(questionStatuses)));
    }

    @Test
    public void testGetQuestionStatusResourceById() throws Exception {
        Long questionStatusId = 1L;
        QuestionStatusResource questionStatus = QuestionStatusResourceBuilder.newQuestionStatusResource().build();

        when(questionStatusService.getQuestionStatusResourceById(questionStatusId)).thenReturn(serviceSuccess
                (questionStatus));

        mockMvc.perform(get("/questionStatus/" + questionStatusId))
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(questionStatus)));
    }

    @Test
    public void testGetAssignedQuestionsCountByApplicationIdAndAssigneeId() throws Exception {
        Long applicationId = 1L;
        Long assigneeId = 2L;
        Integer count = 2;

        when(questionStatusService.getCountByApplicationIdAndAssigneeId(applicationId, assigneeId)).thenReturn
                (serviceSuccess(count));

        mockMvc.perform(get("/questionStatus/getAssignedQuestionsCountByApplicationIdAndAssigneeId/" + applicationId
                + "/" + assigneeId))
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(count)));
    }

    @Test
    public void markTeamAsInComplete() throws Exception {
        long questionId = 1L;
        long applicationId = 2L;
        long markedAsInCompleteById = 3L;

        QuestionApplicationCompositeId ids = new QuestionApplicationCompositeId(questionId, applicationId);

        when(questionStatusService.markTeamAsInComplete(ids, markedAsInCompleteById)).thenReturn(serviceSuccess(emptyList
                ()));

        mockMvc.perform(put("/questionStatus/mark-team-as-in-complete/{questionId}/{applicationId" +
                        "}/{markedAsInCompleteById}",
                questionId, applicationId, markedAsInCompleteById))
                .andExpect(status().isOk());

        verify(questionStatusService, only()).markTeamAsInComplete(ids, markedAsInCompleteById);
    }
}
