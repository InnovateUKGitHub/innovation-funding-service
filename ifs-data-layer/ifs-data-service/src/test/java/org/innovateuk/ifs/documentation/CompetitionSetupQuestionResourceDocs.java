package org.innovateuk.ifs.documentation;

import org.innovateuk.ifs.competition.builder.CompetitionSetupQuestionResourceBuilder;
import org.springframework.restdocs.payload.FieldDescriptor;

import static org.innovateuk.ifs.competition.builder.CompetitionSetupQuestionResourceBuilder.newCompetitionSetupQuestionResource;
import static org.innovateuk.ifs.form.builder.GuidanceRowResourceBuilder.newFormInputGuidanceRowResourceBuilder;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

public class CompetitionSetupQuestionResourceDocs {
    public static final FieldDescriptor[] competitionSetupQuestionResourceFields = {
            fieldWithPath("questionId").description("Id of the question"),
            fieldWithPath("number").description("The question number"),
            fieldWithPath("shortTitle").description("Short title of the question"),
            fieldWithPath("title").description("Title of the question"),
            fieldWithPath("subTitle").description("Sub title of the question"),
            fieldWithPath("guidanceTitle").description("The title of the guidance for the question"),
            fieldWithPath("guidance").description("The content of the guidance for the question"),
            fieldWithPath("textArea").description("Does this question include the textarea section"),
            fieldWithPath("maxWords").description("The maximum words allowed for the question response"),
            fieldWithPath("multipleChoice").description("Does this question contain the multiple choice section"),
            fieldWithPath("choices").description("The choices for a multiple choice section"),
            fieldWithPath("appendix").description("Does the question include an appendix section"),
            fieldWithPath("allowedAppendixResponseFileTypes").description("Specifies types of files that will be allowed to be uploaded for appendix"),
            fieldWithPath("appendixGuidance").description("Guidance for the file upload field"),
            fieldWithPath("templateDocument").description("Does the question include a template document section"),
            fieldWithPath("templateTitle").description("The title of the template document"),
            fieldWithPath("templateFormInput").description("The id of the form input for template document"),
            fieldWithPath("templateFilename").description("filename of the template document"),
            fieldWithPath("allowedTemplateResponseFileTypes").description("Specifies types of files that will be allowed to be uploaded for a template"),
            fieldWithPath("assessmentGuidance").description("Guidance for the assessor"),
            fieldWithPath("assessmentGuidanceTitle").description("Guidance title for the assessor"),
            fieldWithPath("assessmentMaxWords").description("Maximum words for the assessors feedback"),
            fieldWithPath("scored").description("Should the assessor see the score section"),
            fieldWithPath("scoreTotal").description("What is the question scored out of for the assessor"),
            fieldWithPath("type").description("The type of competition setup question this is"),
            fieldWithPath("scope").description("Is there a scope question for the assessor"),
            fieldWithPath("researchCategoryQuestion").description("Is there a research category question for the assessor"),
            fieldWithPath("writtenFeedback").description("Should the assessor provide written feedback"),
            fieldWithPath("guidanceRows[]").description("The rows of extra guidance information displayed to assessors"),
            fieldWithPath("guidanceRows[].id").description("The id of the guidance row in the database"),
            fieldWithPath("guidanceRows[].subject").description("The subject of the guidance"),
            fieldWithPath("guidanceRows[].justification").description("The justification of the guidance"),
            fieldWithPath("guidanceRows[].priority").description("The priority of the form input the guidance is linked to")
    };

    public static final CompetitionSetupQuestionResourceBuilder competitionSetupQuestionResourceBuilder = newCompetitionSetupQuestionResource()
            .withQuestionId(1L)
            .withAppendix(false)
            .withScored(false)
            .withWrittenFeedback(false)
            .withGuidance("guidance")
            .withGuidanceTitle("guidanceTitle")
            .withMaxWords(1)
            .withNumber("number")
            .withTitle("title")
            .withShortTitle("shortTitle")
            .withSubTitle("subTitle")
            .withAssessmentMaxWords(1)
            .withAssessmentGuidance("blah")
            .withAssessmentGuidanceTitle("assessmentGuidanceTitle")
            .withScoreTotal(1)
            .withGuidanceRows(
                    newFormInputGuidanceRowResourceBuilder()
                        .withSubject("Subject")
                        .withJustification("justi")
                        .withId(2L)
                    .build(1)
            );
}
