package org.innovateuk.ifs.application.security;

import org.innovateuk.ifs.BasePermissionRulesTest;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.repository.ApplicationRepository;
import org.innovateuk.ifs.application.resource.ApplicationState;
import org.innovateuk.ifs.application.resource.FormInputResponseFileEntryId;
import org.innovateuk.ifs.application.resource.FormInputResponseFileEntryResource;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.project.core.domain.Project;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.EnumSet;
import java.util.Optional;
import java.util.Set;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.file.builder.FileEntryResourceBuilder.newFileEntryResource;
import static org.innovateuk.ifs.project.core.builder.ProjectBuilder.newProject;
import static org.innovateuk.ifs.user.builder.ProcessRoleBuilder.newProcessRole;
import static org.innovateuk.ifs.user.builder.UserBuilder.newUser;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.user.resource.Role.*;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
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
    private ApplicationRepository applicationRepository;

    private static final long formInputId = 123L;
    private static final long applicationId = 456L;
    private static final long processRoleId = 789L;
    private static final long fileEntryId = 111L;

    @Test
    public void applicantCanUploadFilesInResponsesForOwnApplication() {
        Application application = newApplication().withApplicationState(ApplicationState.OPENED).build();

        User user = newUser().build();
        UserResource userResource = newUserResource().withId(user.getId()).build();

        ProcessRole applicantProcessRole =
                newProcessRole().withUser(user).withRole(LEADAPPLICANT).withApplication(application).build();

        FileEntryResource fileEntry = newFileEntryResource().build();
        FormInputResponseFileEntryResource file = new FormInputResponseFileEntryResource(fileEntry, formInputId, applicationId, processRoleId, Optional.of(fileEntryId));
        Set<Role> expectedRoles = EnumSet.of(LEADAPPLICANT, COLLABORATOR);

        when(applicationRepository.findById(applicationId)).thenReturn(Optional.of(application));
        when(processRoleRepository.findByUserIdAndRoleInAndApplicationId(user.getId(), expectedRoles, applicationId)).thenReturn(singletonList(applicantProcessRole));

        assertTrue(fileUploadRules.applicantCanUploadFilesInResponsesForOwnApplication(file, userResource));

        verify(processRoleRepository).findByUserIdAndRoleInAndApplicationId(user.getId(), expectedRoles, applicationId);
    }

    @Test
    public void applicantCanUploadFilesInResponsesForOwnApplicationButNotAMemberOfApplication() {

        UserResource user = newUserResource().build();
        FileEntryResource fileEntry = newFileEntryResource().build();
        FormInputResponseFileEntryResource file = new FormInputResponseFileEntryResource(fileEntry, formInputId, applicationId, processRoleId, Optional.of(fileEntryId));

        Set<Role> expectedRoles = EnumSet.of(LEADAPPLICANT, COLLABORATOR);

        when(processRoleRepository.findByUserIdAndRoleInAndApplicationId(user.getId(), expectedRoles, applicationId)).thenReturn(emptyList());

        assertFalse(fileUploadRules.applicantCanUploadFilesInResponsesForOwnApplication(file, user));

        verify(processRoleRepository).findByUserIdAndRoleInAndApplicationId(user.getId(), expectedRoles, applicationId);
    }

    @Test
    public void applicantCanUploadFilesInResponsesForOwnApplicationButLeadApplicantRoleNotFound() {

        UserResource user = newUserResource().build();
        FileEntryResource fileEntry = newFileEntryResource().build();
        FormInputResponseFileEntryResource file = new FormInputResponseFileEntryResource(fileEntry, formInputId, applicationId, processRoleId, Optional.of(fileEntryId));

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
        Application application = newApplication().withApplicationState(ApplicationState.OPENED).build();

        Competition competition = newCompetition().build();
        application.setCompetition(competition);
        UserResource stakeholderUserResource = newUserResource()
                .withRoleGlobal(STAKEHOLDER)
                .build();

        when(applicationRepository.findById(application.getId())).thenReturn(Optional.of(application));
        when(stakeholderRepository.existsByCompetitionIdAndUserId(competition.getId(), stakeholderUserResource.getId())).thenReturn(true);

        FormInputResponseFileEntryResource fileEntry = new FormInputResponseFileEntryResource();
        fileEntry.setCompoundId(new FormInputResponseFileEntryId(1L, application.getId(), 2L, Optional.of(fileEntryId)));

        assertTrue(fileUploadRules.stakeholdersCanDownloadFilesInResponse(fileEntry, stakeholderUserResource));
    }

    @Test
    public void externalFinanceCanDownloadFilesInResponse() {
        Application application = newApplication().withApplicationState(ApplicationState.OPENED).build();

        Competition competition = newCompetition().build();
        application.setCompetition(competition);
        UserResource externalFinanceUserResource = newUserResource()
                .withRoleGlobal(EXTERNAL_FINANCE)
                .build();

        when(applicationRepository.findById(application.getId())).thenReturn(Optional.of(application));
        when(externalFinanceRepository.existsByCompetitionIdAndUserId(competition.getId(), externalFinanceUserResource.getId())).thenReturn(true);

        FormInputResponseFileEntryResource fileEntry = new FormInputResponseFileEntryResource();
        fileEntry.setCompoundId(new FormInputResponseFileEntryId(1L, application.getId(), 2L, Optional.of(fileEntryId)));

        assertTrue(fileUploadRules.externalFinanceCanDownloadFilesInResponse(fileEntry, externalFinanceUserResource));
    }

    @Test
    public void monitoringOfficersCanSeeTheResearchParticipantPercentageInApplications() {
        Project project = newProject().build();
        when(projectRepository.findOneByApplicationId(anyLong())).thenReturn(project);
        when(projectMonitoringOfficerRepository.existsByProjectIdAndUserId(project.getId(), monitoringOfficerUser().getId())).thenReturn(true);

        long applicationId = 3L;
        FormInputResponseFileEntryResource fileEntry = new FormInputResponseFileEntryResource();
        fileEntry.setCompoundId(new FormInputResponseFileEntryId(1L, applicationId, 2L, Optional.of(fileEntryId)));

        allGlobalRoleUsers.forEach(user -> {
            if (user.hasRole(MONITORING_OFFICER)) {
                assertTrue(fileUploadRules.monitoringOfficerCanDownloadFilesInResponses(fileEntry, monitoringOfficerUser()));
            } else {
                assertFalse(fileUploadRules.monitoringOfficerCanDownloadFilesInResponses(fileEntry, user));
            }
        });
    }
}
