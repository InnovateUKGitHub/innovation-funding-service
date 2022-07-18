package org.innovateuk.ifs.assessment.documentation;

import org.innovateuk.ifs.assessment.builder.AssessmentDecisionOutcomeResourceBuilder;

import static java.lang.Boolean.TRUE;
import static org.innovateuk.ifs.assessment.builder.AssessmentDecisionOutcomeResourceBuilder.newAssessmentDecisionOutcomeResource;

public class AssessmentDecisionOutcomeDocs {

    public static final AssessmentDecisionOutcomeResourceBuilder assessmentDecisionOutcomeResourceBuilder =
            newAssessmentDecisionOutcomeResource()
                    .withFundingConfirmation(TRUE)
                    .withComment("comment")
                    .withFeedback("feedback");
}
