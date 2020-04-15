package org.innovateuk.ifs.application.security;

import org.innovateuk.ifs.BasePermissionRulesTest;
import org.innovateuk.ifs.competition.domain.InnovationLead;
import org.innovateuk.ifs.competition.repository.InnovationLeadRepository;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Test;
import org.mockito.Mock;

import java.util.List;

import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.competition.builder.InnovationLeadBuilder.newInnovationLead;
import static org.innovateuk.ifs.user.builder.UserBuilder.newUser;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.user.resource.Role.*;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class CompetitionSummaryPermissionRulesTest extends BasePermissionRulesTest<CompetitionSummaryPermissionRules> {

    @Mock
    private InnovationLeadRepository innovationLeadRepository;

    @Override
    protected CompetitionSummaryPermissionRules supplyPermissionRulesUnderTest() {
        return new CompetitionSummaryPermissionRules();
    }

    @Test
    public void internalUsersCanViewCompetitionSummaryOtherThanInnovationLeads() {
        CompetitionResource competitionResource = newCompetitionResource().build();
        allGlobalRoleUsers.forEach(user -> {
            if (!user.hasRole(INNOVATION_LEAD) && !user.hasRole(STAKEHOLDER) && allInternalUsers.contains(user)) {
                assertTrue(rules.allInternalUsersCanViewCompetitionSummaryOtherThanInnovationLeadsAndStakeholders(competitionResource, user));
            } else {
                assertFalse(rules.allInternalUsersCanViewCompetitionSummaryOtherThanInnovationLeadsAndStakeholders(competitionResource, user));
            }
        });
    }

    @Test
    public void innovationLeadsCanViewCompetitionSummaryOnAssignedComps() {
        CompetitionResource competitionResource = newCompetitionResource().build();
        List<Role> innovationLeadRoles = singletonList(INNOVATION_LEAD);
        UserResource innovationLeadAssignedToCompetition = newUserResource().withRolesGlobal(innovationLeadRoles).build();
        UserResource innovationLeadNotAssignedToCompetition = newUserResource().withRolesGlobal(innovationLeadRoles).build();
        List<InnovationLead> innovationLeads = newInnovationLead().withUser(newUser().withId
                (innovationLeadAssignedToCompetition.getId()).build()).build(1);

        when(innovationLeadRepository.findInnovationsLeads(competitionResource.getId())).thenReturn(innovationLeads);

        assertTrue(rules.innovationLeadsCanViewCompetitionSummaryOnAssignedComps(competitionResource, innovationLeadAssignedToCompetition));
        assertFalse(rules.innovationLeadsCanViewCompetitionSummaryOnAssignedComps(competitionResource, innovationLeadNotAssignedToCompetition));
    }

    @Test
    public void stakeholdersCanViewCompetitionSummaryOnAssignedComps() {
        CompetitionResource competitionResource = newCompetitionResource().build();
        List<Role> stakeholderRoles = singletonList(STAKEHOLDER);
        UserResource stakeholderAssignedToCompetition = newUserResource().withRolesGlobal(stakeholderRoles).build();
        UserResource stakeholderNotAssignedToCompetition = newUserResource().withRolesGlobal(stakeholderRoles).build();

        when(stakeholderRepository.existsByCompetitionIdAndUserId(competitionResource.getId(), stakeholderAssignedToCompetition.getId())).thenReturn(true);

        assertTrue(rules.stakeholdersCanViewCompetitionSummaryOnAssignedComps(competitionResource, stakeholderAssignedToCompetition));
        assertFalse(rules.stakeholdersCanViewCompetitionSummaryOnAssignedComps(competitionResource, stakeholderNotAssignedToCompetition));
    }

    @Test
    public void competitionFinanceUsersCanViewCompetitionSummaryOnAssignedComps() {
        CompetitionResource competitionResource = newCompetitionResource().build();
        List<Role> competitionFinanceRole = singletonList(COMPETITION_FINANCE);
        UserResource competitionFinanceUserAssignedToCompetition = newUserResource().withRolesGlobal(competitionFinanceRole).build();
        UserResource competitionFinanceUserNotAssignedToCompetition = newUserResource().withRolesGlobal(competitionFinanceRole).build();

        when(competitionFinanceRepository.existsByCompetitionIdAndUserId(competitionResource.getId(), competitionFinanceUserAssignedToCompetition.getId())).thenReturn(true);

        assertTrue(rules.competitionFinanceUsersCanViewCompetitionSummaryOnAssignedComps(competitionResource, competitionFinanceUserAssignedToCompetition));
        assertFalse(rules.competitionFinanceUsersCanViewCompetitionSummaryOnAssignedComps(competitionResource, competitionFinanceUserNotAssignedToCompetition));
    }
}
