package org.innovateuk.ifs.documentation;

import org.innovateuk.ifs.competition.builder.CompetitionSetupQuestionResourceBuilder;

import static org.innovateuk.ifs.competition.builder.CompetitionSetupQuestionResourceBuilder.newCompetitionSetupQuestionResource;
import static org.innovateuk.ifs.form.builder.GuidanceRowResourceBuilder.newFormInputGuidanceRowResourceBuilder;

public class CompetitionSetupQuestionResourceDocs {

    public static final CompetitionSetupQuestionResourceBuilder competitionSetupQuestionResourceBuilder = newCompetitionSetupQuestionResource()
            .withQuestionId(1L)
            .withAppendix(false)
            .withNumberOfUploads(0)
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
