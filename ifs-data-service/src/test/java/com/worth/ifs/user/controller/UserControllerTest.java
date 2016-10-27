package com.worth.ifs.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.worth.ifs.BaseControllerMockMVCTest;
import com.worth.ifs.commons.error.Error;
import com.worth.ifs.token.domain.Token;
import com.worth.ifs.user.domain.User;
import com.worth.ifs.user.resource.*;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static com.worth.ifs.commons.error.CommonErrors.notFoundError;
import static com.worth.ifs.commons.error.CommonFailureKeys.USERS_EMAIL_VERIFICATION_TOKEN_EXPIRED;
import static com.worth.ifs.commons.service.ServiceResult.serviceFailure;
import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;
import static com.worth.ifs.token.resource.TokenType.VERIFY_EMAIL_ADDRESS;
import static com.worth.ifs.user.builder.AffiliationResourceBuilder.newAffiliationResource;
import static com.worth.ifs.user.builder.ProfileContractResourceBuilder.newProfileContractResource;
import static com.worth.ifs.user.builder.ProfileSkillsResourceBuilder.newProfileSkillsResource;
import static com.worth.ifs.user.builder.UserProfileResourceBuilder.newUserProfileResource;
import static com.worth.ifs.user.builder.UserResourceBuilder.newUserResource;
import static com.worth.ifs.user.controller.UserController.URL_PASSWORD_RESET;
import static com.worth.ifs.user.controller.UserController.URL_VERIFY_EMAIL;
import static com.worth.ifs.user.resource.UserStatus.INACTIVE;
import static com.worth.ifs.util.JsonMappingUtil.toJson;
import static java.time.LocalDateTime.now;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class UserControllerTest extends BaseControllerMockMVCTest<UserController> {

    @Override
    protected UserController supplyControllerUnderTest() {
        return new UserController();
    }

    @Test
    public void resendEmailVerificationNotification() throws Exception {
        final String emailAddress = "sample@me.com";

        final UserResource userResource = newUserResource().build();

        when(userServiceMock.findInactiveByEmail(emailAddress)).thenReturn(serviceSuccess(userResource));
        when(registrationServiceMock.resendUserVerificationEmail(userResource)).thenReturn(serviceSuccess());

        mockMvc.perform(put("/user/resendEmailVerificationNotification/{emailAddress}/", emailAddress))
                .andExpect(status().isOk());

        verify(registrationServiceMock, only()).resendUserVerificationEmail(userResource);
    }

    @Test
    public void resendEmailVerificationNotification_notFound() throws Exception {
        final String emailAddress = "sample@me.com";

        when(userServiceMock.findInactiveByEmail(emailAddress)).thenReturn(serviceFailure(notFoundError(User.class, emailAddress, INACTIVE)));

        mockMvc.perform(put("/user/resendEmailVerificationNotification/{emailAddress}/", emailAddress))
                .andExpect(status().isNotFound());
    }

    @Test
    public void createUser() throws Exception {
        final Long organisationId = 9999L;

        final UserResource userResource = newUserResource().build();
        when(registrationServiceMock.createOrganisationUser(organisationId, userResource)).thenReturn(serviceSuccess(userResource));
        when(registrationServiceMock.sendUserVerificationEmail(userResource, empty())).thenReturn(serviceSuccess());

        mockMvc.perform(post("/user/createLeadApplicantForOrganisation/{organisationId}",organisationId)
                .contentType(APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(userResource)))
                .andExpect(status().isCreated())
                .andExpect(content().string(new ObjectMapper().writeValueAsString(userResource)));

        verify(registrationServiceMock, times(1)).createOrganisationUser(organisationId, userResource);
        verify(registrationServiceMock, times(1)).sendUserVerificationEmail(userResource, empty());
        verifyNoMoreInteractions(registrationServiceMock);
    }

    @Test
    public void createUserWithCompetitionId() throws Exception {
        final Long organisationId = 9999L;
        final Long competitionId = 8888L;

        final UserResource userResource = newUserResource().build();
        when(registrationServiceMock.createOrganisationUser(organisationId, userResource)).thenReturn(serviceSuccess(userResource));
        when(registrationServiceMock.sendUserVerificationEmail(userResource, of(competitionId))).thenReturn(serviceSuccess());

        mockMvc.perform(post("/user/createLeadApplicantForOrganisation/{organisationId}/{competitionId}",organisationId, competitionId)
                .contentType(APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(userResource)))
                .andExpect(status().isCreated())
                .andExpect(content().string(new ObjectMapper().writeValueAsString(userResource)));

        verify(registrationServiceMock, times(1)).createOrganisationUser(organisationId, userResource);
        verify(registrationServiceMock, times(1)).sendUserVerificationEmail(userResource, of(competitionId));
        verifyNoMoreInteractions(registrationServiceMock);
    }

    @Test
    public void userControllerShouldReturnAllUsers() throws Exception {
        UserResource testUser1 = newUserResource().withId(1L).withFirstName("test").withLastName("User1").withEmail("email1@email.nl").build();
        UserResource testUser2 = newUserResource().withId(2L).withFirstName("test").withLastName("User2").withEmail("email2@email.nl").build();
        UserResource testUser3 = newUserResource().withId(3L).withFirstName("test").withLastName("User3").withEmail("email3@email.nl").build();

        List<UserResource> users = new ArrayList<>();
        users.add(testUser1);
        users.add(testUser2);
        users.add(testUser3);

        when(userServiceMock.findAll()).thenReturn(serviceSuccess(users));
        mockMvc.perform(get("/user/findAll/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("[0]id", is((Number) testUser1.getId().intValue())))
                .andExpect(jsonPath("[0]firstName", is(testUser1.getFirstName())))
                .andExpect(jsonPath("[0]lastName", is(testUser1.getLastName())))
                .andExpect(jsonPath("[0]imageUrl", is(testUser1.getImageUrl())))
                .andExpect(jsonPath("[0]uid", is(testUser1.getUid())))
                .andExpect(jsonPath("[1]id", is((Number) testUser2.getId().intValue())))
                .andExpect(jsonPath("[1]firstName", is(testUser2.getFirstName())))
                .andExpect(jsonPath("[1]lastName", is(testUser2.getLastName())))
                .andExpect(jsonPath("[1]imageUrl", is(testUser2.getImageUrl())))
                .andExpect(jsonPath("[1]uid", is(testUser2.getUid())))
                .andExpect(jsonPath("[2]id", is((Number) testUser3.getId().intValue())))
                .andExpect(jsonPath("[2]firstName", is(testUser3.getFirstName())))
                .andExpect(jsonPath("[2]lastName", is(testUser3.getLastName())))
                .andExpect(jsonPath("[2]imageUrl", is(testUser3.getImageUrl())))
                .andExpect(jsonPath("[2]uid", is(testUser3.getUid())))
                .andDo(document("user/get-all-users"));
    }

    @Test
    public void userControllerShouldReturnUserById() throws Exception {
        UserResource testUser1 = newUserResource().withId(1L).withFirstName("test").withLastName("User1").withEmail("email1@email.nl").build();

        when(userServiceMock.getUserById(testUser1.getId())).thenReturn(serviceSuccess(testUser1));
        mockMvc.perform(get("/user/id/" + testUser1.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("id", is((Number) testUser1.getId().intValue())))
                .andExpect(jsonPath("firstName", is(testUser1.getFirstName())))
                .andExpect(jsonPath("lastName", is(testUser1.getLastName())))
                .andExpect(jsonPath("imageUrl", is(testUser1.getImageUrl())))
                .andExpect(jsonPath("uid", is(testUser1.getUid())))
                .andDo(document("user/get-user"));
    }

    @Test
    public void updatePassword() throws Exception {
        final String password = "Passw0rd";
        final String hash = "bf5b6392-1e08-4acc-b667-f0a16d6744de";
        when(userServiceMock.changePassword(hash, password)).thenReturn(serviceSuccess(null));
        mockMvc.perform(post("/user/" + URL_PASSWORD_RESET + "/{hash}", hash).content(password))
                .andExpect(status().isOk())
                .andExpect(content().string(""))
                .andDo(document("user/update-password",
                        pathParameters(
                                parameterWithName("hash").description("The hash to validate the legitimacy of the request")
                        )
                ));
    }

    @Test
    public void verifyEmail() throws Exception {
        final String hash = "8eda60ad3441ee883cc95417e2abaa036c308dd9eb19468fcc8597fb4cb167c32a7e5daf5e237385";
        final Long userId = 1L;
        final Token token = new Token(VERIFY_EMAIL_ADDRESS, User.class.getName(), userId, hash, now(), null);
        when(tokenServiceMock.getEmailToken(hash)).thenReturn(serviceSuccess((token)));
        when(registrationServiceMock.activateUser(1L)).thenReturn(serviceSuccess());
        mockMvc.perform(get("/user/" + URL_VERIFY_EMAIL + "/{hash}", hash))
                .andExpect(status().isOk())
                .andExpect(content().string(""))
                .andDo(document("user/verify-email",
                        pathParameters(
                                parameterWithName("hash").description("The hash to validate the legitimacy of the request")
                        ))
                );
    }


    @Test
    public void verifyEmailNotFound() throws Exception {
        final String hash = "5f415b7ec9e9cc497996e251294b1d6bccfebba8dfc708d87b52f1420c19507ab24683bd7e8f49a0";
        final Error error = notFoundError(Token.class, hash);
        when(tokenServiceMock.getEmailToken(hash)).thenReturn(serviceFailure(error));
        mockMvc.perform(get("/user/" + URL_VERIFY_EMAIL + "/{hash}", hash))
                .andExpect(status().isNotFound())
                .andExpect(contentError(error));
    }

    @Test
    public void verifyEmailExpired() throws Exception {
        final String hash = "5f415b7ec9e9cc497996e251294b1d6bccfebba8dfc708d87b52f1420c19507ab24683bd7e8f49a0";
        final Error error = new Error(USERS_EMAIL_VERIFICATION_TOKEN_EXPIRED);
        when(tokenServiceMock.getEmailToken(hash)).thenReturn(serviceFailure(error));
        mockMvc.perform(get("/user/" + URL_VERIFY_EMAIL + "/{hash}", hash))
                .andExpect(status().isBadRequest())
                .andExpect(contentError(error));
    }

    @Test
    public void updatePasswordTokenNotFound() throws Exception {
        final String password = "Passw0rd";
        final String hash = "bf5b6392-1e08-4acc-b667-f0a16d6744de";
        final Error error = notFoundError(Token.class, hash);
        when(userServiceMock.changePassword(hash, password)).thenReturn(serviceFailure(error));
        mockMvc.perform(post("/user/" + URL_PASSWORD_RESET + "/" + hash).content(password))
                .andExpect(status().isNotFound())
                .andExpect(contentError(error))
                .andDo(document("user/update-password-token-not-found"));
    }

    @Test
    public void userControllerShouldReturnUserByUid() throws Exception {
        UserResource testUser1 = newUserResource().withUID("aebr34-ab345g-234gae-agewg").withId(1L).withFirstName("test").withLastName("User1").withEmail("email1@email.nl").build();

        when(userServiceMock.getUserResourceByUid(testUser1.getUid())).thenReturn(serviceSuccess(testUser1));

        mockMvc.perform(get("/user/uid/" + testUser1.getUid()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("id", is((Number) testUser1.getId().intValue())))
                .andExpect(jsonPath("firstName", is(testUser1.getFirstName())))
                .andExpect(jsonPath("lastName", is(testUser1.getLastName())))
                .andExpect(jsonPath("imageUrl", is(testUser1.getImageUrl())))
                .andExpect(jsonPath("uid", is(testUser1.getUid())))
                .andDo(document("user/get-user-by-token"));
    }

    @Test
    public void userControllerShouldReturnListOfSingleUserWhenFoundByEmail() throws Exception {
        User user = new User();
        user.setEmail("testemail@email.email");
        user.setFirstName("testFirstName");
        user.setLastName("testLastName");
        user.setPhoneNumber("testPhoneNumber");
        user.setFirstName("testFirstName");
        user.setLastName("testLastName");
        user.setTitle("Mr");

        when(userServiceMock.findByEmail(user.getEmail())).thenReturn(serviceFailure(notFoundError(User.class, user.getEmail())));

        mockMvc.perform(get("/user/findByEmail/" + user.getEmail() + "/", "json")
                .contentType(APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void userControllerShouldReturnEmptyListWhenNoUserIsFoundByEmail() throws Exception {

        String email = "testemail@email.com";

        when(userServiceMock.findByEmail(email)).thenReturn(serviceFailure(notFoundError(User.class, email)));

        mockMvc.perform(get("/user/findByEmail/" + email + "/", "json")
                .contentType(APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void getProfileSkills() throws Exception {
        Long userId = 1L;
        ProfileSkillsResource profileSkills = newProfileSkillsResource().build();

        when(userProfileServiceMock.getProfileSkills(userId)).thenReturn(serviceSuccess(profileSkills));

        mockMvc.perform(get("/user/id/{id}/getProfileSkills", userId))
                .andExpect(status().isOk())
                .andExpect(content().string(toJson(profileSkills)));

        verify(userProfileServiceMock, only()).getProfileSkills(userId);
    }

    @Test
    public void updateProfileSkills() throws Exception {
        ProfileSkillsResource profileSkills = newProfileSkillsResource().build();
        Long userId = 1L;

        when(userProfileServiceMock.updateProfileSkills(userId, profileSkills)).thenReturn(serviceSuccess());

        mockMvc.perform(put("/user/id/{id}/updateProfileSkills", userId)
                .contentType(APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(profileSkills)))
                .andExpect(status().isOk());

        verify(userProfileServiceMock, only()).updateProfileSkills(userId, profileSkills);
    }

    @Test
    public void getProfileContract() throws Exception {
        Long userId = 1L;
        ProfileContractResource profileContract = newProfileContractResource().build();

        when(userProfileServiceMock.getProfileContract(userId)).thenReturn(serviceSuccess(profileContract));

        mockMvc.perform(get("/user/id/{id}/getProfileContract", userId))
                .andExpect(status().isOk())
                .andExpect(content().string(toJson(profileContract)));

        verify(userProfileServiceMock, only()).getProfileContract(userId);
    }

    @Test
    public void updateProfileContract() throws Exception {
        Long userId = 1L;

        when(userProfileServiceMock.updateProfileContract(userId)).thenReturn(serviceSuccess());

        mockMvc.perform(put("/user/id/{id}/updateProfileContract", userId))
                .andExpect(status().isOk());

        verify(userProfileServiceMock, only()).updateProfileContract(userId);
    }

    @Test
    public void getUserAffiliations() throws Exception {
        Long userId = 1L;
        List<AffiliationResource> affiliations = newAffiliationResource().build(2);

        when(userProfileServiceMock.getUserAffiliations(userId)).thenReturn(serviceSuccess(affiliations));

        mockMvc.perform(get("/user/id/{id}/getUserAffiliations", userId)
                .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(content().string(toJson(affiliations)));

        verify(userProfileServiceMock, only()).getUserAffiliations(userId);
    }

    @Test
    public void updateUserAffiliations() throws Exception {
        Long userId = 1L;
        List<AffiliationResource> affiliations = newAffiliationResource().build(2);

        when(userProfileServiceMock.updateUserAffiliations(userId, affiliations)).thenReturn(serviceSuccess());

        mockMvc.perform(put("/user/id/{id}/updateUserAffiliations", userId)
                .contentType(APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(affiliations)))
                .andExpect(status().isOk());

        verify(userProfileServiceMock, only()).updateUserAffiliations(userId, affiliations);
    }

    @Test
    public void getProfileAddress() throws Exception {
        Long userId = 1L;
        UserProfileResource profileDetails = newUserProfileResource().build();

        when(userProfileServiceMock.getUserProfile(userId)).thenReturn(serviceSuccess(profileDetails));

        mockMvc.perform(get("/user/id/{userId}/getUserProfile", userId)
                .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(toJson(profileDetails)));

        verify(userProfileServiceMock, only()).getUserProfile(userId);
    }

    @Test
    public void updateProfileAddress() throws Exception {
        UserProfileResource profileDetails = newUserProfileResource().build();
        Long userId = 1L;

        when(userProfileServiceMock.updateUserProfile(userId, profileDetails)).thenReturn(serviceSuccess());

        mockMvc.perform(put("/user/id/{userId}/updateUserProfile", userId)
                .contentType(APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(profileDetails)))
                .andExpect(status().isOk());

        verify(userProfileServiceMock, only()).updateUserProfile(userId, profileDetails);
    }
}