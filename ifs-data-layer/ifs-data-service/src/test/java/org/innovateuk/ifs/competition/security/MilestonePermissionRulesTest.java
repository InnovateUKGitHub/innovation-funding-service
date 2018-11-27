package org.innovateuk.ifs.competition.security;

import org.innovateuk.ifs.BasePermissionRulesTest;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.domain.InnovationLead;
import org.innovateuk.ifs.competition.domain.Stakeholder;
import org.innovateuk.ifs.competition.repository.CompetitionRepository;
import org.innovateuk.ifs.competition.repository.InnovationLeadRepository;
import org.innovateuk.ifs.competition.repository.StakeholderRepository;
import org.innovateuk.ifs.competition.resource.CompetitionCompositeId;
import org.innovateuk.ifs.competition.resource.CompetitionStatus;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Test;
import org.mockito.Mock;

import java.util.List;

import static java.util.Arrays.stream;
import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.competition.builder.InnovationLeadBuilder.newInnovationLead;
import static org.innovateuk.ifs.competition.builder.StakeholderBuilder.newStakeholder;
import static org.innovateuk.ifs.user.builder.UserBuilder.newUser;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.user.resource.Role.STAKEHOLDER;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class MilestonePermissionRulesTest extends BasePermissionRulesTest<MilestonePermissionRules> {

    @Mock
    private InnovationLeadRepository innovationLeadRepository;

    @Mock
    private StakeholderRepository stakeholderRepository;

    @Mock
    private CompetitionRepository competitionRepository;

	@Override
	protected MilestonePermissionRules supplyPermissionRulesUnderTest() {
		return new MilestonePermissionRules();
	}

	private long competitionId = 123L;
	private CompetitionCompositeId compositeId = CompetitionCompositeId.id(123L);

    @Test
    public void internalUsersOtherThanInnovationLeadsCanViewAllMilestones() {
        allGlobalRoleUsers.forEach(user -> {
            if (!user.hasRole(Role.INNOVATION_LEAD) && !user.hasRole(STAKEHOLDER) && allInternalUsers.contains(user)) {
                assertTrue(rules.allInternalUsersCanViewCompetitionMilestonesOtherThanInnovationLeads(compositeId, user));
            } else {
                assertFalse(rules.allInternalUsersCanViewCompetitionMilestonesOtherThanInnovationLeads(compositeId, user));
            }
        });
    }

    @Test
    public void onlyInnovationLeadUsersAssignedToCompCanAccess() {
        List<Role> innovationLeadRoles = singletonList(Role.INNOVATION_LEAD);
        UserResource innovationLeadAssignedToCompetition = newUserResource().withRolesGlobal(innovationLeadRoles).build();
        UserResource innovationLeadNotAssignedToCompetition = newUserResource().withRolesGlobal(innovationLeadRoles).build();
        List<InnovationLead> innovationLeads = newInnovationLead().withUser(newUser().withId
                (innovationLeadAssignedToCompetition.getId()).build()).build(1);

        when(innovationLeadRepository.findInnovationsLeads(competitionId)).thenReturn(innovationLeads);

        assertTrue(rules.innovationLeadsCanViewMilestonesOnAssignedComps(compositeId, innovationLeadAssignedToCompetition));
        assertFalse(rules.innovationLeadsCanViewMilestonesOnAssignedComps(compositeId, innovationLeadNotAssignedToCompetition));
    }

    @Test
    public void onlyStakeholdersAssignedToCompCanAccess() {
        List<Role> stakeholderRoles = singletonList(STAKEHOLDER);
        UserResource stakeholderAssignedToCompetition = newUserResource().withRolesGlobal(stakeholderRoles).build();
        UserResource stakeholderNotAssignedToCompetition = newUserResource().withRolesGlobal(stakeholderRoles).build();
        List<Stakeholder> stakeholders = newStakeholder().withUser(newUser().withId
                (stakeholderAssignedToCompetition.getId()).build()).build(1);

        when(stakeholderRepository.findStakeholders(competitionId)).thenReturn(stakeholders);

        assertTrue(rules.stakeholdersCanViewMilestonesOnAssignedComps(compositeId, stakeholderAssignedToCompetition));
        assertFalse(rules.stakeholdersCanViewMilestonesOnAssignedComps(compositeId, stakeholderNotAssignedToCompetition));
    }

    @Test
    public void internalUsersCanReadMilestoneByType() {
        allGlobalRoleUsers.forEach(user -> {
            if (allInternalUsers.contains(user)) {
                assertTrue(rules.allInternalUsersCanViewCompetitionMilestonesByType(compositeId, user));
            } else {
                assertFalse(rules.allInternalUsersCanViewCompetitionMilestonesByType(compositeId, user));
            }
        });
    }

    @Test
    public void compAdminsAndProjectFinanceUserCanUpdateCompletionStageDuringCompetitionSetup() {

        Competition competitionInSetup = newCompetition().
                withCompetitionStatus(CompetitionStatus.COMPETITION_SETUP).
                build();

        when(competitionRepository.findById(competitionId)).thenReturn(competitionInSetup);

        allGlobalRoleUsers.forEach(user -> {
            if (compAdminAndProjectFinance.contains(user)) {
                assertTrue(rules.compAdminsAndProjectFinanceUserCanUpdateCompletionStageDuringCompetitionSetup(compositeId, user));
            } else {
                assertFalse(rules.compAdminsAndProjectFinanceUserCanUpdateCompletionStageDuringCompetitionSetup(compositeId, user));
            }
        });

        verify(competitionRepository, times(2)).findById(competitionId);
    }

    @Test
    public void compAdminsAndProjectFinanceUserCanUpdateCompletionStageDuringCompetitionSetup_ButNotWhenCompetitionPastSetup() {

        stream(CompetitionStatus.values()).forEach(status -> {

            Competition competitionInSetup = newCompetition().
                    withCompetitionStatus(status).
                    build();

            when(competitionRepository.findById(competitionId)).thenReturn(competitionInSetup);

            compAdminAndProjectFinance.forEach(user -> {
                if (CompetitionStatus.COMPETITION_SETUP.equals(status)) {
                    assertTrue(rules.compAdminsAndProjectFinanceUserCanUpdateCompletionStageDuringCompetitionSetup(compositeId, user));
                } else {
                    assertFalse(rules.compAdminsAndProjectFinanceUserCanUpdateCompletionStageDuringCompetitionSetup(compositeId, user));
                }
            });

            verify(competitionRepository, times(2)).findById(competitionId);
            reset(competitionRepository);
        });
    }
}
