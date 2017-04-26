package org.innovateuk.ifs.application.security;

import org.innovateuk.ifs.BasePermissionRulesTest;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.ApplicationState;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.resource.CompetitionStatus;
import org.innovateuk.ifs.project.domain.Project;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.user.domain.Role;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.resource.UserRoleType;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.competition.resource.CompetitionStatus.*;
import static org.innovateuk.ifs.invite.domain.ProjectParticipantRole.PROJECT_PARTNER;
import static org.innovateuk.ifs.project.builder.ProjectBuilder.newProject;
import static org.innovateuk.ifs.project.builder.ProjectUserBuilder.newProjectUser;
import static org.innovateuk.ifs.user.builder.ProcessRoleBuilder.newProcessRole;
import static org.innovateuk.ifs.user.builder.RoleBuilder.newRole;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.user.resource.UserRoleType.*;
import static org.innovateuk.ifs.util.CollectionFunctions.combineLists;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyList;
import static org.mockito.Mockito.*;

public class ApplicationPermissionRulesTest extends BasePermissionRulesTest<ApplicationPermissionRules> {

    @Override
    protected ApplicationPermissionRules supplyPermissionRulesUnderTest() {
        return new ApplicationPermissionRules();
    }

    private ApplicationResource applicationResource1;
    private ApplicationResource applicationResource2;
    private Application application1;
    private Application application2;
    private ProcessRole processRole1;
    private ProcessRole processRole2;
    private ProcessRole assessorProcessRole;
    private UserResource leadOnApplication1;
    private UserResource user2;
    private UserResource user3;
    private UserResource assessor;
    private UserResource compAdmin;

    private Role leadApplicantRole = newRole().withType(LEADAPPLICANT).build();
    private Role collaboratorRole = newRole().withType(UserRoleType.COLLABORATOR).build();
    private Role applicantRole = newRole().withType(UserRoleType.APPLICANT).build();
    private Role assessorRole = newRole().withType(UserRoleType.ASSESSOR).build();
    private List<Role> applicantRoles = new ArrayList<>();

    @Before
    public void setup() {
        leadOnApplication1 = newUserResource().build();
        user2 = newUserResource().build();
        user3 = newUserResource().build();
        compAdmin = compAdminUser();
        assessor = assessorUser();

        processRole1 = newProcessRole().withRole(leadApplicantRole).build();
        processRole2 = newProcessRole().withRole(applicantRole).build();
        assessorProcessRole = newProcessRole().withRole(assessorRole).build();
        applicationResource1 = newApplicationResource().withApplicationState(ApplicationState.OPEN).build();
        applicationResource2 = newApplicationResource().build();
        application1 = newApplication().withId(applicationResource1.getId()).withProcessRoles(processRole1).build();
        application2 = newApplication().withId(applicationResource2.getId()).withProcessRoles(processRole2).build();
        processRole1.setApplicationId(application1.getId());
        processRole2.setApplicationId(application2.getId());

        applicantRoles.add(leadApplicantRole);
        applicantRoles.add(collaboratorRole);

        when(applicationRepositoryMock.exists(applicationResource1.getId())).thenReturn(true);
        when(applicationRepositoryMock.exists(applicationResource2.getId())).thenReturn(true);
        when(applicationRepositoryMock.exists(null)).thenReturn(false);

        when(roleRepositoryMock.findByNameIn(anyList())).thenReturn(applicantRoles);
        when(roleRepositoryMock.findOneByName(leadApplicantRole.getName())).thenReturn(leadApplicantRole);

        when(processRoleRepositoryMock.findByUserIdAndApplicationId(leadOnApplication1.getId(), applicationResource1.getId())).thenReturn(processRole1);
        when(processRoleRepositoryMock.findByUserIdAndApplicationId(leadOnApplication1.getId(), applicationResource2.getId())).thenReturn(null);
        when(processRoleRepositoryMock.findByUserIdAndApplicationId(user2.getId(), applicationResource1.getId())).thenReturn(null);
        when(processRoleRepositoryMock.findByUserIdAndApplicationId(user2.getId(), applicationResource2.getId())).thenReturn(processRole1);
        when(processRoleRepositoryMock.findByUserIdAndApplicationId(user3.getId(), applicationResource2.getId())).thenReturn(processRole2);

        when(processRoleRepositoryMock.findByUserIdAndRoleInAndApplicationId(leadOnApplication1.getId(), applicantRoles, applicationResource1.getId())).thenReturn(singletonList(processRole1));
        when(processRoleRepositoryMock.findByUserIdAndRoleInAndApplicationId(user2.getId(), applicantRoles, applicationResource1.getId())).thenReturn(singletonList(processRole1));
        when(processRoleRepositoryMock.findByUserIdAndRoleInAndApplicationId(user3.getId(), applicantRoles, applicationResource1.getId())).thenReturn(emptyList());
        when(processRoleRepositoryMock.findByUserIdAndApplicationId(assessor.getId(), applicationResource1.getId())).thenReturn(assessorProcessRole);
    }

    @Test
    public void testUsersConnectedToTheApplicationCanView() {
        assertTrue(rules.usersConnectedToTheApplicationCanView(applicationResource1, leadOnApplication1));
        assertTrue(rules.usersConnectedToTheApplicationCanView(applicationResource2, user2));
        assertFalse(rules.usersConnectedToTheApplicationCanView(applicationResource1, user2));
        assertFalse(rules.usersConnectedToTheApplicationCanView(applicationResource2, leadOnApplication1));
    }

    @Test
    public void testUsersConnectedToTheApplicationCanViewInnovationAreas() {
        assertTrue(rules.usersConnectedToTheApplicationCanViewInnovationAreas(applicationResource1, leadOnApplication1));
        assertTrue(rules.usersConnectedToTheApplicationCanViewInnovationAreas(applicationResource2, user2));
        assertFalse(rules.usersConnectedToTheApplicationCanViewInnovationAreas(applicationResource1, user2));
        assertFalse(rules.usersConnectedToTheApplicationCanViewInnovationAreas(applicationResource2, leadOnApplication1));
    }

    @Test
    public void testInternalUsersCanViewApplications() {
        assertTrue(rules.internalUsersCanViewApplications(applicationResource1, compAdmin));
        assertTrue(rules.internalUsersCanViewApplications(applicationResource1, projectFinanceUser()));
        assertFalse(rules.internalUsersCanViewApplications(applicationResource1, leadOnApplication1));
    }

    @Test
    public void testAssessorCanSeeTheApplicationFinancesTotals() {
        assertTrue(rules.assessorCanSeeTheApplicationFinancesTotals(applicationResource1, assessor));
        assertFalse(rules.assessorCanSeeTheApplicationFinancesTotals(applicationResource1, user2));
        assertFalse(rules.assessorCanSeeTheApplicationFinancesTotals(applicationResource1, leadOnApplication1));
        assertFalse(rules.usersConnectedToTheApplicationCanView(applicationResource2, assessor));
    }

    @Test
    public void testProjectFinanceUserCanSeeApplicationFinanceTotals() {

        ApplicationResource applicationResource = newApplicationResource().build();
        allGlobalRoleUsers.forEach(user -> {
            if (user.equals(projectFinanceUser())) {
                assertTrue(rules.projectFinanceUserCanSeeApplicationFinancesTotals(applicationResource, user));
            } else {
                assertFalse(rules.projectFinanceUserCanSeeApplicationFinancesTotals(applicationResource, user));
            }
        });
    }

    @Test
    public void onlyUsersPartOfTheApplicationCanChangeApplicationResourceTest() {
        assertTrue(rules.applicantCanUpdateApplicationResource(applicationResource1, leadOnApplication1));
        assertTrue(rules.applicantCanUpdateApplicationResource(applicationResource1, user2));
        assertFalse(rules.applicantCanUpdateApplicationResource(applicationResource1, user3));
    }

    @Test
    public void userIsConnectedToApplicationResourceTest() {
        assertTrue(rules.userIsConnectedToApplicationResource(applicationResource1, leadOnApplication1));
    }


    @Test
    public void assessorCanSeeTheResearchParticipantPercentageInApplicationsTheyAssessTest() {
        assertTrue(rules.assessorCanSeeTheResearchParticipantPercentageInApplicationsTheyAssess(applicationResource1, assessor));
        assertFalse(rules.assessorCanSeeTheResearchParticipantPercentageInApplicationsTheyAssess(applicationResource1, compAdmin));
    }

    @Test
    public void consortiumCanSeeTheResearchParticipantPercentageTest() {
        assertTrue(rules.consortiumCanSeeTheResearchParticipantPercentage(applicationResource1, leadOnApplication1));
        assertFalse(rules.consortiumCanSeeTheResearchParticipantPercentage(applicationResource1, compAdmin));
    }

    @Test
    public void leadApplicantCanUpdateTheInnovationArea() {
        assertTrue(rules.leadApplicantCanUpdateApplicationResource(applicationResource1, leadOnApplication1));
        assertFalse(rules.leadApplicantCanUpdateApplicationResource(applicationResource1, user2));
    }

    @Test
    public void leadApplicantCanSeeTheApplicationFinanceDetailsTest() {
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
    public void testInternalUserCanUploadAssessorFeedbackToApplicationWhenCompetitionInFundersPanelOrAssessorFeedbackState() {
        // For each possible Competition Status...
        asList(CompetitionStatus.values()).forEach(competitionStatus -> {

            // For each possible role
            allGlobalRoleUsers.forEach(user -> {

                ApplicationResource application = newApplicationResource().withCompetitionStatus(competitionStatus).build();

                // if the user is not a Comp Admin, immediately fail
                if (!allInternalUsers.contains(user)) {
                    assertFalse(rules.internalUserCanUploadAssessorFeedbackToApplicationInFundersPanelOrAssessorFeedbackState(application, user));
                    verifyNoMoreInteractions(competitionRepositoryMock, processRoleRepositoryMock);

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
    public void testCompAdminCanRemoveAssessorFeedbackThatHasNotYetBeenPublished() {
        // For each possible Competition Status...
        asList(CompetitionStatus.values()).forEach(competitionStatus -> {

            // For each possible role
            allGlobalRoleUsers.forEach(user -> {

                ApplicationResource application = newApplicationResource().withCompetitionStatus(competitionStatus).build();

                // if the user is not a Comp Admin, immediately fail
                if (!user.equals(compAdminUser())) {
                    assertFalse(rules.compAdminCanRemoveAssessorFeedbackThatHasNotYetBeenPublished(application, user));
                    verifyNoMoreInteractions(competitionRepositoryMock, processRoleRepositoryMock);

                } else {

                    if (!singletonList(PROJECT_SETUP).contains(competitionStatus)) {
                        assertTrue(rules.compAdminCanRemoveAssessorFeedbackThatHasNotYetBeenPublished(application, user));
                    } else {
                        assertFalse(rules.compAdminCanRemoveAssessorFeedbackThatHasNotYetBeenPublished(application, user));
                    }

                    verifyNoMoreInteractions(competitionRepositoryMock);
                    reset(competitionRepositoryMock);
                }
            });
        });
    }

    @Test
    public void testInternalUserCanSeeAndDownloadAllAssessorFeedbackAtAnyTime() {
        // For each possible Competition Status...
        asList(CompetitionStatus.values()).forEach(competitionStatus -> {

            // For each possible role
            allGlobalRoleUsers.forEach(user -> {

                Competition competition = newCompetition().withCompetitionStatus(competitionStatus).build();
                ApplicationResource application = newApplicationResource().withCompetition(competition.getId()).build();

                // if the user is not a Comp Admin, immediately fail
                if (!allInternalUsers.contains(user)) {

                    assertFalse(rules.internalUserCanSeeAndDownloadAllAssessorFeedbackAtAnyTime(application, user));
                    verifyNoMoreInteractions(competitionRepositoryMock, processRoleRepositoryMock);

                } else {

                    assertTrue(rules.internalUserCanSeeAndDownloadAllAssessorFeedbackAtAnyTime(application, user));
                    verifyNoMoreInteractions(competitionRepositoryMock, processRoleRepositoryMock);
                }
            });
        });
    }

    @Test
    public void testApplicationTeamCanSeeAndDownloadPublishedAssessorFeedbackForTheirApplications() {

        long competitionId = 123L;

        ApplicationResource application = newApplicationResource().withCompetition(competitionId).build();

        UserResource leadApplicantUser = newUserResource().build();
        UserResource collaboratorUser = newUserResource().build();
        UserResource assessorUser = newUserResource().build();

        List<UserResource> allUsersToTests = combineLists(allGlobalRoleUsers, leadApplicantUser, collaboratorUser, assessorUser);

        ProcessRole leadApplicantProcessRole = newProcessRole().withRole(LEADAPPLICANT).build();
        ProcessRole collaboratorProcessRole = newProcessRole().withRole(COLLABORATOR).build();
        ProcessRole assessorProcessRole = newProcessRole().withRole(ASSESSOR).build();

        // For each possible Competition Status...
        asList(CompetitionStatus.values()).forEach(competitionStatus -> {

            application.setCompetitionStatus(competitionStatus);

            allUsersToTests.forEach(user -> {

                reset(processRoleRepositoryMock);

                when(processRoleRepositoryMock.findByUserIdAndApplicationId(leadApplicantUser.getId(), application.getId())).thenReturn(leadApplicantProcessRole);
                when(processRoleRepositoryMock.findByUserIdAndApplicationId(collaboratorUser.getId(), application.getId())).thenReturn(collaboratorProcessRole);
                when(processRoleRepositoryMock.findByUserIdAndApplicationId(assessorUser.getId(), application.getId())).thenReturn(assessorProcessRole);

                // if the user under test is the lead applicant or a collaboraator for the application, the rule will pass IF the Competition is in Project Setup
                if (user == leadApplicantUser || user == collaboratorUser) {

                    if (singletonList(PROJECT_SETUP).contains(competitionStatus)) {
                        assertTrue(rules.applicationTeamCanSeeAndDownloadPublishedAssessorFeedbackForTheirApplications(application, user));

                        if (user == leadApplicantUser) {
                            verify(processRoleRepositoryMock, times(1)).findByUserIdAndApplicationId(user.getId(), application.getId());
                        } else {
                            verify(processRoleRepositoryMock, times(2)).findByUserIdAndApplicationId(user.getId(), application.getId());
                        }

                    } else {
                        assertFalse(rules.applicationTeamCanSeeAndDownloadPublishedAssessorFeedbackForTheirApplications(application, user));
                        verify(processRoleRepositoryMock, never()).findByUserIdAndApplicationId(user.getId(), application.getId());
                    }

                    verifyNoMoreInteractions(competitionRepositoryMock, processRoleRepositoryMock);
                }
                // otherwise this rule doesn't apply to the user under test, so it should fail
                else {

                    assertFalse(rules.applicationTeamCanSeeAndDownloadPublishedAssessorFeedbackForTheirApplications(application, user));

                    if (singletonList(PROJECT_SETUP).contains(competitionStatus)) {
                        verify(processRoleRepositoryMock, times(2)).findByUserIdAndApplicationId(user.getId(), application.getId());
                    } else {
                        verify(processRoleRepositoryMock, never()).findByUserIdAndApplicationId(user.getId(), application.getId());
                    }
                    verifyNoMoreInteractions(competitionRepositoryMock, processRoleRepositoryMock);
                }
            });
        });
    }

    @Test
    public void testProjectPartnerCanViewApplicationsLinkedToTheirProjects() {

        UserResource user = newUserResource().build();
        ApplicationResource application = newApplicationResource().build();
        Project linkedProject = newProject().build();

        when(projectRepositoryMock.findOneByApplicationId(application.getId())).thenReturn(linkedProject);
        when(projectUserRepositoryMock.findByProjectIdAndUserIdAndRole(linkedProject.getId(), user.getId(), PROJECT_PARTNER)).
                thenReturn(newProjectUser().build(1));

        assertTrue(rules.projectPartnerCanViewApplicationsLinkedToTheirProjects(application, user));

        verify(projectRepositoryMock).findOneByApplicationId(application.getId());
        verify(projectUserRepositoryMock).findByProjectIdAndUserIdAndRole(linkedProject.getId(), user.getId(), PROJECT_PARTNER);
    }

    @Test
    public void testProjectPartnerCanViewApplicationsLinkedToTheirProjectsButNoProjectForApplication() {

        UserResource user = newUserResource().build();
        ApplicationResource application = newApplicationResource().build();
        Project linkedProject = newProject().build();

        when(projectRepositoryMock.findOneByApplicationId(application.getId())).thenReturn(null);

        assertFalse(rules.projectPartnerCanViewApplicationsLinkedToTheirProjects(application, user));

        verify(projectRepositoryMock).findOneByApplicationId(application.getId());
        verify(projectUserRepositoryMock, never()).findByProjectIdAndUserIdAndRole(linkedProject.getId(), user.getId(), PROJECT_PARTNER);
    }

    @Test
    public void testProjectPartnerCanViewApplicationsLinkedToTheirProjectsButNotPartnerOnLinkedProject() {

        UserResource user = newUserResource().build();
        ApplicationResource application = newApplicationResource().build();
        Project linkedProject = newProject().build();

        when(projectRepositoryMock.findOneByApplicationId(application.getId())).thenReturn(linkedProject);
        when(projectUserRepositoryMock.findByProjectIdAndUserIdAndRole(linkedProject.getId(), user.getId(), PROJECT_PARTNER)).
                thenReturn(emptyList());

        assertFalse(rules.projectPartnerCanViewApplicationsLinkedToTheirProjects(application, user));

        verify(projectRepositoryMock).findOneByApplicationId(application.getId());
        verify(projectUserRepositoryMock).findByProjectIdAndUserIdAndRole(linkedProject.getId(), user.getId(), PROJECT_PARTNER);
    }
}
