package com.worth.ifs.documentation;

import com.worth.ifs.application.builder.QuestionBuilder;

import com.worth.ifs.application.builder.QuestionResourceBuilder;
import org.springframework.restdocs.payload.FieldDescriptor;

import static com.worth.ifs.application.builder.QuestionBuilder.newQuestion;
import static com.worth.ifs.application.builder.QuestionResourceBuilder.newQuestionResource;
import static com.worth.ifs.application.builder.QuestionStatusBuilder.newQuestionStatus;
import static com.worth.ifs.application.builder.ResponseBuilder.newResponse;
import static com.worth.ifs.application.builder.SectionBuilder.newSection;
import static com.worth.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static com.worth.ifs.finance.builder.CostBuilder.newCost;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

public class QuestionDocs {
    public static final FieldDescriptor[] questionFields = {
        fieldWithPath("id").description("Id of the question"),
        fieldWithPath("name").description("Question name"),
        fieldWithPath("shortName").description("short version of the question name"),
        fieldWithPath("description").description("question description"),
        fieldWithPath("guidanceQuestion").description("guidance on the question"),
        fieldWithPath("guidanceAnswer").description("guidance on how to answer the question"),
        fieldWithPath("markAsCompletedEnabled").description("boolean to indicate if the question can be marked as complete"),
        fieldWithPath("assignEnabled").description("boolean to indicate if the question can be assigned"),
        fieldWithPath("multipleStatuses").description("boolean to indicate if the question has multiple statuses"),
        fieldWithPath("priority").description("priority of the question, used for rendering purposes only"),
        fieldWithPath("needingAssessorScore").description("boolean to indicate of the question still needs an assessor score"),
        fieldWithPath("needingAssessorFeedback").description("boolean to indicate if the question still needs assessor feedback"),
        fieldWithPath("formInputs").description("list of the inputs used to answer the question"),
        fieldWithPath("assessorConfirmationQuestion").description("scope verification question for the assessor"),
        fieldWithPath("questionStatuses").description("List of question statuses"),
        fieldWithPath("questionNumber").description("number of the question")
    };

    public static final QuestionBuilder questionBuilder = newQuestion()
        .withId(1L)
        .withName("question name")
        .withShortName("name")
        .withDescription("description")
        .withGuidanceQuestion("guidance question")
        .withPriority(1)
        .withAssessorConfirmationQuestion("confirmation question")
        .withCompetition(newCompetition().build())
        .withSection(newSection().build())
        .withResponses(newResponse().build(2))
        .withQuestionStatuses(newQuestionStatus().build(2))
        .withCosts(newCost().build(2))
        .withQuestionNumber("1");

    public static final QuestionResourceBuilder questionBuilder = newQuestionResource()
            .withId(1L)
            .withName("question name")
            .withShortName("name")
            .withDescription("description")
            .withGuidanceQuestion("guidance question")
            .withPriority(1)
            .withAssessorConfirmationQuestion("confirmation question")
            .withCompetition(newCompetition().build())
            .withSection(newSection().build())
            .withResponses(newResponse().build(2))
            .withQuestionStatuses(newQuestionStatus().build(2))
            .withCosts(newCost().build(2))
            .withQuestionNumber("1");
}
