package org.innovateuk.ifs.competition.security;

import org.innovateuk.ifs.BasePermissionRulesTest;
import org.innovateuk.ifs.assessment.repository.AssessmentParticipantRepository;
import org.innovateuk.ifs.competition.resource.CompetitionCompositeId;
import org.innovateuk.ifs.assessment.domain.AssessmentParticipant;
import org.innovateuk.ifs.competition.domain.CompetitionParticipantRole;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Test;
import org.mockito.Mock;

import java.util.List;

import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.assessment.builder.AssessmentParticipantBuilder.newAssessmentParticipant;
import static org.innovateuk.ifs.user.builder.UserBuilder.newUser;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class MilestonePermissionRulesTest extends BasePermissionRulesTest<MilestonePermissionRules> {

    @Mock
    private AssessmentParticipantRepository assessmentParticipantRepositoryMock;

	@Override
	protected MilestonePermissionRules supplyPermissionRulesUnderTest() {
		return new MilestonePermissionRules();
	}

    @Test
    public void testInternalUsersOtherThanInnovationLeadsCanViewAllMilestones() {
        allGlobalRoleUsers.forEach(user -> {
            if (!user.hasRole(Role.INNOVATION_LEAD) && allInternalUsers.contains(user)) {
                assertTrue(rules.allInternalUsersCanViewCompetitionMilestonesOtherThanInnovationLeads(CompetitionCompositeId.id(1L), user));
            } else {
                assertFalse(rules.allInternalUsersCanViewCompetitionMilestonesOtherThanInnovationLeads(CompetitionCompositeId.id(1L), user));
            }
        });
    }

    @Test
    public void testOnlyInnovationLeadUsersAssignedToCompCanAccess() {
        List<Role> innovationLeadRoles = singletonList(Role.INNOVATION_LEAD);
        UserResource innovationLeadAssignedToCompetition = newUserResource().withRolesGlobal(innovationLeadRoles).build();
        UserResource innovationLeadNotAssignedToCompetition = newUserResource().withRolesGlobal(innovationLeadRoles).build();
        List<AssessmentParticipant> competitionParticipants = newAssessmentParticipant().withUser(newUser().withId(innovationLeadAssignedToCompetition.getId()).build()).build(1);

        when(assessmentParticipantRepositoryMock.getByCompetitionIdAndRole(1L, CompetitionParticipantRole.INNOVATION_LEAD)).thenReturn(competitionParticipants);

        assertTrue(rules.innovationLeadsCanViewMilestonesOnAssignedComps(CompetitionCompositeId.id(1L), innovationLeadAssignedToCompetition));
        assertFalse(rules.innovationLeadsCanViewMilestonesOnAssignedComps(CompetitionCompositeId.id(1L), innovationLeadNotAssignedToCompetition));
    }

    @Test
    public void testInternalUsersCanReadMilestoneByType() {
        allGlobalRoleUsers.forEach(user -> {
            if (allInternalUsers.contains(user)) {
                assertTrue(rules.allInternalUsersCanViewCompetitionMilestonesByType(CompetitionCompositeId.id(1L), user));
            } else {
                assertFalse(rules.allInternalUsersCanViewCompetitionMilestonesByType(CompetitionCompositeId.id(1L), user));
            }
        });
    }
}
