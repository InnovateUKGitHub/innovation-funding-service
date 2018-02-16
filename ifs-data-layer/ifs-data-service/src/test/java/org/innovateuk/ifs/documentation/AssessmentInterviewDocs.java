package org.innovateuk.ifs.documentation;

import org.innovateuk.ifs.assessment.builder.AssessmentInterviewResourceBuilder;
import org.springframework.restdocs.payload.FieldDescriptor;

import java.time.LocalDate;

import static org.innovateuk.ifs.assessment.builder.AssessmentFundingDecisionOutcomeResourceBuilder.newAssessmentFundingDecisionOutcomeResource;
import static org.innovateuk.ifs.assessment.builder.AssessmentInterviewResourceBuilder.newAssessmentInterviewResource;
import static org.innovateuk.ifs.assessment.interview.resource.AssessmentInterviewState.CREATED;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

public class AssessmentInterviewDocs {

    public static final FieldDescriptor[] assessmentInterviewFields = {
            fieldWithPath("id").description("Id of the assessment review"),
            fieldWithPath("startDate").description("start date of the assessment review"),
            fieldWithPath("endDate").description("end date of the assessment review"),
            fieldWithPath("fundingDecision").description("Response to the application funding confirmation"),
            fieldWithPath("processRole").description("process role of the assigned assessor"),
            fieldWithPath("application").description("the id of the application being assessed"),
            fieldWithPath("applicationName").description("the name of the application being assessed"),
            fieldWithPath("assessmentInterviewState").description("the current workflow state of the assessment interview process"),
    };

    public static final AssessmentInterviewResourceBuilder assessmentInterviewResourceBuilder = newAssessmentInterviewResource()
            .withId(1L)
            .withStartDate(LocalDate.now())
            .withEndDate(LocalDate.now().plusDays(14))
            .withFundingDecision(newAssessmentFundingDecisionOutcomeResource()
                    .withFundingConfirmation(true)
                    .withFeedback()
                    .withComment()
                    .build())
            .withProcessRole(1L)
            .withApplication(2L)
            .withApplicationName()
            .withActivityState(CREATED);

}

