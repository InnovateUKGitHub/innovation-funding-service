package com.worth.ifs.application.security;

import com.worth.ifs.BaseUnitTestMocksTest;
import com.worth.ifs.application.builder.ApplicationStatusBuilder;
import com.worth.ifs.application.constant.ApplicationStatusConstants;
import com.worth.ifs.application.domain.Application;
import com.worth.ifs.application.domain.ApplicationStatus;
import com.worth.ifs.application.resource.FormInputResponseFileEntryResource;
import com.worth.ifs.file.resource.FileEntryResource;
import com.worth.ifs.user.domain.ProcessRole;
import com.worth.ifs.user.domain.Role;
import com.worth.ifs.user.domain.User;
import com.worth.ifs.user.resource.UserResource;
import org.junit.Test;
import org.mockito.InjectMocks;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.worth.ifs.application.builder.ApplicationBuilder.newApplication;
import static com.worth.ifs.file.builder.FileEntryResourceBuilder.newFileEntryResource;
import static com.worth.ifs.user.builder.ProcessRoleBuilder.newProcessRole;
import static com.worth.ifs.user.builder.RoleBuilder.newRole;
import static com.worth.ifs.user.builder.UserBuilder.newUser;
import static com.worth.ifs.user.builder.UserResourceBuilder.newUserResource;
import static com.worth.ifs.user.resource.UserRoleType.*;
import static java.util.Collections.emptyList;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

}
