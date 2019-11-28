package org.innovateuk.ifs.project.spendprofile.security;

import org.innovateuk.ifs.BasePermissionRulesTest;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.repository.ApplicationRepository;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.domain.InnovationLead;
import org.innovateuk.ifs.competition.domain.Stakeholder;
import org.innovateuk.ifs.competition.repository.InnovationLeadRepository;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.project.core.builder.ProjectBuilder;
import org.innovateuk.ifs.project.core.domain.Project;
import org.innovateuk.ifs.project.core.domain.ProjectProcess;
import org.innovateuk.ifs.project.core.repository.ProjectProcessRepository;
import org.innovateuk.ifs.project.resource.ProjectCompositeId;
import org.innovateuk.ifs.project.resource.ProjectOrganisationCompositeId;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.resource.ProjectState;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;

import static java.util.Collections.singleton;
import static java.util.Collections.singletonList;
import static junit.framework.TestCase.assertFalse;
import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.competition.builder.InnovationLeadBuilder.newInnovationLead;
import static org.innovateuk.ifs.competition.builder.StakeholderBuilder.newStakeholder;
import static org.innovateuk.ifs.organisation.builder.OrganisationBuilder.newOrganisation;
import static org.innovateuk.ifs.organisation.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.innovateuk.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static org.innovateuk.ifs.project.core.builder.ProjectProcessBuilder.newProjectProcess;
import static org.innovateuk.ifs.user.builder.UserBuilder.newUser;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.user.resource.Role.STAKEHOLDER;
import static org.innovateuk.ifs.util.SecurityRuleUtil.isInternalAdmin;
import static org.innovateuk.ifs.util.SecurityRuleUtil.isSupport;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class SpendProfilePermissionRulesTest extends BasePermissionRulesTest<SpendProfilePermissionRules> {

    private ProjectResource projectResource1;
    private Competition competition;
    private UserResource innovationLeadUserResourceOnProject1;
    private UserResource stakeholderUserResourceOnCompetition;
    private ProjectProcess projectProcess;

    @Mock
    private ProjectProcessRepository projectProcessRepositoryMock;

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
        Stakeholder stakeholder = newStakeholder().withUser(stakeholderUserOnCompetition).build();

        competition = newCompetition().withLeadTechnologist(innovationLeadUserOnProject1).build();
        Application application1 = newApplication().withCompetition(competition).build();
        ApplicationResource applicationResource1 = newApplicationResource().withId(application1.getId()).withCompetition(competition.getId()).build();
        projectResource1 = newProjectResource().withApplication(applicationResource1).build();
        projectProcess = newProjectProcess().withActivityState(ProjectState.SETUP).build();

        Project project = ProjectBuilder.newProject()
                .withId(projectResource1.getId())
                .withApplication(application1)
                .build();

        when(projectRepositoryMock.findById(projectResource1.getId())).thenReturn(Optional.of(project));
        when(projectProcessRepositoryMock.findOneByTargetId(anyLong())).thenReturn(projectProcess);
        when(applicationRepositoryMock.findById(application1.getId())).thenReturn(Optional.of(application1));
        when(innovationLeadRepository.findInnovationsLeads(competition.getId())).thenReturn(singletonList(innovationLead));
        when(stakeholderRepository.findStakeholders(competition.getId())).thenReturn(singletonList(stakeholder));
    }

    @Test
    public void internalAdminTeamCanViewCompetitionStatus() {
        allGlobalRoleUsers.forEach(user -> {
            if (isInternalAdmin(user)) {
                assertTrue(rules.internalAdminTeamCanViewCompetitionStatus(newProjectResource().build(), user));
            } else {
                assertFalse(rules.internalAdminTeamCanViewCompetitionStatus(newProjectResource().build(), user));
            }
        });
    }

    @Test
    public void supportCanViewCompetitionStatus() {
        allGlobalRoleUsers.forEach(user -> {
            if (isSupport(user)) {
                assertTrue(rules.supportCanViewCompetitionStatus(newProjectResource().build(), user));
            } else {
                assertFalse(rules.supportCanViewCompetitionStatus(newProjectResource().build(), user));
            }
        });
    }

    @Test
    public void assignedInnovationLeadCanViewProjectStatus() {
        assertTrue(rules.assignedInnovationLeadCanViewSPStatus(projectResource1, innovationLeadUserResourceOnProject1));
        assertFalse(rules.assignedInnovationLeadCanViewSPStatus(projectResource1, innovationLeadUser()));
    }

    @Test
    public void assignedStakeholderCanViewSPStatus() {
        when(stakeholderRepository.existsByCompetitionIdAndUserId(competition.getId(), stakeholderUserResourceOnCompetition.getId())).thenReturn(true);

        assertTrue(rules.assignedStakeholderCanViewSPStatus(projectResource1, stakeholderUserResourceOnCompetition));
        assertFalse(rules.assignedStakeholderCanViewSPStatus(projectResource1, stakeholderUser()));
    }

    @Test
    public void projectManagerCanCompleteSpendProfile() throws Exception {
        ProjectResource project = newProjectResource().build();
        UserResource user = newUserResource().build();

        setUpUserAsProjectManager(project, user);

        assertTrue(rules.projectManagerCanCompleteSpendProfile(ProjectCompositeId.id(project.getId()), user));
    }

    @Test
    public void leadPartnerCanIncompleteAnySpendProfile() throws Exception {
        ProjectResource project = newProjectResource().build();
        UserResource user = newUserResource().build();
        ProjectOrganisationCompositeId projectOrganisationCompositeId =
                new ProjectOrganisationCompositeId(project.getId(), newOrganisation().build().getId());

        setupUserAsLeadPartner(project, user);

        assertTrue(rules.leadPartnerCanMarkSpendProfileIncomplete(projectOrganisationCompositeId, user));
    }

    @Test
    public void nonLeadPartnerCannotIncompleteSpendProfile() throws Exception {
        ProjectResource project = newProjectResource().build();
        UserResource user = newUserResource().build();
        ProjectOrganisationCompositeId projectOrganisationCompositeId =
                new ProjectOrganisationCompositeId(project.getId(), newOrganisation().build().getId());

        setupUserNotAsLeadPartner(project, user);

        assertFalse(rules.leadPartnerCanMarkSpendProfileIncomplete(projectOrganisationCompositeId, user));
    }

    @Test
    public void leadPartnerCanViewAnySpendProfileData() throws Exception {

        ProjectResource project = newProjectResource().build();
        UserResource user = newUserResource().build();
        ProjectOrganisationCompositeId projectOrganisationCompositeId =
                new ProjectOrganisationCompositeId(project.getId(), newOrganisation().build().getId());

        setupUserAsLeadPartner(project, user);

        assertTrue(rules.leadPartnerCanViewAnySpendProfileData(projectOrganisationCompositeId, user));
    }

    @Test
    public void monitoringOfficerCanViewAnySpendProfileData() throws Exception {

        ProjectResource project = newProjectResource().build();
        UserResource user = newUserResource().build();
        ProjectOrganisationCompositeId projectOrganisationCompositeId =
                new ProjectOrganisationCompositeId(project.getId(), newOrganisation().build().getId());

        setupUserAsMonitoringOfficer(project, user);

        assertTrue(rules.monitoringOfficerCanViewProjectsSpendProfileData(projectOrganisationCompositeId, user));
    }

    @Test
    public void userNotLeadPartnerCannotViewSpendProfile() throws Exception {
        ProjectResource project = newProjectResource().build();
        UserResource user = newUserResource().build();
        ProjectOrganisationCompositeId projectOrganisationCompositeId =
                new ProjectOrganisationCompositeId(project.getId(), newOrganisation().build().getId());

        setupUserNotAsLeadPartner(project, user);

        assertFalse(rules.leadPartnerCanViewAnySpendProfileData(projectOrganisationCompositeId, user));
    }

    @Test
    public void partnersCanViewTheirOwnSpendProfileData() {
        ProjectResource project = newProjectResource().build();
        UserResource user = newUserResource().build();
        OrganisationResource org = newOrganisationResource().build();
        ProjectOrganisationCompositeId projectOrganisationCompositeId =
                new ProjectOrganisationCompositeId(project.getId(), org.getId());

        setupUserAsPartner(project, user, org);
        assertTrue(rules.partnersCanViewTheirOwnSpendProfileData(projectOrganisationCompositeId, user));

        setupUserNotAsPartner(project, user, org);
        assertFalse(rules.partnersCanViewTheirOwnSpendProfileData(projectOrganisationCompositeId, user));
    }

    @Test
    public void projectFinanceUserCanViewAnySpendProfileData() {
        ProjectOrganisationCompositeId projectOrganisationCompositeId =
                new ProjectOrganisationCompositeId(1L, newOrganisation().build().getId());

        allGlobalRoleUsers.forEach(user -> {
            if (user.equals(projectFinanceUser())) {
                assertTrue(rules.projectFinanceUserCanViewAnySpendProfileData(projectOrganisationCompositeId, user));
            } else {
                assertFalse(rules.projectFinanceUserCanViewAnySpendProfileData(projectOrganisationCompositeId, user));
            }
        });
    }

    @Test
    public void partnersCanViewTheirOwnSpendProfileCsv() {
        ProjectResource project = newProjectResource().build();
        UserResource user = newUserResource().build();
        OrganisationResource org = newOrganisationResource().build();
        ProjectOrganisationCompositeId projectOrganisationCompositeId =
                new ProjectOrganisationCompositeId(project.getId(), org.getId());

        setupUserAsPartner(project, user, org);
        assertTrue(rules.partnersCanViewTheirOwnSpendProfileCsv(projectOrganisationCompositeId, user));

        setupUserNotAsPartner(project, user, org);
        assertFalse(rules.partnersCanViewTheirOwnSpendProfileCsv(projectOrganisationCompositeId, user));
    }

    @Test
    public void internalAdminUsersCanSeeSpendProfileCsv() {
        ProjectOrganisationCompositeId projectOrganisationCompositeId =
                new ProjectOrganisationCompositeId(1L, newOrganisation().build().getId());

        allGlobalRoleUsers.forEach(user -> {
            if (isInternalAdmin(user)) {
                assertTrue(rules.internalAdminUsersCanSeeSpendProfileCsv(projectOrganisationCompositeId, user));
            } else {
                assertFalse(rules.internalAdminUsersCanSeeSpendProfileCsv(projectOrganisationCompositeId, user));
            }
        });
    }

    @Test
    public void supportUsersCanSeeSpendProfileCsv() {
        ProjectOrganisationCompositeId projectOrganisationCompositeId =
                new ProjectOrganisationCompositeId(1L, newOrganisation().build().getId());

        allGlobalRoleUsers.forEach(user -> {
            if (isSupport(user)) {
                assertTrue(rules.supportUsersCanSeeSpendProfileCsv(projectOrganisationCompositeId, user));
            } else {
                assertFalse(rules.supportUsersCanSeeSpendProfileCsv(projectOrganisationCompositeId, user));
            }
        });
    }

    @Test
    public void stakeholdersCanSeeSpendProfileCsv() {
        ProjectOrganisationCompositeId projectOrganisationCompositeId = new ProjectOrganisationCompositeId(projectResource1.getId(), newOrganisation().build().getId());

        when(stakeholderRepository.existsByCompetitionIdAndUserId(competition.getId(), stakeholderUserResourceOnCompetition.getId())).thenReturn(true);

        assertTrue(rules.stakeholdersCanSeeSpendProfileCsv(projectOrganisationCompositeId, stakeholderUserResourceOnCompetition));
        assertFalse(rules.stakeholdersCanSeeSpendProfileCsv(projectOrganisationCompositeId, stakeholderUser()));
    }

    @Test
    public void leadPartnerCanViewAnySpendProfileCsv() {
        ProjectResource project = newProjectResource().build();
        UserResource user = newUserResource().build();
        ProjectOrganisationCompositeId projectOrganisationCompositeId =
                new ProjectOrganisationCompositeId(project.getId(), newOrganisation().build().getId());

        setupUserAsLeadPartner(project, user);
        assertTrue(rules.leadPartnerCanViewAnySpendProfileCsv(projectOrganisationCompositeId, user));

        setupUserNotAsLeadPartner(project, user);
        assertFalse(rules.leadPartnerCanViewAnySpendProfileCsv(projectOrganisationCompositeId, user));
    }

    @Test
    public void partnersCanEditTheirOwnSpendProfileData() {
        ProjectResource project = newProjectResource().build();
        UserResource user = newUserResource().build();
        OrganisationResource org = newOrganisationResource().build();
        ProjectOrganisationCompositeId projectOrganisationCompositeId =
                new ProjectOrganisationCompositeId(project.getId(), org.getId());

        setupUserAsPartner(project, user, org);
        assertTrue(rules.partnersCanEditTheirOwnSpendProfileData(projectOrganisationCompositeId, user));

        setupUserNotAsPartner(project, user, org);
        assertFalse(rules.partnersCanEditTheirOwnSpendProfileData(projectOrganisationCompositeId, user));
    }

    @Test
    public void partnersCanMarkSpendProfileAsComplete() {
        ProjectResource project = newProjectResource().build();
        UserResource user = newUserResource().build();
        OrganisationResource org = newOrganisationResource().build();
        ProjectOrganisationCompositeId projectOrganisationCompositeId =
                new ProjectOrganisationCompositeId(project.getId(), org.getId());

        setupUserAsPartner(project, user, org);
        assertTrue(rules.partnersCanMarkSpendProfileAsComplete(projectOrganisationCompositeId, user));

        setupUserNotAsPartner(project, user, org);
        assertFalse(rules.partnersCanMarkSpendProfileAsComplete(projectOrganisationCompositeId, user));
    }

    @Override
    protected SpendProfilePermissionRules supplyPermissionRulesUnderTest() {
        return new SpendProfilePermissionRules();
    }
}