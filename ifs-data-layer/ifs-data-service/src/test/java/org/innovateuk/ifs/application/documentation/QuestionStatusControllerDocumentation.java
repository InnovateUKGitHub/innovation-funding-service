package org.innovateuk.ifs.application.documentation;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.application.controller.QuestionStatusController;
import org.innovateuk.ifs.application.resource.QuestionApplicationCompositeId;
import org.innovateuk.ifs.application.transactional.QuestionStatusService;
import org.junit.Test;
import org.mockito.Mock;

import java.util.Set;

import static java.util.Collections.emptyList;
import static org.hibernate.validator.internal.util.CollectionHelper.asSet;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
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

        mockMvc.perform(get("/questionStatus/mark-as-complete/{questionId}/{applicationId}/{markedAsCompleteById}", questionId, applicationId, markedAsCompleteById))
                .andDo(document("question-status/{method-name}",
                            pathParameters(
                                    parameterWithName("questionId").description("Id of the question to be marked as complete"),
                                    parameterWithName("applicationId").description("Id of the application for which the question should be marked as complete"),
                                    parameterWithName("markedAsCompleteById").description("Id of the user that marked the application as complete")
                            )
                        ));
    }

    @Test
    public void markAsIncomplete() throws Exception {
        Long questionId = 1L;
        Long applicationId = 2L;
        Long markedAsCompleteById = 3L;

        when(questionStatusService.markAsInComplete(new QuestionApplicationCompositeId(questionId, applicationId), markedAsCompleteById)).thenReturn(serviceSuccess(null));


        mockMvc.perform(get("/questionStatus/mark-as-incomplete/{questionId}/{applicationId}/{markedAsIncompleteById}", questionId, applicationId, markedAsCompleteById))
                .andDo(document("question-status/{method-name}",
                        pathParameters(
                                parameterWithName("questionId").description("Id of the question to be marked as incomplete"),
                                parameterWithName("applicationId").description("Id of the application for which the question should be marked as incomplete"),
                                parameterWithName("markedAsIncompleteById").description("Id of the user that marked the application as incomplete")
                        )
                ));
    }

    @Test
    public void assign() throws Exception {
        Long questionId = 1L;
        Long applicationId = 2L;
        Long assignedTo = 3L;
        Long assignedBy = 4L;

        when(questionStatusService.assign(new QuestionApplicationCompositeId(questionId, applicationId), assignedTo, assignedBy)).thenReturn(serviceSuccess(null));


        mockMvc.perform(get("/questionStatus/assign/{questionId}/{applicationId}/{assigneeId}/{assignedById}", questionId, applicationId, assignedTo, assignedBy))
                .andDo(document("question-status/{method-name}",
                        pathParameters(
                                parameterWithName("questionId").description("Id of the question to be reassigned"),
                                parameterWithName("applicationId").description("Id of the application for which the question will get a new assignee"),
                                parameterWithName("assigneeId").description("Id of the user that will be assigned to the question"),
                                parameterWithName("assignedById").description("Id of the user that assigns a new user to the question")
                        )
                ));
    }

    @Test
    public void getMarkedAsComplete() throws Exception {
        final Long applicationId = 1L;
        final Long organisationId = 2L;

        Set<Long> ids = asSet(1L,2L,3L);

        when(questionStatusService.getMarkedAsComplete(applicationId, organisationId)).thenReturn(serviceSuccess(ids));

        mockMvc.perform(get("/questionStatus/get-marked-as-complete/{applicationId}/{organisationId}", applicationId, organisationId))
                .andDo(document("question-status/{method-name}",
                        pathParameters(
                                parameterWithName("applicationId").description("Id of the application for which to get the questions that are marked as complete"),
                                parameterWithName("organisationId").description("Id of the organisation for which to get the questions that are marked as complete")
                        ),
                        responseFields(
                                fieldWithPath("[]").description("List with unique ids of questions which have been marked as complete")
                        )
                    ));
    }

    @Test
    public void updateNotify() throws Exception {
        final Long questionStatusId = 1L;
        final boolean notify = true;

        when(questionStatusService.updateNotification(questionStatusId, notify)).thenReturn(serviceSuccess(null));

        mockMvc.perform(get("/questionStatus/update-notification/{questionStatusId}/{notify}", questionStatusId, notify))
                .andDo(document("question-status/{method-name}",
                        pathParameters(
                                parameterWithName("questionStatusId").description("question status of which the notification status should be altered"),
                                parameterWithName("notify").description("whether the notification should be shown or not")
                        )
                ));
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
                .andDo(document("question-status/{method-name}",
                        pathParameters(
                                parameterWithName("questionId").description("Id of the question to be marked as " +
                                        "incomplete"),
                                parameterWithName("applicationId").description("Id of the application for which the " +
                                        "question should be marked as incomplete"),
                                parameterWithName("markedAsInCompleteById").description("Id of the user that marked " +
                                        "the question as incomplete")
                        )))
                .andExpect(status().isOk());
    }

}
