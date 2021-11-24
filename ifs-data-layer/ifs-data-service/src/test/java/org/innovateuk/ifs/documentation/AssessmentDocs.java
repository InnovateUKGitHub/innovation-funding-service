package org.innovateuk.ifs.documentation;

import org.innovateuk.ifs.assessment.builder.AssessmentResourceBuilder;
import org.innovateuk.ifs.assessment.builder.AssessmentSubmissionsResourceBuilder;
import org.springframework.restdocs.payload.FieldDescriptor;

import java.time.LocalDate;

import static java.util.Arrays.asList;
import static org.innovateuk.ifs.assessment.builder.AssessmentResourceBuilder.newAssessmentResource;
import static org.innovateuk.ifs.assessment.builder.AssessmentSubmissionsResourceBuilder.newAssessmentSubmissionsResource;
import static org.innovateuk.ifs.assessment.documentation.AssessmentFundingDecisionOutcomeDocs.assessmentFundingDecisionOutcomeResourceBuilder;
import static org.innovateuk.ifs.assessment.documentation.AssessmentRejectOutcomeDocs.assessmentRejectOutcomeResourceBuilder;
import static org.innovateuk.ifs.assessment.resource.AssessmentState.OPEN;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

public class AssessmentDocs {

    public static final AssessmentResourceBuilder assessmentResourceBuilder = newAssessmentResource()
            .withId(1L)
            .withStartDate(LocalDate.now())
            .withEndDate(LocalDate.now().plusDays(14))
            .withFundingDecision(assessmentFundingDecisionOutcomeResourceBuilder)
            .withRejection(assessmentRejectOutcomeResourceBuilder)
            .withActivityState(OPEN)
            .withProcessRole(1L)
            .withApplication(2L);

    public static final AssessmentSubmissionsResourceBuilder assessmentSubmissionsResourceBuilder =
            newAssessmentSubmissionsResource()
                    .withAssessmentIds(asList(1L, 2L));
}
