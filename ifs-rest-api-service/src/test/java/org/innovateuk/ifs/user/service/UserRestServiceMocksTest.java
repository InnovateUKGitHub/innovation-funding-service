package org.innovateuk.ifs.user.service;

import org.innovateuk.ifs.BaseRestServiceUnitTest;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.user.resource.*;
import org.junit.Test;
import org.springframework.http.HttpStatus;

import java.util.List;

import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.id;
import static org.innovateuk.ifs.commons.service.ParameterizedTypeReferences.affiliationResourceListType;
import static org.innovateuk.ifs.commons.service.ParameterizedTypeReferences.userListType;
import static org.innovateuk.ifs.user.builder.AffiliationResourceBuilder.newAffiliationResource;
import static org.innovateuk.ifs.user.builder.ProfileAgreementResourceBuilder.newProfileAgreementResource;
import static org.innovateuk.ifs.user.builder.ProfileSkillsEditResourceBuilder.newProfileSkillsEditResource;
import static org.innovateuk.ifs.user.builder.ProfileSkillsResourceBuilder.newProfileSkillsResource;
import static org.innovateuk.ifs.user.builder.UserProfileResourceBuilder.newUserProfileResource;
import static org.innovateuk.ifs.user.builder.UserProfileStatusResourceBuilder.newUserProfileStatusResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static java.lang.String.format;
import static java.util.Arrays.asList;
import static org.innovateuk.ifs.user.resource.Title.Miss;
import static org.innovateuk.ifs.user.resource.Title.Mr;
import static org.innovateuk.ifs.user.resource.Title.Mrs;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
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
        ProfileSkillsEditResource profileSkillsEditResource = newProfileSkillsEditResource().build();

        setupPutWithRestResultExpectations(format("%s/id/%s/updateProfileSkills", usersUrl, userId), profileSkillsEditResource, OK);

        RestResult<Void> response = service.updateProfileSkills(userId, profileSkillsEditResource);
        assertTrue(response.isSuccess());
    }

    @Test
    public void getProfileAgreement() {
        Long userId = 1L;
        ProfileAgreementResource expected = newProfileAgreementResource().build();

        setupGetWithRestResultExpectations(format("%s/id/%s/getProfileAgreement", usersUrl, userId), ProfileAgreementResource.class, expected, OK);

        ProfileAgreementResource response = service.getProfileAgreement(userId).getSuccessObjectOrThrowException();
        assertEquals(expected, response);
    }


    @Test
    public void updateProfileAgreement() {
        Long userId = 1L;

        setupPutWithRestResultExpectations(format("%s/id/%s/updateProfileAgreement", usersUrl, userId), null, OK);

        RestResult<Void> response = service.updateProfileAgreement(userId);
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

        UserProfileResource response = service.getUserProfile(userId).getSuccessObjectOrThrowException();
        assertEquals(expected, response);
    }

    @Test
    public void updateProfileAddress() {
        Long userId = 1L;
        UserProfileResource profileDetails = newUserProfileResource().build();

        setupPutWithRestResultExpectations(format("%s/id/%s/updateUserProfile", usersUrl, userId), profileDetails, OK);

        RestResult<Void> response = service.updateUserProfile(userId, profileDetails);
        assertTrue(response.isSuccess());
    }

    @Test
    public void getProfileStatus() {
        Long userId = 1L;
        UserProfileStatusResource expected = newUserProfileStatusResource().build();

        setupGetWithRestResultExpectations(format("%s/id/%s/profileStatus", usersUrl, userId), UserProfileStatusResource.class, expected, OK);

        UserProfileStatusResource response = service.getUserProfileStatus(userId).getSuccessObjectOrThrowException();
        assertEquals(expected, response);
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
}
