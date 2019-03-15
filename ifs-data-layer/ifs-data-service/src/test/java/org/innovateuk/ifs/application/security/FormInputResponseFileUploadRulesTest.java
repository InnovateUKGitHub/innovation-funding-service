package org.innovateuk.ifs.application.security;

import org.codehaus.groovy.runtime.InvokerHelper;
import org.innovateuk.ifs.BasePermissionRulesTest;
import org.innovateuk.ifs.BaseUnitTestMocksTest;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.repository.ApplicationRepository;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.ApplicationState;
import org.innovateuk.ifs.application.resource.FormInputResponseFileEntryId;
import org.innovateuk.ifs.application.resource.FormInputResponseFileEntryResource;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.domain.Stakeholder;
import org.innovateuk.ifs.competition.repository.StakeholderRepository;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.project.core.domain.Project;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.repository.ProcessRoleRepository;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;
import java.util.Optional;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.competition.builder.StakeholderBuilder.newStakeholder;
import static org.innovateuk.ifs.file.builder.FileEntryResourceBuilder.newFileEntryResource;
import static org.innovateuk.ifs.project.core.builder.ProjectBuilder.newProject;
import static org.innovateuk.ifs.user.builder.ProcessRoleBuilder.newProcessRole;
import static org.innovateuk.ifs.user.builder.UserBuilder.newUser;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.user.resource.Role.*;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests around the security rules defining which applicants can upload  and download files to a response to a Question in the Application Form
 */
public class FormInputResponseFileUploadRulesTest extends BasePermissionRulesTest<FormInputResponseFileUploadRules> {

    @Override
    protected FormInputResponseFileUploadRules supplyPermissionRulesUnderTest() {
        return new FormInputResponseFileUploadRules();
    }

    @InjectMocks
    private FormInputResponseFileUploadRules fileUploadRules;

    @Mock
    private ApplicationRepository applicationRepositoryMock;

    @Mock
    private StakeholderRepository stakeholderRepositoryMock;

    private static final long formInputId = 123L;
    private static final long applicationId = 456L;
    private static final long processRoleId = 789L;

    @Test
    public void applicantCanUploadFilesInResponsesForOwnApplication() {
        Application application = newApplication().withApplicationState(ApplicationState.OPEN).build();

        User user = newUser().build();
        UserResource userResource = newUserResource().withId(user.getId()).build();

        ProcessRole applicantProcessRole =
                newProcessRole().withUser(user).withRole(LEADAPPLICANT).withApplication(application).build();

        FileEntryResource fileEntry = newFileEntryResource().build();
        FormInputResponseFileEntryResource file = new FormInputResponseFileEntryResource(fileEntry, formInputId, applicationId, processRoleId);
        List<Role> expectedRoles = asList(LEADAPPLICANT, COLLABORATOR);

        when(applicationRepositoryMock.findById(applicationId)).thenReturn(Optional.of(application));
        when(processRoleRepositoryMock.findByUserIdAndRoleInAndApplicationId(user.getId(), expectedRoles, applicationId)).thenReturn(singletonList(applicantProcessRole));
        
        assertTrue(fileUploadRules.applicantCanUploadFilesInResponsesForOwnApplication(file, userResource));

        verify(processRoleRepositoryMock).findByUserIdAndRoleInAndApplicationId(user.getId(), expectedRoles, applicationId);
    }

    @Test
    public void applicantCanUploadFilesInResponsesForOwnApplicationButNotAMemberOfApplication() {

        UserResource user = newUserResource().build();
        FileEntryResource fileEntry = newFileEntryResource().build();
        FormInputResponseFileEntryResource file = new FormInputResponseFileEntryResource(fileEntry, formInputId, applicationId, processRoleId);

        List<Role> expectedRoles = asList(LEADAPPLICANT, COLLABORATOR);

        when(processRoleRepositoryMock.findByUserIdAndRoleInAndApplicationId(user.getId(), expectedRoles, applicationId)).thenReturn(emptyList());

        assertFalse(fileUploadRules.applicantCanUploadFilesInResponsesForOwnApplication(file, user));

        verify(processRoleRepositoryMock).findByUserIdAndRoleInAndApplicationId(user.getId(), expectedRoles, applicationId);
    }

    @Test
    public void applicantCanUploadFilesInResponsesForOwnApplicationButLeadApplicantRoleNotFound() {

        UserResource user = newUserResource().build();
        FileEntryResource fileEntry = newFileEntryResource().build();
        FormInputResponseFileEntryResource file = new FormInputResponseFileEntryResource(fileEntry, formInputId, applicationId, processRoleId);

        assertFalse(fileUploadRules.applicantCanUploadFilesInResponsesForOwnApplication(file, user));
    }

    @Test
    public void internalUserCanDownloadFilesInResponses() {
        UserResource compAdmin = newUserResource().withRolesGlobal(singletonList(Role.COMP_ADMIN)).build();
        UserResource projectFinance = newUserResource().withRolesGlobal(singletonList(Role.PROJECT_FINANCE)).build();

        FormInputResponseFileEntryResource fileEntry = new FormInputResponseFileEntryResource();

        assertTrue(fileUploadRules.internalUserCanDownloadFilesInResponses(fileEntry, compAdmin));
        assertTrue(fileUploadRules.internalUserCanDownloadFilesInResponses(fileEntry, projectFinance));
    }

    @Test
    public void stakeholdersCanDownloadFilesInResponse() {
        Application application = newApplication().withApplicationState(ApplicationState.OPEN).build();

        Competition competition = newCompetition().build();
        application.setCompetition(competition);
        UserResource stakeholderUserResource = newUserResource()
                .withRoleGlobal(STAKEHOLDER)
                .build();
        User stakeholderUser = newUser()
                .withId(stakeholderUserResource.getId())
                .build();
        Stakeholder stakeholder = newStakeholder()
                .withUser(stakeholderUser)
                .build();

        when(applicationRepositoryMock.findById(application.getId())).thenReturn(Optional.of(application));
        when(stakeholderRepositoryMock.findStakeholders(competition.getId())).thenReturn(InvokerHelper.asList(stakeholder));

        FormInputResponseFileEntryResource fileEntry = new FormInputResponseFileEntryResource();
        fileEntry.setCompoundId(new FormInputResponseFileEntryId(1L, application.getId(), 2L));

        assertTrue(fileUploadRules.stakeholdersCanDownloadFilesInResponse(fileEntry, stakeholderUserResource));
    }

    @Test
    public void monitoringOfficersCanSeeTheResearchParticipantPercentageInApplications() {
        Project project = newProject().build();
        when(projectRepositoryMock.findOneByApplicationId(any())).thenReturn(project);
        when(projectMonitoringOfficerRepositoryMock.existsByProjectIdAndUserId(project.getId(), monitoringOfficerUser().getId())).thenReturn(true);

        long applicationId = 3L;
        FormInputResponseFileEntryResource fileEntry = new FormInputResponseFileEntryResource();
        fileEntry.setCompoundId(new FormInputResponseFileEntryId(1L, applicationId, 2L));

        allGlobalRoleUsers.forEach(user -> {
            if (user.hasRole(MONITORING_OFFICER)) {
                assertTrue(fileUploadRules.monitoringOfficerCanDownloadFilesInResponses(fileEntry, monitoringOfficerUser()));
            } else {
                assertFalse(fileUploadRules.monitoringOfficerCanDownloadFilesInResponses(fileEntry, user));
            }
        });
    }
}
