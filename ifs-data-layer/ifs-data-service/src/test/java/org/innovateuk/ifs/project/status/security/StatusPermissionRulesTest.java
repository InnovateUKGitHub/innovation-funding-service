package org.innovateuk.ifs.project.status.security;

import org.innovateuk.ifs.BasePermissionRulesTest;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.repository.ApplicationRepository;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.domain.InnovationLead;
import org.innovateuk.ifs.competition.domain.Stakeholder;
import org.innovateuk.ifs.competition.repository.InnovationLeadRepository;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.Collections;
import java.util.Optional;

import static java.util.Collections.singleton;
import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.competition.builder.InnovationLeadBuilder.newInnovationLead;
import static org.innovateuk.ifs.competition.builder.StakeholderBuilder.newStakeholder;
import static org.innovateuk.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static org.innovateuk.ifs.user.builder.UserBuilder.newUser;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.user.resource.Role.STAKEHOLDER;
import static org.innovateuk.ifs.util.SecurityRuleUtil.*;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class StatusPermissionRulesTest extends BasePermissionRulesTest<StatusPermissionRules> {
    private ProjectResource project = newProjectResource().build();
    private UserResource user = newUserResource().build();

    private CompetitionResource competitionResource;
    private ProjectResource projectResource1;
    private UserResource innovationLeadUserResourceOnProject1;
    private UserResource stakeholderUserResourceOnCompetition;
    private Competition competition;
    private Stakeholder stakeholder;

    @Mock
    private ApplicationRepository applicationRepositoryMock;

    @Mock
    private InnovationLeadRepository innovationLeadRepository;

    @Before
    public void setup() {
        User innovationLeadUserOnProject1 = newUser().withRoles(singleton(Role.INNOVATION_LEAD)).build();
        innovationLeadUserResourceOnProject1 = newUserResource().withId(innovationLeadUserOnProject1.getId()).withRolesGlobal(singletonList(Role.INNOVATION_LEAD)).build();
        InnovationLead innovationLead = newInnovationLead().withUser(innovationLeadUserOnProject1).build();

        User stakeholderUserOnCompetition = newUser().withRoles(singleton(STAKEHOLDER)).build();
        stakeholderUserResourceOnCompetition = newUserResource().withId(stakeholderUserOnCompetition.getId()).withRolesGlobal(singletonList(STAKEHOLDER)).build();
        stakeholder = newStakeholder().withUser(stakeholderUserOnCompetition).build();

        competition = newCompetition().withLeadTechnologist(innovationLeadUserOnProject1).build();
        competitionResource = newCompetitionResource().withId(competition.getId()).build();
        Application application1 = newApplication().withCompetition(competition).build();
        ApplicationResource applicationResource1 = newApplicationResource().withId(application1.getId()).withCompetition(competition.getId()).build();
        projectResource1 = newProjectResource().withApplication(applicationResource1).build();

        when(applicationRepositoryMock.findById(application1.getId())).thenReturn(Optional.of(application1));
        when(innovationLeadRepository.findInnovationsLeads(competition.getId())).thenReturn(Collections.singletonList(innovationLead));
        when(stakeholderRepository.findStakeholders(competition.getId())).thenReturn(singletonList(stakeholder));
    }

    @Override
    protected StatusPermissionRules supplyPermissionRulesUnderTest() {
        return new StatusPermissionRules();
    }

    @Test
    public void testPartnersCanViewTeamStatus() {
        setupUserAsPartner(project, user);
        assertTrue(rules.partnersCanViewTeamStatus(project, user));
    }

    @Test
    public void testNonPartnersCannotViewTeamStatus() {
        setupUserNotAsPartner(project, user);
        assertFalse(rules.partnersCanViewTeamStatus(project, user));
    }

    @Test
    public void testInternalUsersCanViewTeamStatus() {
        assertTrue(rules.internalUsersCanViewTeamStatus(project, compAdminUser()));
        assertTrue(rules.internalUsersCanViewTeamStatus(project, projectFinanceUser()));
    }

    @Test
    public void testPartnersCanViewStatus(){
        setupUserAsPartner(project, user);
        assertTrue(rules.partnersCanViewStatus(project, user));
    }

    @Test
    public void testNonPartnersCannotViewStatus(){
        setupUserNotAsPartner(project, user);
        assertFalse(rules.partnersCanViewStatus(project, user));
    }

    @Test
    public void testInternalUsersCanViewStatus(){
        allGlobalRoleUsers.forEach(user -> {
            if (isInternal(user)) {
                assertTrue(rules.internalUsersCanViewStatus(newProjectResource().build(), user));
            } else {
                assertFalse(rules.internalUsersCanViewStatus(newProjectResource().build(), user));
            }
        });
    }

    @Test
    public void stakeholdersCanViewTeamStatus() {

        ProjectResource project = newProjectResource()
                .withCompetition(competition.getId())
                .build();

        when(stakeholderRepository.existsByCompetitionIdAndUserId(competition.getId(), stakeholderUserResourceOnCompetition.getId())).thenReturn(true);

        assertTrue(rules.stakeholdersCanViewTeamStatus(project, stakeholderUserResourceOnCompetition));
        allInternalUsers.forEach(user -> {
            assertFalse(rules.stakeholdersCanViewTeamStatus(project, user));
        });
    }

    @Test
    public void testInternalAdminTeamCanViewCompetitionStatus(){
        allGlobalRoleUsers.forEach(user -> {
            if (isInternalAdmin(user)) {
                assertTrue(rules.internalAdminTeamCanViewCompetitionStatus(newCompetitionResource().build(), user));
            } else {
                assertFalse(rules.internalAdminTeamCanViewCompetitionStatus(newCompetitionResource().build(), user));
            }
        });
    }

    @Test
    public void testSupportCanViewCompetitionStatus(){
        allGlobalRoleUsers.forEach(user -> {
            if (isSupport(user)) {
                assertTrue(rules.supportCanViewCompetitionStatus(newCompetitionResource().build(), user));
            } else {
                assertFalse(rules.supportCanViewCompetitionStatus(newCompetitionResource().build(), user));
            }
        });
    }

    @Test
    public void testAssignedInnovationLeadCanViewCompetitionStatus(){
        assertTrue(rules.assignedInnovationLeadCanViewCompetitionStatus(competitionResource, innovationLeadUserResourceOnProject1));
        assertFalse(rules.assignedInnovationLeadCanViewCompetitionStatus(competitionResource, innovationLeadUser()));
    }

    @Test
    public void assignedStakeholderCanViewCompetitionStatus() {
        when(stakeholderRepository.existsByCompetitionIdAndUserId(competition.getId(), stakeholderUserResourceOnCompetition.getId())).thenReturn(true);

        assertTrue(rules.assignedStakeholderCanViewCompetitionStatus(competitionResource, stakeholderUserResourceOnCompetition));
        assertFalse(rules.assignedStakeholderCanViewCompetitionStatus(competitionResource, stakeholderUser()));
    }

    @Test
    public void testInternalAdminTeamCanViewProjectStatus(){
        allGlobalRoleUsers.forEach(user -> {
            if (isInternalAdmin(user)) {
                assertTrue(rules.internalAdminTeamCanViewProjectStatus(newProjectResource().build(), user));
            } else {
                assertFalse(rules.internalAdminTeamCanViewProjectStatus(newProjectResource().build(), user));
            }
        });
    }

    @Test
    public void testSupportCanViewProjectStatus(){
        allGlobalRoleUsers.forEach(user -> {
            if (isSupport(user)) {
                assertTrue(rules.supportCanViewProjectStatus(newProjectResource().build(), user));
            } else {
                assertFalse(rules.supportCanViewProjectStatus(newProjectResource().build(), user));
            }
        });
    }

    @Test
    public void testAssignedInnovationLeadCanViewProjectStatus(){
        assertTrue(rules.assignedInnovationLeadCanViewProjectStatus(projectResource1, innovationLeadUserResourceOnProject1));
        assertFalse(rules.assignedInnovationLeadCanViewProjectStatus(projectResource1, innovationLeadUser()));
    }

    @Test
    public void assignedStakeholderCanViewProjectStatus() {
        when(stakeholderRepository.existsByCompetitionIdAndUserId(competition.getId(), stakeholderUserResourceOnCompetition.getId())).thenReturn(true);

        assertTrue(rules.assignedStakeholderCanViewProjectStatus(projectResource1, stakeholderUserResourceOnCompetition));
        assertFalse(rules.assignedStakeholderCanViewProjectStatus(projectResource1, stakeholderUser()));
    }
}
