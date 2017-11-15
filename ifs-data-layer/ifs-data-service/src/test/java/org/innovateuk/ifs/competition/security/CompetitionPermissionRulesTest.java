package org.innovateuk.ifs.competition.security;

import org.innovateuk.ifs.BasePermissionRulesTest;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSearchResultItem;
import org.innovateuk.ifs.competition.resource.CompetitionStatus;
import org.innovateuk.ifs.invite.domain.CompetitionAssessmentParticipant;
import org.innovateuk.ifs.invite.domain.CompetitionParticipantRole;
import org.innovateuk.ifs.user.resource.RoleResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.resource.UserRoleType;
import org.junit.Test;

import java.util.List;

import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.assessment.builder.CompetitionAssessmentParticipantBuilder.newCompetitionAssessmentParticipant;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.competition.builder.CompetitionSearchResultItemBuilder.newCompetitionSearchResultItem;
import static org.innovateuk.ifs.user.builder.RoleResourceBuilder.newRoleResource;
import static org.innovateuk.ifs.user.builder.UserBuilder.newUser;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

/**
 * Tests the logic within the individual CompetitionPermissionRules methods that secures basic Competition details
 */
public class CompetitionPermissionRulesTest extends BasePermissionRulesTest<CompetitionPermissionRules> {

	@Override
	protected CompetitionPermissionRules supplyPermissionRulesUnderTest() {
		return new CompetitionPermissionRules();
	}
	
    @Test
    public void testExternalUsersCannotViewACompetitionInSetup() {
        //null user cannot see competition in setup.
        assertFalse(rules.externalUsersCannotViewCompetitionsInSetup(newCompetitionResource().withCompetitionStatus(CompetitionStatus.COMPETITION_SETUP).build(), null));
        //null user can see open competitions
        assertTrue(rules.externalUsersCannotViewCompetitionsInSetup(newCompetitionResource().withCompetitionStatus(CompetitionStatus.OPEN).build(), null));
    }

    @Test
    public void testInternalUsersOtherThanInnoLeadsCanViewAllCompetitions() {

        allGlobalRoleUsers.forEach(user -> {
            if (!user.hasRole(UserRoleType.INNOVATION_LEAD) && allInternalUsers.contains(user)) {
                assertTrue(rules.internalUserCanViewAllCompetitions(newCompetitionResource().build(), user));
            } else {
                assertFalse(rules.internalUserCanViewAllCompetitions(newCompetitionResource().build(), user));
            }
        });
    }

    @Test
    public void testInternalUsersOtherThanInnoLeadsCanViewAllCompetitionSearchResults() {

        allGlobalRoleUsers.forEach(user -> {
            if (!user.hasRole(UserRoleType.INNOVATION_LEAD) && allInternalUsers.contains(user)) {
                assertTrue(rules.internalUserCanViewAllCompetitionSearchResults(newCompetitionSearchResultItem().build(), user));
            } else {
                assertFalse(rules.internalUserCanViewAllCompetitionSearchResults(newCompetitionSearchResultItem().build(), user));
            }
        });
    }

    @Test
    public void testInternalAdminCanManageInnovationLeadsForCompetition() {
        allGlobalRoleUsers.forEach(user -> {
            if (getUserWithRole(UserRoleType.COMP_ADMIN).equals(user) || getUserWithRole(UserRoleType.PROJECT_FINANCE).equals(user)) {
                assertTrue(rules.internalAdminCanManageInnovationLeadsForCompetition(newCompetitionResource().build(), user));
            } else {
                assertFalse(rules.internalAdminCanManageInnovationLeadsForCompetition(newCompetitionResource().build(), user));
            }
        });
    }

    @Test
    public void testInternalUsersBarringInnovationLeadAndIFSAdminCanViewUnsuccessfulApplications() {
        allGlobalRoleUsers.forEach(user -> {
            if ((allInternalUsers.contains(user) && !user.hasRoles(UserRoleType.INNOVATION_LEAD))
                    || getUserWithRole(UserRoleType.IFS_ADMINISTRATOR).equals(user)) {
                assertTrue(rules.internalUsersAndIFSAdminCanViewUnsuccessfulApplications(newCompetitionResource().build(), user));
            } else {
                assertFalse(rules.internalUsersAndIFSAdminCanViewUnsuccessfulApplications(newCompetitionResource().build(), user));
            }
        });
    }

    @Test
    public void testOnlyInnovationLeadUsersAssignedToCompCanViewUnsuccessfulApplications() {
        List<RoleResource> innovationLeadRoles = singletonList(newRoleResource().withType(UserRoleType.INNOVATION_LEAD).build());
        UserResource innovationLeadAssignedToCompetition = newUserResource().withRolesGlobal(innovationLeadRoles).build();
        UserResource innovationLeadNotAssignedToCompetition = newUserResource().withRolesGlobal(innovationLeadRoles).build();
        List<CompetitionAssessmentParticipant> competitionParticipants = newCompetitionAssessmentParticipant().withUser(newUser().withId(innovationLeadAssignedToCompetition.getId()).build()).build(1);
        CompetitionResource competition = newCompetitionResource().withId(1L).build();

        when(competitionParticipantRepositoryMock.getByCompetitionIdAndRole(1L, CompetitionParticipantRole.INNOVATION_LEAD)).thenReturn(competitionParticipants);

        assertTrue(rules.innovationLeadForCompetitionCanViewUnsuccessfulApplications(competition, innovationLeadAssignedToCompetition));
        assertFalse(rules.innovationLeadForCompetitionCanViewUnsuccessfulApplications(competition, innovationLeadNotAssignedToCompetition));
    }

    @Test
    public void testOnlyInnovationLeadUsersAssignedToCompCanAccess() {
        List<RoleResource> innovationLeadRoles = singletonList(newRoleResource().withType(UserRoleType.INNOVATION_LEAD).build());
        UserResource innovationLeadAssignedToCompetition = newUserResource().withRolesGlobal(innovationLeadRoles).build();
        UserResource innovationLeadNotAssignedToCompetition = newUserResource().withRolesGlobal(innovationLeadRoles).build();
        List<CompetitionAssessmentParticipant> competitionParticipants = newCompetitionAssessmentParticipant().withUser(newUser().withId(innovationLeadAssignedToCompetition.getId()).build()).build(1);
        CompetitionSearchResultItem competitionSearchResultItem = newCompetitionSearchResultItem().withCompetitionStatus(CompetitionStatus.OPEN).withId(1L).build();
        CompetitionSearchResultItem competitionSearchResultItemFeedbackReleased = newCompetitionSearchResultItem().withCompetitionStatus(CompetitionStatus.ASSESSOR_FEEDBACK).withId(2L).build();

        when(competitionParticipantRepositoryMock.getByCompetitionIdAndRole(1L, CompetitionParticipantRole.INNOVATION_LEAD)).thenReturn(competitionParticipants);

        assertTrue(rules.innovationLeadCanViewCompetitionAssignedToThem(competitionSearchResultItem, innovationLeadAssignedToCompetition));
        assertFalse(rules.innovationLeadCanViewCompetitionAssignedToThem(competitionSearchResultItemFeedbackReleased, innovationLeadAssignedToCompetition));
        assertFalse(rules.innovationLeadCanViewCompetitionAssignedToThem(competitionSearchResultItem, innovationLeadNotAssignedToCompetition));
        assertFalse(rules.innovationLeadCanViewCompetitionAssignedToThem(competitionSearchResultItemFeedbackReleased, innovationLeadNotAssignedToCompetition));
    }

    @Test
    public void  testOnlyInnovationLeadUsersAssignedToCompWithoutFeedbackReleasedCanAccessComp(){
        List<RoleResource> innovationLeadRoles = singletonList(newRoleResource().withType(UserRoleType.INNOVATION_LEAD).build());
        UserResource innovationLeadAssignedToCompetition = newUserResource().withRolesGlobal(innovationLeadRoles).build();
        UserResource innovationLeadNotAssignedToCompetition = newUserResource().withRolesGlobal(innovationLeadRoles).build();
        List<CompetitionAssessmentParticipant> competitionParticipants = newCompetitionAssessmentParticipant().withUser(newUser().withId(innovationLeadAssignedToCompetition.getId()).build()).build(1);
        CompetitionResource openCompetition= newCompetitionResource().withCompetitionStatus(CompetitionStatus.OPEN).withId(1L).build();
        CompetitionResource feedbackReleasedCompetition = newCompetitionResource().withId(2L).withCompetitionStatus(CompetitionStatus.PROJECT_SETUP).withId(2L).build();

        when(competitionParticipantRepositoryMock.getByCompetitionIdAndRole(1L, CompetitionParticipantRole.INNOVATION_LEAD)).thenReturn(competitionParticipants);
        when(competitionParticipantRepositoryMock.getByCompetitionIdAndRole(2L, CompetitionParticipantRole.INNOVATION_LEAD)).thenReturn(competitionParticipants);

        assertTrue(rules.innovationLeadCanViewCompetitionAssignedToThem(openCompetition, innovationLeadAssignedToCompetition));
        assertFalse(rules.innovationLeadCanViewCompetitionAssignedToThem(openCompetition, innovationLeadNotAssignedToCompetition));
        assertTrue(rules.innovationLeadCanViewCompetitionAssignedToThem(feedbackReleasedCompetition, innovationLeadAssignedToCompetition));
        assertFalse(rules.innovationLeadCanViewCompetitionAssignedToThem(feedbackReleasedCompetition, innovationLeadNotAssignedToCompetition));
    }
}
