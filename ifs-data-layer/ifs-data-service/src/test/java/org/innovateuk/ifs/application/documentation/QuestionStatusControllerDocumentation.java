package org.innovateuk.ifs.application.documentation;

import com.google.common.collect.ImmutableSet;
import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.application.controller.QuestionStatusController;
import org.innovateuk.ifs.application.resource.QuestionApplicationCompositeId;
import org.innovateuk.ifs.application.transactional.QuestionStatusService;
import org.junit.Test;
import org.mockito.Mock;

import java.util.Set;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class QuestionStatusControllerDocumentation extends BaseControllerMockMVCTest<QuestionStatusController> {

    @Override
    protected QuestionStatusController supplyControllerUnderTest() {
        return new QuestionStatusController();
    }

    @Mock
    private QuestionStatusService questionStatusService;

    @Test
    public void markAsComplete() throws Exception {
        Long questionId = 1L;
        Long applicationId = 2L;
        Long markedAsCompleteById = 3L;

        when(questionStatusService.markAsComplete(new QuestionApplicationCompositeId(questionId, applicationId), markedAsCompleteById)).thenReturn(serviceSuccess(null));

        mockMvc.perform(get("/question-status/mark-as-complete/{questionId}/{applicationId}/{markedAsCompleteById}", questionId, applicationId, markedAsCompleteById)
                .header("IFS_AUTH_TOKEN", "123abc"));
    }

    @Test
    public void markAsIncomplete() throws Exception {
        Long questionId = 1L;
        Long applicationId = 2L;
        Long markedAsCompleteById = 3L;

        when(questionStatusService.markAsInComplete(new QuestionApplicationCompositeId(questionId, applicationId), markedAsCompleteById)).thenReturn(serviceSuccess(null));


        mockMvc.perform(get("/question-status/mark-as-incomplete/{questionId}/{applicationId}/{markedAsIncompleteById}", questionId, applicationId, markedAsCompleteById)
                .header("IFS_AUTH_TOKEN", "123abc"));
    }

    @Test
    public void assign() throws Exception {
        Long questionId = 1L;
        Long applicationId = 2L;
        Long assignedTo = 3L;
        Long assignedBy = 4L;

        when(questionStatusService.assign(new QuestionApplicationCompositeId(questionId, applicationId), assignedTo, assignedBy)).thenReturn(serviceSuccess(null));


        mockMvc.perform(get("/question-status/assign/{questionId}/{applicationId}/{assigneeId}/{assignedById}", questionId, applicationId, assignedTo, assignedBy)
                .header("IFS_AUTH_TOKEN", "123abc"));
    }

    @Test
    public void getMarkedAsComplete() throws Exception {
        final Long applicationId = 1L;
        final Long organisationId = 2L;

        Set<Long> ids = ImmutableSet.of(1L,2L,3L);

        when(questionStatusService.getMarkedAsComplete(applicationId, organisationId)).thenReturn(serviceSuccess(ids));

        mockMvc.perform(get("/question-status/get-marked-as-complete/{applicationId}/{organisationId}", applicationId, organisationId)
                .header("IFS_AUTH_TOKEN", "123abc"));
    }

    @Test
    public void updateNotify() throws Exception {
        final Long questionStatusId = 1L;
        final boolean notify = true;

        when(questionStatusService.updateNotification(questionStatusId, notify)).thenReturn(serviceSuccess(null));

        mockMvc.perform(get("/question-status/update-notification/{questionStatusId}/{notify}", questionStatusId, notify)
                .header("IFS_AUTH_TOKEN", "123abc"));
    }

    @Test
    public void markTeamAsInComplete() throws Exception {
        long questionId = 1L;
        long applicationId = 2L;
        long markedAsInCompleteById = 3L;

        QuestionApplicationCompositeId ids = new QuestionApplicationCompositeId(questionId, applicationId);

        when(questionStatusService.markTeamAsInComplete(ids, markedAsInCompleteById)).thenReturn(serviceSuccess(emptyList
                ()));

        mockMvc.perform(put("/question-status/mark-team-as-in-complete/{questionId}/{applicationId" +
                        "}/{markedAsInCompleteById}",
                questionId, applicationId, markedAsInCompleteById)
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andExpect(status().isOk());
    }

}
