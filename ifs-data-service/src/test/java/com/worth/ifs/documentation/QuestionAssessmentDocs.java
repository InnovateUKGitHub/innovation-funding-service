package com.worth.ifs.documentation;

import com.worth.ifs.application.builder.AssessmentScoreRowResourceBuilder;
import com.worth.ifs.application.builder.QuestionAssessmentResourceBuilder;
import org.springframework.restdocs.payload.FieldDescriptor;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

public class QuestionAssessmentDocs {
    public static final FieldDescriptor[] questionAssessmentFields = {
            fieldWithPath("id").description("Id of the question"),
            fieldWithPath("scored").description("Is the assessment scored"),
            fieldWithPath("scoreTotal").description("Total score allowed for assessment"),
            fieldWithPath("writtenFeedback").description("Does the assessment require written feedback"),
            fieldWithPath("guidance").description("Assessor guidance description"),
            fieldWithPath("wordCount").description("Total word count for assessor feedback"),
            fieldWithPath("scoreRows[]").description("Justification for each scoring band"),
            fieldWithPath("scoreRows[].start").description("Start of scoring band"),
            fieldWithPath("scoreRows[].end").description("End of scoring band"),
            fieldWithPath("scoreRows[].justification").description("Justification for scoring band")
    };

    public static final QuestionAssessmentResourceBuilder questionAssesmentBuilder = QuestionAssessmentResourceBuilder.newQuestionAssessment()
            .withId(1L)
            .withScored(false)
            .withScoreTotal(2)
            .withWrittenFeedback(false)
            .withGuidance("guidance")
            .withWordCount(400)
            .withScoreRows(AssessmentScoreRowResourceBuilder
                    .newAssessmentScoreRow()
                    .withStart(1)
                    .withEnd(2)
                    .withJustification("Justification")
            .build(1));
}
