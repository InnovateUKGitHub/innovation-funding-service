package com.worth.ifs.application.transactional;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.worth.ifs.BaseServiceSecurityTest;
import com.worth.ifs.application.domain.Application;
import com.worth.ifs.application.resource.ApplicationResource;
import com.worth.ifs.application.resource.CompletedPercentageResource;
import com.worth.ifs.application.resource.FormInputResponseFileEntryId;
import com.worth.ifs.application.resource.FormInputResponseFileEntryResource;
import com.worth.ifs.application.security.ApplicationLookupStrategy;
import com.worth.ifs.application.security.ApplicationPermissionRules;
import com.worth.ifs.application.security.FormInputResponseFileUploadLookupStrategies;
import com.worth.ifs.application.security.FormInputResponseFileUploadRules;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.file.resource.FileEntryResource;
import com.worth.ifs.form.domain.FormInputResponse;
import com.worth.ifs.user.resource.UserResource;
import com.worth.ifs.user.resource.UserRoleType;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.method.P;

import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.function.Supplier;

import static com.worth.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static com.worth.ifs.file.builder.FileEntryResourceBuilder.newFileEntryResource;
import static com.worth.ifs.user.builder.RoleResourceBuilder.newRoleResource;
import static com.worth.ifs.user.builder.UserResourceBuilder.newUserResource;
import static com.worth.ifs.user.resource.UserRoleType.APPLICANT;
import static com.worth.ifs.user.resource.UserRoleType.SYSTEM_REGISTRATION_USER;
import static java.util.Collections.singletonList;
import static java.util.EnumSet.complementOf;
import static java.util.EnumSet.of;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;

/**
 * Testing the security annotations on the ApplicationService interface
 */
public class ApplicationServiceSecurityTest extends BaseServiceSecurityTest<ApplicationService> {

    private FormInputResponseFileUploadRules fileUploadRules;
    private FormInputResponseFileUploadLookupStrategies fileUploadLookup;
    private ApplicationPermissionRules applicationRules;
    private ApplicationLookupStrategy applicationLookupStrategy;

    @Before
    public void lookupPermissionRules() {
        fileUploadRules = getMockPermissionRulesBean(FormInputResponseFileUploadRules.class);
        applicationRules = getMockPermissionRulesBean(ApplicationPermissionRules.class);
        fileUploadLookup = getMockPermissionEntityLookupStrategiesBean(FormInputResponseFileUploadLookupStrategies.class);
        applicationLookupStrategy = getMockPermissionEntityLookupStrategiesBean(ApplicationLookupStrategy.class);
    }

    @Test
    public void testSendNotificationApplicationSubmitted() {
        final long applicationId = 1L;
        when(applicationLookupStrategy.getApplicationResource(applicationId)).thenReturn(newApplicationResource().build());
        assertAccessDenied(
                () -> classUnderTest.sendNotificationApplicationSubmitted(applicationId),
                () -> verify(applicationRules).aLeadApplicantCanSendApplicationSubmittedNotification(isA(ApplicationResource.class), isA(UserResource.class))
        );
    }


    @Test
    public void testGetApplicationResource() {
        final long applicationId = 1L;
        when(applicationLookupStrategy.getApplicationResource(applicationId)).thenReturn(newApplicationResource().build());
        assertAccessDenied(
                () -> classUnderTest.getApplicationById(applicationId),
                () -> {
                    verify(applicationRules).usersConnectedToTheApplicationCanView(isA(ApplicationResource.class), isA(UserResource.class));
                    verify(applicationRules).compAdminsCanViewApplications(isA(ApplicationResource.class), isA(UserResource.class));
                }
        );
    }


    @Test
    public void test_createApplicationByAppNameForUserIdAndCompetitionId_allowedIfGlobalApplicationRole() {
        setLoggedInUser(newUserResource().withRolesGlobal(singletonList(newRoleResource().withType(APPLICANT).build())).build());
        classUnderTest.createApplicationByApplicationNameForUserIdAndCompetitionId("An application", 123L, 456L);
    }

    @Test
    public void test_createApplicationByAppNameForUserIdAndCompetitionId_deniedIfNotLoggedIn() {

        setLoggedInUser(null);
        try {
            classUnderTest.createApplicationByApplicationNameForUserIdAndCompetitionId("An application", 123L, 456L);
            fail("Should not have been able to create an com.worth.ifs.Application without first logging in");
        } catch (AccessDeniedException e) {
            // expected behaviour
        }
    }

    @Test
    public void test_createApplicationByAppNameForUserIdAndCompetitionId_deniedIfNoGlobalRolesAtAll() {

        try {
            classUnderTest.createApplicationByApplicationNameForUserIdAndCompetitionId("An application", 123L, 456L);
            fail("Should not have been able to create an com.worth.ifs.Application without the global Applicant role");
        } catch (AccessDeniedException e) {
            // expected behaviour
        }
    }

   @Test
    public void test_createApplicationByAppNameForUserIdAndCompetitionId_deniedIfNotCorrectGlobalRolesOrASystemRegistrar() {
        EnumSet<UserRoleType> nonApplicantRoles = complementOf(of(APPLICANT, SYSTEM_REGISTRATION_USER));

        nonApplicantRoles.forEach(role -> {
            setLoggedInUser(newUserResource().withRolesGlobal(singletonList(newRoleResource().withType(role).build())).build());

            try {
                classUnderTest.createApplicationByApplicationNameForUserIdAndCompetitionId("An application", 123L, 456L);
                fail("Should not have been able to create an com.worth.ifs.Application without the global Applicant role or as a system registrar");
            } catch (AccessDeniedException e) {
                // expected behaviour
            }
        });
    }

    @Test
    public void testCreateFormInputResponseFileUpload() {

        FileEntryResource fileEntry = newFileEntryResource().build();
        FormInputResponseFileEntryResource file = new FormInputResponseFileEntryResource(fileEntry, 123L, 456L, 789L);

        when(fileUploadRules.applicantCanUploadFilesInResponsesForOwnApplication(file, getLoggedInUser())).thenReturn(true);

        classUnderTest.createFormInputResponseFileUpload(file, () -> null);

        verify(fileUploadRules).applicantCanUploadFilesInResponsesForOwnApplication(file, getLoggedInUser());
    }

    @Test
    public void testCreateFormInputResponseFileUploadDenied() {

        FileEntryResource fileEntry = newFileEntryResource().build();
        FormInputResponseFileEntryResource file = new FormInputResponseFileEntryResource(fileEntry, 123L, 456L, 789L);

        when(fileUploadRules.applicantCanUploadFilesInResponsesForOwnApplication(file, getLoggedInUser())).thenReturn(false);

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

        when(fileUploadRules.applicantCanUploadFilesInResponsesForOwnApplication(file, getLoggedInUser())).thenReturn(true);

        classUnderTest.updateFormInputResponseFileUpload(file, () -> null);

        verify(fileUploadRules).applicantCanUploadFilesInResponsesForOwnApplication(file, getLoggedInUser());
    }

    @Test
    public void testUpdateFormInputResponseFileUploadDenied() {

        FileEntryResource fileEntry = newFileEntryResource().build();
        FormInputResponseFileEntryResource file = new FormInputResponseFileEntryResource(fileEntry, 123L, 456L, 789L);

        when(fileUploadRules.applicantCanUploadFilesInResponsesForOwnApplication(file, getLoggedInUser())).thenReturn(false);

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
        when(fileUploadRules.applicantCanUploadFilesInResponsesForOwnApplication(file, getLoggedInUser())).thenReturn(true);

        classUnderTest.deleteFormInputResponseFileUpload(file.getCompoundId());

        verify(fileUploadRules).applicantCanUploadFilesInResponsesForOwnApplication(file, getLoggedInUser());
    }

    @Test
    public void testDeleteFormInputResponseFileUploadDenied() {

        FileEntryResource fileEntry = newFileEntryResource().build();
        FormInputResponseFileEntryResource file = new FormInputResponseFileEntryResource(fileEntry, 123L, 456L, 789L);

        when(fileUploadLookup.getFormInputResponseFileEntryResource(file.getCompoundId())).thenReturn(file);
        when(fileUploadRules.applicantCanUploadFilesInResponsesForOwnApplication(file, getLoggedInUser())).thenReturn(false);

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
        when(fileUploadRules.applicantCanUploadFilesInResponsesForOwnApplication(file, getLoggedInUser())).thenReturn(false);

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
        when(fileUploadRules.applicantCanDownloadFilesInResponsesForOwnApplication(file, getLoggedInUser())).thenReturn(true);

        classUnderTest.getFormInputResponseFileUpload(file.getCompoundId());

        verify(fileUploadRules).applicantCanDownloadFilesInResponsesForOwnApplication(file, getLoggedInUser());
    }

    @Test
    public void testGetFormInputResponseFileUploadDenied() {

        FileEntryResource fileEntry = newFileEntryResource().build();
        FormInputResponseFileEntryResource file = new FormInputResponseFileEntryResource(fileEntry, 123L, 456L, 789L);

        when(fileUploadLookup.getFormInputResponseFileEntryResource(file.getCompoundId())).thenReturn(file);
        when(fileUploadRules.applicantCanDownloadFilesInResponsesForOwnApplication(file, getLoggedInUser())).thenReturn(false);

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
    protected Class<? extends ApplicationService> getClassUnderTest() {
        return TestApplicationService.class;
    }

    /**
     * Dummy implementation (for satisfying Spring Security's need to read parameter information from
     * methods, which is lost when using mocks)
     */
    public static class TestApplicationService implements ApplicationService {

        @Override
        public ServiceResult<ApplicationResource> createApplicationByApplicationNameForUserIdAndCompetitionId(String applicationName, Long competitionId, Long userId) {
            return null;
        }

        @Override
        public ServiceResult<FormInputResponseFileEntryResource> createFormInputResponseFileUpload(@P("fileEntry") FormInputResponseFileEntryResource fileEntry, Supplier<InputStream> inputStreamSupplier) {
            return null;
        }

        @Override
        public ServiceResult<Void> updateFormInputResponseFileUpload(@P("fileEntry") FormInputResponseFileEntryResource fileEntry, Supplier<InputStream> inputStreamSupplier) {
            return null;
        }

        @Override
        public ServiceResult<FormInputResponse> deleteFormInputResponseFileUpload(@P("fileEntry") FormInputResponseFileEntryId fileEntryId) {
            return null;
        }

        @Override
        public ServiceResult<FormInputResponseFileAndContents> getFormInputResponseFileUpload(@P("fileEntry") FormInputResponseFileEntryId fileEntryId) {
            return null;
        }

        @Override
        public ServiceResult<ApplicationResource> getApplicationById(Long id) {
            return null;
        }

        @Override
        public ServiceResult<List<ApplicationResource>> findAll() {
            return null;
        }

        @Override
        public ServiceResult<List<ApplicationResource>> findByUserId(Long userId) {
            return null;
        }

        @Override
        public ServiceResult<ApplicationResource> saveApplicationDetails(Long id, ApplicationResource application) {
            return null;
        }

        @Override
        public ServiceResult<ApplicationResource> saveApplicationSubmitDateTime(@P("applicationId") Long id, LocalDateTime date) {
            return null;
        }

        @Override
        public ServiceResult<CompletedPercentageResource> getProgressPercentageByApplicationId(Long applicationId) {
            return null;
        }

        @Override
        public ServiceResult<ApplicationResource> updateApplicationStatus(Long id, Long statusId) {
            return null;
        }

        @Override
        public ServiceResult<Void> sendNotificationApplicationSubmitted(Long application) {
            return null;
        }

        @Override
        public ServiceResult<List<ApplicationResource>> getApplicationsByCompetitionIdAndUserId(Long competitionId, Long userId, UserRoleType role) {
            return null;
        }

        @Override
        public ServiceResult<ApplicationResource> findByProcessRole(Long id) {
            return null;
        }

        @Override
        public ServiceResult<ObjectNode> applicationReadyForSubmit(Long id) {
            return null;
        }

        @Override
        public ServiceResult<List<Application>> getApplicationsByCompetitionIdAndStatus(Long competitionId,
                                                                                        Collection<Long> applicationStatusId) {
            return null;
        }

        @Override public ServiceResult<BigDecimal> getProgressPercentageBigDecimalByApplicationId(final Long applicationId) {
            return null;
        }
    }
}
