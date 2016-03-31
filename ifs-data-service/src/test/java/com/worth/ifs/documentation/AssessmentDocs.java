package com.worth.ifs.documentation;

import java.time.LocalDate;

import com.worth.ifs.assessment.builder.AssessmentBuilder;
import com.worth.ifs.assessment.domain.AssessmentStates;
import com.worth.ifs.workflow.domain.ProcessEvent;

import org.springframework.restdocs.payload.FieldDescriptor;

import static com.worth.ifs.assessment.builder.AssessmentBuilder.newAssessment;
import static com.worth.ifs.assessment.builder.ProcessOutcomeBuilder.newProcessOutcome;
import static com.worth.ifs.user.builder.ProcessRoleBuilder.newProcessRole;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

public class AssessmentDocs {
    public static final FieldDescriptor[] assessmentFields = {
            fieldWithPath("id").description("Id of the assessment"),
            fieldWithPath("startDate").description("start date of the assessment"),
            fieldWithPath("endDate").description("end date of the assessment"),
            fieldWithPath("processOutcomes").description("outcomes of the assessment process"),
            fieldWithPath("lastOutcome").description("last outcome for the assessment process"),
            fieldWithPath("processStatus").description("current status of the assessment process"),
            fieldWithPath("processEvent").description("currently not used"),
            fieldWithPath("processRole").description("process role of the assigned assessor")
    };

    public static final AssessmentBuilder assessmentBuilder = newAssessment()
            .withId(1L)
            .withStartDate(LocalDate.now())
            .withEndDate(LocalDate.now().plusDays(14))
            .withProcessOutcome(newProcessOutcome().build(2))
            .withProcessStatus(AssessmentStates.OPEN)
            .withProcessEvent(ProcessEvent.ASSESSMENT)
            .withProcessRole(newProcessRole().build());
}
