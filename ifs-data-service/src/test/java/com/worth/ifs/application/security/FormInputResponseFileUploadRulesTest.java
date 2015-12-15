package com.worth.ifs.application.security;

import com.worth.ifs.BaseUnitTestMocksTest;
import com.worth.ifs.application.domain.Application;
import com.worth.ifs.application.resource.FormInputResponseFileEntryResource;
import com.worth.ifs.file.resource.FileEntryResource;
import com.worth.ifs.form.domain.FormInputResponse;
import com.worth.ifs.user.domain.ProcessRole;
import com.worth.ifs.user.domain.Role;
import com.worth.ifs.user.domain.User;
import org.junit.Test;
import org.mockito.InjectMocks;

import static com.worth.ifs.BuilderAmendFunctions.application;
import static com.worth.ifs.BuilderAmendFunctions.id;
import static com.worth.ifs.application.builder.ApplicationBuilder.newApplication;
import static com.worth.ifs.file.resource.builders.FileEntryResourceBuilder.newFileEntryResource;
import static com.worth.ifs.form.builder.FormInputResponseBuilder.newFormInputResponse;
import static com.worth.ifs.user.builder.ProcessRoleBuilder.newProcessRole;
import static com.worth.ifs.user.builder.RoleBuilder.newRole;
import static com.worth.ifs.user.builder.UserBuilder.newUser;
import static com.worth.ifs.user.domain.UserRoleType.APPLICANT;
import static com.worth.ifs.user.domain.UserRoleType.ASSESSOR;
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

    @Test
    public void testApplicantCanUploadFilesInResponsesForOwnApplication() {

        Application application = newApplication().build();

        User user = newUser().build();
        Role applicantRole = newRole().withType(APPLICANT).build();
        newProcessRole().withUser(user).withRole(applicantRole).withApplication(application).build();

        FileEntryResource fileEntry = newFileEntryResource().build();
        FormInputResponseFileEntryResource file = new FormInputResponseFileEntryResource(fileEntry, 123L);

        FormInputResponse formInputResponse = newFormInputResponse().with(id(123L)).with(application(application)).build();
        when(formInputResponseRepository.findOne(formInputResponse.getId())).thenReturn(formInputResponse);

        assertTrue(fileUploadRules.applicantCanUploadFilesInResponsesForOwnApplication(file, user));

        verify(formInputResponseRepository).findOne(formInputResponse.getId());
    }

    @Test
    public void testApplicantCanUploadFilesInResponsesForOwnApplicationButNotAMemberOfApplication() {

        User user = newUser().build();
        FileEntryResource fileEntry = newFileEntryResource().build();
        FormInputResponseFileEntryResource file = new FormInputResponseFileEntryResource(fileEntry, 123L);

        assertFalse(fileUploadRules.applicantCanUploadFilesInResponsesForOwnApplication(file, user));
    }

    @Test
    public void testApplicantCanUploadFilesInResponsesForOwnApplicationButApplicantOnDifferentApplication() {

        Application application = newApplication().build();
        Application differentApplication = newApplication().build();

        User user = newUser().build();
        Role applicantRole = newRole().withType(APPLICANT).build();

        ProcessRole applicantProcessRole = newProcessRole().
                withUser(user).
                withRole(applicantRole).
                withApplication(differentApplication).build();

        FileEntryResource fileEntry = newFileEntryResource().build();
        FormInputResponseFileEntryResource file = new FormInputResponseFileEntryResource(fileEntry, 123L);

        FormInputResponse formInputResponse = newFormInputResponse().with(id(123L)).with(application(application)).build();
        when(formInputResponseRepository.findOne(formInputResponse.getId())).thenReturn(formInputResponse);

        assertFalse(fileUploadRules.applicantCanUploadFilesInResponsesForOwnApplication(file, user));

        verify(formInputResponseRepository).findOne(formInputResponse.getId());
    }

    @Test
    public void testApplicantCanUploadFilesInResponsesForOwnApplicationButHasDifferentRoleOnApplication() {

        Application application = newApplication().build();

        User user = newUser().build();
        Role anotherRole = newRole().withType(ASSESSOR).build();

        ProcessRole applicantProcessRole = newProcessRole().
                withUser(user).
                withRole(anotherRole).
                withApplication(application).build();

        FileEntryResource fileEntry = newFileEntryResource().build();
        FormInputResponseFileEntryResource file = new FormInputResponseFileEntryResource(fileEntry, 123L);

        FormInputResponse formInputResponse = newFormInputResponse().with(id(123L)).with(application(application)).build();
        when(formInputResponseRepository.findOne(formInputResponse.getId())).thenReturn(formInputResponse);

        assertFalse(fileUploadRules.applicantCanUploadFilesInResponsesForOwnApplication(file, user));

        verify(formInputResponseRepository).findOne(formInputResponse.getId());
    }

}
