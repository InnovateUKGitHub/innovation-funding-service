package org.innovateuk.ifs.application.transactional;

import org.innovateuk.ifs.BaseServiceSecurityTest;
import org.innovateuk.ifs.application.resource.FormInputResponseFileEntryResource;
import org.innovateuk.ifs.application.security.FormInputResponseFileUploadLookupStrategies;
import org.innovateuk.ifs.application.security.FormInputResponseFileUploadRules;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.access.AccessDeniedException;

import static org.innovateuk.ifs.file.builder.FileEntryResourceBuilder.newFileEntryResource;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;

public class ApplicationFormInputUploadServiceSecurityTest extends
        BaseServiceSecurityTest<ApplicationFormInputUploadService> {

    private FormInputResponseFileUploadRules fileUploadRules;
    private FormInputResponseFileUploadLookupStrategies fileUploadLookup;

    @Before
    public void lookupPermissionRules() {
        fileUploadRules = getMockPermissionRulesBean(FormInputResponseFileUploadRules.class);
        fileUploadLookup = getMockPermissionEntityLookupStrategiesBean(FormInputResponseFileUploadLookupStrategies
                .class);
    }

    @Test
    public void testCreateFormInputResponseFileUpload() {

        FileEntryResource fileEntry = newFileEntryResource().build();
        FormInputResponseFileEntryResource file = new FormInputResponseFileEntryResource(fileEntry, 123L, 456L, 789L);

        when(fileUploadRules.applicantCanUploadFilesInResponsesForOwnApplication(file, getLoggedInUser())).thenReturn
                (true);

        classUnderTest.createFormInputResponseFileUpload(file, () -> null);

        verify(fileUploadRules).applicantCanUploadFilesInResponsesForOwnApplication(file, getLoggedInUser());
    }

    @Test
    public void testCreateFormInputResponseFileUploadDenied() {

        FileEntryResource fileEntry = newFileEntryResource().build();
        FormInputResponseFileEntryResource file = new FormInputResponseFileEntryResource(fileEntry, 123L, 456L, 789L);

        when(fileUploadRules.applicantCanUploadFilesInResponsesForOwnApplication(file, getLoggedInUser())).thenReturn
                (false);

        try {
            classUnderTest.createFormInputResponseFileUpload(file, () -> null);
            fail("Should not have been able to create the file upload, as access was denied");
        } catch (AccessDeniedException e) {
            // expected behaviour
        }

        verify(fileUploadRules).applicantCanUploadFilesInResponsesForOwnApplication(file, getLoggedInUser());
    }

    @Test
    public void testUpdateFormInputResponseFileUpload() {

        FileEntryResource fileEntry = newFileEntryResource().build();
        FormInputResponseFileEntryResource file = new FormInputResponseFileEntryResource(fileEntry, 123L, 456L, 789L);

        when(fileUploadRules.applicantCanUploadFilesInResponsesForOwnApplication(file, getLoggedInUser())).thenReturn
                (true);

        classUnderTest.updateFormInputResponseFileUpload(file, () -> null);

        verify(fileUploadRules).applicantCanUploadFilesInResponsesForOwnApplication(file, getLoggedInUser());
    }

    @Test
    public void testUpdateFormInputResponseFileUploadDenied() {

        FileEntryResource fileEntry = newFileEntryResource().build();
        FormInputResponseFileEntryResource file = new FormInputResponseFileEntryResource(fileEntry, 123L, 456L, 789L);

        when(fileUploadRules.applicantCanUploadFilesInResponsesForOwnApplication(file, getLoggedInUser())).thenReturn
                (false);

        try {
            classUnderTest.updateFormInputResponseFileUpload(file, () -> null);
            fail("Should not have been able to update the file upload, as access was denied");
        } catch (AccessDeniedException e) {
            // expected behaviour
        }

        verify(fileUploadRules).applicantCanUploadFilesInResponsesForOwnApplication(file, getLoggedInUser());
    }

    @Test
    public void testDeleteFormInputResponseFileUpload() {

        FileEntryResource fileEntry = newFileEntryResource().build();
        FormInputResponseFileEntryResource file = new FormInputResponseFileEntryResource(fileEntry, 123L, 456L, 789L);

        when(fileUploadLookup.getFormInputResponseFileEntryResource(file.getCompoundId())).thenReturn(file);
        when(fileUploadRules.applicantCanUploadFilesInResponsesForOwnApplication(file, getLoggedInUser())).thenReturn
                (true);

        classUnderTest.deleteFormInputResponseFileUpload(file.getCompoundId());

        verify(fileUploadRules).applicantCanUploadFilesInResponsesForOwnApplication(file, getLoggedInUser());
    }

    @Test
    public void testDeleteFormInputResponseFileUploadDenied() {

        FileEntryResource fileEntry = newFileEntryResource().build();
        FormInputResponseFileEntryResource file = new FormInputResponseFileEntryResource(fileEntry, 123L, 456L, 789L);

        when(fileUploadLookup.getFormInputResponseFileEntryResource(file.getCompoundId())).thenReturn(file);
        when(fileUploadRules.applicantCanUploadFilesInResponsesForOwnApplication(file, getLoggedInUser())).thenReturn
                (false);

        try {
            classUnderTest.deleteFormInputResponseFileUpload(file.getCompoundId());
            fail("Should not have been able to delete the file upload, as access was denied");
        } catch (AccessDeniedException e) {
            // expected behaviour
        }

        verify(fileUploadRules).applicantCanUploadFilesInResponsesForOwnApplication(file, getLoggedInUser());
    }

    @Test
    public void testDeleteFormInputResponseButResourceLookupFails() {

        FileEntryResource fileEntry = newFileEntryResource().build();
        FormInputResponseFileEntryResource file = new FormInputResponseFileEntryResource(fileEntry, 123L, 456L, 789L);

        when(fileUploadLookup.getFormInputResponseFileEntryResource(file.getCompoundId())).thenReturn(null);
        when(fileUploadRules.applicantCanUploadFilesInResponsesForOwnApplication(file, getLoggedInUser())).thenReturn
                (false);

        try {
            classUnderTest.deleteFormInputResponseFileUpload(file.getCompoundId());
            fail("Should not have been able to delete the file upload, as the resource was not looked up successfully");
        } catch (AccessDeniedException e) {
            // expected behaviour
        }

        verify(fileUploadRules, never()).applicantCanUploadFilesInResponsesForOwnApplication(file, getLoggedInUser());
    }

    @Test
    public void testGetFormInputResponseFileUpload() {

        FileEntryResource fileEntry = newFileEntryResource().build();
        FormInputResponseFileEntryResource file = new FormInputResponseFileEntryResource(fileEntry, 123L, 456L, 789L);

        when(fileUploadLookup.getFormInputResponseFileEntryResource(file.getCompoundId())).thenReturn(file);
        when(fileUploadRules.applicantCanDownloadFilesInResponsesForOwnApplication(file, getLoggedInUser()))
                .thenReturn(true);

        classUnderTest.getFormInputResponseFileUpload(file.getCompoundId());

        verify(fileUploadRules).applicantCanDownloadFilesInResponsesForOwnApplication(file, getLoggedInUser());
    }

    @Test
    public void testGetFormInputResponseFileUploadDenied() {

        FileEntryResource fileEntry = newFileEntryResource().build();
        FormInputResponseFileEntryResource file = new FormInputResponseFileEntryResource(fileEntry, 123L, 456L, 789L);

        when(fileUploadLookup.getFormInputResponseFileEntryResource(file.getCompoundId())).thenReturn(file);
        when(fileUploadRules.applicantCanDownloadFilesInResponsesForOwnApplication(file, getLoggedInUser()))
                .thenReturn(false);

        try {
            classUnderTest.getFormInputResponseFileUpload(file.getCompoundId());
            fail("Should not have been able to read the file upload, as access was denied");
        } catch (AccessDeniedException e) {
            // expected behaviour
        }

        verify(fileUploadRules).applicantCanDownloadFilesInResponsesForOwnApplication(file, getLoggedInUser());
    }

    @Test
    public void testGetFormInputResponseFileUploadButLookupFails() {

        FileEntryResource fileEntry = newFileEntryResource().build();
        FormInputResponseFileEntryResource file = new FormInputResponseFileEntryResource(fileEntry, 123L, 456L, 789L);

        when(fileUploadLookup.getFormInputResponseFileEntryResource(file.getCompoundId())).thenReturn(null);

        try {
            classUnderTest.getFormInputResponseFileUpload(file.getCompoundId());
            fail("Should not have been able to read the file upload, as resource lookup failed");
        } catch (AccessDeniedException e) {
            // expected behaviour
        }

        verify(fileUploadRules, never()).applicantCanDownloadFilesInResponsesForOwnApplication(file, getLoggedInUser());
    }

    @Override
    protected Class<? extends ApplicationFormInputUploadService> getClassUnderTest() {
        return ApplicationFormInputUploadServiceImpl.class;
    }
}
