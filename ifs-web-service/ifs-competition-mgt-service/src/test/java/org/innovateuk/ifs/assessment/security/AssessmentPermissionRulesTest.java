package org.innovateuk.ifs.assessment.security;

import org.innovateuk.ifs.BasePermissionRulesTest;
import org.innovateuk.ifs.competition.resource.CompetitionCompositeId;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionStatus;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Test;
import org.mockito.Mock;

import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class AssessmentPermissionRulesTest extends BasePermissionRulesTest<AssessmentPermissionRules> {

    @Mock
    private CompetitionRestService competitionRestService;

    @Override
    protected AssessmentPermissionRules supplyPermissionRulesUnderTest() {
        return new AssessmentPermissionRules();
    }

    @Test
    public void reviewPanel() {
        UserResource loggedInUser = compAdminUser();

        for (CompetitionStatus competitionStatus : CompetitionStatus.values()) {
            final CompetitionResource competition = newCompetitionResource()
                    .withCompetitionStatus(competitionStatus)
                    .build();

            when(competitionRestService.getCompetitionById(competition.getId())).thenReturn(restSuccess(competition));

            switch (competitionStatus) {
                case ASSESSOR_FEEDBACK: case PROJECT_SETUP:
                    assertFalse("With status " + competitionStatus.toString(),
                            rules.assessment(CompetitionCompositeId.id(competition.getId()), loggedInUser));
                    break;
                default:
                    assertTrue("With status " + competitionStatus.toString(),
                            rules.assessment(CompetitionCompositeId.id(competition.getId()), loggedInUser));
            }
        }
    }

    @Test
    public void assessmentApplications() {
        UserResource loggedInUser = compAdminUser();

        for (CompetitionStatus competitionStatus : CompetitionStatus.values()) {
            final CompetitionResource competition = newCompetitionResource()
                    .withHasAssessmentPanel(true)
                    .withCompetitionStatus(competitionStatus)
                    .build();

            when(competitionRestService.getCompetitionById(competition.getId())).thenReturn(restSuccess(competition));

            switch (competitionStatus) {
                case ASSESSOR_FEEDBACK: case PROJECT_SETUP:
                    assertFalse("With status " + competitionStatus.toString(),
                            rules.assessmentApplications(CompetitionCompositeId.id(competition.getId()), loggedInUser));
                    break;
                default:
                    assertTrue("With status " + competitionStatus.toString(),
                            rules.assessmentApplications(CompetitionCompositeId.id(competition.getId()), loggedInUser));
            }
        }
    }
}