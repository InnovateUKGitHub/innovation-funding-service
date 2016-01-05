package com.worth.ifs.application.transactional;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.worth.ifs.BaseServiceSecurityTest;
import com.worth.ifs.application.domain.Application;
import com.worth.ifs.application.resource.ApplicationResource;
import com.worth.ifs.application.resource.ApplicationResourceHateoas;
import com.worth.ifs.application.resource.FormInputResponseFileEntryId;
import com.worth.ifs.application.resource.FormInputResponseFileEntryResource;
import com.worth.ifs.application.security.FormInputResponseFileUploadLookupStrategies;
import com.worth.ifs.application.security.FormInputResponseFileUploadRules;
import com.worth.ifs.file.resource.FileEntryResource;
import com.worth.ifs.form.domain.FormInputResponse;
import com.worth.ifs.transactional.ServiceFailure;
import com.worth.ifs.user.domain.UserRoleType;
import com.worth.ifs.util.Either;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Before;
import org.junit.Test;
import org.springframework.hateoas.Resources;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.method.P;

import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.function.Supplier;

import static com.worth.ifs.BuilderAmendFunctions.name;
import static com.worth.ifs.application.builder.ApplicationBuilder.newApplication;
import static com.worth.ifs.file.resource.builders.FileEntryResourceBuilder.newFileEntryResource;
import static com.worth.ifs.user.builder.RoleBuilder.newRole;
import static com.worth.ifs.user.builder.UserBuilder.newUser;
import static com.worth.ifs.user.domain.UserRoleType.APPLICANT;
import static com.worth.ifs.util.Either.right;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;

/**
 * Testing the security annotations on the ApplicationService interface
 */
public class ApplicationServiceSecurityTest extends BaseServiceSecurityTest<ApplicationService> {

    private FormInputResponseFileUploadRules fileUploadRules;
    private FormInputResponseFileUploadLookupStrategies fileUploadLookup;

    @Before
    public void lookupPermissionRules() {
        fileUploadRules = getMockPermissionRulesBean(FormInputResponseFileUploadRules.class);
        fileUploadLookup = getMockPermissionEntityLookupStrategiesBean(FormInputResponseFileUploadLookupStrategies.class);
    }

    @Test
    public void test_createApplicationByAppNameForUserIdAndCompetitionId_allowedIfGlobalApplicationRole() {

        setLoggedInUser(newUser().withRolesGlobal(newRole().withType(APPLICANT).build()).build());
        Application newApplication = service.createApplicationByApplicationNameForUserIdAndCompetitionId("An application", 123L, 456L);
        assertEquals("An application", newApplication.getName());
    }

    @Test
    public void test_createApplicationByAppNameForUserIdAndCompetitionId_deniedIfNotLoggedIn() {

        setLoggedInUser(null);
        try {
            service.createApplicationByApplicationNameForUserIdAndCompetitionId("An application", 123L, 456L);
            fail("Should not have been able to create an Application without first logging in");
        } catch (AccessDeniedException e) {
            // expected behaviour
        }
    }

    @Test
    public void test_createApplicationByAppNameForUserIdAndCompetitionId_deniedIfNoGlobalRolesAtAll() {

        try {
            service.createApplicationByApplicationNameForUserIdAndCompetitionId("An application", 123L, 456L);
            fail("Should not have been able to create an Application without the global Applicant role");
        } catch (AccessDeniedException e) {
            // expected behaviour
        }
    }

    @Test
    public void test_createApplicationByAppNameForUserIdAndCompetitionId_deniedIfNotCorrectGlobalRoles() {

        List<UserRoleType> nonApplicantRoles = asList(UserRoleType.values()).stream().filter(type -> type != APPLICANT).collect(toList());

        nonApplicantRoles.forEach(role -> {

            setLoggedInUser(newUser().withRolesGlobal(newRole().withType(role).build()).build());

            try {
                service.createApplicationByApplicationNameForUserIdAndCompetitionId("An application", 123L, 456L);
                fail("Should not have been able to create an Application without the global Applicant role");
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

        service.createFormInputResponseFileUpload(file, () -> null);

        verify(fileUploadRules).applicantCanUploadFilesInResponsesForOwnApplication(file, getLoggedInUser());
    }

    @Test
    public void testCreateFormInputResponseFileUploadDenied() {

        FileEntryResource fileEntry = newFileEntryResource().build();
        FormInputResponseFileEntryResource file = new FormInputResponseFileEntryResource(fileEntry, 123L, 456L, 789L);

        when(fileUploadRules.applicantCanUploadFilesInResponsesForOwnApplication(file, getLoggedInUser())).thenReturn(false);

        try {
            service.createFormInputResponseFileUpload(file, () -> null);
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

        service.updateFormInputResponseFileUpload(file, () -> null);

        verify(fileUploadRules).applicantCanUploadFilesInResponsesForOwnApplication(file, getLoggedInUser());
    }

    @Test
    public void testUpdateFormInputResponseFileUploadDenied() {

        FileEntryResource fileEntry = newFileEntryResource().build();
        FormInputResponseFileEntryResource file = new FormInputResponseFileEntryResource(fileEntry, 123L, 456L, 789L);

        when(fileUploadRules.applicantCanUploadFilesInResponsesForOwnApplication(file, getLoggedInUser())).thenReturn(false);

        try {
            service.updateFormInputResponseFileUpload(file, () -> null);
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

        service.deleteFormInputResponseFileUpload(file.getCompoundId());

        verify(fileUploadRules).applicantCanUploadFilesInResponsesForOwnApplication(file, getLoggedInUser());
    }

    @Test
    public void testDeleteFormInputResponseFileUploadDenied() {

        FileEntryResource fileEntry = newFileEntryResource().build();
        FormInputResponseFileEntryResource file = new FormInputResponseFileEntryResource(fileEntry, 123L, 456L, 789L);

        when(fileUploadLookup.getFormInputResponseFileEntryResource(file.getCompoundId())).thenReturn(file);
        when(fileUploadRules.applicantCanUploadFilesInResponsesForOwnApplication(file, getLoggedInUser())).thenReturn(false);

        try {
            service.deleteFormInputResponseFileUpload(file.getCompoundId());
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
            service.deleteFormInputResponseFileUpload(file.getCompoundId());
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

        service.getFormInputResponseFileUpload(file.getCompoundId());

        verify(fileUploadRules).applicantCanDownloadFilesInResponsesForOwnApplication(file, getLoggedInUser());
    }

    @Test
    public void testGetFormInputResponseFileUploadDenied() {

        FileEntryResource fileEntry = newFileEntryResource().build();
        FormInputResponseFileEntryResource file = new FormInputResponseFileEntryResource(fileEntry, 123L, 456L, 789L);

        when(fileUploadLookup.getFormInputResponseFileEntryResource(file.getCompoundId())).thenReturn(file);
        when(fileUploadRules.applicantCanDownloadFilesInResponsesForOwnApplication(file, getLoggedInUser())).thenReturn(false);

        try {
            service.getFormInputResponseFileUpload(file.getCompoundId());
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
            service.getFormInputResponseFileUpload(file.getCompoundId());
            fail("Should not have been able to read the file upload, as resource lookup failed");
        } catch (AccessDeniedException e) {
            // expected behaviour
        }

        verify(fileUploadRules, never()).applicantCanDownloadFilesInResponsesForOwnApplication(file, getLoggedInUser());
    }
    @Override
    protected Class<? extends ApplicationService> getServiceClass() {
        return TestApplicationService.class;
    }

    /**
     * Dummy implementation (for satisfying Spring Security's need to read parameter information from
     * methods, which is lost when using mocks)
     */
    private static class TestApplicationService implements ApplicationService {

        @Override
        public Application createApplicationByApplicationNameForUserIdAndCompetitionId(String applicationName, Long competitionId, Long userId) {
            return newApplication().with(name(applicationName)).build();
        }

        @Override
        public Either<ServiceFailure, Pair<File, FormInputResponseFileEntryResource>> createFormInputResponseFileUpload(FormInputResponseFileEntryResource fileEntry, Supplier<InputStream> inputStreamSupplier) {
            return right(Pair.of(new File("", ""), new FormInputResponseFileEntryResource()));
        }

        @Override
        public Either<ServiceFailure, Pair<FormInputResponseFileEntryResource, Supplier<InputStream>>> getFormInputResponseFileUpload(FormInputResponseFileEntryId fileEntryId) {
            return null;
        }

        @Override
        public Either<ServiceFailure, Pair<File, FormInputResponseFileEntryResource>> updateFormInputResponseFileUpload(@P("fileEntry") FormInputResponseFileEntryResource fileEntry, Supplier<InputStream> inputStreamSupplier) {
            return null;
        }

        @Override
        public Either<ServiceFailure, FormInputResponse> deleteFormInputResponseFileUpload(FormInputResponseFileEntryId fileEntryId) {
            return null;
        }

        @Override
        public ApplicationResourceHateoas getApplicationByIdHateoas(Long id) {
            return null;
        }

        @Override
        public Resources<ApplicationResourceHateoas> findAllHateoas() {
            return null;
        }

        @Override
        public ApplicationResource getApplicationById(Long id) {
            return null;
        }

        @Override
        public List<ApplicationResource> findAll() {
            return null;
        }

        @Override
        public List<ApplicationResource> findByUserId(Long userId) {
            return null;
        }

        @Override
        public ResponseEntity<String> saveApplicationDetails(Long id, ApplicationResource application) {
            return null;
        }

        @Override
        public ObjectNode getProgressPercentageByApplicationId(Long applicationId) {
            return null;
        }

        @Override
        public ResponseEntity<String> updateApplicationStatus(Long id, Long statusId) {
            return null;
        }

        @Override
        public List<ApplicationResource> getApplicationsByCompetitionIdAndUserId(Long competitionId, Long userId, UserRoleType role) {
            return null;
        }

        @Override
        public ApplicationResource createApplicationByApplicationNameForUserIdAndCompetitionId(Long competitionId, Long userId, JsonNode jsonObj) {
            return null;
        }
    }
}
