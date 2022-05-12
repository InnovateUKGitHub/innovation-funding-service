package org.innovateuk.ifs.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.commons.lang3.StringUtils;
import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.activitylog.transactional.SILMessagingService;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.security.UserAuthenticationService;
import org.innovateuk.ifs.crm.transactional.CrmService;
import org.innovateuk.ifs.invite.resource.EditUserResource;
import org.innovateuk.ifs.invite.transactional.ApplicationInviteServiceImpl;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.registration.resource.InternalUserRegistrationResource;
import org.innovateuk.ifs.sil.SIlPayloadKeyType;
import org.innovateuk.ifs.sil.SIlPayloadType;
import org.innovateuk.ifs.sil.crm.resource.SilEDIStatus;
import org.innovateuk.ifs.token.domain.Token;
import org.innovateuk.ifs.token.transactional.TokenService;
import org.innovateuk.ifs.user.command.GrantRoleCommand;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.resource.*;
import org.innovateuk.ifs.user.transactional.BaseUserService;
import org.innovateuk.ifs.user.transactional.RegistrationServiceImpl;
import org.innovateuk.ifs.user.transactional.UserService;
import org.innovateuk.ifs.util.TimeMachine;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.LinkedMultiValueMap;

import java.math.BigDecimal;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import static java.time.ZonedDateTime.now;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.core.Is.is;
import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.*;
import static org.innovateuk.ifs.commons.service.BaseRestService.buildPaginationUri;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.registration.builder.InternalUserRegistrationResourceBuilder.newInternalUserRegistrationResource;
import static org.innovateuk.ifs.token.resource.TokenType.VERIFY_EMAIL_ADDRESS;
import static org.innovateuk.ifs.user.builder.UserBuilder.newUser;
import static org.innovateuk.ifs.user.builder.UserOrganisationResourceBuilder.newUserOrganisationResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.user.resource.Title.Mr;
import static org.innovateuk.ifs.user.resource.UserCreationResource.UserCreationResourceBuilder.anUserCreationResource;
import static org.innovateuk.ifs.user.resource.UserRelatedURLs.URL_PASSWORD_RESET;
import static org.innovateuk.ifs.user.resource.UserRelatedURLs.URL_VERIFY_EMAIL;
import static org.innovateuk.ifs.user.resource.UserStatus.INACTIVE;
import static org.innovateuk.ifs.util.JsonMappingUtil.toJson;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class UserControllerTest extends BaseControllerMockMVCTest<UserController> {

    @Mock
    private UserService userService;
    @Mock
    private UserAuthenticationService userAuthenticationService;
    @Mock
    private RegistrationServiceImpl registrationService;
    @Mock
    private BaseUserService baseuserService;
    @Mock
    private TokenService tokenService;
    @Mock
    private CrmService crmService;

    @Override
    protected UserController supplyControllerUnderTest() {
        return new UserController();
    }

    private final SilEDIStatus silStatus = new SilEDIStatus();
    private UserResource user;
    @Mock
    private SILMessagingService silMessagingService;

    @Mock
    private ObjectMapper mapper;

    @Before
    public void setup() {
        user = newUserResource().withId(1L).build();
        silStatus.setEdiStatus(EDIStatus.INPROGRESS);
        silStatus.setEdiReviewDate(ZonedDateTime.now(ZoneId.of("UTC")));
        when(userService.updateDetails(user)).thenReturn(serviceSuccess(user));
        when(mapper.writer()).thenReturn(new ObjectMapper().writer());

        doNothing().when(silMessagingService).recordSilMessage(SIlPayloadType.APPLICATION_UPDATE, SIlPayloadKeyType.APPLICATION_ID, "1", "", null);

    }

    @Test
    public void resendEmailVerificationNotification() throws Exception {
        final String emailAddress = "sample@me.com";

        final UserResource userResource = newUserResource().build();

        when(userService.findInactiveByEmail(emailAddress)).thenReturn(serviceSuccess(userResource));
        when(registrationService.resendUserVerificationEmail(userResource)).thenReturn(serviceSuccess());

        mockMvc.perform(put("/user/resend-email-verification-notification/{emailAddress}/", emailAddress))
                .andExpect(status().isOk());

        verify(registrationService, only()).resendUserVerificationEmail(userResource);
    }

    @Test
    public void resendEmailVerificationNotification_notFound() throws Exception {
        final String emailAddress = "sample@me.com";

        when(userService.findInactiveByEmail(emailAddress)).thenReturn(serviceFailure(notFoundError(User.class, emailAddress, INACTIVE)));

        mockMvc.perform(put("/user/resend-email-verification-notification/{emailAddress}/", emailAddress))
                .andExpect(status().isNotFound());
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

        when(baseuserService.findAll()).thenReturn(serviceSuccess(users));
        mockMvc.perform(get("/user/find-all/")
                        .header("IFS_AUTH_TOKEN", "123abc"))
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
                .andExpect(jsonPath("[2]uid", is(testUser3.getUid())));
    }

    @Test
    public void userControllerShouldReturnUserById() throws Exception {
        UserResource testUser1 = newUserResource().withId(1L).withFirstName("test").withLastName("User1").withEmail("email1@email.nl").build();

        when(baseuserService.getUserById(testUser1.getId())).thenReturn(serviceSuccess(testUser1));
        mockMvc.perform(get("/user/id/" + testUser1.getId())
                        .header("IFS_AUTH_TOKEN", "123abc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("id", is((Number) testUser1.getId().intValue())))
                .andExpect(jsonPath("firstName", is(testUser1.getFirstName())))
                .andExpect(jsonPath("lastName", is(testUser1.getLastName())))
                .andExpect(jsonPath("imageUrl", is(testUser1.getImageUrl())))
                .andExpect(jsonPath("uid", is(testUser1.getUid())));
    }

    @Test
    public void updatePassword() throws Exception {
        final String password = "Passw0rd1357";
        final String hash = "bf5b6392-1e08-4acc-b667-f0a16d6744de";
        when(userService.changePassword(hash, password)).thenReturn(serviceSuccess(null));
        mockMvc.perform(post("/user/" + URL_PASSWORD_RESET + "/{hash}", hash).content(password)
                        .header("IFS_AUTH_TOKEN", "123abc"))
                .andExpect(status().isOk())
                .andExpect(content().string(""));
    }

    @Test
    public void verifyEmailWithApplicationExtraAttributes() throws Exception {
        final String hash = "8eda60ad3441ee883cc95417e2abaa036c308dd9eb19468fcc8597fb4cb167c32a7e5daf5e237385";
        final Long userId = 1L;
        final Long appId = 1L;
        final Long compId = 1L;

        ObjectNode node = JsonNodeFactory.instance.objectNode();
        node.put("inviteId", 111L);
        final Token token = new Token(VERIFY_EMAIL_ADDRESS, User.class.getName(), userId, hash, now(), node);
        when(tokenService.getEmailToken(hash)).thenReturn(serviceSuccess((token)));

        ApplicationResource applicationResource = new ApplicationResource();
        applicationResource.setCompetition(compId);
        applicationResource.setId(appId);

        when(crmService.syncCrmContact(userId, appId, compId)).thenReturn(serviceSuccess());
        when(tokenService.handleApplicationExtraAttributes(any())).thenReturn(serviceSuccess((applicationResource)));
        when(registrationService.activateApplicantAndSendDiversitySurvey(anyLong(), anyLong())).thenReturn(serviceSuccess());

        mockMvc.perform(get("/user/" + URL_VERIFY_EMAIL + "/{hash}", hash)
                        .header("IFS_AUTH_TOKEN", "123abc"))
                .andExpect(status().isOk())
                .andExpect(content().string(""));

        verify(crmService).syncCrmContact(userId, appId, compId);
    }

    @Test
    public void verifyEmailWithProjectExtraAttributes() throws Exception {
        final String hash = "8eda60ad3441ee883cc95417e2abaa036c308dd9eb19468fcc8597fb4cb167c32a7e5daf5e237385";
        final Long userId = 1L;
        final Long projectId = 1L;
        final Long compId = 1L;

        ObjectNode node = JsonNodeFactory.instance.objectNode();
        node.put("inviteId", 111L);
        final Token token = new Token(VERIFY_EMAIL_ADDRESS, User.class.getName(), userId, hash, now(), node);
        when(tokenService.getEmailToken(hash)).thenReturn(serviceSuccess((token)));

        ProjectResource projectResource = new ProjectResource();
        projectResource.setId(projectId);

        when(crmService.syncCrmContact(userId, projectId)).thenReturn(serviceSuccess());
        when(tokenService.handleProjectExtraAttributes(token)).thenReturn(serviceSuccess(projectResource));
        when(tokenService.handleApplicationExtraAttributes(any())).thenReturn(serviceFailure(GENERAL_NOT_FOUND));
        when(registrationService.activateApplicantAndSendDiversitySurvey(anyLong(), anyLong())).thenReturn(serviceSuccess());

        mockMvc.perform(get("/user/" + URL_VERIFY_EMAIL + "/{hash}", hash)
                        .header("IFS_AUTH_TOKEN", "123abc"))
                .andExpect(status().isOk())
                .andExpect(content().string(""));

        verify(crmService).syncCrmContact(userId, projectId);
    }

    @Test
    public void verifyEmailWithoutExtraAttributes() throws Exception {
        final String hash = "8eda60ad3441ee883cc95417e2abaa036c308dd9eb19468fcc8597fb4cb167c32a7e5daf5e237385";
        final Long userId = 1L;
        ObjectNode node = JsonNodeFactory.instance.objectNode();
        node.put("inviteId", 111L);
        final Token token = new Token(VERIFY_EMAIL_ADDRESS, User.class.getName(), userId, hash, now(), node);

        when(tokenService.getEmailToken(hash)).thenReturn(serviceSuccess((token)));

        when(crmService.syncCrmContact(userId)).thenReturn(serviceSuccess());
        when(tokenService.handleApplicationExtraAttributes(token)).thenReturn(serviceFailure(PROJECT_CANNOT_BE_WITHDRAWN));
        when(tokenService.handleProjectExtraAttributes(token)).thenReturn(serviceFailure(GENERAL_NOT_FOUND));
        when(registrationService.activateApplicantAndSendDiversitySurvey(anyLong(), anyLong())).thenReturn(serviceSuccess());
        mockMvc.perform(get("/user/" + URL_VERIFY_EMAIL + "/{hash}", hash)
                        .header("IFS_AUTH_TOKEN", "123abc"))
                .andExpect(status().isOk())
                .andExpect(content().string(""));

        verify(crmService).syncCrmContact(userId);
    }

    @Test
    public void verifyEmailNotFound() throws Exception {
        final String hash = "5f415b7ec9e9cc497996e251294b1d6bccfebba8dfc708d87b52f1420c19507ab24683bd7e8f49a0";
        final Error error = notFoundError(Token.class, hash);
        when(tokenService.getEmailToken(hash)).thenReturn(serviceFailure(error));
        mockMvc.perform(get("/user/" + URL_VERIFY_EMAIL + "/{hash}", hash))
                .andExpect(status().isNotFound())
                .andExpect(contentError(error));
    }

    @Test
    public void verifyEmailExpired() throws Exception {
        final String hash = "5f415b7ec9e9cc497996e251294b1d6bccfebba8dfc708d87b52f1420c19507ab24683bd7e8f49a0";
        final Error error = new Error(USERS_EMAIL_VERIFICATION_TOKEN_EXPIRED);
        when(tokenService.getEmailToken(hash)).thenReturn(serviceFailure(error));
        mockMvc.perform(get("/user/" + URL_VERIFY_EMAIL + "/{hash}", hash))
                .andExpect(status().isBadRequest())
                .andExpect(contentError(error));
    }

    @Test
    public void updatePasswordTokenNotFound() throws Exception {
        final String password = "Passw0rd1357";
        final String hash = "bf5b6392-1e08-4acc-b667-f0a16d6744de";
        final Error error = notFoundError(Token.class, hash);
        when(userService.changePassword(hash, password)).thenReturn(serviceFailure(error));
        mockMvc.perform(post("/user/" + URL_PASSWORD_RESET + "/" + hash).content(password)
                        .header("IFS_AUTH_TOKEN", "123abc"))
                .andExpect(status().isNotFound())
                .andExpect(contentError(error));
    }

    @Test
    public void userControllerShouldReturnUserByUid() throws Exception {
        ZonedDateTime fixedClock = ZonedDateTime.parse("2021-10-12T00:00:00.0Z");
        TimeMachine.useFixedClockAt(fixedClock);

        UserResource testUser1 = newUserResource().withUid("aebr34-ab345g-234gae-agewg").withId(1L)
                .withFirstName("test").withLastName("User1").withEmail("email1@email.nl").withEdiStatus(EDIStatus.COMPLETE)
                .withEdiStatusReviewDate(fixedClock).build();


        when(baseuserService.getUserResourceByUid(testUser1.getUid())).thenReturn(serviceSuccess(testUser1));

        mockMvc.perform(get("/user/uid/" + testUser1.getUid())
                        .header("IFS_AUTH_TOKEN", "123abc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("id", is((Number) testUser1.getId().intValue())))
                .andExpect(jsonPath("firstName", is(testUser1.getFirstName())))
                .andExpect(jsonPath("lastName", is(testUser1.getLastName())))
                .andExpect(jsonPath("imageUrl", is(testUser1.getImageUrl())))
                .andExpect(jsonPath("ediStatus", is(testUser1.getEdiStatus().name())))
                .andExpect(jsonPath("ediReviewDate", is(closeTo(new BigDecimal(testUser1.getEdiReviewDate().toEpochSecond()), BigDecimal.valueOf(0.0)))))
                .andExpect(jsonPath("uid", is(testUser1.getUid())));
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
        user.setTitle(Mr);

        when(userService.findByEmail(user.getEmail())).thenReturn(serviceFailure(notFoundError(User.class, user.getEmail())));

        mockMvc.perform(get("/user/find-by-email/" + user.getEmail() + "/", "json")
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void userControllerShouldReturnEmptyListWhenNoUserIsFoundByEmail() throws Exception {

        String email = "testemail@email.com";

        when(userService.findByEmail(email)).thenReturn(serviceFailure(notFoundError(User.class, email)));

        mockMvc.perform(get("/user/find-by-email/" + email + "/", "json")
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void findActive() throws Exception {
        when(userService.findActive(null, PageRequest.of(0, 5, UserController.DEFAULT_USER_SORT))).thenReturn(serviceSuccess(new ManageUserPageResource()));
        mockMvc.perform(get(buildPaginationUri("/user/active", 0, 5, null, new LinkedMultiValueMap<>()))).andExpect(status().isOk());
    }

    @Test
    public void findInactive() throws Exception {
        when(userService.findInactive(null, PageRequest.of(0, 5, UserController.DEFAULT_USER_SORT))).thenReturn(serviceSuccess(new ManageUserPageResource()));
        mockMvc.perform(get(buildPaginationUri("/user/inactive", 0, 5, null, new LinkedMultiValueMap<>()))).andExpect(status().isOk());
    }

    @Test
    public void testCreateInternalUser() throws Exception {
        InternalUserRegistrationResource internalUserRegistrationResource = newInternalUserRegistrationResource()
                .withFirstName("First")
                .withLastName("Last")
                .withPassword("Passw0rd1357123")
                .build();

        when(registrationService.createUser(refEq(anUserCreationResource()
                .withFirstName(internalUserRegistrationResource.getFirstName())
                .withLastName(internalUserRegistrationResource.getLastName())
                .withPassword(internalUserRegistrationResource.getPassword())
                .withInviteHash("SomeHashString")
                .build()))).thenReturn(serviceSuccess(new UserResource()));
        mockMvc.perform(
                post("/user/internal/create/SomeHashString")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(internalUserRegistrationResource))
        ).andExpect(status().isCreated());
    }

    @Test
    public void agreeNewSiteTermsAndConditions() throws Exception {
        long userId = 1L;

        when(userService.agreeNewTermsAndConditions(1L)).thenReturn(serviceSuccess(newUserResource().build()));

        mockMvc.perform(post("/user/id/{userId}/agree-new-site-terms-and-conditions", userId))
                .andExpect(status().isOk());

        verify(userService, only()).agreeNewTermsAndConditions(userId);
    }

    @Test
    public void editInternalUser() throws Exception {

        EditUserResource editUserResource = new EditUserResource(1L, "First", "Last", Role.IFS_ADMINISTRATOR);
        when(registrationService.editInternalUser(any(), any())).thenReturn(serviceSuccess(newUserResource().build()));

        mockMvc.perform(post("/user/internal/edit")
                        .contentType(APPLICATION_JSON)
                        .content(toJson(editUserResource)))
                .andExpect(status().isOk());

        verify(registrationService).editInternalUser(any(), any());
    }

    @Test
    public void deactivateUser() throws Exception {
        when(registrationService.deactivateUser(123L)).thenReturn(serviceSuccess(newUserResource().build()));
        mockMvc.perform(post("/user/id/123/deactivate")).andExpect(status().isOk());
        verify(registrationService).deactivateUser(123L);
    }

    @Test
    public void reactivateUser() throws Exception {
        when(registrationService.activateUser(123L)).thenReturn(serviceSuccess(newUserResource().build()));
        mockMvc.perform(post("/user/id/123/reactivate")).andExpect(status().isOk());
        verify(registrationService).activateUser(123L);
    }

    @Test
    public void findExternalUsers() throws Exception {

        String searchString = "%aar%";
        SearchCategory searchCategory = SearchCategory.NAME;

        List<UserOrganisationResource> userOrganisationResources = newUserOrganisationResource().build(2);
        when(userService.findByProcessRolesAndSearchCriteria(EnumSet.of(Role.APPLICANT), searchString, searchCategory)).thenReturn(serviceSuccess(userOrganisationResources));

        mockMvc.perform(get("/user/find-external-users?searchString=" + searchString + "&searchCategory=" + searchCategory))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(userOrganisationResources)));

        verify(userService).findByProcessRolesAndSearchCriteria(EnumSet.of(Role.APPLICANT), searchString, searchCategory);
    }

    @Test
    public void grantRole() throws Exception {
        long userId = 1L;
        Role grantRole = Role.APPLICANT;

        when(userService.grantRole(new GrantRoleCommand(userId, grantRole))).thenReturn(serviceSuccess(newUserResource().build()));

        mockMvc.perform(post("/user/{userId}/grant/{role}", userId, grantRole.name()))
                .andExpect(status().isOk());

        verify(userService).grantRole(new GrantRoleCommand(userId, grantRole));
    }


    @Test
    public void updateUserEDIStatusCOMPLETE() throws Exception {

        silStatus.setEdiStatus(EDIStatus.COMPLETE);
        when(userAuthenticationService.getAuthenticatedUser(any())).thenReturn(user);

        when(userService.updateDetails(user)).thenReturn(serviceSuccess(user));
        mockMvc.perform(patch("/user/v1/edi").contentType(APPLICATION_JSON).content(toJson(silStatus)))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    public void updateUserEDIStatusINPROGRESS() throws Exception {
        silStatus.setEdiStatus(EDIStatus.INPROGRESS);
        when(userAuthenticationService.getAuthenticatedUser(any())).thenReturn(user);


        mockMvc.perform(patch("/user/v1/edi").contentType(APPLICATION_JSON).content(toJson(silStatus)))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    public void updateUserEDIStatusINCOMPLETE() throws Exception {
        silStatus.setEdiStatus(EDIStatus.INCOMPLETE);
        when(userAuthenticationService.getAuthenticatedUser(any())).thenReturn(user);

        when(userService.updateDetails(user)).thenReturn(serviceSuccess(user));
        mockMvc.perform(patch("/user/v1/edi").contentType(APPLICATION_JSON).content(toJson(silStatus)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void updateUserWithIncorrectAuthToken() throws Exception {
        silStatus.setEdiStatus(EDIStatus.COMPLETE);
        when(userAuthenticationService.getAuthenticatedUser(any())).thenReturn(null);

        when(userService.updateDetails(user)).thenReturn(serviceSuccess(user));
        mockMvc.perform(patch("/user/v1/edi").contentType(APPLICATION_JSON).content(toJson(silStatus)))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void updateUserWithNoReviewDate() throws Exception {
        silStatus.setEdiStatus(EDIStatus.COMPLETE);
        silStatus.setEdiReviewDate(null);
        when(userAuthenticationService.getAuthenticatedUser(any())).thenReturn(null);

        when(userService.updateDetails(user)).thenReturn(serviceSuccess(user));
        String errorMsg = mockMvc.perform(patch("/user/v1/edi").contentType(APPLICATION_JSON).content(toJson(silStatus)))
                .andDo(print())
                .andExpect(status().isBadRequest()).andReturn().getResponse().getContentAsString();
        assertTrue(StringUtils.contains(errorMsg, "EDI review date is required"));

    }

    @Test
    public void updateUserWithNoEDIStatus() throws Exception {
        silStatus.setEdiStatus(null);

        when(userAuthenticationService.getAuthenticatedUser(any())).thenReturn(null);

        when(userService.updateDetails(user)).thenReturn(serviceSuccess(user));
        String errorMsg = mockMvc.perform(patch("/user/v1/edi").contentType(APPLICATION_JSON).content(toJson(silStatus)))
                .andDo(print())
                .andExpect(status().isBadRequest()).andReturn().getResponse().getContentAsString();
        assertTrue(StringUtils.contains(errorMsg, "EDI Status is required"));

    }

    @Test
    public void verifyActivateApplicantWithoutDiversityEmailSuccess() throws Exception {
        ReflectionTestUtils.setField(registrationService, "isEdiUpdateEnabled", true);
        final String hash = "8eda60ad3441ee883cc95417e2abaa036c308dd9eb19468fcc8597fb4cb167c32a7e5daf5e237385";
        final Long userId = 1L;
        final Long appId = 1L;
        final Long compId = 1L;

        ObjectNode node = JsonNodeFactory.instance.objectNode();
        node.put("inviteId", 111L);
        final Token token = new Token(VERIFY_EMAIL_ADDRESS, User.class.getName(), userId, hash, now(), node);
        when(tokenService.getEmailToken(hash)).thenReturn(serviceSuccess((token)));

        ApplicationResource applicationResource = new ApplicationResource();
        applicationResource.setCompetition(compId);
        applicationResource.setId(appId);

        when(crmService.syncCrmContact(userId, appId, compId)).thenReturn(serviceSuccess());
        when(tokenService.handleApplicationExtraAttributes(any())).thenReturn(serviceSuccess((applicationResource)));
        when(registrationService.activateApplicantAndSendDiversitySurvey(anyLong(), anyLong())).thenReturn(serviceSuccess());

        mockMvc.perform(get("/user/" + URL_VERIFY_EMAIL + "/{hash}", hash)
                        .header("IFS_AUTH_TOKEN", "123abc"))
                .andExpect(status().isOk())
                .andExpect(content().string(""));

        verify(crmService).syncCrmContact(userId, appId, compId);
    }

    @Test
    public void createUserProfileStatus() throws Exception {
        User user = newUser().build();
        when(registrationService.createUserProfileStatus(user.getId())).thenReturn(serviceSuccess());
        mockMvc.perform(
                post("/user/user-profile-status/" + user.getId())).andExpect(status().isCreated());

        verify(registrationService).createUserProfileStatus(user.getId());
    }
}