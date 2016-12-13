package org.innovateuk.ifs.application.security;

import org.innovateuk.ifs.BaseUnitTestMocksTest;
import org.innovateuk.ifs.application.builder.ApplicationStatusBuilder;
import org.innovateuk.ifs.application.constant.ApplicationStatusConstants;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.domain.ApplicationStatus;
import org.innovateuk.ifs.application.resource.FormInputResponseFileEntryResource;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.user.builder.RoleResourceBuilder;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.user.domain.Role;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.resource.UserRoleType;
import org.junit.Test;
import org.mockito.InjectMocks;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.file.builder.FileEntryResourceBuilder.newFileEntryResource;
import static org.innovateuk.ifs.user.builder.ProcessRoleBuilder.newProcessRole;
import static org.innovateuk.ifs.user.builder.RoleBuilder.newRole;
import static org.innovateuk.ifs.user.builder.UserBuilder.newUser;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.user.resource.UserRoleType.*;
import static java.util.Collections.emptyList;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.innovateuk.ifs.user.builder.RoleResourceBuilder.newRoleResource;

/**
 * Tests around the security rules defining who can upload files to a response to a Question in the Application Form
 */
public class FormInputResponseFileUploadRulesTest extends BaseUnitTestMocksTest {

    @InjectMocks
    private FormInputResponseFileUploadRules fileUploadRules;

    private long formInputId = 123L;
    private long applicationId = 456L;
    private long processRoleId = 789L;
    private Role applicantRole = newRole().withType(APPLICANT).build();
    //TODO: Implement tests for lead applicant and collaborator type users as well and not just applicant.

    @Test
    public void testApplicantCanUploadFilesInResponsesForOwnApplication() {

        Application application = newApplication().build();

        ApplicationStatus applicationStatusOpen = ApplicationStatusBuilder.newApplicationStatus().withName(ApplicationStatusConstants.OPEN).build();
        application.setApplicationStatus(applicationStatusOpen);

        User user = newUser().build();
        UserResource userResource = newUserResource().withId(user.getId()).build();

        ProcessRole applicantProcessRole =
                newProcessRole().withUser(user).withRole(applicantRole).withApplication(application).build();

        FileEntryResource fileEntry = newFileEntryResource().build();
        FormInputResponseFileEntryResource file = new FormInputResponseFileEntryResource(fileEntry, formInputId, applicationId, processRoleId);
        List<Role> roles = Collections.singletonList(applicantRole);

        when(applicationRepositoryMock.findOne(applicationId)).thenReturn(application);
        when(roleRepositoryMock.findByNameIn(Arrays.asList(APPLICANT.getName(), LEADAPPLICANT.getName(), COLLABORATOR.getName()))).thenReturn(Collections.singletonList(applicantRole));
        when(processRoleRepositoryMock.findByUserIdAndRoleInAndApplicationId(user.getId(), roles, applicationId)).thenReturn(Collections.singletonList(applicantProcessRole));
        
        assertTrue(fileUploadRules.applicantCanUploadFilesInResponsesForOwnApplication(file, userResource));

        verify(roleRepositoryMock).findByNameIn(Arrays.asList(APPLICANT.getName(), LEADAPPLICANT.getName(), COLLABORATOR.getName()));
        verify(processRoleRepositoryMock).findByUserIdAndRoleInAndApplicationId(user.getId(), roles, applicationId);
    }

    @Test
    public void testApplicantCanUploadFilesInResponsesForOwnApplicationButNotAMemberOfApplication() {

        UserResource user = newUserResource().build();
        FileEntryResource fileEntry = newFileEntryResource().build();
        FormInputResponseFileEntryResource file = new FormInputResponseFileEntryResource(fileEntry, formInputId, applicationId, processRoleId);

        List<Role> roles = Collections.singletonList(applicantRole);

        when(roleRepositoryMock.findByNameIn(Arrays.asList(APPLICANT.getName(), LEADAPPLICANT.getName(), COLLABORATOR.getName()))).thenReturn(Collections.singletonList(applicantRole));
        when(processRoleRepositoryMock.findByUserIdAndRoleInAndApplicationId(user.getId(), roles, applicationId)).thenReturn(emptyList());

        assertFalse(fileUploadRules.applicantCanUploadFilesInResponsesForOwnApplication(file, user));

        verify(roleRepositoryMock).findByNameIn(Arrays.asList(APPLICANT.getName(), LEADAPPLICANT.getName(), COLLABORATOR.getName()));
        verify(processRoleRepositoryMock).findByUserIdAndRoleInAndApplicationId(user.getId(), roles, applicationId);
    }

    @Test
    public void testApplicantCanUploadFilesInResponsesForOwnApplicationButLeadApplicantRoleNotFound() {

        UserResource user = newUserResource().build();
        FileEntryResource fileEntry = newFileEntryResource().build();
        FormInputResponseFileEntryResource file = new FormInputResponseFileEntryResource(fileEntry, formInputId, applicationId, processRoleId);

        when(roleRepositoryMock.findByNameIn(Arrays.asList(APPLICANT.getName(), LEADAPPLICANT.getName(), COLLABORATOR.getName()))).thenReturn(emptyList());
        assertFalse(fileUploadRules.applicantCanUploadFilesInResponsesForOwnApplication(file, user));
        verify(roleRepositoryMock).findByNameIn(Arrays.asList(APPLICANT.getName(), LEADAPPLICANT.getName(), COLLABORATOR.getName()));
    }

    @Test
    public void testCompAdminCanDownloadFilesInResponses() {
        UserResource user = newUserResource().withRolesGlobal(newRoleResource().withType(UserRoleType.COMP_ADMIN).build(1)).build();
        FormInputResponseFileEntryResource fileEntry = new FormInputResponseFileEntryResource();

        assertTrue(fileUploadRules.compAdminCanDownloadFilesInResponses(fileEntry, user));
    }

    @Test
    public void testProjectFinanceUserCanDownloadFilesInResponses() {
        UserResource user = newUserResource().withRolesGlobal(newRoleResource().withType(UserRoleType.PROJECT_FINANCE).build(1)).build();
        FormInputResponseFileEntryResource fileEntry = new FormInputResponseFileEntryResource();

        assertTrue(fileUploadRules.projectFinanceUserCanDownloadFilesInResponses(fileEntry, user));
    }

}
