package org.innovateuk.ifs.documentation;

import org.innovateuk.ifs.assessment.builder.AssessmentTotalScoreResourceBuilder;
import org.springframework.restdocs.payload.FieldDescriptor;

import static org.innovateuk.ifs.assessment.builder.AssessmentTotalScoreResourceBuilder.newAssessmentTotalScoreResource;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

public class AssessmentTotalScoreResourceDocs {

    public static final FieldDescriptor[] assessmentTotalScoreResourceFields = {
            fieldWithPath("totalScoreGiven").description("The sum of the scores given for each of the assessed questions"),
            fieldWithPath("totalScorePossible").description("The sum of the maximum possible scores allowed for each of the questions")
    };

    public static final AssessmentTotalScoreResourceBuilder assessmentTotalScoreResourceBuilder = newAssessmentTotalScoreResource()
            .withTotalScoreGiven(55)
            .withTotalScorePossible(200);

}
