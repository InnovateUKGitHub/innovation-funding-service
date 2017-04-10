package org.innovateuk.ifs.documentation;

import org.innovateuk.ifs.assessment.builder.AssessmentResourceBuilder;
import org.innovateuk.ifs.assessment.builder.AssessmentSubmissionsResourceBuilder;
import org.innovateuk.ifs.workflow.resource.ProcessEvent;
import org.springframework.restdocs.payload.FieldDescriptor;

import java.time.LocalDate;
import java.time.ZonedDateTime;

import static java.util.Arrays.asList;
import static org.innovateuk.ifs.assessment.builder.AssessmentResourceBuilder.newAssessmentResource;
import static org.innovateuk.ifs.assessment.builder.AssessmentSubmissionsResourceBuilder.newAssessmentSubmissionsResource;
import static org.innovateuk.ifs.assessment.documentation.AssessmentFundingDecisionOutcomeDocs.assessmentFundingDecisionOutcomeResourceBuilder;
import static org.innovateuk.ifs.assessment.documentation.AssessmentRejectOutcomeDocs.assessmentRejectOutcomeResourceBuilder;
import static org.innovateuk.ifs.assessment.resource.AssessmentStates.OPEN;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

public class AssessmentDocs {
    public static final FieldDescriptor[] assessmentFields = {
            fieldWithPath("id").description("Id of the assessment"),
            fieldWithPath("event").description("currently not used"),
            fieldWithPath("lastModified").description("last modified"),
            fieldWithPath("startDate").description("start date of the assessment"),
            fieldWithPath("endDate").description("end date of the assessment"),
            fieldWithPath("fundingDecision").description("Response to the application funding confirmation"),
            fieldWithPath("rejection").description("The reason for rejecting the application"),
            fieldWithPath("processRole").description("process role of the assigned assessor"),
            fieldWithPath("application").description("the id of the application being assessed"),
            fieldWithPath("applicationName").description("the name of the application being assessed"),
            fieldWithPath("competition").description("the competition id of the application being assessed"),
            fieldWithPath("assessmentState").description("the current workflow state of the assessment process"),
            fieldWithPath("internalParticipant").description("the user id of an internal user who is working on the process"),
    };

    public static final AssessmentResourceBuilder assessmentResourceBuilder = newAssessmentResource()
            .withId(1L)
            .withStartDate(LocalDate.now())
            .withEndDate(LocalDate.now().plusDays(14))
            .withFundingDecision(assessmentFundingDecisionOutcomeResourceBuilder)
            .withRejection(assessmentRejectOutcomeResourceBuilder)
            .withActivityState(OPEN)
            .withProcessEvent(ProcessEvent.ASSESSMENT)
            .withLastModifiedDate(ZonedDateTime.now())
            .withProcessRole(1L)
            .withApplication(2L);

    public static final FieldDescriptor[] assessmentSubmissionsFields = {
            fieldWithPath("assessmentIds").description("List of assessment ids to submit.")
    };

    public static final AssessmentSubmissionsResourceBuilder assessmentSubmissionsResourceBuilder =
            newAssessmentSubmissionsResource()
                    .withAssessmentIds(asList(1L, 2L));
}
