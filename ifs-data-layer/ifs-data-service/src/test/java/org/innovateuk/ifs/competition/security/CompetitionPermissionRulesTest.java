package org.innovateuk.ifs.competition.security;

import org.innovateuk.ifs.BasePermissionRulesTest;
import org.innovateuk.ifs.competition.domain.InnovationLead;
import org.innovateuk.ifs.competition.repository.InnovationLeadRepository;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionStatus;
import org.innovateuk.ifs.competition.resource.search.CompetitionSearchResultItem;
import org.innovateuk.ifs.project.core.domain.ProjectParticipantRole;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Test;
import org.mockito.Mock;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.competition.builder.InnovationLeadBuilder.newInnovationLead;
import static org.innovateuk.ifs.competition.builder.LiveCompetitionSearchResultItemBuilder.newLiveCompetitionSearchResultItem;
import static org.innovateuk.ifs.competition.resource.CompetitionStatus.*;
import static org.innovateuk.ifs.project.core.domain.ProjectParticipantRole.PROJECT_USER_ROLES;
import static org.innovateuk.ifs.user.builder.UserBuilder.newUser;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.user.resource.Role.*;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
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
        assertTrue(rules.externalUsersCannotViewCompetitionsInSetup(newCompetitionResource().withCompetitionStatus(OPEN).build(), null));
    }

    @Test
    public void internalUsersOtherThanInnovationLeadsCanViewAllCompetitions() {

        allGlobalRoleUsers.forEach(user -> {
            if (!user.hasRole(INNOVATION_LEAD) && !user.hasRole(STAKEHOLDER) && allInternalUsers.contains(user)) {
                assertTrue(rules.internalUserCanViewAllCompetitions(newCompetitionResource().build(), user));
            } else {
                assertFalse(rules.internalUserCanViewAllCompetitions(newCompetitionResource().build(), user));
            }
        });
    }

    @Test
    public void internalUsersOtherThanInnoLeadsCanViewAllCompetitionSearchResults() {

        allGlobalRoleUsers.forEach(user -> {
            if (!user.hasRole(INNOVATION_LEAD) && !user.hasRole(STAKEHOLDER) && allInternalUsers.contains(user)) {
                assertTrue(rules.internalUserCanViewAllCompetitionSearchResults(newLiveCompetitionSearchResultItem().build(), user));
            } else {
                assertFalse(rules.internalUserCanViewAllCompetitionSearchResults(newLiveCompetitionSearchResultItem().build(), user));
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
    public void internalUsersBarringInnovationLeadAndIFSAdminCanViewPreviousApplications() {
        allGlobalRoleUsers.forEach(user -> {
            if ((allInternalUsers.contains(user) && !user.hasRole(INNOVATION_LEAD) && !user.hasRole(STAKEHOLDER))
                    || getUserWithRole(IFS_ADMINISTRATOR).equals(user)) {
                assertTrue(rules.internalUsersAndIFSAdminCanViewPreviousApplications(newCompetitionResource().build(), user));
            } else {
                assertFalse(rules.internalUsersAndIFSAdminCanViewPreviousApplications(newCompetitionResource().build(), user));
            }
        });
    }

    @Test
    public void onlyInnovationLeadUsersAssignedToCompCanViewPreviousApplications() {
        List<Role> innovationLeadRoles = singletonList(INNOVATION_LEAD);
        UserResource innovationLeadAssignedToCompetition = newUserResource().withRolesGlobal(innovationLeadRoles).build();
        UserResource innovationLeadNotAssignedToCompetition = newUserResource().withRolesGlobal(innovationLeadRoles).build();
        List<InnovationLead> innovationLeads = newInnovationLead().withUser(newUser().withId(innovationLeadAssignedToCompetition.getId()).build()).build(1);
        CompetitionResource competition = newCompetitionResource().withId(1L).build();

        when(innovationLeadRepository.findInnovationsLeads(1L)).thenReturn(innovationLeads);

        assertTrue(rules.innovationLeadForCompetitionCanViewPreviousApplications(competition, innovationLeadAssignedToCompetition));
        assertFalse(rules.innovationLeadForCompetitionCanViewPreviousApplications(competition, innovationLeadNotAssignedToCompetition));
    }

    @Test
    public void onlyStakeholdersAssignedToCompCanViewPreviousApplications() {
        List<Role> stakeholderRoles = singletonList(STAKEHOLDER);
        UserResource stakeholderAssignedToCompetition = newUserResource().withRolesGlobal(stakeholderRoles).build();
        UserResource stakeholderNotAssignedToCompetition = newUserResource().withRolesGlobal(stakeholderRoles).build();
        CompetitionResource competition = newCompetitionResource().withId(1L).build();

        when(stakeholderRepository.existsByCompetitionIdAndUserId(competition.getId(), stakeholderAssignedToCompetition.getId())).thenReturn(true);

        assertTrue(rules.stakeholderForCompetitionCanViewPreviousApplications(competition, stakeholderAssignedToCompetition));
        assertFalse(rules.stakeholderForCompetitionCanViewPreviousApplications(competition, stakeholderNotAssignedToCompetition));
    }

    @Test
    public void onlyInnovationLeadUsersAssignedToCompCanAccess() {
        List<Role> innovationLeadRoles = singletonList(INNOVATION_LEAD);
        UserResource innovationLeadAssignedToCompetition = newUserResource().withRolesGlobal(innovationLeadRoles).build();
        UserResource innovationLeadNotAssignedToCompetition = newUserResource().withRolesGlobal(innovationLeadRoles).build();
        List<InnovationLead> innovationLeads = newInnovationLead().withUser(newUser().withId(innovationLeadAssignedToCompetition.getId()).build()).build(1);
        CompetitionSearchResultItem competitionSearchResultItem = newLiveCompetitionSearchResultItem().withCompetitionStatus(OPEN).withId(1L).build();
        CompetitionSearchResultItem competitionSearchResultItemFeedbackReleased = newLiveCompetitionSearchResultItem().withCompetitionStatus(CompetitionStatus.ASSESSOR_FEEDBACK).withId(2L).build();

        when(innovationLeadRepository.findInnovationsLeads(1L)).thenReturn(innovationLeads);

        assertTrue(rules.innovationLeadCanViewCompetitionAssignedToThem(competitionSearchResultItem, innovationLeadAssignedToCompetition));
        assertFalse(rules.innovationLeadCanViewCompetitionAssignedToThem(competitionSearchResultItemFeedbackReleased, innovationLeadAssignedToCompetition));
        assertFalse(rules.innovationLeadCanViewCompetitionAssignedToThem(competitionSearchResultItem, innovationLeadNotAssignedToCompetition));
        assertFalse(rules.innovationLeadCanViewCompetitionAssignedToThem(competitionSearchResultItemFeedbackReleased, innovationLeadNotAssignedToCompetition));
    }

    @Test
    public void onlyStakeholdersAssignedToCompCanAccess() {

        long competitionId = 1L;

        List<Role> stakeholdersRoles = singletonList(STAKEHOLDER);
        UserResource stakeholderAssignedToCompetition = newUserResource().withRolesGlobal(stakeholdersRoles).build();
        UserResource stakeholderNotAssignedToCompetition = newUserResource().withRolesGlobal(stakeholdersRoles).build();

        CompetitionResource competition = newCompetitionResource().withId(competitionId).build();

        CompetitionSearchResultItem competitionSearchResultItem = newLiveCompetitionSearchResultItem().withCompetitionStatus(OPEN).withId(competitionId).build();
        CompetitionSearchResultItem competitionSearchResultItemFeedbackReleased = newLiveCompetitionSearchResultItem().withCompetitionStatus(CompetitionStatus.ASSESSOR_FEEDBACK).withId(2L).build();

        when(stakeholderRepository.existsByCompetitionIdAndUserId(competition.getId(), stakeholderAssignedToCompetition.getId())).thenReturn(true);

        assertTrue(rules.stakeholderCanViewCompetitionAssignedToThem(competitionSearchResultItem, stakeholderAssignedToCompetition));
        assertFalse(rules.stakeholderCanViewCompetitionAssignedToThem(competitionSearchResultItemFeedbackReleased, stakeholderAssignedToCompetition));
        assertFalse(rules.stakeholderCanViewCompetitionAssignedToThem(competitionSearchResultItem, stakeholderNotAssignedToCompetition));
        assertFalse(rules.stakeholderCanViewCompetitionAssignedToThem(competitionSearchResultItemFeedbackReleased, stakeholderNotAssignedToCompetition));
    }

    @Test
    public void onlyCompetitionFinanceUsersAssignedToCompCanAccess() {

        long competitionId = 1L;

        List<Role> competitionFinanceRoles = singletonList(EXTERNAL_FINANCE);
        UserResource competitionFinanceUserAssignedToCompetition = newUserResource().withRolesGlobal(competitionFinanceRoles).build();
        UserResource competitionFinanceUserNotAssignedToCompetition = newUserResource().withRolesGlobal(competitionFinanceRoles).build();

        CompetitionResource competition = newCompetitionResource().withId(competitionId).build();

        CompetitionSearchResultItem competitionSearchResultItem = newLiveCompetitionSearchResultItem().withCompetitionStatus(OPEN).withId(competitionId).build();
        CompetitionSearchResultItem competitionSearchResultItemFeedbackReleased = newLiveCompetitionSearchResultItem().withCompetitionStatus(ASSESSOR_FEEDBACK).withId(2L).build();

        when(externalFinanceRepository.existsByCompetitionIdAndUserId(competition.getId(), competitionFinanceUserAssignedToCompetition.getId())).thenReturn(true);

        assertTrue(rules.compFinanceCanViewCompetitionAssignedToThem(competitionSearchResultItem, competitionFinanceUserAssignedToCompetition));
        assertFalse(rules.compFinanceCanViewCompetitionAssignedToThem(competitionSearchResultItemFeedbackReleased, competitionFinanceUserAssignedToCompetition));
        assertFalse(rules.compFinanceCanViewCompetitionAssignedToThem(competitionSearchResultItem, competitionFinanceUserNotAssignedToCompetition));
        assertFalse(rules.compFinanceCanViewCompetitionAssignedToThem(competitionSearchResultItemFeedbackReleased, competitionFinanceUserNotAssignedToCompetition));
    }

    @Test
    public void onlyInnovationLeadUsersAssignedToCompWithoutFeedbackReleasedCanAccessComp() {
        List<Role> innovationLeadRoles = singletonList(INNOVATION_LEAD);
        UserResource innovationLeadAssignedToCompetition = newUserResource().withRolesGlobal(innovationLeadRoles).build();
        UserResource innovationLeadNotAssignedToCompetition = newUserResource().withRolesGlobal(innovationLeadRoles).build();
        List<InnovationLead> innovationLeads = newInnovationLead().withUser(newUser().withId(innovationLeadAssignedToCompetition.getId()).build()).build(1);
        CompetitionResource openCompetition = newCompetitionResource().withCompetitionStatus(OPEN).withId(1L).build();
        CompetitionResource feedbackReleasedCompetition = newCompetitionResource().withId(2L).withCompetitionStatus(CompetitionStatus.PROJECT_SETUP).withId(2L).build();

        when(innovationLeadRepository.findInnovationsLeads(1L)).thenReturn(innovationLeads);
        when(innovationLeadRepository.findInnovationsLeads(2L)).thenReturn(innovationLeads);

        assertTrue(rules.innovationLeadCanViewCompetitionAssignedToThem(openCompetition, innovationLeadAssignedToCompetition));
        assertFalse(rules.innovationLeadCanViewCompetitionAssignedToThem(openCompetition, innovationLeadNotAssignedToCompetition));
        assertTrue(rules.innovationLeadCanViewCompetitionAssignedToThem(feedbackReleasedCompetition, innovationLeadAssignedToCompetition));
        assertFalse(rules.innovationLeadCanViewCompetitionAssignedToThem(feedbackReleasedCompetition, innovationLeadNotAssignedToCompetition));
    }

    @Test
    public void onlyStakeholdersAssignedToCompWithoutFeedbackReleasedCanAccessComp() {

        long competitionId = 1L;

        List<Role> stakeholderRoles = singletonList(STAKEHOLDER);
        UserResource stakeholderAssignedToCompetition = newUserResource().withRolesGlobal(stakeholderRoles).build();
        UserResource stakeholderNotAssignedToCompetition = newUserResource().withRolesGlobal(stakeholderRoles).build();

        CompetitionResource openCompetition = newCompetitionResource().withCompetitionStatus(OPEN).withId(competitionId).build();
        CompetitionResource feedbackReleasedCompetition = newCompetitionResource().withId(2L).withCompetitionStatus(CompetitionStatus.PROJECT_SETUP).withId(2L).build();

        when(stakeholderRepository.existsByCompetitionIdAndUserId(openCompetition.getId(), stakeholderAssignedToCompetition.getId())).thenReturn(true);
        when(stakeholderRepository.existsByCompetitionIdAndUserId(feedbackReleasedCompetition.getId(), stakeholderAssignedToCompetition.getId())).thenReturn(true);

        assertTrue(rules.stakeholderCanViewCompetitionAssignedToThem(openCompetition, stakeholderAssignedToCompetition));
        assertFalse(rules.stakeholderCanViewCompetitionAssignedToThem(openCompetition, stakeholderNotAssignedToCompetition));
        assertTrue(rules.stakeholderCanViewCompetitionAssignedToThem(feedbackReleasedCompetition, stakeholderAssignedToCompetition));
        assertFalse(rules.stakeholderCanViewCompetitionAssignedToThem(feedbackReleasedCompetition, stakeholderNotAssignedToCompetition));
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

    @Test
    public void internalAdminCanSetPostAwardServiceForCompetition() {
        allGlobalRoleUsers.forEach(user -> {
            if (getUserWithRole(COMP_ADMIN).equals(user) || getUserWithRole(PROJECT_FINANCE).equals(user)) {
                assertTrue(rules.internalAdminCanSetPostAwardServiceForCompetition(newCompetitionResource().build(), user));
            } else {
                assertFalse(rules.internalAdminCanSetPostAwardServiceForCompetition(newCompetitionResource().build(), user));
            }
        });
    }

    @Test
    public void internalAdminCanReadPostAwardServiceForCompetition() {
        allGlobalRoleUsers.forEach(user -> {
            if (getUserWithRole(COMP_ADMIN).equals(user) || getUserWithRole(PROJECT_FINANCE).equals(user)) {
                assertTrue(rules.internalAdminCanReadPostAwardServiceForCompetition(newCompetitionResource().build(), user));
            } else {
                assertFalse(rules.internalAdminCanReadPostAwardServiceForCompetition(newCompetitionResource().build(), user));
            }
        });
    }

    @Test
    public void internalUsersCanReadPostAwardServiceForCompetition() {

        allGlobalRoleUsers.forEach(user -> {
            if (allInternalUsers.contains(user)) {
                assertTrue(rules.internalUsersCanReadPostAwardServiceForCompetition(newCompetitionResource().build(), user));
            } else {
                assertFalse(rules.internalUsersCanReadPostAwardServiceForCompetition(newCompetitionResource().build(), user));
            }
        });
    }

    @Test
    public void monitoringOfficerCanReadPostAwardServiceForCompetition() {

        CompetitionResource competition = newCompetitionResource().withId(15L).build();

        allGlobalRoleUsers.forEach(user -> {
            if (getUserWithRole(MONITORING_OFFICER).equals(user)) {
                when(projectMonitoringOfficerRepository.existsByProjectApplicationCompetitionIdAndUserId(competition.getId(), user.getId())).thenReturn(true);
                assertTrue(rules.monitoringOfficerCanReadPostAwardServiceForCompetition(competition, user));
            } else {
                when(projectMonitoringOfficerRepository.existsByProjectApplicationCompetitionIdAndUserId(competition.getId(), user.getId())).thenReturn(false);
                assertFalse(rules.monitoringOfficerCanReadPostAwardServiceForCompetition(competition, user));
            }
            verify(projectMonitoringOfficerRepository).existsByProjectApplicationCompetitionIdAndUserId(competition.getId(), user.getId());
        });
    }

    @Test
    public void projectUsersCanReadPostAwardServiceForCompetition() {

        UserResource user = newUserResource().withId(5L).build();
        CompetitionResource competition = newCompetitionResource().withId(15L).build();

        List<ProjectParticipantRole> projectRoles = PROJECT_USER_ROLES.stream().collect(Collectors.toList());
        when(projectUserRepository.existsByProjectApplicationCompetitionIdAndUserId(competition.getId(), user.getId())).thenReturn(true);

        assertTrue(rules.projectUsersCanReadPostAwardServiceForCompetition(competition, user));
        verify(projectUserRepository).existsByProjectApplicationCompetitionIdAndUserId(competition.getId(), user.getId());
    }

    @Test
    public void nonProjectUsersCannotReadPostAwardServiceForCompetition() {

        UserResource user = newUserResource().withId(5L).build();
        CompetitionResource competition = newCompetitionResource().withId(15L).build();

        List<ProjectParticipantRole> projectRoles = PROJECT_USER_ROLES.stream().collect(Collectors.toList());
        when(projectUserRepository.existsByProjectApplicationCompetitionIdAndUserId(competition.getId(), user.getId())).thenReturn(false);

        assertFalse(rules.projectUsersCanReadPostAwardServiceForCompetition(competition, user));
        verify(projectUserRepository).existsByProjectApplicationCompetitionIdAndUserId(competition.getId(), user.getId());
    }
}
