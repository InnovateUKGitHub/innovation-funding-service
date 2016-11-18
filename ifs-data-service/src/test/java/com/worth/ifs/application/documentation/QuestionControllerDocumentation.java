package com.worth.ifs.application.documentation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.worth.ifs.BaseControllerMockMVCTest;
import com.worth.ifs.application.controller.QuestionController;
import com.worth.ifs.application.resource.QuestionApplicationCompositeId;
import com.worth.ifs.application.resource.QuestionResource;
import com.worth.ifs.application.transactional.QuestionService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;

import java.util.Set;

import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;
import static com.worth.ifs.documentation.QuestionDocs.questionBuilder;
import static com.worth.ifs.documentation.QuestionDocs.questionFields;
import static java.util.Arrays.asList;
import static org.hibernate.validator.internal.util.CollectionHelper.asSet;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;

public class QuestionControllerDocumentation extends BaseControllerMockMVCTest<QuestionController> {
    private RestDocumentationResultHandler document;

    @Override
    protected QuestionController supplyControllerUnderTest() {
        return new QuestionController();
    }

    @Mock
    QuestionService questionService;

    @Before
    public void setup(){
        this.document = document("question/{method-name}",
                preprocessResponse(prettyPrint()));
    }

    @Test
    public void getById() throws Exception {
        Long id = 1L;

        when(questionService.getQuestionById(id)).thenReturn(serviceSuccess(questionBuilder.build()));

        mockMvc.perform(get("/question/id/{id}", id))
                .andDo(this.document.snippets(
                        pathParameters(
                                parameterWithName("id").description("id of the question to be found")
                        ),
                        responseFields(
                                questionFields
                        )
                ));
    }

    @Test
    public void markAsComplete() throws Exception {
        Long questionId = 1L;
        Long applicationId = 2L;
        Long markedAsCompleteById = 3L;

        when(questionService.markAsComplete(new QuestionApplicationCompositeId(questionId, applicationId), markedAsCompleteById)).thenReturn(serviceSuccess(null));

        mockMvc.perform(get("/question/markAsComplete/{questionId}/{applicationId}/{markedAsCompleteById}", questionId, applicationId, markedAsCompleteById))
                .andDo(this.document.snippets(
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

        when(questionService.markAsInComplete(new QuestionApplicationCompositeId(questionId, applicationId), markedAsCompleteById)).thenReturn(serviceSuccess(null));


        mockMvc.perform(get("/question/markAsInComplete/{questionId}/{applicationId}/{markedAsInCompleteById}", questionId, applicationId, markedAsCompleteById))
                .andDo(this.document.snippets(
                        pathParameters(
                                parameterWithName("questionId").description("Id of the question to be marked as incomplete"),
                                parameterWithName("applicationId").description("Id of the application for which the question should be marked as incomplete"),
                                parameterWithName("markedAsInCompleteById").description("Id of the user that marked the application as incomplete")
                        )
                ));
    }

    @Test
    public void assign() throws Exception {
        Long questionId = 1L;
        Long applicationId = 2L;
        Long assignedTo = 3L;
        Long assignedBy = 4L;

        when(questionService.assign(new QuestionApplicationCompositeId(questionId, applicationId), assignedTo, assignedBy)).thenReturn(serviceSuccess(null));


        mockMvc.perform(get("/question/assign/{questionId}/{applicationId}/{assigneeId}/{assignedById}", questionId, applicationId, assignedTo, assignedBy))
                .andDo(this.document.snippets(
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

        when(questionService.getMarkedAsComplete(applicationId, organisationId)).thenReturn(serviceSuccess(ids));

        mockMvc.perform(get("/question/getMarkedAsComplete/{applicationId}/{organisationId}", applicationId, organisationId))
                .andDo(this.document.snippets(
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

        when(questionService.updateNotification(questionStatusId, notify)).thenReturn(serviceSuccess(null));

        mockMvc.perform(get("/question/updateNotification/{questionStatusId}/{notify}", questionStatusId, notify))
                .andDo(this.document.snippets(
                        pathParameters(
                                parameterWithName("questionStatusId").description("question status of which the notification status should be altered"),
                                parameterWithName("notify").description("whether the notification should be shown or not")
                        )
                ));
    }

    @Test
    public void findByCompetition() throws Exception {
        final Long competitionId = 1L;

        when(questionService.findByCompetition(competitionId)).thenReturn(serviceSuccess(questionBuilder.build(2)));

        mockMvc.perform(get("/question/findByCompetition/{competitionId}", competitionId))
                .andDo(this.document.snippets(
                        pathParameters(
                                parameterWithName("competitionId").description("Id of the competition for which the questions are requested")
                        ),
                        responseFields(
                                fieldWithPath("[]").description("List of questions belonging to the competition")
                        )
                ));
    }

    @Test
    public void getNextQuestion() throws Exception {
        final Long questionId = 1L;

        when(questionService.getNextQuestion(questionId)).thenReturn(serviceSuccess(questionBuilder.build()));

        mockMvc.perform(get("/question/getNextQuestion/{questionId}", questionId))
                .andDo(this.document.snippets(
                        pathParameters(
                                parameterWithName("questionId").description("Id of the current question")
                        ),
                        responseFields(questionFields)
                ));
    }

    @Test
    public void getNextQuestionBySection() throws Exception {
        final Long sectionId = 1L;

        when(questionService.getNextQuestionBySection(sectionId)).thenReturn(serviceSuccess(questionBuilder.build()));

        mockMvc.perform(get("/question/getNextQuestionBySection/{sectionId}", sectionId))
                .andDo(this.document.snippets(
                        pathParameters(
                                parameterWithName("sectionId").description("Id of the current section")
                        ),
                        responseFields(questionFields)
                ));
    }

    @Test
    public void getPreviousQuestion() throws Exception {
        final Long questionId = 1L;

        when(questionService.getPreviousQuestion(questionId)).thenReturn(serviceSuccess(questionBuilder.build()));

        mockMvc.perform(get("/question/getPreviousQuestion/{questionId}", questionId))
                .andDo(this.document.snippets(
                        pathParameters(
                                parameterWithName("questionId").description("Id of the current question")
                        ),
                        responseFields(questionFields)
                ));
    }

    @Test
    public void getPreviousQuestionBySection() throws Exception {
        final Long sectionId = 1L;

        when(questionService.getPreviousQuestionBySection(sectionId)).thenReturn(serviceSuccess(questionBuilder.build()));

        mockMvc.perform(get("/question/getPreviousQuestionBySection/{sectionId}", sectionId))
                .andDo(this.document.snippets(
                        pathParameters(
                                parameterWithName("sectionId").description("Id of the current section")
                        ),
                        responseFields(questionFields)
                ));
    }

    @Test
    public void getQuestionByCompetitionIdAndFormInputType() throws Exception {
        final String formInputType = "type";
        Long competitionId = 123L;

        when(questionService.getQuestionResourceByByCompetitionIdAndFormInputType(competitionId, formInputType)).thenReturn(serviceSuccess(questionBuilder.build()));

        mockMvc.perform(get("/question/getQuestionByCompetitionIdAndFormInputType/{competitionId}/{formInputType}", competitionId, formInputType))
                .andDo(this.document.snippets(
                        pathParameters(
                                parameterWithName("competitionId").description("The id of the competition to which the returned Question will belong"),
                                parameterWithName("formInputType").description("form input type")
                        ),
                        responseFields(questionFields)
                ));
    }

    @Test
    public void save() throws Exception {
        QuestionResource questionResource = questionBuilder.build();
        ObjectMapper mapper = new ObjectMapper();

        when(questionService.save(questionResource)).thenReturn(serviceSuccess(questionResource));

        mockMvc.perform(put("/question/")
                    .contentType(APPLICATION_JSON)
                    .content(mapper.writeValueAsString(questionResource)))
                .andDo(this.document.snippets(
                        responseFields(questionFields)
                ));

    }

    @Test
    public void getQuestionsByAssessmentId() throws Exception {
        final Long assessmentId = 1L;

        when(questionService.getQuestionsByAssessmentId(assessmentId)).thenReturn(serviceSuccess(asList(questionBuilder.build())));

        mockMvc.perform(get("/question/getQuestionsByAssessment/{assessmentId}", assessmentId))
                .andDo(this.document.snippets(
                        pathParameters(
                                parameterWithName("assessmentId").description("Id of the assessment for which questions should be returned for")
                        ),
                        responseFields(
                                fieldWithPath("[]").description("An array of the questions which are visible for the specified assessment")
                        )
                ));
    }
}