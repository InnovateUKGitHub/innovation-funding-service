package org.innovateuk.ifs.user.service;

import org.innovateuk.ifs.BaseRestServiceUnitTest;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.invite.resource.EditUserResource;
import org.innovateuk.ifs.registration.resource.InternalUserRegistrationResource;
import org.innovateuk.ifs.user.resource.*;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.util.LinkedMultiValueMap;

import java.util.List;

import static java.lang.String.format;
import static java.util.Arrays.asList;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.id;
import static org.innovateuk.ifs.commons.service.BaseRestService.buildPaginationUri;
import static org.innovateuk.ifs.commons.service.ParameterizedTypeReferences.userListType;
import static org.innovateuk.ifs.registration.builder.InternalUserRegistrationResourceBuilder.newInternalUserRegistrationResource;
import static org.innovateuk.ifs.user.builder.RoleResourceBuilder.newRoleResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.user.resource.Title.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;


public class UserRestServiceMocksTest extends BaseRestServiceUnitTest<UserRestServiceImpl> {

    private static final String usersUrl = "/user";
    private static final String processRoleRestURL = "/processrole";

    @Override
    protected UserRestServiceImpl registerRestServiceUnderTest() {
        UserRestServiceImpl userRestService = new UserRestServiceImpl();
        return userRestService;
    }

    @Test
    public void test_findAll() {
        UserResource user1 = new UserResource();
        UserResource user2 = new UserResource();

        List<UserResource> userList = asList(user1, user2);
        setupGetWithRestResultExpectations(usersUrl + "/findAll/", userListType(), userList);

        List<UserResource> users = service.findAll().getSuccessObject();
        assertEquals(2, users.size());
        assertEquals(user1, users.get(0));
        assertEquals(user2, users.get(1));
    }

    @Test
    public void findExistingUserByEmailShouldReturnUserResource() {
        UserResource userResource = newUserResource().withEmail("testemail@email.com").build();

        setupGetWithRestResultAnonymousExpectations(usersUrl + "/findByEmail/" + userResource.getEmail() + "/", UserResource.class, userResource);

        UserResource user = service.findUserByEmail(userResource.getEmail()).getSuccessObject();
        assertEquals(userResource, user);
    }

    @Test
    public void findingNonExistingUserByEmailShouldReturnEmptyList() {
        String email = "email@test.test";

        setupGetWithRestResultAnonymousExpectations(usersUrl + "/findByEmail/" + email + "/", UserResource.class, null, HttpStatus.NOT_FOUND);

        RestResult<UserResource> restResult = service.findUserByEmail(email);
        assertTrue(restResult.isFailure());
    }

    @Test
    public void searchingByEmptyUserEmailShouldReturnNull() {
        String email = "";
        RestResult<UserResource> restResult = service.findUserByEmail(email);
        assertTrue(restResult.isFailure());
        assertEquals(restResult.getStatusCode(), HttpStatus.NOT_FOUND);
    }

    @Test
    public void createLeadApplicantForOrganisation() {

        setLoggedInUser(null);

        UserResource userResource = newUserResource()
                .with(id(null))
                .withEmail("testemail@test.test")
                .withTitle(Mr)
                .withFirstName("testFirstName")
                .withLastName("testLastName")
                .withPassword("testPassword")
                .withPhoneNumber("1234567890")
                .withAllowMarketingEmails(true)
                .build();

        Long organisationId = 1L;

        setupPostWithRestResultAnonymousExpectations(usersUrl + "/createLeadApplicantForOrganisation/" + organisationId, UserResource.class, userResource, userResource, OK);

        UserResource receivedResource = service.createLeadApplicantForOrganisation(userResource.getFirstName(),
                userResource.getLastName(),
                userResource.getPassword(),
                userResource.getEmail(),
                userResource.getTitle() != null ? userResource.getTitle().toString() : null,
                userResource.getPhoneNumber(),
                userResource.getGender() != null ? userResource.getGender().toString() : null,
                userResource.getEthnicity(),
                userResource.getDisability() != null ? userResource.getDisability().toString() : null,
                organisationId,
                userResource.getAllowMarketingEmails()
        ).getSuccessObject();

        assertEquals(userResource, receivedResource);
    }

    @Test
    public void createLeadApplicantForOrganisationWithDiversity() {

        setLoggedInUser(null);

        UserResource userResource = newUserResource()
                .with(id(null))
                .withEmail("testemail@test.test")
                .withTitle(Mr)
                .withFirstName("testFirstName")
                .withLastName("testLastName")
                .withPassword("testPassword")
                .withPhoneNumber("1234567890")
                .withGender(Gender.MALE)
                .withEthnicity(2L)
                .withDisability(Disability.YES)
                .withAllowMarketingEmails(true)
                .build();

        Long organisationId = 1L;

        setupPostWithRestResultAnonymousExpectations(usersUrl + "/createLeadApplicantForOrganisation/" + organisationId, UserResource.class, userResource, userResource, OK);

        UserResource receivedResource = service.createLeadApplicantForOrganisation(userResource.getFirstName(),
                userResource.getLastName(),
                userResource.getPassword(),
                userResource.getEmail(),
                userResource.getTitle() != null ? userResource.getTitle().toString() : null,
                userResource.getPhoneNumber(),
                userResource.getGender() != null ? userResource.getGender().toString() : null,
                userResource.getEthnicity(),
                userResource.getDisability() != null ? userResource.getDisability().toString() : null,
                organisationId,
                userResource.getAllowMarketingEmails()
        ).getSuccessObject();

        assertEquals(userResource, receivedResource);
    }

    @Test
    public void createLeadApplicantForOrganisationWithCompetitionId() {
        setLoggedInUser(null);

        UserResource userResource = newUserResource()
                .with(id(null))
                .withEmail("testemail@test.test")
                .withTitle(Mrs)
                .withFirstName("testFirstName")
                .withLastName("testLastName")
                .withPassword("testPassword")
                .withPhoneNumber("1234567890")
                .withAllowMarketingEmails(true)
                .build();

        Long organisationId = 1L;
        Long competitionId = 1L;

        setupPostWithRestResultAnonymousExpectations(usersUrl + "/createLeadApplicantForOrganisation/" + organisationId + "/" + competitionId, UserResource.class, userResource, userResource, OK);

        UserResource receivedResource = service.createLeadApplicantForOrganisationWithCompetitionId(userResource.getFirstName(),
                userResource.getLastName(),
                userResource.getPassword(),
                userResource.getEmail(),
                userResource.getTitle() != null ? userResource.getTitle().toString() : null,
                userResource.getPhoneNumber(),
                userResource.getGender() != null ? userResource.getGender().toString() : "",
                userResource.getEthnicity(),
                userResource.getDisability() != null ? userResource.getDisability().toString() : "",
                organisationId,
                competitionId,
                userResource.getAllowMarketingEmails()
        ).getSuccessObject();

        assertEquals(userResource, receivedResource);
    }

    @Test
    public void createLeadApplicantForOrganisationWithCompetitionIdWithDiversity() {
        setLoggedInUser(null);

        UserResource userResource = newUserResource()
                .with(id(null))
                .withEmail("testemail@test.test")
                .withTitle(Miss)
                .withFirstName("testFirstName")
                .withLastName("testLastName")
                .withPassword("testPassword")
                .withPhoneNumber("1234567890")
                .withDisability(Disability.YES)
                .withEthnicity(2L)
                .withGender(Gender.FEMALE)
                .withAllowMarketingEmails(true)
                .build();

        Long organisationId = 1L;
        Long competitionId = 1L;

        setupPostWithRestResultAnonymousExpectations(usersUrl + "/createLeadApplicantForOrganisation/" + organisationId + "/" + competitionId, UserResource.class, userResource, userResource, OK);

        UserResource receivedResource = service.createLeadApplicantForOrganisationWithCompetitionId(userResource.getFirstName(),
                userResource.getLastName(),
                userResource.getPassword(),
                userResource.getEmail(),
                userResource.getTitle() != null ? userResource.getTitle().toString() : null,
                userResource.getPhoneNumber(),
                userResource.getGender() != null ? userResource.getGender().toString() : "",
                userResource.getEthnicity(),
                userResource.getDisability() != null ? userResource.getDisability().toString() : "",
                organisationId,
                competitionId,
                userResource.getAllowMarketingEmails()
        ).getSuccessObject();

        assertEquals(userResource, receivedResource);
    }

    @Test
    public void resendEmailVerificationNotification() {
        final String emailAddress = "sample@me.com";

        setupPutWithRestResultAnonymousExpectations(usersUrl + "/resendEmailVerificationNotification/" + emailAddress + "/", null, OK);
        final RestResult<Void> result = service.resendEmailVerificationNotification(emailAddress);
        assertTrue(result.isSuccess());
    }

    @Test
    public void userHasApplicationForCompetition() {
        Long userId = 1L;
        Long competitionId = 2L;
        Boolean expected = true;

        setupGetWithRestResultExpectations(format("%s/userHasApplicationForCompetition/%s/%s", processRoleRestURL, userId, competitionId), Boolean.class, expected, OK);

        Boolean response = service.userHasApplicationForCompetition(userId, competitionId).getSuccessObjectOrThrowException();
        assertEquals(expected, response);
    }

    @Test
    public void testGetActiveInternalUsers(){
        UserPageResource expected = new UserPageResource();

        setupGetWithRestResultExpectations(buildPaginationUri(usersUrl + "/internal/active", 0, 5, null, new LinkedMultiValueMap<>()), UserPageResource.class, expected, OK);

        UserPageResource result = service.getActiveInternalUsers(0, 5).getSuccessObjectOrThrowException();

        assertEquals(expected, result);
    }

    @Test
    public void testGetInactiveInternalUsers(){
        UserPageResource expected = new UserPageResource();

        setupGetWithRestResultExpectations(buildPaginationUri(usersUrl + "/internal/inactive", 0, 5, null, new LinkedMultiValueMap<>()), UserPageResource.class, expected, OK);

        UserPageResource result = service.getInactiveInternalUsers(0, 5).getSuccessObjectOrThrowException();

        assertEquals(expected, result);
    }

    @Test
    public void testCreateInternalUser(){
        setLoggedInUser(null);

        List<RoleResource> roleResources = newRoleResource().withType(UserRoleType.PROJECT_FINANCE).build(1);

        InternalUserRegistrationResource internalUserRegistrationResource = newInternalUserRegistrationResource()
                .withFirstName("First")
                .withLastName("Last")
                .withEmail("email@example.com")
                .withPassword("Passw0rd123")
                .withRoles(roleResources)
                .build();

        String inviteHash = "hash";

        setupPostWithRestResultAnonymousExpectations(usersUrl + "/internal/create/" + inviteHash, Void.class, internalUserRegistrationResource, null, CREATED);

        RestResult<Void> result = service.createInternalUser(inviteHash, internalUserRegistrationResource);

        assertTrue(result.isSuccess());

        assertEquals(CREATED, result.getStatusCode());
    }

    @Test
    public void editInternalUser() throws Exception {
        EditUserResource editUserResource = new EditUserResource();
        String url = usersUrl + "/internal/edit";
        setupPostWithRestResultExpectations(url, editUserResource, HttpStatus.OK);

        RestResult<Void> result = service.editInternalUser(editUserResource);
        assertTrue(result.isSuccess());
        assertEquals(OK, result.getStatusCode());
    }

    @Test
    public void deactivateUser() throws Exception {
        String url = usersUrl + "/id/123/deactivate";
        setupGetWithRestResultExpectations(url, Void.class, null);

        RestResult<Void> result = service.deactivateUser(123L);
        assertTrue(result.isSuccess());
        assertEquals(OK, result.getStatusCode());
    }

    @Test
    public void reactivateUser() throws Exception {
        String url = usersUrl + "/id/123/reactivate";
        setupGetWithRestResultExpectations(url, Void.class, null);

        RestResult<Void> result = service.reactivateUser(123L);
        assertTrue(result.isSuccess());
        assertEquals(OK, result.getStatusCode());
    }
}
