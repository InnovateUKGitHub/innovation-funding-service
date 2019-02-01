package org.innovateuk.ifs.application.security;

import org.codehaus.groovy.runtime.InvokerHelper;
import org.innovateuk.ifs.BaseUnitTestMocksTest;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.repository.ApplicationRepository;
import org.innovateuk.ifs.application.resource.ApplicationState;
import org.innovateuk.ifs.application.resource.FormInputResponseFileEntryId;
import org.innovateuk.ifs.application.resource.FormInputResponseFileEntryResource;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.domain.Stakeholder;
import org.innovateuk.ifs.competition.repository.StakeholderRepository;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.repository.ProcessRoleRepository;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.competition.builder.StakeholderBuilder.newStakeholder;
import static org.innovateuk.ifs.file.builder.FileEntryResourceBuilder.newFileEntryResource;
import static org.innovateuk.ifs.user.builder.ProcessRoleBuilder.newProcessRole;
import static org.innovateuk.ifs.user.builder.UserBuilder.newUser;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.user.resource.Role.COLLABORATOR;
import static org.innovateuk.ifs.user.resource.Role.LEADAPPLICANT;
import static org.innovateuk.ifs.user.resource.Role.STAKEHOLDER;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests around the security rules defining which applicants can upload  and download files to a response to a Question in the Application Form
 */
public class FormInputResponseFileUploadRulesTest extends BaseUnitTestMocksTest {

    @InjectMocks
    private FormInputResponseFileUploadRules fileUploadRules;

    @Mock
    private ApplicationRepository applicationRepositoryMock;

    @Mock
    private ProcessRoleRepository processRoleRepositoryMock;

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

        when(applicationRepositoryMock.findOne(applicationId)).thenReturn(application);
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

        when(applicationRepositoryMock.findById(application.getId())).thenReturn(application);
        when(stakeholderRepositoryMock.findStakeholders(competition.getId())).thenReturn(InvokerHelper.asList(stakeholder));

        FormInputResponseFileEntryResource fileEntry = new FormInputResponseFileEntryResource();
        fileEntry.setCompoundId(new FormInputResponseFileEntryId(1L, application.getId(), 2L));

        assertTrue(fileUploadRules.stakeholdersCanDownloadFilesInResponse(fileEntry, stakeholderUserResource));
    }
}
