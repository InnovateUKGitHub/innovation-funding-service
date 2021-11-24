package org.innovateuk.ifs.assessment.documentation;

import org.innovateuk.ifs.assessment.builder.AssessmentRejectOutcomeResourceBuilder;

import static org.innovateuk.ifs.assessment.builder.AssessmentRejectOutcomeResourceBuilder.newAssessmentRejectOutcomeResource;
import static org.innovateuk.ifs.assessment.resource.AssessmentRejectOutcomeValue.CONFLICT_OF_INTEREST;

public class AssessmentRejectOutcomeDocs {

    public static final AssessmentRejectOutcomeResourceBuilder assessmentRejectOutcomeResourceBuilder = newAssessmentRejectOutcomeResource()
            .withRejectReason(CONFLICT_OF_INTEREST)
            .withRejectComment("Member of board of directors");
}
