package org.innovateuk.ifs.documentation;

import org.innovateuk.ifs.application.builder.ApplicationAssessorResourceBuilder;

import static org.innovateuk.ifs.application.builder.ApplicationAssessorResourceBuilder.newApplicationAssessorResource;
import static org.innovateuk.ifs.assessment.resource.AssessmentRejectOutcomeValue.CONFLICT_OF_INTEREST;
import static org.innovateuk.ifs.assessment.resource.AssessmentRejectOutcomeValue.TOO_MANY_ASSESSMENTS;
import static org.innovateuk.ifs.assessment.resource.AssessmentState.READY_TO_SUBMIT;
import static org.innovateuk.ifs.assessment.resource.AssessmentState.REJECTED;
import static org.innovateuk.ifs.user.resource.BusinessType.ACADEMIC;
import static org.innovateuk.ifs.user.resource.BusinessType.BUSINESS;

public class ApplicationAssessorResourceDocs {

    public static final ApplicationAssessorResourceBuilder applicationAssessorResourceBuilder = newApplicationAssessorResource()
            .withUserId(1L, 2L)
            .withFirstName("Oliver", "Irving")
            .withLastName("Romero", "Wolfe")
            .withBusinessType(ACADEMIC, BUSINESS)
            .withSkillAreas("Human computer interaction, Wearables, IoT", "Solar Power, Genetics, Recycling")
            .withRejectReason(CONFLICT_OF_INTEREST, TOO_MANY_ASSESSMENTS)
            .withRejectComment("Member of board of directors", "I do like reviewing the applications to your competitions but please do not assign so many to me.")
            .withMostRecentAssessmentId(1L)
            .withMostRecentAssessmentState(READY_TO_SUBMIT, REJECTED)
            .withTotalApplicationsCount(6L, 8L)
            .withAssignedCount(4L, 6L)
            .withSubmittedCount(1L, 3L);
}