package org.innovateuk.ifs.documentation;

import org.innovateuk.ifs.form.builder.QuestionResourceBuilder;
import org.springframework.restdocs.payload.FieldDescriptor;

import static org.innovateuk.ifs.form.builder.QuestionResourceBuilder.newQuestionResource;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

public class QuestionDocs {
    public static final FieldDescriptor[] questionFields = {
            fieldWithPath("id").description("Id of the question").optional(),
            fieldWithPath("name").description("Question name").optional(),
            fieldWithPath("shortName").description("short version of the question name").optional(),
            fieldWithPath("description").description("question description").optional(),
            fieldWithPath("markAsCompletedEnabled").description("boolean to indicate if the question can be marked as complete").optional(),
            fieldWithPath("assignEnabled").description("boolean to indicate if the question can be assigned").optional(),
            fieldWithPath("multipleStatuses").description("boolean to indicate if the question has multiple statuses").optional(),
            fieldWithPath("priority").description("priority of the question, used for rendering purposes only").optional(),
            fieldWithPath("formInputs").description("list of the inputs used to answer the question").optional(),
            fieldWithPath("questionNumber").description("number of the question").optional(),
            fieldWithPath("section").description("Id of the section of which the question is part of").optional(),
            fieldWithPath("competition").description("Id of the competition").optional(),
            fieldWithPath("type").description("The type of question").optional(),
            fieldWithPath("questionSetupType").description("The setup type of question").optional(),
            fieldWithPath("assessorMaximumScore").description("Maximum score that can be awarded to this question by an assessor").optional()
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
