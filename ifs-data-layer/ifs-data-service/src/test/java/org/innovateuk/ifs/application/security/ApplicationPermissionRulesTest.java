package org.innovateuk.ifs.application.security;

import org.innovateuk.ifs.BasePermissionRulesTest;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.repository.ApplicationRepository;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.ApplicationState;
import org.innovateuk.ifs.competition.builder.StakeholderBuilder;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.domain.InnovationLead;
import org.innovateuk.ifs.competition.domain.Stakeholder;
import org.innovateuk.ifs.competition.repository.CompetitionRepository;
import org.innovateuk.ifs.competition.repository.InnovationLeadRepository;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionStatus;
import org.innovateuk.ifs.project.core.domain.Project;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.*;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.competition.builder.CompetitionTypeBuilder.newCompetitionType;
import static org.innovateuk.ifs.competition.builder.InnovationLeadBuilder.newInnovationLead;
import static org.innovateuk.ifs.competition.resource.CompetitionStatus.*;
import static org.innovateuk.ifs.project.core.builder.ProjectBuilder.newProject;
import static org.innovateuk.ifs.project.core.builder.ProjectUserBuilder.newProjectUser;
import static org.innovateuk.ifs.project.core.domain.ProjectParticipantRole.PROJECT_PARTNER;
import static org.innovateuk.ifs.user.builder.ProcessRoleBuilder.newProcessRole;
import static org.innovateuk.ifs.user.builder.UserBuilder.newUser;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.user.resource.Role.*;
import static org.innovateuk.ifs.util.CollectionFunctions.combineLists;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class ApplicationPermissionRulesTest extends BasePermissionRulesTest<ApplicationPermissionRules> {

    @Override
    protected ApplicationPermissionRules supplyPermissionRulesUnderTest() {
        return new ApplicationPermissionRules();
    }

    private Competition competition;
    private ApplicationResource applicationResource1;
    private ApplicationResource applicationResource2;
    private ProcessRole processRole1;
    private ProcessRole processRole2;
    private UserResource leadOnApplication1;
    private UserResource innovationLeadOnApplication1;
    private UserResource stakeholderUserResourceOnCompetition;
    private UserResource monitoringOfficerOnProjectForApplication1;
    private UserResource user2;
    private UserResource user3;
    private UserResource assessor;
    private UserResource compAdmin;
    private UserResource projectFinance;
    private UserResource panelAssessor;
    private UserResource interviewAssessor;

    private static final Set<Role> applicantRoles = EnumSet.of(LEADAPPLICANT, COLLABORATOR);

    @Mock
    private ApplicationRepository applicationRepository;

    @Mock
    private CompetitionRepository competitionRepository;

    @Mock
    private InnovationLeadRepository innovationLeadRepository;

    @Before
    public void setup() {
        competition = newCompetition().withLeadTechnologist().build();
        User innovationLeadOnApp1 = newUser().build();
        innovationLeadOnApplication1 = newUserResource().withRolesGlobal(singletonList(INNOVATION_LEAD)).build();
        innovationLeadOnApplication1.setId(innovationLeadOnApp1.getId());
        InnovationLead innovationLead = newInnovationLead().withUser(innovationLeadOnApp1).build();

        User stakeholderUserOnCompetition = newUser().build();
        stakeholderUserResourceOnCompetition = newUserResource().withId(stakeholderUserOnCompetition.getId()).withRoleGlobal(STAKEHOLDER).build();
        Stakeholder stakeholder = StakeholderBuilder.newStakeholder().withUser(stakeholderUserOnCompetition).build();

        monitoringOfficerOnProjectForApplication1 = newUserResource().build();

        leadOnApplication1 = newUserResource().build();
        user2 = newUserResource().build();
        user3 = newUserResource().build();
        compAdmin = compAdminUser();
        assessor = assessorUser();
        projectFinance = projectFinanceUser();
        panelAssessor = newUserResource().withRolesGlobal(singletonList(ASSESSOR)).build();
        interviewAssessor = newUserResource().withRolesGlobal(singletonList(ASSESSOR)).build();

        applicationResource1 = newApplicationResource().withCompetition(competition.getId()).withApplicationState(ApplicationState.OPENED).build();
        applicationResource2 = newApplicationResource().build();
        Application application1 = newApplication().withId(applicationResource1.getId()).withCompetition(competition).withProcessRoles(processRole1).build();
        Application application2 = newApplication().withId(applicationResource2.getId()).withProcessRoles(processRole2).build();
        processRole1 = newProcessRole().withRole(LEADAPPLICANT).withApplication(application1).build();
        processRole2 = newProcessRole().withRole(APPLICANT).withApplication(application2).build();

        when(applicationRepository.existsById(applicationResource1.getId())).thenReturn(true);
        when(applicationRepository.existsById(applicationResource2.getId())).thenReturn(true);
        when(applicationRepository.existsById(null)).thenReturn(false);

        when(processRoleRepository.existsByUserIdAndApplicationIdAndRole(leadOnApplication1.getId(), applicationResource1.getId(), LEADAPPLICANT)).thenReturn(true);
        when(processRoleRepository.existsByUserIdAndApplicationIdAndRole(user2.getId(), applicationResource1.getId(), COLLABORATOR)).thenReturn(true);
        when(processRoleRepository.findOneByUserIdAndRoleInAndApplicationId(leadOnApplication1.getId(), applicantProcessRoles(), applicationResource2.getId())).thenReturn(null);
        when(processRoleRepository.findOneByUserIdAndRoleInAndApplicationId(user2.getId(), applicantProcessRoles(), applicationResource1.getId())).thenReturn(null);
        when(processRoleRepository.existsByUserIdAndApplicationIdAndRole(user2.getId(), applicationResource2.getId(), LEADAPPLICANT)).thenReturn(true);
        when(processRoleRepository.existsByUserIdAndApplicationIdAndRole(user3.getId(), applicationResource2.getId(), APPLICANT)).thenReturn(true);

        when(processRoleRepository.existsByUserIdAndApplicationId(leadOnApplication1.getId(), applicationResource1.getId())).thenReturn(true);
        when(processRoleRepository.existsByUserIdAndApplicationId(leadOnApplication1.getId(), applicationResource2.getId())).thenReturn(false);
        when(processRoleRepository.existsByUserIdAndApplicationId(user2.getId(), applicationResource1.getId())).thenReturn(false);
        when(processRoleRepository.existsByUserIdAndApplicationId(user2.getId(), applicationResource2.getId())).thenReturn(true);

        when(processRoleRepository.findByUserIdAndRoleInAndApplicationId(leadOnApplication1.getId(), applicantRoles, applicationResource1.getId())).thenReturn(singletonList(processRole1));
        when(processRoleRepository.findByUserIdAndRoleInAndApplicationId(user2.getId(), applicantRoles, applicationResource1.getId())).thenReturn(singletonList(processRole1));
        when(processRoleRepository.findByUserIdAndRoleInAndApplicationId(user3.getId(), applicantRoles, applicationResource1.getId())).thenReturn(emptyList());
        when(processRoleRepository.existsByUserIdAndApplicationId(assessor.getId(), applicationResource2.getId())).thenReturn(false);
        when(processRoleRepository.existsByUserIdAndApplicationIdAndRole(assessor.getId(), applicationResource1.getId(), ASSESSOR)).thenReturn(true);
        when(processRoleRepository.existsByUserIdAndApplicationIdAndRole(panelAssessor.getId(), applicationResource1.getId(), PANEL_ASSESSOR)).thenReturn(true);
        when(processRoleRepository.existsByUserIdAndApplicationIdAndRole(interviewAssessor.getId(), applicationResource1.getId(), PANEL_ASSESSOR)).thenReturn(true);

        when(innovationLeadRepository.findInnovationsLeads(competition.getId())).thenReturn(singletonList
                (innovationLead));
        when(stakeholderRepository.findStakeholders(competition.getId())).thenReturn(singletonList(stakeholder));
        when(projectMonitoringOfficerRepository.existsByProjectApplicationIdAndUserId(application1.getId(), monitoringOfficerOnProjectForApplication1.getId()))
                .thenReturn(true);
    }

    @Test
    public void usersConnectedToTheApplicationCanView() {
        assertTrue(rules.usersConnectedToTheApplicationCanView(applicationResource1, leadOnApplication1));
        assertTrue(rules.usersConnectedToTheApplicationCanView(applicationResource2, user2));
        assertFalse(rules.usersConnectedToTheApplicationCanView(applicationResource1, user2));
        assertFalse(rules.usersConnectedToTheApplicationCanView(applicationResource2, leadOnApplication1));
    }

    @Test
    public void usersConnectedToTheApplicationCanViewInnovationAreas() {
        assertTrue(rules.usersConnectedToTheApplicationCanViewInnovationAreas(applicationResource1, leadOnApplication1));
        assertTrue(rules.usersConnectedToTheApplicationCanViewInnovationAreas(applicationResource2, user2));
        assertFalse(rules.usersConnectedToTheApplicationCanViewInnovationAreas(applicationResource1, user2));
        assertFalse(rules.usersConnectedToTheApplicationCanViewInnovationAreas(applicationResource2, leadOnApplication1));
    }

    @Test
    public void internalUsersOtherThanInnovationLeadAndStakeholderCanViewApplications() {
        assertTrue(rules.internalUsersCanViewApplications(applicationResource1, compAdmin));
        assertTrue(rules.internalUsersCanViewApplications(applicationResource1, projectFinanceUser()));
        assertFalse(rules.internalUsersCanViewApplications(applicationResource1, innovationLeadUser()));
        assertFalse(rules.internalUsersCanViewApplications(applicationResource1, stakeholderUser()));
        assertFalse(rules.internalUsersCanViewApplications(applicationResource1, leadOnApplication1));
    }

    @Test
    public void onlyInnovationLeadAssignedCompetitionForApplicationCanAccessApplication() {
        assertTrue(rules.innovationLeadAssignedToCompetitionCanViewApplications(applicationResource1, innovationLeadOnApplication1));
        assertFalse(rules.innovationLeadAssignedToCompetitionCanViewApplications(applicationResource1, innovationLeadUser()));
    }

    @Test
    public void onlyStakeholdersAssignedToCompetitionForApplicationCanAccessApplication() {
        when(stakeholderRepository.existsByCompetitionIdAndUserId(competition.getId(), stakeholderUserResourceOnCompetition.getId())).thenReturn(true);

        assertTrue(rules.stakeholderAssignedToCompetitionCanViewApplications(applicationResource1, stakeholderUserResourceOnCompetition));
        assertFalse(rules.stakeholderAssignedToCompetitionCanViewApplications(applicationResource1, monitoringOfficerUser()));
    }

    @Test
    public void monitoringOfficerAssignedToProjectCanViewApplications() {
        assertTrue(rules.monitoringOfficerAssignedToProjectCanViewApplications(applicationResource1, monitoringOfficerOnProjectForApplication1));
        assertFalse(rules.monitoringOfficerAssignedToProjectCanViewApplications(applicationResource1, stakeholderUser()));
    }

    @Test
    public void assessorCanSeeTheApplicationFinancesTotals() {
        assertTrue(rules.assessorCanSeeTheApplicationFinancesTotals(applicationResource1, assessor));
        assertFalse(rules.assessorCanSeeTheApplicationFinancesTotals(applicationResource1, user2));
        assertFalse(rules.assessorCanSeeTheApplicationFinancesTotals(applicationResource1, leadOnApplication1));
        assertFalse(rules.usersConnectedToTheApplicationCanView(applicationResource2, assessor));
    }

    @Test
    public void internalUsersCanSeeApplicationFinanceTotals() {
        ApplicationResource applicationResource = newApplicationResource().build();
        allGlobalRoleUsers.forEach(user -> {
            if (user.hasRole(COMP_ADMIN) ||
                    user.hasRole(PROJECT_FINANCE) ||
                    user.hasRole(SUPPORT) ||
                    user.hasRole(INNOVATION_LEAD) ||
                    user.hasRole(IFS_ADMINISTRATOR)) {
                assertTrue(rules.internalUserCanSeeApplicationFinancesTotals(applicationResource, user));
            } else {
                assertFalse(rules.internalUserCanSeeApplicationFinancesTotals(applicationResource, user));
            }
        });
    }

    @Test
    public void monitoringOfficerCanSeeApplicationFinanceTotals() {
        Project project = newProject().build();
        when(projectRepository.findOneByApplicationId(anyLong())).thenReturn(project);
        when(projectMonitoringOfficerRepository.existsByProjectIdAndUserId(project.getId(), monitoringOfficerUser().getId())).thenReturn(true);
        ApplicationResource applicationResource = newApplicationResource().build();

        allGlobalRoleUsers.forEach(user -> {
            if (user.hasRole(MONITORING_OFFICER)) {
                assertTrue(rules.monitoringOfficersCanSeeApplicationFinancesTotals(applicationResource, monitoringOfficerUser()));
            } else {
                assertFalse(rules.monitoringOfficersCanSeeApplicationFinancesTotals(applicationResource, user));
            }
        });
    }

    @Test
    public void stakeholdersCanSeeTheResearchParticipantPercentageInApplications() {
        ApplicationResource applicationResource = newApplicationResource()
                .withCompetition(competition.getId())
                .build();

        when(stakeholderRepository.existsByCompetitionIdAndUserId(competition.getId(), stakeholderUserResourceOnCompetition.getId())).thenReturn(true);

        assertTrue(rules.stakeholdersCanSeeTheResearchParticipantPercentageInApplications(applicationResource, stakeholderUserResourceOnCompetition));
        assertFalse(rules.stakeholdersCanSeeTheResearchParticipantPercentageInApplications(applicationResource, user2));
        assertFalse(rules.stakeholdersCanSeeTheResearchParticipantPercentageInApplications(applicationResource, user3));
    }

    @Test
    public void monitoringOfficersCanSeeTheResearchParticipantPercentageInApplications() {
        Project project = newProject().build();
        when(projectRepository.findOneByApplicationId(anyLong())).thenReturn(project);
        when(projectMonitoringOfficerRepository.existsByProjectIdAndUserId(project.getId(), monitoringOfficerUser().getId())).thenReturn(true);
        ApplicationResource applicationResource = newApplicationResource().build();

        allGlobalRoleUsers.forEach(user -> {
            if (user.hasRole(MONITORING_OFFICER)) {
                assertTrue(rules.monitoringOfficersCanSeeTheResearchParticipantPercentageInApplications(applicationResource, monitoringOfficerUser()));
            } else {
                assertFalse(rules.monitoringOfficersCanSeeTheResearchParticipantPercentageInApplications(applicationResource, user));
            }
        });
    }

    @Test
    public void stakeholdersCanSeeApplicationFinancesTotals() {
        ApplicationResource applicationResource = newApplicationResource()
                .withCompetition(competition.getId())
                .build();

        when(stakeholderRepository.existsByCompetitionIdAndUserId(competition.getId(), stakeholderUserResourceOnCompetition.getId())).thenReturn(true);

        assertTrue(rules.stakeholdersCanSeeApplicationFinancesTotals(applicationResource, stakeholderUserResourceOnCompetition));
        assertFalse(rules.stakeholdersCanSeeApplicationFinancesTotals(applicationResource, user2));
        assertFalse(rules.stakeholdersCanSeeApplicationFinancesTotals(applicationResource, user3));
    }

    @Test
    public void onlyUsersPartOfTheApplicationCanChangeApplicationResource() {
        assertTrue(rules.applicantCanUpdateApplicationResource(applicationResource1, leadOnApplication1));
        assertTrue(rules.applicantCanUpdateApplicationResource(applicationResource1, user2));
        assertFalse(rules.applicantCanUpdateApplicationResource(applicationResource1, user3));
    }

    @Test
    public void userIsConnectedToApplicationResource() {
        assertTrue(rules.userIsConnectedToApplicationResource(applicationResource1, leadOnApplication1));
    }

    @Test
    public void assessorCanSeeTheResearchParticipantPercentageInApplicationsTheyAssess() {
        assertTrue(rules.assessorCanSeeTheResearchParticipantPercentageInApplicationsTheyAssess(applicationResource1, assessor));
        assertTrue(rules.assessorCanSeeTheResearchParticipantPercentageInApplicationsTheyAssess(applicationResource1, panelAssessor));
        assertTrue(rules.assessorCanSeeTheResearchParticipantPercentageInApplicationsTheyAssess(applicationResource1, interviewAssessor));
        assertFalse(rules.assessorCanSeeTheResearchParticipantPercentageInApplicationsTheyAssess(applicationResource1, compAdmin));
    }

    @Test
    public void consortiumCanSeeTheResearchParticipantPercentage() {
        assertTrue(rules.consortiumCanSeeTheResearchParticipantPercentage(applicationResource1, leadOnApplication1));
        assertFalse(rules.consortiumCanSeeTheResearchParticipantPercentage(applicationResource1, compAdmin));
    }

    @Test
    public void leadApplicantCanUpdateTheInnovationArea() {
        assertTrue(rules.leadApplicantCanUpdateApplicationResource(applicationResource1, leadOnApplication1));
        assertFalse(rules.leadApplicantCanUpdateApplicationResource(applicationResource1, user2));
    }

    @Test
    public void leadApplicantCanSeeTheApplicationFinanceDetails() {
        assertTrue(rules.leadApplicantCanSeeTheApplicationFinanceDetails(applicationResource1, leadOnApplication1));
        assertFalse(rules.leadApplicantCanSeeTheApplicationFinanceDetails(applicationResource1, user2));
    }

    @Test
    public void internalUsersCanSeeTheResearchParticipantPercentageInApplications() {
        assertTrue(rules.internalUsersCanSeeTheResearchParticipantPercentageInApplications(applicationResource1, compAdmin));
        assertTrue(rules.internalUsersCanSeeTheResearchParticipantPercentageInApplications(applicationResource1, projectFinanceUser()));
        assertFalse(rules.internalUsersCanSeeTheResearchParticipantPercentageInApplications(applicationResource1, leadOnApplication1));
    }

    @Test
    public void sendNotificationApplicationSubmitted() {
        assertTrue(rules.aLeadApplicantCanSendApplicationSubmittedNotification(applicationResource1, leadOnApplication1));
        assertFalse(rules.aLeadApplicantCanSendApplicationSubmittedNotification(applicationResource1, user2));
    }

    @Test
    public void projectFinanceUserCanRemoveAssessorFeedbackThatHasNotYetBeenPublished() {
        assertTrue(rules.projectFinanceUserCanRemoveAssessorFeedbackThatHasNotYetBeenPublished(applicationResource1, projectFinanceUser()));
        assertFalse(rules.projectFinanceUserCanRemoveAssessorFeedbackThatHasNotYetBeenPublished(applicationResource1, user2));
    }

    @Test
    public void internalUserCanUploadAssessorFeedbackToApplicationWhenCompetitionInFundersPanelOrAssessorFeedbackState() {
        // For each possible Competition Status...
        asList(CompetitionStatus.values()).forEach(competitionStatus -> {

            // For each possible role
            allGlobalRoleUsers.forEach(user -> {

                ApplicationResource application = newApplicationResource().withCompetitionStatus(competitionStatus).build();

                // if the user is not a Comp Admin, immediately fail
                if (!allInternalUsers.contains(user)) {
                    assertFalse(rules.internalUserCanUploadAssessorFeedbackToApplicationInFundersPanelOrAssessorFeedbackState(application, user));
                    verifyNoMoreInteractions(competitionRepository, processRoleRepository);

                } else {

                    if (asList(FUNDERS_PANEL, ASSESSOR_FEEDBACK).contains(competitionStatus)) {
                        assertTrue(rules.internalUserCanUploadAssessorFeedbackToApplicationInFundersPanelOrAssessorFeedbackState(application, user));
                    } else {
                        assertFalse(rules.internalUserCanUploadAssessorFeedbackToApplicationInFundersPanelOrAssessorFeedbackState(application, user));
                    }
                }
            });
        });
    }

    @Test
    public void compAdminCanRemoveAssessorFeedbackThatHasNotYetBeenPublished() {
        // For each possible Competition Status...
        asList(CompetitionStatus.values()).forEach(competitionStatus -> {

            // For each possible role
            allGlobalRoleUsers.forEach(user -> {

                ApplicationResource application = newApplicationResource().withCompetitionStatus(competitionStatus).build();

                // if the user is not a Comp Admin, immediately fail
                if (!user.equals(compAdminUser())) {
                    assertFalse(rules.compAdminCanRemoveAssessorFeedbackThatHasNotYetBeenPublished(application, user));
                    verifyNoMoreInteractions(competitionRepository, processRoleRepository);

                } else {

                    if (!singletonList(PROJECT_SETUP).contains(competitionStatus)) {
                        assertTrue(rules.compAdminCanRemoveAssessorFeedbackThatHasNotYetBeenPublished(application, user));
                    } else {
                        assertFalse(rules.compAdminCanRemoveAssessorFeedbackThatHasNotYetBeenPublished(application, user));
                    }

                    verifyNoMoreInteractions(competitionRepository);
                    reset(competitionRepository);
                }
            });
        });
    }

    @Test
    public void internalUserCanSeeAndDownloadAllAssessorFeedbackAtAnyTime() {
        // For each possible Competition Status...
        asList(CompetitionStatus.values()).forEach(competitionStatus -> {

            // For each possible role
            allGlobalRoleUsers.forEach(user -> {

                Competition competition = newCompetition().withCompetitionStatus(competitionStatus).build();
                ApplicationResource application = newApplicationResource().withCompetition(competition.getId()).build();

                // if the user is not a Comp Admin, immediately fail
                if (!allInternalUsers.contains(user)) {

                    assertFalse(rules.internalUserCanSeeAndDownloadAllAssessorFeedbackAtAnyTime(application, user));
                    verifyNoMoreInteractions(competitionRepository, processRoleRepository);

                } else {

                    assertTrue(rules.internalUserCanSeeAndDownloadAllAssessorFeedbackAtAnyTime(application, user));
                    verifyNoMoreInteractions(competitionRepository, processRoleRepository);
                }
            });
        });
    }

    @Test
    public void applicationTeamCanSeeAndDownloadPublishedAssessorFeedbackForTheirApplications() {

        long competitionId = 123L;

        ApplicationResource application = newApplicationResource().withCompetition(competitionId).build();

        UserResource leadApplicantUser = newUserResource().build();
        UserResource collaboratorUser = newUserResource().build();
        UserResource assessorUser = newUserResource().build();

        List<UserResource> allUsersToTests = combineLists(allGlobalRoleUsers, leadApplicantUser, collaboratorUser, assessorUser);

        // For each possible Competition Status...
        asList(CompetitionStatus.values()).forEach(competitionStatus -> {

            application.setCompetitionStatus(competitionStatus);

            allUsersToTests.forEach(user -> {

                reset(processRoleRepository);

                when(processRoleRepository.existsByUserIdAndApplicationIdAndRole(leadApplicantUser.getId(), application.getId(), LEADAPPLICANT)).thenReturn(true);
                when(processRoleRepository.existsByUserIdAndApplicationIdAndRole(collaboratorUser.getId(), application.getId(), COLLABORATOR)).thenReturn(true);
                when(processRoleRepository.existsByUserIdAndApplicationIdAndRole(assessorUser.getId(), application.getId(), ASSESSOR)).thenReturn(true);


                // if the user under test is the lead applicant or a collaboraator for the application, the rule will pass IF the Competition is in Project Setup
                if (user == leadApplicantUser || user == collaboratorUser) {

                    if (singletonList(PROJECT_SETUP).contains(competitionStatus)) {
                        assertTrue(rules.applicationTeamCanSeeAndDownloadPublishedAssessorFeedbackForTheirApplications(application, user));

                        if (user == leadApplicantUser) {
                            verify(processRoleRepository, times(1)).existsByUserIdAndApplicationIdAndRole(user.getId(), application.getId(), LEADAPPLICANT);
                        } else {
                            verify(processRoleRepository, times(1)).existsByUserIdAndApplicationIdAndRole(user.getId(), application.getId(), COLLABORATOR);
                            verify(processRoleRepository, times(1)).existsByUserIdAndApplicationIdAndRole(user.getId(), application.getId(), LEADAPPLICANT);
                        }

                    } else {
                        assertFalse(rules.applicationTeamCanSeeAndDownloadPublishedAssessorFeedbackForTheirApplications(application, user));
                        verify(processRoleRepository, never()).findOneByUserIdAndRoleInAndApplicationId(user.getId(), applicantProcessRoles(), application.getId());
                    }

                    verifyNoMoreInteractions(competitionRepository, processRoleRepository);
                }
                // otherwise this rule doesn't apply to the user under test, so it should fail
                else {

                    assertFalse(rules.applicationTeamCanSeeAndDownloadPublishedAssessorFeedbackForTheirApplications(application, user));

                    if (singletonList(PROJECT_SETUP).contains(competitionStatus)) {
                        verify(processRoleRepository, times(1)).existsByUserIdAndApplicationIdAndRole(user.getId(), application.getId(), COLLABORATOR);
                        verify(processRoleRepository, times(1)).existsByUserIdAndApplicationIdAndRole(user.getId(), application.getId(), LEADAPPLICANT);
                    } else {
                        verify(processRoleRepository, never()).findOneByUserIdAndRoleInAndApplicationId(user.getId(), applicantProcessRoles(), application.getId());
                    }
                    verifyNoMoreInteractions(competitionRepository, processRoleRepository);
                }
            });
        });
    }

    @Test
    public void leadApplicantCanUpdateApplicationState() {
        assertTrue(rules.leadApplicantCanUpdateApplicationState(applicationResource1, leadOnApplication1));
        assertFalse(rules.leadApplicantCanUpdateApplicationState(applicationResource1, compAdmin));
        assertFalse(rules.leadApplicantCanUpdateApplicationState(applicationResource1, user2));
    }

    @Test
    public void compAdminCanUpdateApplicationState() {
        assertTrue(rules.compAdminCanUpdateApplicationState(applicationResource1, compAdmin));
        assertFalse(rules.compAdminCanUpdateApplicationState(applicationResource1, leadOnApplication1));
        assertFalse(rules.compAdminCanUpdateApplicationState(applicationResource1, user2));
    }

    @Test
    public void projectFinanceCanUpdateApplicationState() {
        assertTrue(rules.projectFinanceCanUpdateApplicationState(applicationResource1, projectFinance));
        assertFalse(rules.projectFinanceCanUpdateApplicationState(applicationResource1, compAdmin));
        assertFalse(rules.projectFinanceCanUpdateApplicationState(applicationResource1, leadOnApplication1));
        assertFalse(rules.projectFinanceCanUpdateApplicationState(applicationResource1, user2));
    }

    @Test
    public void userCanCreateNewApplication() {
        // For each possible Competition Status...
        asList(CompetitionStatus.values()).forEach(competitionStatus -> {

            // For each possible role
            allGlobalRoleUsers.forEach(user -> {
                CompetitionResource competition = newCompetitionResource().withCompetitionStatus(competitionStatus).build();

                // if the user has global role applicant or system registrar and competition is open
                if ((user.hasRole(APPLICANT) || user.hasRole(SYSTEM_REGISTRATION_USER)) && competition.isOpen()) {
                    assertTrue(rules.userCanCreateNewApplication(competition, user));
                } else {
                    assertFalse(rules.userCanCreateNewApplication(competition, user));
                }
            });
        });
    }

    @Test
    public void markAsIneligibleAllowedBeforeAssessment() {
        asList(CompetitionStatus.values()).forEach(competitionStatus -> allGlobalRoleUsers.forEach(user -> {
            Competition competition = newCompetition()
                    .withCompetitionStatus(competitionStatus)
                    .withCompetitionType(newCompetitionType().withName("Sector").build())
                    .build();
            ApplicationResource application = newApplicationResource().withCompetition(competition.getId()).build();
            when(competitionRepository.findById(application.getCompetition())).thenReturn(Optional.of(competition));
            if (!EnumSet.of(FUNDERS_PANEL, ASSESSOR_FEEDBACK, PROJECT_SETUP, PREVIOUS).contains(competitionStatus) && user.hasAnyRoles(PROJECT_FINANCE, COMP_ADMIN, INNOVATION_LEAD)) {
                assertTrue(rules.markAsInelgibileAllowedBeforeAssesment(application, user));
            } else {
                assertFalse(rules.markAsInelgibileAllowedBeforeAssesment(application, user));
            }
        }));
    }

    @Test
    public void consortiumCanCheckCollaborativeFundingCriteriaIsMet() {
        assertTrue(rules.consortiumCanCheckCollaborativeFundingCriteriaIsMet(applicationResource1, leadOnApplication1));
        assertTrue(rules.consortiumCanCheckCollaborativeFundingCriteriaIsMet(applicationResource1, user2));
        assertFalse(rules.consortiumCanCheckCollaborativeFundingCriteriaIsMet(applicationResource1, user3));
    }

    @Test
    public void projectPartnerCanViewApplicationsLinkedToTheirProjects() {

        UserResource user = newUserResource().build();
        ApplicationResource application = newApplicationResource().build();
        Project linkedProject = newProject().build();

        when(projectRepository.findOneByApplicationId(application.getId())).thenReturn(linkedProject);
        when(projectUserRepository.findByProjectIdAndUserIdAndRole(linkedProject.getId(), user.getId(), PROJECT_PARTNER)).
                thenReturn(newProjectUser().build(1));

        assertTrue(rules.projectPartnerCanViewApplicationsLinkedToTheirProjects(application, user));

        verify(projectRepository).findOneByApplicationId(application.getId());
        verify(projectUserRepository).findByProjectIdAndUserIdAndRole(linkedProject.getId(), user.getId(), PROJECT_PARTNER);
    }

    @Test
    public void projectPartnerCanViewApplicationsLinkedToTheirProjectsButNoProjectForApplication() {

        UserResource user = newUserResource().build();
        ApplicationResource application = newApplicationResource().build();
        Project linkedProject = newProject().build();

        when(projectRepository.findOneByApplicationId(application.getId())).thenReturn(null);

        assertFalse(rules.projectPartnerCanViewApplicationsLinkedToTheirProjects(application, user));

        verify(projectRepository).findOneByApplicationId(application.getId());
        verify(projectUserRepository, never()).findByProjectIdAndUserIdAndRole(linkedProject.getId(), user.getId(), PROJECT_PARTNER);
    }

    @Test
    public void projectPartnerCanViewApplicationsLinkedToTheirProjectsButNotPartnerOnLinkedProject() {

        UserResource user = newUserResource().build();
        ApplicationResource application = newApplicationResource().build();
        Project linkedProject = newProject().build();

        when(projectRepository.findOneByApplicationId(application.getId())).thenReturn(linkedProject);
        when(projectUserRepository.findByProjectIdAndUserIdAndRole(linkedProject.getId(), user.getId(), PROJECT_PARTNER)).
                thenReturn(emptyList());

        assertFalse(rules.projectPartnerCanViewApplicationsLinkedToTheirProjects(application, user));

        verify(projectRepository).findOneByApplicationId(application.getId());
        verify(projectUserRepository).findByProjectIdAndUserIdAndRole(linkedProject.getId(), user.getId(), PROJECT_PARTNER);
    }
}