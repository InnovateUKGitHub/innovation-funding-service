package org.innovateuk.ifs.competition.security;

import org.innovateuk.ifs.BasePermissionRulesTest;
import org.innovateuk.ifs.competition.domain.InnovationLead;
import org.innovateuk.ifs.competition.repository.InnovationLeadRepository;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSearchResultItem;
import org.innovateuk.ifs.competition.resource.CompetitionStatus;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Test;
import org.mockito.Mock;

import java.util.List;

import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.competition.builder.CompetitionSearchResultItemBuilder.newCompetitionSearchResultItem;
import static org.innovateuk.ifs.competition.builder.InnovationLeadBuilder.newInnovationLead;
import static org.innovateuk.ifs.competition.resource.CompetitionStatus.COMPETITION_SETUP;
import static org.innovateuk.ifs.competition.resource.CompetitionStatus.READY_TO_OPEN;
import static org.innovateuk.ifs.user.builder.UserBuilder.newUser;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.user.resource.Role.*;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

/**
 * Tests the logic within the individual CompetitionPermissionRules methods that secures basic Competition details
 */
public class CompetitionPermissionRulesTest extends BasePermissionRulesTest<CompetitionPermissionRules> {

    @Mock
    private InnovationLeadRepository innovationLeadRepository;

	@Override
	protected CompetitionPermissionRules supplyPermissionRulesUnderTest() {
		return new CompetitionPermissionRules();
	}
	
    @Test
    public void externalUsersCannotViewACompetitionInSetup() {
        //null user cannot see competition in setup.
        assertFalse(rules.externalUsersCannotViewCompetitionsInSetup(newCompetitionResource().withCompetitionStatus(COMPETITION_SETUP).build(), null));
        //null user can see open competitions
        assertTrue(rules.externalUsersCannotViewCompetitionsInSetup(newCompetitionResource().withCompetitionStatus(CompetitionStatus.OPEN).build(), null));
    }

    @Test
    public void internalUsersOtherThanInnoLeadsCanViewAllCompetitions() {

        allGlobalRoleUsers.forEach(user -> {
            if (!user.hasRole(INNOVATION_LEAD) && allInternalUsers.contains(user)) {
                assertTrue(rules.internalUserCanViewAllCompetitions(newCompetitionResource().build(), user));
            } else {
                assertFalse(rules.internalUserCanViewAllCompetitions(newCompetitionResource().build(), user));
            }
        });
    }

    @Test
    public void internalUsersOtherThanInnoLeadsCanViewAllCompetitionSearchResults() {

        allGlobalRoleUsers.forEach(user -> {
            if (!user.hasRole(INNOVATION_LEAD) && allInternalUsers.contains(user)) {
                assertTrue(rules.internalUserCanViewAllCompetitionSearchResults(newCompetitionSearchResultItem().build(), user));
            } else {
                assertFalse(rules.internalUserCanViewAllCompetitionSearchResults(newCompetitionSearchResultItem().build(), user));
            }
        });
    }

    @Test
    public void internalAdminCanManageInnovationLeadsForCompetition() {
        allGlobalRoleUsers.forEach(user -> {
            if (getUserWithRole(COMP_ADMIN).equals(user) || getUserWithRole(PROJECT_FINANCE).equals(user)) {
                assertTrue(rules.internalAdminCanManageInnovationLeadsForCompetition(newCompetitionResource().build(), user));
            } else {
                assertFalse(rules.internalAdminCanManageInnovationLeadsForCompetition(newCompetitionResource().build(), user));
            }
        });
    }

    @Test
    public void internalUsersBarringInnovationLeadAndIFSAdminCanViewUnsuccessfulApplications() {
        allGlobalRoleUsers.forEach(user -> {
            if ((allInternalUsers.contains(user) && !user.hasRoles(INNOVATION_LEAD))
                    || getUserWithRole(IFS_ADMINISTRATOR).equals(user)) {
                assertTrue(rules.internalUsersAndIFSAdminCanViewUnsuccessfulApplications(newCompetitionResource().build(), user));
            } else {
                assertFalse(rules.internalUsersAndIFSAdminCanViewUnsuccessfulApplications(newCompetitionResource().build(), user));
            }
        });
    }

    @Test
    public void onlyInnovationLeadUsersAssignedToCompCanViewUnsuccessfulApplications() {
        List<Role> innovationLeadRoles = singletonList(INNOVATION_LEAD);
        UserResource innovationLeadAssignedToCompetition = newUserResource().withRolesGlobal(innovationLeadRoles).build();
        UserResource innovationLeadNotAssignedToCompetition = newUserResource().withRolesGlobal(innovationLeadRoles).build();
        List<InnovationLead> innovationLeads = newInnovationLead().withUser(newUser().withId(innovationLeadAssignedToCompetition.getId()).build()).build(1);
        CompetitionResource competition = newCompetitionResource().withId(1L).build();

        when(innovationLeadRepository.findInnovationsLeads(1L)).thenReturn(innovationLeads);

        assertTrue(rules.innovationLeadForCompetitionCanViewUnsuccessfulApplications(competition, innovationLeadAssignedToCompetition));
        assertFalse(rules.innovationLeadForCompetitionCanViewUnsuccessfulApplications(competition, innovationLeadNotAssignedToCompetition));
    }

    @Test
    public void onlyInnovationLeadUsersAssignedToCompCanAccess() {
        List<Role> innovationLeadRoles = singletonList(INNOVATION_LEAD);
        UserResource innovationLeadAssignedToCompetition = newUserResource().withRolesGlobal(innovationLeadRoles).build();
        UserResource innovationLeadNotAssignedToCompetition = newUserResource().withRolesGlobal(innovationLeadRoles).build();
        List<InnovationLead> innovationLeads = newInnovationLead().withUser(newUser().withId(innovationLeadAssignedToCompetition.getId()).build()).build(1);
        CompetitionSearchResultItem competitionSearchResultItem = newCompetitionSearchResultItem().withCompetitionStatus(CompetitionStatus.OPEN).withId(1L).build();
        CompetitionSearchResultItem competitionSearchResultItemFeedbackReleased = newCompetitionSearchResultItem().withCompetitionStatus(CompetitionStatus.ASSESSOR_FEEDBACK).withId(2L).build();

        when(innovationLeadRepository.findInnovationsLeads(1L)).thenReturn(innovationLeads);

        assertTrue(rules.innovationLeadCanViewCompetitionAssignedToThem(competitionSearchResultItem, innovationLeadAssignedToCompetition));
        assertFalse(rules.innovationLeadCanViewCompetitionAssignedToThem(competitionSearchResultItemFeedbackReleased, innovationLeadAssignedToCompetition));
        assertFalse(rules.innovationLeadCanViewCompetitionAssignedToThem(competitionSearchResultItem, innovationLeadNotAssignedToCompetition));
        assertFalse(rules.innovationLeadCanViewCompetitionAssignedToThem(competitionSearchResultItemFeedbackReleased, innovationLeadNotAssignedToCompetition));
    }

    @Test
    public void onlyInnovationLeadUsersAssignedToCompWithoutFeedbackReleasedCanAccessComp() {
        List<Role> innovationLeadRoles = singletonList(INNOVATION_LEAD);
        UserResource innovationLeadAssignedToCompetition = newUserResource().withRolesGlobal(innovationLeadRoles).build();
        UserResource innovationLeadNotAssignedToCompetition = newUserResource().withRolesGlobal(innovationLeadRoles).build();
        List<InnovationLead> innovationLeads = newInnovationLead().withUser(newUser().withId(innovationLeadAssignedToCompetition.getId()).build()).build(1);
        CompetitionResource openCompetition= newCompetitionResource().withCompetitionStatus(CompetitionStatus.OPEN).withId(1L).build();
        CompetitionResource feedbackReleasedCompetition = newCompetitionResource().withId(2L).withCompetitionStatus(CompetitionStatus.PROJECT_SETUP).withId(2L).build();

        when(innovationLeadRepository.findInnovationsLeads(1L)).thenReturn(innovationLeads);
        when(innovationLeadRepository.findInnovationsLeads(2L)).thenReturn(innovationLeads);

        assertTrue(rules.innovationLeadCanViewCompetitionAssignedToThem(openCompetition, innovationLeadAssignedToCompetition));
        assertFalse(rules.innovationLeadCanViewCompetitionAssignedToThem(openCompetition, innovationLeadNotAssignedToCompetition));
        assertTrue(rules.innovationLeadCanViewCompetitionAssignedToThem(feedbackReleasedCompetition, innovationLeadAssignedToCompetition));
        assertFalse(rules.innovationLeadCanViewCompetitionAssignedToThem(feedbackReleasedCompetition, innovationLeadNotAssignedToCompetition));
    }

    @Test
    public void onlyCompAdminsCanDeleteCompetitionsInPreparation() {
        List<CompetitionResource> competitions = newCompetitionResource()
                .withCompetitionStatus(CompetitionStatus.values())
                .build(CompetitionStatus.values().length);

        allGlobalRoleUsers.forEach(user -> competitions.forEach(competitionResource -> {
            if ((user.hasRole(COMP_ADMIN) || user.hasRole(PROJECT_FINANCE) || user.hasRole(IFS_ADMINISTRATOR)) &&
                    (competitionResource.getCompetitionStatus() == COMPETITION_SETUP ||
                    competitionResource.getCompetitionStatus() == READY_TO_OPEN)) {
                assertTrue(rules.internalAdminAndIFSAdminCanDeleteCompetitionInPreparation(competitionResource, user));
            } else {
                assertFalse(rules.internalAdminAndIFSAdminCanDeleteCompetitionInPreparation(competitionResource, user));
            }
        }));
    }
}
