package org.innovateuk.ifs.application.security;

import org.innovateuk.ifs.BasePermissionRulesTest;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.repository.ApplicationRepository;
import org.innovateuk.ifs.application.resource.ApplicationState;
import org.innovateuk.ifs.application.resource.FormInputResponseFileEntryResource;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.EnumSet;
import java.util.Optional;
import java.util.Set;

import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.file.builder.FileEntryResourceBuilder.newFileEntryResource;
import static org.innovateuk.ifs.user.builder.UserBuilder.newUser;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.user.resource.Role.COLLABORATOR;
import static org.innovateuk.ifs.user.resource.Role.LEADAPPLICANT;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
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
        Application application = newApplication().withId(applicationId).withApplicationState(ApplicationState.OPENED).build();

        User user = newUser().build();
        UserResource userResource = newUserResource().withId(user.getId()).build();

        FileEntryResource fileEntry = newFileEntryResource().build();
        FormInputResponseFileEntryResource file = new FormInputResponseFileEntryResource(fileEntry, formInputId, applicationId, processRoleId, fileEntryId);
        Set<Role> expectedRoles = EnumSet.of(LEADAPPLICANT, COLLABORATOR);

        when(applicationRepository.findById(applicationId)).thenReturn(Optional.of(application));
        when(processRoleRepository.existsByUserIdAndRoleInAndApplicationId(user.getId(), expectedRoles, applicationId)).thenReturn(true);

        assertTrue(fileUploadRules.applicantCanUploadFilesInResponsesForOwnApplication(file, userResource));

        verify(processRoleRepository).existsByUserIdAndRoleInAndApplicationId(user.getId(), expectedRoles, applicationId);
    }

    @Test
    public void applicantCanUploadFilesInResponsesForOwnApplicationButNotAMemberOfApplication() {
        Application application = newApplication().withId(applicationId).withApplicationState(ApplicationState.OPENED).build();

        UserResource user = newUserResource().build();
        FileEntryResource fileEntry = newFileEntryResource().build();
        FormInputResponseFileEntryResource file = new FormInputResponseFileEntryResource(fileEntry, formInputId, applicationId, processRoleId, fileEntryId);

        Set<Role> expectedRoles = EnumSet.of(LEADAPPLICANT, COLLABORATOR);

        when(applicationRepository.findById(applicationId)).thenReturn(Optional.of(application));

        when(processRoleRepository.existsByUserIdAndRoleInAndApplicationId(user.getId(), expectedRoles, applicationId)).thenReturn(false);

        assertFalse(fileUploadRules.applicantCanUploadFilesInResponsesForOwnApplication(file, user));

        verify(processRoleRepository).existsByUserIdAndRoleInAndApplicationId(user.getId(), expectedRoles, applicationId);
    }

    @Test
    public void applicantCanUploadFilesInResponsesForOwnApplicationButLeadApplicantRoleNotFound() {
        Application application = newApplication().withId(applicationId).withApplicationState(ApplicationState.OPENED).build();

        when(applicationRepository.findById(applicationId)).thenReturn(Optional.of(application));

        UserResource user = newUserResource().build();
        FileEntryResource fileEntry = newFileEntryResource().build();
        FormInputResponseFileEntryResource file = new FormInputResponseFileEntryResource(fileEntry, formInputId, applicationId, processRoleId, fileEntryId);

        assertFalse(fileUploadRules.applicantCanUploadFilesInResponsesForOwnApplication(file, user));
    }
}
