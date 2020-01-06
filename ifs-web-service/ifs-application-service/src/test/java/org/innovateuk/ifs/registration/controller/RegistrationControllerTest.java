package org.innovateuk.ifs.registration.controller;

import org.innovateuk.ifs.AbstractInviteMockMVCTest;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.exception.GeneralUnexpectedErrorException;
import org.innovateuk.ifs.commons.exception.InvalidURLException;
import org.innovateuk.ifs.commons.exception.RegistrationTokenExpiredException;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.exception.ErrorControllerAdvice;
import org.innovateuk.ifs.filter.CookieFlashMessageFilter;
import org.innovateuk.ifs.invite.constant.InviteStatus;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.project.invite.service.ProjectPartnerInviteRestService;
import org.innovateuk.ifs.registration.form.InviteAndIdCookie;
import org.innovateuk.ifs.registration.service.RegistrationCookieService;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.OrganisationRestService;
import org.innovateuk.ifs.user.service.UserRestService;
import org.innovateuk.ifs.user.service.UserService;
import org.innovateuk.ifs.util.EncryptedCookieService;
import org.innovateuk.ifs.util.NavigationUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;
import java.util.UUID;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.*;
import static org.innovateuk.ifs.project.invite.builder.SentProjectPartnerInviteResourceBuilder.newSentProjectPartnerInviteResource;
import static org.innovateuk.ifs.util.CookieTestUtil.encryptor;
import static org.innovateuk.ifs.util.CookieTestUtil.setupEncryptedCookieService;
import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.rest.RestResult.restFailure;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.organisation.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.user.resource.Title.Mr;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyBoolean;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(MockitoJUnitRunner.Silent.class)
@TestPropertySource(locations = "classpath:application.properties")
public class RegistrationControllerTest extends AbstractInviteMockMVCTest<RegistrationController> {

    @InjectMocks
    private RegistrationController registrationController;

    @Mock
    private CookieFlashMessageFilter cookieFlashMessageFilter;

    @Mock
    private RegistrationCookieService registrationCookieService;

    @Mock
    private Validator validator;

    @Mock
    private EncryptedCookieService cookieUtil;

    @Mock
    private UserService userService;

    @Mock
    private UserRestService userRestService;

    @Mock
    private OrganisationRestService organisationRestService;

    @Mock
    private ProjectPartnerInviteRestService projectPartnerInviteRestService;

    @Spy
    @SuppressWarnings("unused")
    private NavigationUtils navigationUtils;

    private Cookie inviteHashCookie;
    private Cookie usedInviteHashCookie;
    private Cookie organisationCookie;

    @Override
    protected RegistrationController supplyControllerUnderTest() {
        return new RegistrationController();
    }

    @Before
    public void setUpCommonExpectations() {

        MockitoAnnotations.initMocks(this);
        setupInvites();
        setupEncryptedCookieService(cookieUtil);

        registrationController.setValidator(new LocalValidatorFactoryBean());

        when(userService.findUserByEmail(anyString())).thenReturn(Optional.of(new UserResource()));
        when(userService.createUserForOrganisation(anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), anyLong(), anyBoolean())).thenReturn(serviceSuccess(new UserResource()));

        inviteHashCookie = new Cookie(RegistrationCookieService.INVITE_HASH, encryptor.encrypt(INVITE_HASH));
        usedInviteHashCookie = new Cookie(RegistrationCookieService.INVITE_HASH, encryptor.encrypt(ACCEPTED_INVITE_HASH));
        organisationCookie = new Cookie("organisationId", encryptor.encrypt("1"));

        when(registrationCookieService.getOrganisationIdCookieValue(any(HttpServletRequest.class))).thenReturn(Optional.of(1L));
        when(registrationCookieService.getInviteHashCookieValue(any(HttpServletRequest.class))).thenReturn(Optional.of(INVITE_HASH));
        when(registrationCookieService.getCompetitionIdCookieValue(any(HttpServletRequest.class))).thenReturn(Optional.of(1L));

        logoutCurrentUser();
    }

    @Test
    public void onGetRequestRegistrationViewIsReturned() throws Exception {
        OrganisationResource organisation = newOrganisationResource().withId(1L).withName("Organisation 1").build();
        when(organisationRestService.getOrganisationByIdForAnonymousUserFlow(1L)).thenReturn(restSuccess(organisation));

        mockMvc.perform(get("/registration/register")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .cookie(organisationCookie)
        )
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("registration/register"));
    }

    @Test
    public void onGetRequestRegistrationViewIsReturnedWithInviteEmail() throws Exception {
        OrganisationResource organisation = newOrganisationResource().withId(1L).withName("Organisation 1").build();
        when(organisationRestService.getOrganisationByIdForAnonymousUserFlow(1L)).thenReturn(restSuccess(organisation));

        mockMvc.perform(get("/registration/register")
                .cookie(inviteHashCookie, organisationCookie)
        )
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("registration/register"))
                .andExpect(model().attribute("invitee", true))
        ;
    }

    @Test
    public void onGetRequestRegistrationViewIsReturnedWithUsedInviteEmail() throws Exception {
        OrganisationResource organisation = newOrganisationResource().withId(1L).withName("Organisation 1").build();
        when(registrationCookieService.getInviteHashCookieValue(any(HttpServletRequest.class))).thenReturn(Optional.of(ACCEPTED_INVITE_HASH));
        when(organisationRestService.getOrganisationByIdForAnonymousUserFlow(1L)).thenReturn(restSuccess(organisation));

        mockMvc.perform(get("/registration/register")
                .cookie(usedInviteHashCookie, organisationCookie)
        )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
        verify(cookieFlashMessageFilter).setFlashMessage(isA(HttpServletResponse.class), eq("inviteAlreadyAccepted"));
    }

    @Test
    public void missingOrganisationGetParameterChangesViewWhenViewingForm() throws Exception {
        when(organisationRestService.getOrganisationByIdForAnonymousUserFlow(1L)).thenReturn(restFailure(notFoundError(OrganisationResource.class, 1L)));

        mockMvc.perform(get("/registration/register")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
        )
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/"));
    }

    @Test
    public void testSuccessUrl() throws Exception {
        mockMvc.perform(get("/registration/success").header("referer", "https://localhost/registration/register?organisationId=14"))
                .andExpect(status().isOk())
                .andExpect(view().name("registration/successful"))
        ;
    }

    @Test
    public void testVerifiedUrlNotFoundOnDirectAccess() throws Exception {
        mockMvc.perform(get("/registration/verified"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testVerifyEmail() throws Exception {
        final String hash = UUID.randomUUID().toString();

        when(userRestService.verifyEmail(eq(hash))).thenReturn(RestResult.restSuccess());
        mockMvc.perform(get("/registration/verify-email/" + hash))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/registration/verified"));
    }

    @Test
    public void testVerifyEmailInvalid() throws Exception {
        final String hash = UUID.randomUUID().toString();

        when(userRestService.verifyEmail(eq(hash))).thenThrow(new InvalidURLException(USERS_EMAIL_VERIFICATION_TOKEN_NOT_FOUND.getErrorKey(), null));

        mockMvc.perform(get("/registration/verify-email/" + hash))
                .andExpect(status().isAlreadyReported())
                .andExpect(view().name(ErrorControllerAdvice.URL_HASH_INVALID_TEMPLATE));
    }

    @Test
    public void testVerifyEmailExpired() throws Exception {
        final String hash = UUID.randomUUID().toString();
        when(userRestService.verifyEmail(eq(hash))).thenThrow(new RegistrationTokenExpiredException(USERS_EMAIL_VERIFICATION_TOKEN_EXPIRED.getErrorKey(), null));

        mockMvc.perform(get("/registration/verify-email/" + hash))
                .andExpect(status().isForbidden())
                .andExpect(view().name("registration-token-expired"));
    }

    @Test
    public void organisationGetParameterOfANonExistentOrganisationChangesViewWhenViewingForm() throws Exception {
        when(organisationRestService.getOrganisationByIdForAnonymousUserFlow(1L)).thenReturn(restFailure(notFoundError(OrganisationResource.class, 1L)));
        mockMvc.perform(get("/registration/register")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .cookie(organisationCookie)
        )
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/"));
    }

    @Test
    public void missingOrganisationGetParameterChangesViewWhenSubmittingForm() throws Exception {
        when(organisationRestService.getOrganisationByIdForAnonymousUserFlow(1L)).thenReturn(restFailure(notFoundError(OrganisationResource.class, 1L)));
        mockMvc.perform(post("/registration/register")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
        )
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/"));
    }

    @Test
    public void organisationGetParameterOfANonExistentOrganisationChangesViewWhenSubmittingForm() throws Exception {
        when(organisationRestService.getOrganisationByIdForAnonymousUserFlow(1L)).thenReturn(restFailure(notFoundError(OrganisationResource.class, 1L)));
        mockMvc.perform(post("/registration/register")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .cookie(organisationCookie)
        )
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/"));
    }

    @Test
    public void validButAlreadyExistingEmailInputShouldReturnErrorOnEmailField() throws Exception {
        OrganisationResource organisation = newOrganisationResource().withId(1L).withName("Organisation 1").build();

        String email = "alreadyexistingemail@test.test";

        when(organisationRestService.getOrganisationByIdForAnonymousUserFlow(1L)).thenReturn(restSuccess(organisation));
        when(userService.findUserByEmail(email)).thenReturn(Optional.of(new UserResource()));

        mockMvc.perform(post("/registration/register")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .cookie(organisationCookie)
                .param("email", email)
        )
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("registration/register"))
                .andExpect(model().attributeHasFieldErrors("registrationForm", "email"));
    }

    @Test
    public void emptyFormInputsShouldReturnError() throws Exception {
        OrganisationResource organisation = newOrganisationResource().withId(1L).withName("Organisation 1").build();
        when(organisationRestService.getOrganisationByIdForAnonymousUserFlow(1L)).thenReturn(restSuccess(organisation));
        when(registrationCookieService.getInviteHashCookieValue(any(HttpServletRequest.class))).thenReturn(Optional.empty());

        mockMvc.perform(post("/registration/register")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .cookie(organisationCookie)
                .param("email", "")
                .param("password", "")
                .param("title", "")
                .param("firstName", "")
                .param("lastName", "")
                .param("phoneNumber", "")
                .param("termsAndConditions", "")
        )
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("registration/register"))
                .andExpect(model().attributeHasFieldErrors("registrationForm", "password"))
                .andExpect(model().attributeHasFieldErrors("registrationForm", "email"))
                .andExpect(model().attributeHasFieldErrors("registrationForm", "firstName"))
                .andExpect(model().attributeHasFieldErrors("registrationForm", "lastName"))
                .andExpect(model().attributeHasFieldErrors("registrationForm", "phoneNumber"))
                .andExpect(model().attributeHasFieldErrors("registrationForm", "termsAndConditions"));
    }

    @Test
    public void invalidEmailFormatShouldReturnError() throws Exception {
        OrganisationResource organisation = newOrganisationResource().withId(1L).withName("Organisation 1").build();
        when(organisationRestService.getOrganisationByIdForAnonymousUserFlow(1L)).thenReturn(restSuccess(organisation));

        mockMvc.perform(post("/registration/register")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .cookie(organisationCookie)
                .param("email", "invalid email format")
        )
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("registration/register"))
                .andExpect(model().attributeHasFieldErrors("registrationForm", "email"));
    }

    @Test
    public void invalidCharactersInEmailShouldReturnError() throws Exception {
        OrganisationResource organisation = newOrganisationResource().withId(1L).withName("Organisation 1").build();
        when(registrationCookieService.getInviteHashCookieValue(any(HttpServletRequest.class))).thenReturn(Optional.empty());
        when(organisationRestService.getOrganisationByIdForAnonymousUserFlow(1L)).thenReturn(restSuccess(organisation));

        mockMvc.perform(post("/registration/register")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .cookie(organisationCookie)
                .param("email", "{a|b}@test.test")
        )
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("registration/register"))
                .andExpect(model().attributeHasFieldErrors("registrationForm", "email"));

        verifyNoMoreInteractions(userService);
    }

    @Test
    public void incorrectPasswordSizeShouldReturnError() throws Exception {
        logoutCurrentUser();
        OrganisationResource organisation = newOrganisationResource().withId(1L).withName("Organisation 1").build();
        when(organisationRestService.getOrganisationByIdForAnonymousUserFlow(1L)).thenReturn(restSuccess(organisation));
        when(registrationCookieService.getInviteHashCookieValue(any(HttpServletRequest.class))).thenReturn(Optional.empty());

        mockMvc.perform(post("/registration/register")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .cookie(organisationCookie)
                .param("password", "12345")
        )
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("registration/register"))
                .andExpect(model().attributeHasFieldErrors("registrationForm", "password"));
    }


    @Test
    public void tooWeakPasswordSizeShouldReturnError() throws Exception {
        logoutCurrentUser();
        OrganisationResource organisation = newOrganisationResource().withId(1L).withName("Organisation 1").build();
        when(organisationRestService.getOrganisationByIdForAnonymousUserFlow(1L)).thenReturn(restSuccess(organisation));

        String testEmailAddress = "tester@tester.com";
        when(userService.findUserByEmail(anyString())).thenReturn(Optional.empty());

        Error error = Error.fieldError("password", "INVALID_PASSWORD", BAD_REQUEST.getReasonPhrase());
        when(userService.createLeadApplicantForOrganisationWithCompetitionId(anyString(), anyString(), anyString(), anyString(), nullable(String.class), anyString(), anyLong(), anyLong(), nullable(Boolean.class))).thenReturn(serviceFailure(error));

        mockMvc.perform(post("/registration/register")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .cookie(organisationCookie)
                .param("email", testEmailAddress)
                .param("title", "Mr")
                .param("firstName", "Adam")
                .param("lastName", "Taylor")
                .param("phoneNumber", "012345678")
                .param("termsAndConditions", "1")
                .param("password", "Password123")
        )
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("registration/register"))
                .andExpect(model().attributeHasFieldErrors("registrationForm", "password"));
    }

    @Test
    public void uncheckedTermsAndConditionsCheckboxShouldReturnError() throws Exception {
        OrganisationResource organisation = newOrganisationResource().withId(1L).withName("Organisation 1").build();
        when(registrationCookieService.getInviteHashCookieValue(any(HttpServletRequest.class))).thenReturn(Optional.empty());
        when(organisationRestService.getOrganisationByIdForAnonymousUserFlow(1L)).thenReturn(restSuccess(organisation));

        mockMvc.perform(post("/registration/register")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .cookie(organisationCookie)
        )
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("registration/register"))
                .andExpect(model().attributeHasFieldErrors("registrationForm", "termsAndConditions"));
    }

    @Test
    public void validRegisterPost() throws Exception {
        OrganisationResource organisation = newOrganisationResource().withId(1L).withName("Organisation 1").build();
        when(registrationCookieService.getInviteHashCookieValue(any(HttpServletRequest.class))).thenReturn(Optional.empty());

        UserResource userResource = newUserResource()
                .withPassword("password123")
                .withFirstName("firstName")
                .withLastName("lastName")
                .withTitle(Mr)
                .withPhoneNumber("0123456789")
                .withEmail("test@test.test")
                .withId(1L)
                .build();

        when(organisationRestService.getOrganisationByIdForAnonymousUserFlow(1L)).thenReturn(restSuccess(organisation));
        when(userService.createLeadApplicantForOrganisationWithCompetitionId(eq(userResource.getFirstName()),
                eq(userResource.getLastName()),
                eq(userResource.getPassword()),
                eq(userResource.getEmail()),
                nullable(String.class),
                eq(userResource.getPhoneNumber()),
                eq(1L),
                eq(1L),
                nullable(Boolean.class))).thenReturn(serviceSuccess(userResource));
        when(userService.findUserByEmail("test@test.test")).thenReturn(Optional.empty());

        mockMvc.perform(post("/registration/register")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .cookie(organisationCookie)
                .param("email", userResource.getEmail())
                .param("password", userResource.getPassword())
                .param("title", userResource.getTitle().toString())
                .param("firstName", userResource.getFirstName())
                .param("lastName", userResource.getLastName())
                .param("phoneNumber", userResource.getPhoneNumber())
                .param("termsAndConditions", "1")
        )
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/registration/success"));

    }

    @Test
    public void validRegisterPostWithInvite() throws Exception {
        logoutCurrentUser();
        OrganisationResource organisation = newOrganisationResource().withId(1L).withName("Organisation 1").build();

        UserResource userResource = newUserResource()
                .withPassword("password")
                .withFirstName("firstName")
                .withLastName("lastName")
                .withTitle(Mr)
                .withPhoneNumber("0123456789")
                .withEmail("invited@email.com")
                .withId(1L)
                .build();

        when(organisationRestService.getOrganisationByIdForAnonymousUserFlow(1L)).thenReturn(restSuccess(organisation));
        when(userService.createLeadApplicantForOrganisationWithCompetitionId(eq(userResource.getFirstName()),
                eq(userResource.getLastName()),
                eq(userResource.getPassword()),
                eq(userResource.getEmail()),
                nullable(String.class),
                eq(userResource.getPhoneNumber()),
                eq(1L),
                eq(1L),
                nullable(Boolean.class))).thenReturn(serviceSuccess(userResource));
        when(userService.findUserByEmail(eq("invited@email.com"))).thenReturn(Optional.empty());
        when(inviteRestService.acceptInvite(eq(INVITE_HASH), anyLong(), anyLong())).thenReturn(restSuccess());

        mockMvc.perform(post("/registration/register")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .cookie(inviteHashCookie, organisationCookie)
                .param("password", userResource.getPassword())
                .param("title", userResource.getTitle().toString())
                .param("firstName", userResource.getFirstName())
                .param("lastName", userResource.getLastName())
                .param("phoneNumber", userResource.getPhoneNumber())
                .param("termsAndConditions", "1")
        )
                .andExpect(view().name("redirect:/registration/success"))
                .andExpect(status().is3xxRedirection());

    }

    @Test
    public void unsuccessfulPageReturnedForDuplicateOrganisationRegister() throws Exception {
        OrganisationResource organisation = newOrganisationResource().withId(1L).withName("Organisation 1").build();

        UserResource userResource = newUserResource()
                .withPassword("password")
                .withFirstName("firstName")
                .withLastName("lastName")
                .withTitle(Mr)
                .withPhoneNumber("0123456789")
                .withEmail("invited@email.com")
                .withId(1L)
                .build();

        when(organisationRestService.getOrganisationByIdForAnonymousUserFlow(1L)).thenReturn(restSuccess(organisation));
        when(userService.createLeadApplicantForOrganisationWithCompetitionId(eq(userResource.getFirstName()),
                eq(userResource.getLastName()),
                eq(userResource.getPassword()),
                eq(userResource.getEmail()),
                nullable(String.class),
                eq(userResource.getPhoneNumber()),
                eq(1L),
                eq(1L),
                nullable(Boolean.class))).thenReturn(serviceSuccess(userResource));

        InviteAndIdCookie projectInviteCookie = new InviteAndIdCookie(1L, "hashy");

        when(userService.findUserByEmail(eq("invited@email.com"))).thenReturn(Optional.empty());
        when(registrationCookieService.getInviteHashCookieValue(any(HttpServletRequest.class))).thenReturn(Optional.empty());
        when(registrationCookieService.getProjectInviteHashCookieValue(any(HttpServletRequest.class))).thenReturn(Optional.of(projectInviteCookie));
        when(projectPartnerInviteRestService.getInviteByHash(projectInviteCookie.getId(), projectInviteCookie.getHash())).thenReturn(restSuccess(newSentProjectPartnerInviteResource().withEmail(userResource.getEmail()).withStatus(InviteStatus.SENT).build()));
        when(projectPartnerInviteRestService.acceptInvite(anyLong(), anyLong(), anyLong())).thenReturn(restFailure(ORGANISATION_ALREADY_EXISTS_FOR_PROJECT));

        mockMvc.perform(post("/registration/register")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .cookie(inviteHashCookie, organisationCookie)
                .param("email", userResource.getEmail())
                .param("password", userResource.getPassword())
                .param("title", userResource.getTitle().toString())
                .param("firstName", userResource.getFirstName())
                .param("lastName", userResource.getLastName())
                .param("phoneNumber", userResource.getPhoneNumber())
                .param("termsAndConditions", "1")
        )
                .andExpect(view().name("redirect:/registration/duplicate-project-organisation"))
                .andExpect(status().is3xxRedirection());

        InOrder inOrder = inOrder(registrationCookieService, projectPartnerInviteRestService, userService );

        inOrder.verify(registrationCookieService).getInviteHashCookieValue(any(HttpServletRequest.class));
        inOrder.verify(projectPartnerInviteRestService).getInviteByHash(projectInviteCookie.getId(), projectInviteCookie.getHash());
        inOrder.verify(userService).findUserByEmail(eq(userResource.getEmail()));
        inOrder.verify(userService).createLeadApplicantForOrganisationWithCompetitionId(eq(userResource.getFirstName()),
                eq(userResource.getLastName()),
                eq(userResource.getPassword()),
                eq(userResource.getEmail()),
                nullable(String.class),
                eq(userResource.getPhoneNumber()),
                eq(1L),
                eq(1L),
                nullable(Boolean.class));
        inOrder.verify(registrationCookieService).getProjectInviteHashCookieValue(any(HttpServletRequest.class));
        inOrder.verify(projectPartnerInviteRestService).getInviteByHash(projectInviteCookie.getId(), projectInviteCookie.getHash());
        inOrder.verify(registrationCookieService).getOrganisationIdCookieValue(any(HttpServletRequest.class));
        inOrder.verify(projectPartnerInviteRestService).acceptInvite(anyLong(), anyLong(), anyLong());
        inOrder.verify(registrationCookieService).deleteCompetitionIdCookie(any(HttpServletResponse.class));
        inOrder.verify(registrationCookieService).deleteOrganisationIdCookie(any(HttpServletResponse.class));
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void correctOrganisationNameIsAddedToModel() throws Exception {
        logoutCurrentUser();
        Long organisationId = 4L;

        OrganisationResource organisation = newOrganisationResource().withId(organisationId).withName("uniqueOrganisationName").build();
        when(organisationRestService.getOrganisationByIdForAnonymousUserFlow(organisationId)).thenReturn(restSuccess(organisation));
        when(registrationCookieService.getInviteHashCookieValue(any(HttpServletRequest.class))).thenReturn(Optional.of(INVITE_HASH));
        when(registrationCookieService.getOrganisationIdCookieValue(any(HttpServletRequest.class))).thenReturn(Optional.of(organisationId));

        organisationCookie = new Cookie("organisationId", encryptor.encrypt("4"));

        mockMvc.perform(post("/registration/register")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .cookie(organisationCookie)
        ).andExpect(model().attribute("organisationName", "uniqueOrganisationName"));
    }

    @Test
    public void gettingRegistrationPageWithLoggedInUserShouldResultInRedirectOnly() throws Exception {

        setLoggedInUser(newUserResource().withRolesGlobal(singletonList(Role.APPLICANT)).build());

        mockMvc.perform(get("/registration/register")
                .cookie(organisationCookie)
        ).andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:http://localhost:80"));

    }

    @Test
    public void postingRegistrationWithLoggedInUserShouldResultInRedirectOnly() throws Exception {
        setLoggedInUser(newUserResource().withRolesGlobal(singletonList(Role.APPLICANT)).build());

        mockMvc.perform(post("/registration/register")
                .cookie(organisationCookie)
        ).andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:http://localhost:80"));
    }

    @Test
    public void errorsReturnedInEnvelopeAreAddedToTheModel() throws Exception {
        OrganisationResource organisation = newOrganisationResource().withId(1L).withName("Organisation 1").build();
        UserResource userResource = newUserResource()
                .withPassword("password")
                .withFirstName("firstName")
                .withLastName("lastName")
                .withTitle(Mr)
                .withPhoneNumber("0123456789")
                .withEmail("test@test.test")
                .withId(1L)
                .withAllowMarketingEmails(true)
                .build();

        Error error = new Error("errorname", BAD_REQUEST);

        when(organisationRestService.getOrganisationByIdForAnonymousUserFlow(1L)).thenReturn(restSuccess(organisation));
        when(userService.createLeadApplicantForOrganisationWithCompetitionId(userResource.getFirstName(),
                userResource.getLastName(),
                userResource.getPassword(),
                userResource.getEmail(),
                userResource.getTitle() != null ? userResource.getTitle().toString() : null,
                userResource.getPhoneNumber(),
                1L, 1L, userResource.getAllowMarketingEmails())).thenReturn(serviceFailure(error));

        mockMvc.perform(post("/registration/register")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .cookie(organisationCookie)
                .param("email", userResource.getEmail())
                .param("password", userResource.getPassword())
                .param("title", userResource.getTitle().toString())
                .param("firstName", userResource.getFirstName())
                .param("lastName", userResource.getLastName())
                .param("phoneNumber", userResource.getPhoneNumber())
                .param("termsAndConditions", "1")
        )
                .andExpect(model().hasErrors());
    }

    @Test
    public void resendEmailVerification() throws Exception {
        mockMvc.perform(get("/registration/resend-email-verification"))
                .andExpect(status().isOk())
                .andExpect(view().name("registration/resend-email-verification"));
    }

    @Test
    public void validResendEmailVerificationForm() throws Exception {
        mockMvc.perform(post("/registration/resend-email-verification")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("email", "a.b@test.test"))
                .andExpect(status().isOk())
                .andExpect(view().name("registration/resend-email-verification-send"));
    }

    @Test
    public void invalidResendEmailVerificationFormShouldReturnError() throws Exception {
        mockMvc.perform(post("/registration/resend-email-verification")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("email", "{a|b}@test.test"))
                .andExpect(status().isOk())
                .andExpect(view().name("registration/resend-email-verification"))
                .andExpect(model().attributeHasFieldErrors("resendEmailVerificationForm", "email"));
    }

    @Test
    public void validResendEmailVerificationFormWithOtherExceptionShouldByHandled() throws Exception {
        final String emailAddress = "a.b@test.test";

        doThrow(new GeneralUnexpectedErrorException("Other error occurred", asList())).when(userService).resendEmailVerificationNotification(emailAddress);

        mockMvc.perform(post("/registration/resend-email-verification")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("email", emailAddress))
                .andExpect(status().isInternalServerError())
                .andExpect(view().name("error"));
    }
}