package org.innovateuk.ifs.assessment.documentation;

import org.innovateuk.ifs.assessment.builder.AssessorCompetitionSummaryResourceBuilder;

import static org.innovateuk.ifs.assessment.builder.AssessorAssessmentResourceBuilder.newAssessorAssessmentResource;
import static org.innovateuk.ifs.assessment.builder.AssessorCompetitionSummaryResourceBuilder.newAssessorCompetitionSummaryResource;
import static org.innovateuk.ifs.assessment.builder.AssessorProfileResourceBuilder.newAssessorProfileResource;
import static org.innovateuk.ifs.assessment.builder.ProfileResourceBuilder.newProfileResource;
import static org.innovateuk.ifs.assessment.resource.AssessmentState.ACCEPTED;
import static org.innovateuk.ifs.assessment.resource.AssessmentState.SUBMITTED;
import static org.innovateuk.ifs.competition.resource.CompetitionStatus.IN_ASSESSMENT;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;

public class AssessorCompetitionSummaryResourceDocs {

    public static final AssessorCompetitionSummaryResourceBuilder assessorCompetitionSummaryResourceBuilder =
            newAssessorCompetitionSummaryResource()
                    .withCompetitionId(1L)
                    .withCompetitionName("Test Competition")
                    .withCompetitionStatus(IN_ASSESSMENT)
                    .withAssessor(
                            newAssessorProfileResource()
                                    .withUser(newUserResource().build())
                                    .withProfile(newProfileResource().build())
                                    .build()
                    )
                    .withTotalApplications(10L)
                    .withAssignedAssessments(
                            newAssessorAssessmentResource()
                                    .withApplicationId(1L, 2L)
                                    .withApplicationName("Test Application 1", "Test Application 2")
                                    .withLeadOrganisation("Lead Org 1", "Lead Org 2")
                                    .withTotalAssessors(5, 3)
                                    .withState(SUBMITTED, ACCEPTED)
                                    .build(2)
                    );
}
