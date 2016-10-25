package com.worth.ifs.user.service;

import com.worth.ifs.BaseRestServiceUnitTest;
import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.user.resource.*;
import org.junit.Test;
import org.springframework.http.HttpStatus;

import java.util.List;

import static com.worth.ifs.BuilderAmendFunctions.id;
import static com.worth.ifs.commons.service.ParameterizedTypeReferences.affiliationResourceListType;
import static com.worth.ifs.commons.service.ParameterizedTypeReferences.userListType;
import static com.worth.ifs.user.builder.AffiliationResourceBuilder.newAffiliationResource;
import static com.worth.ifs.user.builder.ProfileContractResourceBuilder.newProfileContractResource;
import static com.worth.ifs.user.builder.ProfileSkillsResourceBuilder.newProfileSkillsResource;
import static com.worth.ifs.user.builder.UserProfileResourceBuilder.newUserProfileResource;
import static com.worth.ifs.user.builder.UserResourceBuilder.newUserResource;
import static java.lang.String.format;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.springframework.http.HttpStatus.OK;


public class UserRestServiceMocksTest extends BaseRestServiceUnitTest<UserRestServiceImpl> {

    private static final String usersUrl = "/user";

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

        setupGetWithRestResultExpectations(usersUrl + "/findByEmail/" + userResource.getEmail() + "/", UserResource.class, userResource);

        UserResource user = service.findUserByEmail(userResource.getEmail()).getSuccessObject();
        assertEquals(userResource, user);
    }

    @Test
    public void findingNonExistingUserByEmailShouldReturnEmptyList() {
        String email = "email@test.test";

        setupGetWithRestResultExpectations(usersUrl + "/findByEmail/" + email + "/", UserResource.class, null, HttpStatus.NOT_FOUND);

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
                .withTitle("testTitle")
                .withFirstName("testFirstName")
                .withLastName("testLastName")
                .withPassword("testPassword")
                .withPhoneNumber("1234567890")
                .build();

        Long organisationId = 1L;

        setupPostWithRestResultAnonymousExpectations(usersUrl + "/createLeadApplicantForOrganisation/" + organisationId, UserResource.class, userResource, userResource, OK);

        UserResource receivedResource = service.createLeadApplicantForOrganisation(userResource.getFirstName(),
                userResource.getLastName(),
                userResource.getPassword(),
                userResource.getEmail(),
                userResource.getTitle(),
                userResource.getPhoneNumber(),
                organisationId
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
    public void getProfileSkills() {
        Long userId = 1L;
        ProfileSkillsResource expected = newProfileSkillsResource().build();

        setupGetWithRestResultExpectations(format("%s/id/%s/getProfileSkills", usersUrl, userId), ProfileSkillsResource.class, expected, OK);

        ProfileSkillsResource response = service.getProfileSkills(userId).getSuccessObjectOrThrowException();
        assertEquals(expected, response);
    }

    @Test
    public void updateProfileSkills() {
        Long userId = 1L;
        ProfileSkillsResource profileSkills = newProfileSkillsResource().build();

        setupPutWithRestResultExpectations(format("%s/id/%s/updateProfileSkills", usersUrl, userId), profileSkills, OK);

        RestResult<Void> response = service.updateProfileSkills(userId, profileSkills);
        assertTrue(response.isSuccess());
    }

    @Test
    public void getProfileContract() {
        Long userId = 1L;
        ProfileContractResource expected = newProfileContractResource().build();

        setupGetWithRestResultExpectations(format("%s/id/%s/getProfileContract", usersUrl, userId), ProfileContractResource.class, expected, OK);

        ProfileContractResource response = service.getProfileContract(userId).getSuccessObjectOrThrowException();
        assertEquals(expected, response);
    }


    @Test
    public void updateProfileContract() {
        Long userId = 1L;

        setupPutWithRestResultExpectations(format("%s/id/%s/updateProfileContract", usersUrl, userId), null, OK);

        RestResult<Void> response = service.updateProfileContract(userId);
        assertTrue(response.isSuccess());
    }

    @Test
    public void getUserAffiliations() {
        Long userId = 1L;
        List<AffiliationResource> expected = newAffiliationResource().build(2);

        setupGetWithRestResultExpectations(format("%s/id/%s/getUserAffiliations", usersUrl, userId), affiliationResourceListType(), expected, OK);

        List<AffiliationResource> response = service.getUserAffiliations(userId).getSuccessObjectOrThrowException();
        assertEquals(expected, response);
    }

    @Test
    public void updateUserAffiliations() {
        Long userId = 1L;
        List<AffiliationResource> expected = newAffiliationResource().build(2);

        setupPutWithRestResultExpectations(format("%s/id/%s/updateUserAffiliations", usersUrl, userId), expected, OK);

        RestResult<Void> response = service.updateUserAffiliations(userId, expected);
        assertTrue(response.isSuccess());
    }

    @Test
    public void getProfileAddress() {
        Long userId = 1L;
        UserProfileResource expected = newUserProfileResource().build();

        setupGetWithRestResultExpectations(format("%s/id/%s/getUserProfile", usersUrl, userId), UserProfileResource.class, expected, OK);

        UserProfileResource response = service.getProfileDetails(userId).getSuccessObjectOrThrowException();
        assertEquals(expected, response);
    }

    @Test
    public void updateProfileAddress() {
        Long userId = 1L;
        UserProfileResource profileDetails = newUserProfileResource().build();

        setupPutWithRestResultExpectations(format("%s/id/%s/updateUserProfile", usersUrl, userId), profileDetails, OK);

        RestResult<Void> response = service.updateProfileDetails(userId, profileDetails);
        assertTrue(response.isSuccess());
    }

}
