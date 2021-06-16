package org.innovateuk.ifs.assessment.period.security;

import org.innovateuk.ifs.BasePermissionRulesTest;
import org.innovateuk.ifs.assessment.domain.AssessmentParticipant;
import org.innovateuk.ifs.assessment.repository.AssessmentParticipantRepository;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.invite.domain.ParticipantStatus;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.Collections;

import static org.innovateuk.ifs.assessment.builder.AssessmentParticipantBuilder.newAssessmentParticipant;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class AssessmentPeriodPermissionRuleTest extends BasePermissionRulesTest<AssessmentPeriodPermissionRule> {

    private Long competitionId;
    private CompetitionResource competitionResource;

    @Mock
    private AssessmentParticipantRepository assessmentParticipantRepository;

    @Override
    protected AssessmentPeriodPermissionRule supplyPermissionRulesUnderTest() {
        return new AssessmentPeriodPermissionRule();
    }

    @Before
    public void setup() {
        competitionId = 1L;
        competitionResource = newCompetitionResource()
                .withId(competitionId)
                .build();
    }

    @Test
    public void compAdminCanReadAssessmentPeriod() {
        UserResource compAdmin = compAdminUser();
        UserResource ifsAdmin = ifsAdminUser();

        assertTrue(rules.compAdminCanReadAssessmentPeriod(competitionResource, compAdmin));
        assertFalse(rules.compAdminCanReadAssessmentPeriod(competitionResource, ifsAdmin));
    }

    @Test
    public void userCanReadAssessmentPeriod() {
        UserResource assessorWithAcceptedInvite = assessorUser();
        UserResource assessorWithoutInvite = assessorUser();

        Competition competition = newCompetition()
                .withId(competitionId)
                .build();

        AssessmentParticipant assessmentParticipant = newAssessmentParticipant()
                .withStatus(ParticipantStatus.ACCEPTED)
                .withCompetition(competition)
                .build();

        when(assessmentParticipantRepository.getByAssessorId(assessorWithAcceptedInvite.getId()))
                .thenReturn(Collections.singletonList(assessmentParticipant))
                .thenReturn(Collections.emptyList());

        assertTrue(rules.userCanReadAssessmentPeriod(competitionResource, assessorWithAcceptedInvite));
        assertFalse(rules.userCanReadAssessmentPeriod(competitionResource, assessorWithoutInvite));
    }
}
