package org.innovateuk.ifs.application.security;

import org.innovateuk.ifs.BasePermissionRulesTest;
import org.innovateuk.ifs.competition.builder.StakeholderBuilder;
import org.innovateuk.ifs.competition.domain.InnovationLead;
import org.innovateuk.ifs.competition.domain.Stakeholder;
import org.innovateuk.ifs.competition.repository.InnovationLeadRepository;
import org.innovateuk.ifs.competition.repository.StakeholderRepository;
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
import static org.innovateuk.ifs.user.resource.Role.INNOVATION_LEAD;
import static org.innovateuk.ifs.user.resource.Role.STAKEHOLDER;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class CompetitionSummaryPermissionRulesTest extends BasePermissionRulesTest<CompetitionSummaryPermissionRules> {

    @Mock
    private InnovationLeadRepository innovationLeadRepository;

    @Mock
    private StakeholderRepository stakeholderRepository;

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
        List<Stakeholder> stakeholders = StakeholderBuilder.newStakeholder().withUser(newUser().withId
                (stakeholderAssignedToCompetition.getId()).build()).build(1);

        when(stakeholderRepository.findStakeholders(competitionResource.getId())).thenReturn(stakeholders);

        assertTrue(rules.stakeholdersCanViewCompetitionSummaryOnAssignedComps(competitionResource, stakeholderAssignedToCompetition));
        assertFalse(rules.stakeholdersCanViewCompetitionSummaryOnAssignedComps(competitionResource, stakeholderNotAssignedToCompetition));
    }
}
