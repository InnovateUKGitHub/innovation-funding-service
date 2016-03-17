package com.worth.ifs.application.documentation;

import java.util.Set;

import com.worth.ifs.BaseControllerMockMVCTest;
import com.worth.ifs.application.controller.QuestionController;
import com.worth.ifs.application.transactional.QuestionService;

import org.junit.Test;
import org.mockito.Mock;

import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;
import static com.worth.ifs.documentation.QuestionDocs.questionBuilder;
import static com.worth.ifs.documentation.QuestionDocs.questionFields;
import static org.hibernate.validator.internal.util.CollectionHelper.asSet;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;

public class QuestionControllerDocumentation extends BaseControllerMockMVCTest<QuestionController> {
    @Override
    protected QuestionController supplyControllerUnderTest() {
        return new QuestionController();
    }

    @Mock
    QuestionService questionService;

    @Test
    public void documentGetById() throws Exception {
        Long id = 1L;

        when(questionService.getQuestionById(id)).thenReturn(serviceSuccess(questionBuilder.build()));

        mockMvc.perform(get("/question/id/{id}", id))
                .andDo(document("question/find-one",
                        pathParameters(
                                parameterWithName("id").description("id of the question to be found")
                        ),
                        responseFields(
                                questionFields
                        )
                ));
    }

    @Test
    public void documentMarkAsComplete() throws Exception {
        Long questionId = 1L;
        Long applicationId = 2L;
        Long markedAsCompleteById = 3L;

        when(questionService.markAsComplete(questionId, applicationId, markedAsCompleteById)).thenReturn(serviceSuccess(null));

        mockMvc.perform(get("/question/markAsComplete/{questionId}/{applicationId}/{markedAsCompleteById}", questionId, applicationId, markedAsCompleteById))
                .andDo(document("question/mark-as-complete",
                            pathParameters(
                                    parameterWithName("questionId").description("Id of the question to be marked as complete"),
                                    parameterWithName("applicationId").description("Id of the application for which the question should be marked as complete"),
                                    parameterWithName("markedAsCompleteById").description("Id of the user that marked the application as complete")
                            )
                        ));
    }

    @Test
    public void documentMarkAsIncomplete() throws Exception {
        Long questionId = 1L;
        Long applicationId = 2L;
        Long markedAsCompleteById = 3L;

        when(questionService.markAsInComplete(questionId, applicationId, markedAsCompleteById)).thenReturn(serviceSuccess(null));


        mockMvc.perform(get("/question/markAsInComplete/{questionId}/{applicationId}/{markedAsInCompleteById}", questionId, applicationId, markedAsCompleteById))
                .andDo(document("question/mark-as-incomplete",
                        pathParameters(
                                parameterWithName("questionId").description("Id of the question to be marked as incomplete"),
                                parameterWithName("applicationId").description("Id of the application for which the question should be marked as incomplete"),
                                parameterWithName("markedAsInCompleteById").description("Id of the user that marked the application as incomplete")
                        )
                ));
    }

    @Test
    public void documentAssign() throws Exception {
        Long questionId = 1L;
        Long applicationId = 2L;
        Long assignedTo = 3L;
        Long assignedBy = 4L;

        when(questionService.assign(questionId, applicationId, assignedTo, assignedBy)).thenReturn(serviceSuccess(null));


        mockMvc.perform(get("/question/assign/{questionId}/{applicationId}/{assigneeId}/{assignedById}", questionId, applicationId, assignedTo, assignedBy))
                .andDo(document("question/mark-as-incomplete",
                        pathParameters(
                                parameterWithName("questionId").description("Id of the question to be reassigned"),
                                parameterWithName("applicationId").description("Id of the application for which the question will get a new assignee"),
                                parameterWithName("assigneeId").description("Id of the user that will be assigned to the question"),
                                parameterWithName("assignedById").description("Id of the user that assigns a new user to the question")
                        )
                ));
    }

    @Test
    public void documentGetMarkedAsComplete() throws Exception {
        final Long applicationId = 1L;
        final Long organisationId = 2L;

        Set<Long> ids = asSet(1L,2L,3L);

        when(questionService.getMarkedAsComplete(applicationId, organisationId)).thenReturn(serviceSuccess(ids));

        mockMvc.perform(get("/question/getMarkedAsComplete/{applicationId}/{organisationId}", applicationId, organisationId))
                .andDo(document("question/get-marked-as-complete",
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
    public void documentUpdateNotify() throws Exception {
        final Long questionStatusId = 1L;
        final boolean notify = true;

        when(questionService.updateNotification(questionStatusId, notify)).thenReturn(serviceSuccess(null));

        mockMvc.perform(get("/question/updateNotification/{questionStatusId}/{notify}", questionStatusId, notify))
                .andDo(document("question/notify",
                        pathParameters(
                                parameterWithName("questionStatusId").description("question status of which the notification status should be altered"),
                                parameterWithName("notify").description("whether the notification should be shown or not")
                        )
                ));
    }

    @Test
    public void documentFindByCompetition() throws Exception {
        final Long competitionId = 1L;

        when(questionService.findByCompetition(competitionId)).thenReturn(serviceSuccess(questionBuilder.build(2)));

        mockMvc.perform(get("/question/findByCompetition/{competitionId}", competitionId))
                .andDo(document("question/find-by-competition",
                        pathParameters(
                                parameterWithName("competitionId").description("Id of the competition for which the questions are requested")
                        ),
                        responseFields(
                                fieldWithPath("[]").description("List of questions belonging to the competition")
                        )
                ));
    }

    @Test
    public void documentGetNextQuestion() throws Exception {
        final Long questionId = 1L;

        when(questionService.getNextQuestion(questionId)).thenReturn(serviceSuccess(questionBuilder.build()));

        mockMvc.perform(get("/question/getNextQuestion/{questionId}", questionId))
                .andDo(document("question/get-next",
                        pathParameters(
                                parameterWithName("questionId").description("Id of the current question")
                        ),
                        responseFields(questionFields)
                ));
    }

    @Test
    public void documentGetNextQuestionBySection() throws Exception {
        final Long sectionId = 1L;

        when(questionService.getNextQuestionBySection(sectionId)).thenReturn(serviceSuccess(questionBuilder.build()));

        mockMvc.perform(get("/question/getNextQuestionBySection/{sectionId}", sectionId))
                .andDo(document("question/get-next-by-section",
                        pathParameters(
                                parameterWithName("sectionId").description("Id of the current section")
                        ),
                        responseFields(questionFields)
                ));
    }

    @Test
    public void documentGetPreviousQuestion() throws Exception {
        final Long questionId = 1L;

        when(questionService.getPreviousQuestion(questionId)).thenReturn(serviceSuccess(questionBuilder.build()));

        mockMvc.perform(get("/question/getPreviousQuestion/{questionId}", questionId))
                .andDo(document("question/get-previous",
                        pathParameters(
                                parameterWithName("questionId").description("Id of the current question")
                        ),
                        responseFields(questionFields)
                ));
    }

    @Test
    public void documentGetPreviousQuestionBySection() throws Exception {
        final Long sectionId = 1L;

        when(questionService.getPreviousQuestionBySection(sectionId)).thenReturn(serviceSuccess(questionBuilder.build()));

        mockMvc.perform(get("/question/getPreviousQuestionBySection/{sectionId}", sectionId))
                .andDo(document("question/get-previous-by-section",
                        pathParameters(
                                parameterWithName("sectionId").description("Id of the current section")
                        ),
                        responseFields(questionFields)
                ));
    }

    @Test
    public void documentGetQuestionByFormInputType() throws Exception {
        final String formInputType = "type";

        when(questionService.getQuestionByFormInputType(formInputType)).thenReturn(serviceSuccess(questionBuilder.build()));

        mockMvc.perform(get("/question/getQuestionByFormInputType/{formInputType}", formInputType))
                .andDo(document("question/get-by-form-input-type",
                        pathParameters(
                                parameterWithName("formInputType").description("form input type")
                        ),
                        responseFields(questionFields)
                ));
    }
}