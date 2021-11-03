package org.innovateuk.ifs.documentation;

import org.innovateuk.ifs.assessment.builder.AssessmentTotalScoreResourceBuilder;
import org.springframework.restdocs.payload.FieldDescriptor;

import static org.innovateuk.ifs.assessment.builder.AssessmentTotalScoreResourceBuilder.newAssessmentTotalScoreResource;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

public class AssessmentTotalScoreResourceDocs {

    public static final FieldDescriptor[] assessmentTotalScoreResourceFields = {
            fieldWithPath("totalScoreGiven").description("The sum of the scores given for each of the assessed questions"),
            fieldWithPath("totalScoreGiven").description("The sum of the scores given for each of the assessed questions"),
            fieldWithPath("maxScoreGiven").description("The max Score Given"),
            fieldWithPath("minScoreGiven").description("The min Score Given"),
            fieldWithPath("totalScorePossible").description("The total Score Possible")
    };

    public static final AssessmentTotalScoreResourceBuilder assessmentTotalScoreResourceBuilder = newAssessmentTotalScoreResource()
            .withTotalScoreGiven(55)
            .withTotalScorePossible(200);

}
