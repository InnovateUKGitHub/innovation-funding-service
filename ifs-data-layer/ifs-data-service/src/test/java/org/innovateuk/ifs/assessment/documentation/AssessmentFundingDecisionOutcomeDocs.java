package org.innovateuk.ifs.assessment.documentation;

import org.innovateuk.ifs.assessment.builder.AssessmentFundingDecisionOutcomeResourceBuilder;
import org.springframework.restdocs.payload.FieldDescriptor;

import static java.lang.Boolean.TRUE;
import static org.innovateuk.ifs.assessment.builder.AssessmentFundingDecisionOutcomeResourceBuilder.newAssessmentFundingDecisionOutcomeResource;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

public class AssessmentFundingDecisionOutcomeDocs {
    public static final FieldDescriptor[] assessmentFundingDecisionOutcomeResourceFields = {
            fieldWithPath("fundingConfirmation").description("Flag to signify if the assessor believes that the application is suitable for funding."),
            fieldWithPath("comment").description("Explanation of the decision which will be sent to the applicant. Mandatory if the decision is false."),
            fieldWithPath("feedback").description("Any other comments about this application which will only be shared with Innovate UK.")
    };

    public static final AssessmentFundingDecisionOutcomeResourceBuilder assessmentFundingDecisionOutcomeResourceBuilder =
            newAssessmentFundingDecisionOutcomeResource()
                    .withFundingConfirmation(TRUE)
                    .withComment("comment")
                    .withFeedback("feedback");
}
