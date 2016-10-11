package com.worth.ifs.application.security;

import com.worth.ifs.BasePermissionRulesTest;
import com.worth.ifs.application.builder.ApplicationStatusResourceBuilder;
import com.worth.ifs.application.constant.ApplicationStatusConstants;
import com.worth.ifs.application.domain.Application;
import com.worth.ifs.application.resource.ApplicationResource;
import com.worth.ifs.application.resource.ApplicationStatusResource;
import com.worth.ifs.competition.domain.Competition;
import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.user.domain.ProcessRole;
import com.worth.ifs.user.domain.Role;
import com.worth.ifs.user.resource.UserResource;
import com.worth.ifs.user.resource.UserRoleType;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static com.worth.ifs.application.builder.ApplicationBuilder.newApplication;
import static com.worth.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static com.worth.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static com.worth.ifs.competition.resource.CompetitionResource.Status.*;
import static com.worth.ifs.user.builder.ProcessRoleBuilder.newProcessRole;
import static com.worth.ifs.user.builder.RoleBuilder.newRole;
import static com.worth.ifs.user.builder.UserResourceBuilder.newUserResource;
import static com.worth.ifs.user.resource.UserRoleType.ASSESSOR;
import static com.worth.ifs.user.resource.UserRoleType.COLLABORATOR;
import static com.worth.ifs.user.resource.UserRoleType.LEADAPPLICANT;
import static com.worth.ifs.util.CollectionFunctions.combineLists;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyList;
import static org.mockito.Mockito.*;

public class ApplicationPermissionRulesTest extends BasePermissionRulesTest<ApplicationPermissionRules> {

    @Override
    protected ApplicationPermissionRules supplyPermissionRulesUnderTest() {
        return new ApplicationPermissionRules();
    }

    private ApplicationStatusResource applicationStatusOpen;
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
        applicationStatusOpen = ApplicationStatusResourceBuilder.newApplicationStatusResource().withName(ApplicationStatusConstants.OPEN).build();
        applicationResource1 = newApplicationResource().withProcessRoles(asList(processRole1.getId())).withApplicationStatus(ApplicationStatusConstants.OPEN).build();
        applicationResource2 = newApplicationResource().withProcessRoles(asList(processRole2.getId())).build();
        application1 = newApplication().withId(applicationResource1.getId()).withProcessRoles(processRole1).build();
        application2 = newApplication().withId(applicationResource2.getId()).withProcessRoles(processRole2).build();
        processRole1.setApplication(application1);
        processRole2.setApplication(application2);

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

        when(processRoleRepositoryMock.findByUserIdAndRoleAndApplicationId(leadOnApplication1.getId(), leadApplicantRole, applicationResource1.getId())).thenReturn(singletonList(processRole1));
        when(processRoleRepositoryMock.findByUserIdAndRoleAndApplicationId(leadOnApplication1.getId(), leadApplicantRole, applicationResource2.getId())).thenReturn(emptyList());
        when(processRoleRepositoryMock.findByUserIdAndRoleAndApplicationId(user2.getId(), leadApplicantRole, applicationResource1.getId())).thenReturn(emptyList());
        when(processRoleRepositoryMock.findByUserIdAndRoleAndApplicationId(user2.getId(), leadApplicantRole, applicationResource2.getId())).thenReturn(emptyList());
        when(processRoleRepositoryMock.findByUserIdAndRoleAndApplicationId(user3.getId(), leadApplicantRole, applicationResource2.getId())).thenReturn(emptyList());
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
    public void testCompAdminsCanViewApplications() {
        assertTrue(rules.compAdminsCanViewApplications(applicationResource1, compAdmin));
        assertFalse(rules.compAdminsCanViewApplications(applicationResource1, leadOnApplication1));
    }

    @Test
    public void testProjectFinanceUsersCanViewApplications() {
        assertTrue(rules.projectFinanceUsersCanViewApplications(applicationResource1, projectFinanceUser()));
        assertFalse(rules.projectFinanceUsersCanViewApplications(applicationResource1, leadOnApplication1));
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
    public void compAdminCanSeeTheResearchParticipantPercentageInApplications() {
        assertTrue(rules.compAdminCanSeeTheResearchParticipantPercentageInApplications(applicationResource1, compAdmin));
        assertFalse(rules.compAdminCanSeeTheResearchParticipantPercentageInApplications(applicationResource1, leadOnApplication1));
    }

    @Test
    public void projectFinanceUsersCanSeeTheResearchParticipantPercentageInApplications() {
        assertTrue(rules.projectFinanceUsersCanSeeTheResearchParticipantPercentageInApplications(applicationResource1, projectFinanceUser()));
        assertFalse(rules.projectFinanceUsersCanSeeTheResearchParticipantPercentageInApplications(applicationResource1, leadOnApplication1));
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
    public void projectFinanceUserCanSeeAndDownloadAllAssessorFeedbackAtAnyTime() {
        assertTrue(rules.projectFinanceUserCanSeeAndDownloadAllAssessorFeedbackAtAnyTime(applicationResource1, projectFinanceUser()));
        assertFalse(rules.projectFinanceUserCanSeeAndDownloadAllAssessorFeedbackAtAnyTime(applicationResource1, user2));
    }

    @Test
    public void testCompAdminCanUploadAssessorFeedbackToApplicationWhenCompetitionInFundersPanelOrAssessorFeedbackState() {
        // For each possible Competition Status...
        asList(CompetitionResource.Status.values()).forEach(competitionStatus -> {

            // For each possible role
            allGlobalRoleUsers.forEach(user -> {

                ApplicationResource application = newApplicationResource().withCompetitionStatus(competitionStatus).build();

                // if the user is not a Comp Admin, immediately fail
                if (!user.equals(compAdminUser())) {
                    assertFalse(rules.compAdminCanUploadAssessorFeedbackToApplicationInFundersPanelOrAssessorFeedbackState(application, user));
                    verifyNoMoreInteractions(competitionRepositoryMock, processRoleRepositoryMock);

                } else {

                    if (asList(FUNDERS_PANEL, ASSESSOR_FEEDBACK).contains(competitionStatus)) {
                        assertTrue(rules.compAdminCanUploadAssessorFeedbackToApplicationInFundersPanelOrAssessorFeedbackState(application, user));
                    } else {
                        assertFalse(rules.compAdminCanUploadAssessorFeedbackToApplicationInFundersPanelOrAssessorFeedbackState(application, user));
                    }
                }
            });
        });
    }


    @Test
    public void projectFinanceUserCanUploadAssessorFeedbackToApplicationInFundersPanelOrAssessorFeedbackState() {
        // For each possible Competition Status...
        asList(CompetitionResource.Status.values()).forEach(competitionStatus -> {

            // For each possible role
            allGlobalRoleUsers.forEach(user -> {

                ApplicationResource application = newApplicationResource().withCompetitionStatus(competitionStatus).build();

                // if the user is not a Comp Admin, immediately fail
                if (!user.equals(projectFinanceUser())) {
                    assertFalse(rules.projectFinanceUserCanUploadAssessorFeedbackToApplicationInFundersPanelOrAssessorFeedbackState(application, user));
                    verifyNoMoreInteractions(competitionRepositoryMock, processRoleRepositoryMock);

                } else {

                    if (asList(FUNDERS_PANEL, ASSESSOR_FEEDBACK).contains(competitionStatus)) {
                        assertTrue(rules.projectFinanceUserCanUploadAssessorFeedbackToApplicationInFundersPanelOrAssessorFeedbackState(application, user));
                    } else {
                        assertFalse(rules.projectFinanceUserCanUploadAssessorFeedbackToApplicationInFundersPanelOrAssessorFeedbackState(application, user));
                    }
                }
            });
        });
    }

    @Test
    public void testCompAdminCanRemoveAssessorFeedbackThatHasNotYetBeenPublished() {
        // For each possible Competition Status...
        asList(CompetitionResource.Status.values()).forEach(competitionStatus -> {

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
    public void testCompAdminCanSeeAndDownloadAllAssessorFeedbackAtAnyTime() {
        // For each possible Competition Status...
        asList(CompetitionResource.Status.values()).forEach(competitionStatus -> {

            // For each possible role
            allGlobalRoleUsers.forEach(user -> {

                Competition competition = newCompetition().withCompetitionStatus(competitionStatus).build();
                ApplicationResource application = newApplicationResource().withCompetition(competition.getId()).build();

                // if the user is not a Comp Admin, immediately fail
                if (!user.equals(compAdminUser())) {

                    assertFalse(rules.compAdminCanSeeAndDownloadAllAssessorFeedbackAtAnyTime(application, user));
                    verifyNoMoreInteractions(competitionRepositoryMock, processRoleRepositoryMock);

                } else {

                    assertTrue(rules.compAdminCanSeeAndDownloadAllAssessorFeedbackAtAnyTime(application, user));
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
        asList(CompetitionResource.Status.values()).forEach(competitionStatus -> {

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


}
