package org.innovateuk.ifs.project.grantofferletter.security;

import org.innovateuk.ifs.BasePermissionRulesTest;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.repository.ApplicationRepository;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.domain.InnovationLead;
import org.innovateuk.ifs.competition.domain.Stakeholder;
import org.innovateuk.ifs.competition.repository.InnovationLeadRepository;
import org.innovateuk.ifs.project.core.domain.ProjectProcess;
import org.innovateuk.ifs.project.core.repository.ProjectProcessRepository;
import org.innovateuk.ifs.project.resource.ProjectCompositeId;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.resource.ProjectState;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.Optional;

import static java.util.Collections.singleton;
import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.competition.builder.InnovationLeadBuilder.newInnovationLead;
import static org.innovateuk.ifs.competition.builder.StakeholderBuilder.newStakeholder;
import static org.innovateuk.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static org.innovateuk.ifs.project.core.builder.ProjectProcessBuilder.newProjectProcess;
import static org.innovateuk.ifs.user.builder.UserBuilder.newUser;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.user.resource.Role.STAKEHOLDER;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class GrantOfferLetterPermissionRulesTest extends BasePermissionRulesTest<GrantOfferLetterPermissionRules> {

    private ProjectResource projectResource1;
    private Competition competition;
    private Role innovationLeadRole = Role.INNOVATION_LEAD;
    private UserResource innovationLeadUserResourceOnProject1;
    private UserResource stakeholderUserResourceOnCompetition;
    private ProjectProcess projectProcess;

    @Mock
    private ApplicationRepository applicationRepositoryMock;

    @Mock
    private InnovationLeadRepository innovationLeadRepository;

    @Mock
    private ProjectProcessRepository projectProcessRepositoryMock;

    @Before
    public void setup() {
        User innovationLeadUserOnProject1 = newUser().withRoles(singleton(Role.INNOVATION_LEAD)).build();
        innovationLeadUserResourceOnProject1 = newUserResource().withId(innovationLeadUserOnProject1.getId()).withRolesGlobal(singletonList(innovationLeadRole)).build();
        InnovationLead innovationLead = newInnovationLead().withUser(innovationLeadUserOnProject1).build();

        User stakeholderUserOnCompetition = newUser().withRoles(singleton(STAKEHOLDER)).build();
        stakeholderUserResourceOnCompetition = newUserResource().withId(stakeholderUserOnCompetition.getId()).withRoleGlobal(STAKEHOLDER).build();
        Stakeholder stakeholder = newStakeholder().withUser(stakeholderUserOnCompetition).build();

        competition = newCompetition().withLeadTechnologist(innovationLeadUserOnProject1).build();
        Application application1 = newApplication().withCompetition(competition).build();
        ApplicationResource applicationResource1 = newApplicationResource().withId(application1.getId()).withCompetition(competition.getId()).build();
        projectResource1 = newProjectResource().withApplication(applicationResource1).build();
        projectProcess = newProjectProcess().withActivityState(ProjectState.SETUP).build();

        when(applicationRepositoryMock.findById(application1.getId())).thenReturn(Optional.of(application1));
        when(innovationLeadRepository.findInnovationsLeads(competition.getId())).thenReturn(singletonList(innovationLead));
        when(stakeholderRepository.findStakeholders(competition.getId())).thenReturn(singletonList(stakeholder));
    }

    @Test
    public void testLeadPartnersCanCreateSignedGrantOfferLetter() {
        UserResource user = newUserResource().build();

        ProjectResource project = newProjectResource()
                .withProjectState(ProjectState.SETUP)
                .build();

        setupUserAsLeadPartner(project, user);

        when(projectProcessRepositoryMock.findOneByTargetId(project.getId())).thenReturn(projectProcess);
        assertTrue(rules.leadPartnerCanUploadGrantOfferLetter(project, user));
    }

    @Test
    public void testNonLeadPartnersCannotCreateSignedGrantOfferLetter() {

        ProjectResource project = newProjectResource().build();
        UserResource user = newUserResource().build();

        setupUserNotAsLeadPartner(project, user);

        assertFalse(rules.leadPartnerCanUploadGrantOfferLetter(project, user));
    }

    @Test
    public void testProjectManagerCanCreateSignedGrantOfferLetter() {
        UserResource user = newUserResource().build();

        ProjectResource project = newProjectResource()
                .withProjectState(ProjectState.SETUP)
                .build();

        setUpUserAsProjectManager(project, user);

        when(projectProcessRepositoryMock.findOneByTargetId(project.getId())).thenReturn(projectProcess);
        assertTrue(rules.projectManagerCanUploadGrantOfferLetter(project, user));
    }

    @Test
    public void testNonProjectManagerCannotCreateSignedGrantOfferLetter() {
        ProjectResource project = newProjectResource().build();
        UserResource user = newUserResource().build();

        setUpUserNotAsProjectManager(user);

        assertFalse(rules.projectManagerCanUploadGrantOfferLetter(project, user));

    }


    @Test
    public void testPartnersCanViewGrantOfferLetterDetails() {

        ProjectResource project = newProjectResource().build();
        UserResource user = newUserResource().build();

        setupUserAsPartner(project, user);

        assertTrue(rules.partnersCanViewGrantOfferLetter(project, user));
    }

    @Test
    public void testNonPartnersCannotViewGrantOfferLetterDetails() {

        ProjectResource project = newProjectResource().build();
        UserResource user = newUserResource().build();

        setupUserNotAsPartner(project, user);

        assertFalse(rules.partnersCanViewGrantOfferLetter(project, user));
    }

    @Test
    public void testPartnersCanDownloadGrantOfferLetterDocuments() {

        ProjectResource project = newProjectResource().build();
        UserResource user = newUserResource().build();

        setupUserAsPartner(project, user);

        assertTrue(rules.partnersCanDownloadGrantOfferLetter(project, user));
    }

    @Test
    public void testNonPartnersCannotDownloadGrantOfferLetterDocuments() {

        ProjectResource project = newProjectResource().build();
        UserResource user = newUserResource().build();

        setupUserNotAsPartner(project, user);

        assertFalse(rules.partnersCanDownloadGrantOfferLetter(project, user));
    }

    @Test
    public void testCompAdminsCanDownloadGrantOfferLetterDocuments() {

        ProjectResource project = newProjectResource().build();
        UserResource user = newUserResource().build();

        setUpUserAsCompAdmin(project, user);

        assertTrue(rules.internalUsersCanDownloadGrantOfferLetter(project, user));
    }

    @Test
    public void testNonCompAdminsCannotDownloadGrantOfferLetterDocuments() {

        ProjectResource project = newProjectResource().build();
        UserResource user = newUserResource().build();

        setUpUserNotAsCompAdmin(project, user);

        assertFalse(rules.internalUsersCanDownloadGrantOfferLetter(project, user));
    }

    @Test
    public void testCompAdminsCanViewGrantOfferLetterDocuments() {

        ProjectResource project = newProjectResource().build();
        UserResource user = newUserResource().build();

        setUpUserAsCompAdmin(project, user);

        assertTrue(rules.internalUsersCanViewGrantOfferLetter(project, user));
    }

    @Test
    public void testNonCompAdminsCannotViewGrantOfferLetterDocuments() {

        ProjectResource project = newProjectResource().build();
        UserResource user = newUserResource().build();

        setUpUserNotAsCompAdmin(project, user);

        assertFalse(rules.internalUsersCanViewGrantOfferLetter(project, user));
    }

    @Test
    public void testProjectFinanceCanDownloadGrantOfferLetterDocuments() {

        ProjectResource project = newProjectResource().build();
        UserResource user = newUserResource().build();

        setUpUserAsProjectFinanceUser(project, user);

        assertTrue(rules.internalUsersCanDownloadGrantOfferLetter(project, user));
    }

    @Test
    public void testNonProjectFinanceCannotDownloadGrantOfferLetterDocuments() {

        ProjectResource project = newProjectResource().build();
        UserResource user = newUserResource().build();

        setUpUserNotAsProjectFinanceUser(project, user);

        assertFalse(rules.internalUsersCanDownloadGrantOfferLetter(project, user));
    }

    @Test
    public void testProjectFinanceCanViewGrantOfferLetterDocuments() {

        ProjectResource project = newProjectResource().build();
        UserResource user = newUserResource().build();

        setUpUserAsProjectFinanceUser(project, user);

        assertTrue(rules.internalUsersCanViewGrantOfferLetter(project, user));
    }

    @Test
    public void testNonProjectFinanceCannotViewGrantOfferLetterDocuments() {

        ProjectResource project = newProjectResource().build();
        UserResource user = newUserResource().build();

        setUpUserNotAsProjectFinanceUser(project, user);

        assertFalse(rules.internalUsersCanViewGrantOfferLetter(project, user));
    }

    @Test
    public void testProjectManagerCanSubmitOfferLetter() {
        UserResource user = newUserResource().build();

        ProjectResource project = newProjectResource()
                .withProjectState(ProjectState.SETUP)
                .build();

        setUpUserAsProjectManager(project, user);

        when(projectProcessRepositoryMock.findOneByTargetId(project.getId())).thenReturn(projectProcess);
        assertTrue(rules.projectManagerSubmitGrantOfferLetter(ProjectCompositeId.id(project.getId()), user));
    }

    @Test
    public void testNonProjectManagerCannotSubmitOfferLetter() {
        ProjectResource project = newProjectResource().build();
        UserResource user = newUserResource().build();

        setUpUserNotAsProjectManager(user);

        assertFalse(rules.projectManagerSubmitGrantOfferLetter(ProjectCompositeId.id(project.getId()), user));
    }

    @Test
    public void testCompAdminsCanApproveSignedGrantOfferLetters() {
        UserResource user = newUserResource().build();

        ProjectResource project = newProjectResource()
                .withProjectState(ProjectState.SETUP)
                .build();

        setUpUserAsCompAdmin(project, user);

        when(projectProcessRepositoryMock.findOneByTargetId(project.getId())).thenReturn(projectProcess);
        assertTrue(rules.internalUsersCanApproveOrRejectSignedGrantOfferLetter(project, user));
    }

    @Test
    public void testNonCompAdminsCannotApproveSignedGrantOfferLetters() {
        ProjectResource project = newProjectResource().build();
        UserResource user = newUserResource().build();

        setUpUserNotAsCompAdmin(project, user);

        assertFalse(rules.internalUsersCanApproveOrRejectSignedGrantOfferLetter(project, user));
    }

    @Test
    public void testLeadPartnerCanDeleteSignedGrantOfferLetter() {
        UserResource user = newUserResource().build();

        ProjectResource project = newProjectResource()
                .withProjectState(ProjectState.SETUP)
                .build();

        setupUserAsLeadPartner(project, user);

        when(projectProcessRepositoryMock.findOneByTargetId(project.getId())).thenReturn(projectProcess);
        assertTrue(rules.leadPartnerCanDeleteSignedGrantOfferLetter(project, user));
    }

    @Test
    public void testNonLeadPartnerCanDeleteSignedGrantOfferLetter() {
        ProjectResource project = newProjectResource().build();
        UserResource user = newUserResource().build();

        setupUserNotAsLeadPartner(project, user);

        assertFalse(rules.leadPartnerCanDeleteSignedGrantOfferLetter(project, user));

    }

    @Test
    public void testProjectFinanceUserCanSendGrantOfferLetter() {
        ProjectResource project = newProjectResource()
                .withProjectState(ProjectState.SETUP)
                .build();

        when(projectProcessRepositoryMock.findOneByTargetId(project.getId())).thenReturn(projectProcess);
        assertTrue(rules.internalUserCanSendGrantOfferLetter(project, projectFinanceUser()));
    }

    @Test
    public void testCompAdminsUserCanSendGrantOfferLetter() {
        ProjectResource project = newProjectResource()
                .withProjectState(ProjectState.SETUP)
                .build();

        when(projectProcessRepositoryMock.findOneByTargetId(project.getId())).thenReturn(projectProcess);
        assertTrue(rules.internalUserCanSendGrantOfferLetter(project, compAdminUser()));
    }

    @Test
    public void testPartnerUserCannotSendGrantOfferLetter() {
        ProjectResource project = newProjectResource().build();
        UserResource user = newUserResource().build();
        setupUserAsPartner(project, user);
        assertFalse(rules.internalUserCanSendGrantOfferLetter(project, user));
    }

    @Test
    public void testInternalUserCanViewSendGrantOfferLetterStatus() {

        ProjectResource project = newProjectResource().build();

        allGlobalRoleUsers.forEach(user -> {
            if (user.equals(projectFinanceUser()) || user.equals(compAdminUser())) {
                assertTrue(rules.internalAdminUserCanViewSendGrantOfferLetterStatus(project, user));
            } else {
                assertFalse(rules.internalAdminUserCanViewSendGrantOfferLetterStatus(project, user));
            }
        });
    }

    @Test
    public void testPartnersCanViewSendGrantOfferLetterStatus() {

        ProjectResource project = newProjectResource().build();

        // Ensure partners can access
        UserResource user = newUserResource().build();
        setupUserAsPartner(project, user);
        assertTrue(rules.externalUserCanViewSendGrantOfferLetterStatus(project, user));
    }

    @Test
    public void testNonPartnersCannotViewSendGrantOfferLetterStatus() {

        ProjectResource project = newProjectResource().build();

        // Ensure non-partners cannot access
        allGlobalRoleUsers.forEach(user2 -> assertFalse(rules.externalUserCanViewSendGrantOfferLetterStatus(project, user2)));
    }

    @Override
    protected GrantOfferLetterPermissionRules supplyPermissionRulesUnderTest() {
        return new GrantOfferLetterPermissionRules();
    }

    @Test
    public void testSupportUserCanViewSendGrantOfferLetterStatus() {

        ProjectResource project = newProjectResource().build();

        allGlobalRoleUsers.forEach(user -> {
            if (user.equals(supportUser())) {
                assertTrue(rules.supportUserCanViewSendGrantOfferLetterStatus(project, user));
            } else {
                assertFalse(rules.supportUserCanViewSendGrantOfferLetterStatus(project, user));
            }
        });
    }

    @Test
    public void onlyStakeholdersAssignedToCompetitionCanViewSendGrantOfferLetterStatus() {
        when(stakeholderRepository.existsByCompetitionIdAndUserId(competition.getId(), stakeholderUserResourceOnCompetition.getId())).thenReturn(true);

        assertTrue(rules.stakeholdersCanViewSendGrantOfferLetterStatus(projectResource1, stakeholderUserResourceOnCompetition));
        assertFalse(rules.stakeholdersCanViewSendGrantOfferLetterStatus(projectResource1, stakeholderUser()));
    }

    @Test
    public void testSupportUsersCanDownloadGrantOfferLetter() {

        ProjectResource project = newProjectResource().build();

        allGlobalRoleUsers.forEach(user -> {
            if (user.equals(supportUser())) {
                assertTrue(rules.supportUsersCanDownloadGrantOfferLetter(project, user));
            } else {
                assertFalse(rules.supportUsersCanDownloadGrantOfferLetter(project, user));
            }
        });
    }

    @Test
    public void testOnlyInnovationLeadUsersAssignedToCompetitionCanDownloadGrantOfferLetter() {
        assertTrue(rules.innovationLeadUsersCanDownloadGrantOfferLetter(projectResource1, innovationLeadUserResourceOnProject1));
        assertFalse(rules.innovationLeadUsersCanDownloadGrantOfferLetter(projectResource1, innovationLeadUser()));
    }

    @Test
    public void onlyStakeholdersAssignedToCompetitionCanDownloadGrantOfferLetter() {
        when(stakeholderRepository.existsByCompetitionIdAndUserId(competition.getId(), stakeholderUserResourceOnCompetition.getId())).thenReturn(true);

        assertTrue(rules.stakeholdersCanDownloadGrantOfferLetter(projectResource1, stakeholderUserResourceOnCompetition));
        assertFalse(rules.stakeholdersCanDownloadGrantOfferLetter(projectResource1, stakeholderUser()));
    }

    @Test
    public void testOnlyInnovationLeadUsersAssignedToCompetitionCanViewGrantOfferLetter() {
        assertTrue(rules.innovationLeadUsersCanViewGrantOfferLetter(projectResource1, innovationLeadUserResourceOnProject1));
        assertFalse(rules.innovationLeadUsersCanViewGrantOfferLetter(projectResource1, innovationLeadUser()));
    }

    @Test
    public void onlyStakeholdersAssignedToCompetitionCanViewGrantOfferLetter() {
        when(stakeholderRepository.existsByCompetitionIdAndUserId(competition.getId(), stakeholderUserResourceOnCompetition.getId())).thenReturn(true);

        assertTrue(rules.stakeholdersCanViewGrantOfferLetter(projectResource1, stakeholderUserResourceOnCompetition));
        assertFalse(rules.stakeholdersCanViewGrantOfferLetter(projectResource1, stakeholderUser()));
    }

    @Test
    public void monitoringOfficersCanViewGrantOfferLetterOnTheirProjects() {
        when(projectMonitoringOfficerRepositoryMock.existsByProjectIdAndUserId(projectResource1.getId(), monitoringOfficerUser().getId())).thenReturn(true);
        assertTrue(rules.monitoringOfficerCanViewGrantOfferLetter(projectResource1, monitoringOfficerUser()));
    }

    @Test
    public void monitoringOfficersCanViewSendGrantOfferLetterOnTheirProjects() {
        when(projectMonitoringOfficerRepositoryMock.existsByProjectIdAndUserId(projectResource1.getId(), monitoringOfficerUser().getId())).thenReturn(true);
        assertTrue(rules.monitoringOfficerCanViewSendGrantOfferLetterStatus(projectResource1, monitoringOfficerUser()));
    }

    @Test
    public void testSupportUsersCanViewGrantOfferLetter() {

        ProjectResource project = newProjectResource().build();

        allGlobalRoleUsers.forEach(user -> {
            if (user.equals(supportUser())) {
                assertTrue(rules.supportUsersCanViewGrantOfferLetter(project, user));
            } else {
                assertFalse(rules.supportUsersCanViewGrantOfferLetter(project, user));
            }
        });
    }
}