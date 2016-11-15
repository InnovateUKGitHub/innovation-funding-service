package com.worth.ifs.documentation;

import com.worth.ifs.application.builder.QuestionResourceBuilder;
import org.springframework.restdocs.payload.FieldDescriptor;

import static com.worth.ifs.application.builder.QuestionResourceBuilder.newQuestionResource;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

public class QuestionDocs {
    public static final FieldDescriptor[] questionFields = {
            fieldWithPath("id").description("Id of the question"),
            fieldWithPath("name").description("Question name"),
            fieldWithPath("shortName").description("short version of the question name"),
            fieldWithPath("description").description("question description"),
            fieldWithPath("markAsCompletedEnabled").description("boolean to indicate if the question can be marked as complete"),
            fieldWithPath("assignEnabled").description("boolean to indicate if the question can be assigned"),
            fieldWithPath("multipleStatuses").description("boolean to indicate if the question has multiple statuses"),
            fieldWithPath("priority").description("priority of the question, used for rendering purposes only"),
            fieldWithPath("formInputs").description("list of the inputs used to answer the question"),
            fieldWithPath("questionNumber").description("number of the question"),
            fieldWithPath("section").description("Id of the section of which the question is part of").optional(),
            fieldWithPath("competition").description("Id of the competition"),
            fieldWithPath("type").description("The type of question"),
            fieldWithPath("assessorMaximumScore").description("Maximum score that can be awarded to this question by an assessor"),
            fieldWithPath("questionAssessment").description("Id of the assessment guidance for this question")
    };

    public static final QuestionResourceBuilder questionBuilder = newQuestionResource()
            .withId(1L)
            .withName("question name")
            .withShortName("name")
            .withDescription("description")
            .withPriority(1)
            .withCompetition(1L)
            .withSection(1L)
            .withQuestionNumber("1")
            .withAssessorMaximumScore(10);
}
