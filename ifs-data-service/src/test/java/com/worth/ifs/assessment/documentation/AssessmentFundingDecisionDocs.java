package com.worth.ifs.assessment.documentation;

import com.worth.ifs.assessment.builder.AssessmentFundingDecisionResourceBuilder;
import org.springframework.restdocs.payload.FieldDescriptor;

import static com.worth.ifs.assessment.builder.AssessmentFundingDecisionResourceBuilder.newAssessmentFundingDecisionResource;
import static java.lang.Boolean.TRUE;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

public class AssessmentFundingDecisionDocs {
    public static final FieldDescriptor[] assessmentFundingDecisionResourceFields = {
            fieldWithPath("fundingConfirmation").description("Flag to signify if the assessor believes that the application is suitable for funding."),
            fieldWithPath("comment").description("Explanation of the decision which will be sent to the applicant. Mandatory if the decision is false."),
            fieldWithPath("feedback").description("Any other comments about this application which will only be shared with Innovate UK.")
    };

    public static final AssessmentFundingDecisionResourceBuilder assessmentFundingDecisionResourceBuilder = newAssessmentFundingDecisionResource()
            .withFundingConfirmation(TRUE)
            .withComment("comment")
            .withFeedback("feedback");
}