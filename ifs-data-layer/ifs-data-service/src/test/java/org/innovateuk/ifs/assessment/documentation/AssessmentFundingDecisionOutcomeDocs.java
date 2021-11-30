package org.innovateuk.ifs.assessment.documentation;

import org.innovateuk.ifs.assessment.builder.AssessmentFundingDecisionOutcomeResourceBuilder;

import static java.lang.Boolean.TRUE;
import static org.innovateuk.ifs.assessment.builder.AssessmentFundingDecisionOutcomeResourceBuilder.newAssessmentFundingDecisionOutcomeResource;

public class AssessmentFundingDecisionOutcomeDocs {

    public static final AssessmentFundingDecisionOutcomeResourceBuilder assessmentFundingDecisionOutcomeResourceBuilder =
            newAssessmentFundingDecisionOutcomeResource()
                    .withFundingConfirmation(TRUE)
                    .withComment("comment")
                    .withFeedback("feedback");
}
