package org.innovateuk.ifs.documentation;

import org.innovateuk.ifs.assessment.builder.AssessmentTotalScoreResourceBuilder;

import static org.innovateuk.ifs.assessment.builder.AssessmentTotalScoreResourceBuilder.newAssessmentTotalScoreResource;

public class AssessmentTotalScoreResourceDocs {

    public static final AssessmentTotalScoreResourceBuilder assessmentTotalScoreResourceBuilder = newAssessmentTotalScoreResource()
            .withTotalScoreGiven(55)
            .withTotalScorePossible(200);

}
