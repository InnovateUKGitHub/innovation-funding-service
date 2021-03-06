package org.innovateuk.ifs.user.service;

import org.innovateuk.ifs.BaseRestServiceUnitTest;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.invite.resource.EditUserResource;
import org.innovateuk.ifs.registration.resource.InternalUserRegistrationResource;
import org.innovateuk.ifs.user.resource.*;
import org.junit.Test;
import org.springframework.http.HttpStatus;

import java.util.List;

import static java.lang.String.format;
import static java.util.Arrays.asList;
import static org.innovateuk.ifs.commons.service.ParameterizedTypeReferences.*;
import static org.innovateuk.ifs.registration.builder.InternalUserRegistrationResourceBuilder.newInternalUserRegistrationResource;
import static org.innovateuk.ifs.user.builder.UserOrganisationResourceBuilder.newUserOrganisationResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;


public class UserRestServiceMocksTest extends BaseRestServiceUnitTest<UserRestServiceImpl> {

    private static final String USERS_URL = "/user";

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
        setupGetWithRestResultExpectations(USERS_URL + "/find-all/", userListType(), userList);

        List<UserResource> users = service.findAll().getSuccess();
        assertEquals(2, users.size());
        assertEquals(user1, users.get(0));
        assertEquals(user2, users.get(1));
    }

    @Test
    public void findExistingUserByEmailShouldReturnUserResource() {
        UserResource userResource = newUserResource().withEmail("testemail@email.com").build();

        setupGetWithRestResultAnonymousExpectations(USERS_URL + "/find-by-email/" + userResource.getEmail() + "/", UserResource.class, userResource);

        UserResource user = service.findUserByEmail(userResource.getEmail()).getSuccess();
        assertEquals(userResource, user);
    }

    @Test
    public void findingNonExistingUserByEmailShouldReturnEmptyList() {
        String email = "email@test.test";

        setupGetWithRestResultAnonymousExpectations(USERS_URL + "/find-by-email/" + email + "/", UserResource.class, null, HttpStatus.NOT_FOUND);

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
    public void resendEmailVerificationNotification() {
        final String emailAddress = "sample@me.com";

        setupPutWithRestResultAnonymousExpectations(USERS_URL + "/resend-email-verification-notification/" + emailAddress + "/", null, OK);
        final RestResult<Void> result = service.resendEmailVerificationNotification(emailAddress);
        assertTrue(result.isSuccess());
    }

    @Test
    public void testCreateInternalUser() {
        setLoggedInUser(null);

        InternalUserRegistrationResource internalUserRegistrationResource = newInternalUserRegistrationResource()
                .withFirstName("First")
                .withLastName("Last")
                .withPassword("Passw0rd1357123")
                .build();

        String inviteHash = "hash";

        setupPostWithRestResultAnonymousExpectations(USERS_URL + "/internal/create/" + inviteHash, Void.class, internalUserRegistrationResource, null, CREATED);

        RestResult<Void> result = service.createInternalUser(inviteHash, internalUserRegistrationResource);

        assertTrue(result.isSuccess());

        assertEquals(CREATED, result.getStatusCode());
    }

    @Test
    public void editInternalUser() throws Exception {
        EditUserResource editUserResource = new EditUserResource();
        String url = USERS_URL + "/internal/edit";
        setupPostWithRestResultExpectations(url, editUserResource, HttpStatus.OK);

        RestResult<Void> result = service.editInternalUser(editUserResource);
        assertTrue(result.isSuccess());
        assertEquals(OK, result.getStatusCode());
    }

    @Test
    public void deactivateUser() throws Exception {
        String url = USERS_URL + "/id/123/deactivate";
        setupPostWithRestResultExpectations(url, OK);

        RestResult<Void> result = service.deactivateUser(123L);
        assertTrue(result.isSuccess());
        assertEquals(OK, result.getStatusCode());
    }

    @Test
    public void reactivateUser() throws Exception {
        String url = USERS_URL + "/id/123/reactivate";
        setupPostWithRestResultExpectations(url, OK);

        RestResult<Void> result = service.reactivateUser(123L);
        assertTrue(result.isSuccess());
        assertEquals(OK, result.getStatusCode());
    }

    @Test
    public void findExternalUsers() throws Exception {

        String searchString = "%aar%";
        SearchCategory searchCategory = SearchCategory.NAME;
        String url = USERS_URL + "/find-external-users?searchString=" + searchString + "&searchCategory=" + searchCategory.name();

        List<UserOrganisationResource> userOrganisationResources = newUserOrganisationResource().build(2);
        setupGetWithRestResultExpectations(url, userOrganisationListType(), userOrganisationResources);

        RestResult<List<UserOrganisationResource>> result = service.findExternalUsers(searchString, searchCategory);

        assertTrue(result.isSuccess());
        assertEquals(OK, result.getStatusCode());
        assertEquals(userOrganisationResources, result.getSuccess());
    }

    @Test
    public void agreeNewSiteTermsAndConditions() {
        long userId = 1L;
        setupPostWithRestResultExpectations(format("%s/id/%s/agree-new-site-terms-and-conditions", USERS_URL, userId),
                HttpStatus.OK);
        assertTrue(service.agreeNewSiteTermsAndConditions(userId).isSuccess());
    }

    @Test
    public void grantRole() {
        long userId = 1L;
        Role role = Role.APPLICANT;
        setupPostWithRestResultExpectations(format("%s/%s/grant/%s", USERS_URL, userId, role.name()),
                HttpStatus.OK);
        assertTrue(service.grantRole(userId, role).isSuccess());
    }
}