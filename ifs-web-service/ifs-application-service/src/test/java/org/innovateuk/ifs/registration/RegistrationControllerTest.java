package org.innovateuk.ifs.registration;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.error.exception.GeneralUnexpectedErrorException;
import org.innovateuk.ifs.commons.error.exception.InvalidURLException;
import org.innovateuk.ifs.commons.error.exception.RegistrationTokenExpiredException;
import org.innovateuk.ifs.exception.ErrorControllerAdvice;
import org.innovateuk.ifs.filter.CookieFlashMessageFilter;
import org.innovateuk.ifs.invite.service.EthnicityRestService;
import org.innovateuk.ifs.user.builder.EthnicityResourceBuilder;
import org.innovateuk.ifs.user.resource.Disability;
import org.innovateuk.ifs.user.resource.Gender;
import org.innovateuk.ifs.user.resource.OrganisationResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;
import java.util.UUID;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.USERS_EMAIL_VERIFICATION_TOKEN_EXPIRED;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.USERS_EMAIL_VERIFICATION_TOKEN_NOT_FOUND;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.user.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.innovateuk.ifs.user.builder.RoleResourceBuilder.newRoleResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.user.resource.Title.Mr;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.anyBoolean;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(MockitoJUnitRunner.class)
@TestPropertySource(locations = "classpath:application.properties")
public class RegistrationControllerTest extends BaseControllerMockMVCTest<RegistrationController> {

    @InjectMocks
    private RegistrationController registrationController;

    @Mock
    private CookieFlashMessageFilter cookieFlashMessageFilter;

    @Mock
    private EthnicityRestService ethnicityRestService;

    @Mock
    private Validator validator;

    private Cookie inviteHashCookie;
    private Cookie usedInviteHashCookie;
    private Cookie organisationCookie;

    @Override
    protected RegistrationController supplyControllerUnderTest() {
        return new RegistrationController();
    }

    @Before
    public void setUp() {
        super.setUp();

        MockitoAnnotations.initMocks(this);

        setupUserRoles();
        setupInvites();
        setupCookieUtil();

        registrationController.setValidator(new LocalValidatorFactoryBean());

        when(userService.findUserByEmail(anyString())).thenReturn(Optional.of(new UserResource()));
        when(userService.createUserForOrganisation(anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), anyLong(), anyBoolean())).thenReturn(serviceSuccess(new UserResource()));
        when(ethnicityRestService.findAllActive()).thenReturn(restSuccess(asList(EthnicityResourceBuilder.newEthnicityResource().withId(1L).withDescription("Nerdy People").withName("IFS programmer").withPriority(1).build())));

        inviteHashCookie = new Cookie(AbstractAcceptInviteController.INVITE_HASH, encryptor.encrypt(INVITE_HASH));
        usedInviteHashCookie = new Cookie(AbstractAcceptInviteController.INVITE_HASH, encryptor.encrypt(ACCEPTED_INVITE_HASH));
        organisationCookie = new Cookie("organisationId", encryptor.encrypt("1"));
        logoutCurrentUser();
    }

    @Test
    public void onGetRequestRegistrationViewIsReturned() throws Exception {
        OrganisationResource organisation = newOrganisationResource().withId(1L).withName("Organisation 1").build();
        when(organisationService.getOrganisationByIdForAnonymousUserFlow(1L)).thenReturn(organisation);

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
        when(organisationService.getOrganisationByIdForAnonymousUserFlow(1L)).thenReturn(organisation);

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
        when(organisationService.getOrganisationByIdForAnonymousUserFlow(1L)).thenReturn(organisation);

        mockMvc.perform(get("/registration/register")
                .cookie(usedInviteHashCookie, organisationCookie)
        )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
        verify(cookieFlashMessageFilter).setFlashMessage(isA(HttpServletResponse.class), eq("inviteAlreadyAccepted"));
    }

    @Test
    public void missingOrganisationGetParameterChangesViewWhenViewingForm() throws Exception {
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

        mockMvc.perform(get("/registration/verify-email/" + hash))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/registration/verified"));
    }

    @Test
    public void testVerifyEmailInvalid() throws Exception {
        final String hash = UUID.randomUUID().toString();

        when(userService.verifyEmail(eq(hash))).thenThrow(new InvalidURLException(USERS_EMAIL_VERIFICATION_TOKEN_NOT_FOUND.getErrorKey(), null));

        mockMvc.perform(get("/registration/verify-email/" + hash))
                .andExpect(status().isAlreadyReported())
                .andExpect(view().name(ErrorControllerAdvice.URL_HASH_INVALID_TEMPLATE));
    }

    @Test
    public void testVerifyEmailExpired() throws Exception {
        final String hash = UUID.randomUUID().toString();
        when(userService.verifyEmail(eq(hash))).thenThrow(new RegistrationTokenExpiredException(USERS_EMAIL_VERIFICATION_TOKEN_EXPIRED.getErrorKey(), null));

        mockMvc.perform(get("/registration/verify-email/" + hash))
                .andExpect(status().isForbidden())
                .andExpect(view().name("registration-token-expired"));
    }

    @Test
    public void organisationGetParameterOfANonExistentOrganisationChangesViewWhenViewingForm() throws Exception {

        mockMvc.perform(get("/registration/register")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .cookie(organisationCookie)
        )
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/"));
    }

    @Test
    public void missingOrganisationGetParameterChangesViewWhenSubmittingForm() throws Exception {
        mockMvc.perform(post("/registration/register")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
        )
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/"));
    }

    @Test
    public void organisationGetParameterOfANonExistentOrganisationChangesViewWhenSubmittingForm() throws Exception {
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

        when(organisationService.getOrganisationByIdForAnonymousUserFlow(1L)).thenReturn(organisation);
        when(userService.findUserByEmail(email)).thenReturn(Optional.of(new UserResource()));

        mockMvc.perform(post("/registration/register")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .cookie(organisationCookie)
                .param("email", email)
        )
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("registration-register"))
                .andExpect(model().attributeHasFieldErrors("registrationForm", "email"));
    }

    @Test
    public void emptyFormInputsShouldReturnError() throws Exception {
        OrganisationResource organisation = newOrganisationResource().withId(1L).withName("Organisation 1").build();
        when(organisationService.getOrganisationByIdForAnonymousUserFlow(1L)).thenReturn(organisation);

        mockMvc.perform(post("/registration/register")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .cookie(organisationCookie)
                .param("email", "")
                .param("password", "")
                .param("retypedPassword", "")
                .param("title", "")
                .param("firstName", "")
                .param("lastName", "")
                .param("phoneNumber", "")
                .param("termsAndConditions", "")
        )
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("registration-register"))
                .andExpect(model().attributeHasFieldErrors("registrationForm", "password"))
                .andExpect(model().attributeHasFieldErrors("registrationForm", "email"))
                .andExpect(model().attributeHasFieldErrors("registrationForm", "retypedPassword"))
                .andExpect(model().attributeHasFieldErrors("registrationForm", "firstName"))
                .andExpect(model().attributeHasFieldErrors("registrationForm", "lastName"))
                .andExpect(model().attributeHasFieldErrors("registrationForm", "phoneNumber"))
                .andExpect(model().attributeHasFieldErrors("registrationForm", "termsAndConditions"));
    }

    @Test
    public void invalidEmailFormatShouldReturnError() throws Exception {
        OrganisationResource organisation = newOrganisationResource().withId(1L).withName("Organisation 1").build();
        when(organisationService.getOrganisationByIdForAnonymousUserFlow(1L)).thenReturn(organisation);

        mockMvc.perform(post("/registration/register")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .cookie(organisationCookie)
                .param("email", "invalid email format")
        )
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("registration-register"))
                .andExpect(model().attributeHasFieldErrors("registrationForm", "email"));
    }

    @Test
    public void invalidCharactersInEmailShouldReturnError() throws Exception {
        OrganisationResource organisation = newOrganisationResource().withId(1L).withName("Organisation 1").build();
        when(organisationService.getOrganisationByIdForAnonymousUserFlow(1L)).thenReturn(organisation);

        mockMvc.perform(post("/registration/register")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .cookie(organisationCookie)
                .param("email", "{a|b}@test.test")
        )
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("registration-register"))
                .andExpect(model().attributeHasFieldErrors("registrationForm", "email"));

        verifyNoMoreInteractions(userService);
    }

    @Test
    public void incorrectPasswordSizeShouldReturnError() throws Exception {
        logoutCurrentUser();
        OrganisationResource organisation = newOrganisationResource().withId(1L).withName("Organisation 1").build();
        when(organisationService.getOrganisationByIdForAnonymousUserFlow(1L)).thenReturn(organisation);

        mockMvc.perform(post("/registration/register")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .cookie(organisationCookie)
                .param("password", "12345")
                .param("retypedPassword", "123456789012345678901234567890123")
        )
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("registration-register"))
                .andExpect(model().attributeHasFieldErrors("registrationForm", "password"))
                .andExpect(model().attributeHasFieldErrors("registrationForm", "retypedPassword"));
    }


    @Test
    public void tooWeakPasswordSizeShouldReturnError() throws Exception {
        logoutCurrentUser();
        OrganisationResource organisation = newOrganisationResource().withId(1L).withName("Organisation 1").build();
        when(organisationService.getOrganisationByIdForAnonymousUserFlow(1L)).thenReturn(organisation);

        String testEmailAddress = "tester@tester.com";
        when(userService.findUserByEmail(anyString())).thenReturn(Optional.empty());

        Error error = Error.fieldError("password", "INVALID_PASSWORD", BAD_REQUEST.getReasonPhrase());
        when(userService.createLeadApplicantForOrganisationWithCompetitionId(anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), anyLong(), anyString(), anyLong(), anyLong(), anyBoolean())).thenReturn(serviceFailure(error));

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
                .param("retypedPassword", "Password123")
                .param("gender", Gender.MALE.toString())
                .param("ethnicity", "3")
                .param("disability", Disability.NO.toString())
        )
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("registration-register"))
                .andExpect(model().attributeHasFieldErrors("registrationForm", "password"));
    }

    @Test
    public void unmatchedPasswordAndRetypePasswordShouldReturnError() throws Exception {
        OrganisationResource organisation = newOrganisationResource().withId(1L).withName("Organisation 1").build();
        when(organisationService.getOrganisationByIdForAnonymousUserFlow(1L)).thenReturn(organisation);

        mockMvc.perform(post("/registration/register")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .cookie(organisationCookie)
                .param("password", "12345678")
                .param("retypedPassword", "123456789")
        )
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("registration-register"))
                .andExpect(model().attributeHasFieldErrors("registrationForm", "retypedPassword"));
    }

    @Test
    public void uncheckedTermsAndConditionsCheckboxShouldReturnError() throws Exception {
        OrganisationResource organisation = newOrganisationResource().withId(1L).withName("Organisation 1").build();
        when(organisationService.getOrganisationByIdForAnonymousUserFlow(1L)).thenReturn(organisation);

        mockMvc.perform(post("/registration/register")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .cookie(organisationCookie)
        )
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("registration-register"))
                .andExpect(model().attributeHasFieldErrors("registrationForm", "termsAndConditions"));
    }

    @Test
    public void validRegisterPost() throws Exception {
        OrganisationResource organisation = newOrganisationResource().withId(1L).withName("Organisation 1").build();

        UserResource userResource = newUserResource()
                .withPassword("password123")
                .withFirstName("firstName")
                .withLastName("lastName")
                .withTitle(Mr)
                .withPhoneNumber("0123456789")
                .withEmail("test@test.test")
                .withId(1L)
                .withEthnicity(2L)
                .withDisability(Disability.NO)
                .withGender(Gender.FEMALE)
                .build();


        when(organisationService.getOrganisationByIdForAnonymousUserFlow(1L)).thenReturn(organisation);
        when(userService.createLeadApplicantForOrganisationWithCompetitionId(eq(userResource.getFirstName()),
                eq(userResource.getLastName()),
                eq(userResource.getPassword()),
                eq(userResource.getEmail()),
                anyString(),
                eq(userResource.getPhoneNumber()),
                anyString(),
                anyLong(),
                anyString(),
                eq(1L),
                eq(null),
                anyBoolean())).thenReturn(serviceSuccess(userResource));
        when(userService.findUserByEmail("test@test.test")).thenReturn(Optional.empty());

        mockMvc.perform(post("/registration/register")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .cookie(organisationCookie)
                .param("email", userResource.getEmail())
                .param("password", userResource.getPassword())
                .param("retypedPassword", userResource.getPassword())
                .param("title", userResource.getTitle().toString())
                .param("firstName", userResource.getFirstName())
                .param("lastName", userResource.getLastName())
                .param("phoneNumber", userResource.getPhoneNumber())
                .param("termsAndConditions", "1")
                .param("gender", userResource.getGender().toString())
                .param("ethnicity", userResource.getEthnicity().toString())
                .param("disability", userResource.getDisability().toString())
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
                .withGender(Gender.MALE)
                .withDisability(Disability.NOT_STATED)
                .withEthnicity(2L)
                .build();

        when(organisationService.getOrganisationByIdForAnonymousUserFlow(1L)).thenReturn(organisation);
        when(userService.createLeadApplicantForOrganisationWithCompetitionId(eq(userResource.getFirstName()),
                eq(userResource.getLastName()),
                eq(userResource.getPassword()),
                eq(userResource.getEmail()),
                anyString(),
                eq(userResource.getPhoneNumber()),
                anyString(),
                anyLong(),
                anyString(),
                eq(1L),
                eq(null),
                anyBoolean())).thenReturn(serviceSuccess(userResource));
        when(userService.findUserByEmail(eq("invited@email.com"))).thenReturn(Optional.empty());
        when(inviteRestService.acceptInvite(eq(INVITE_HASH), anyLong())).thenReturn(restSuccess());

        mockMvc.perform(post("/registration/register")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .cookie(inviteHashCookie, organisationCookie)
                .param("password", userResource.getPassword())
                .param("retypedPassword", userResource.getPassword())
                .param("title", userResource.getTitle().toString())
                .param("firstName", userResource.getFirstName())
                .param("lastName", userResource.getLastName())
                .param("phoneNumber", userResource.getPhoneNumber())
                .param("termsAndConditions", "1")
                .param("ethnicity", userResource.getEthnicity().toString())
                .param("disability", userResource.getDisability().toString())
                .param("gender", userResource.getGender().toString())
        )
                .andExpect(view().name("redirect:/registration/success"))
                .andExpect(status().is3xxRedirection());

    }

    @Test
    public void correctOrganisationNameIsAddedToModel() throws Exception {
        logoutCurrentUser();
        OrganisationResource organisation = newOrganisationResource().withId(4L).withName("uniqueOrganisationName").build();

        when(organisationService.getOrganisationByIdForAnonymousUserFlow(4L)).thenReturn(organisation);

        organisationCookie = new Cookie("organisationId", encryptor.encrypt("4"));

        mockMvc.perform(post("/registration/register")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .cookie(organisationCookie)
        ).andExpect(model().attribute("organisationName", "uniqueOrganisationName"));
    }

    @Test
    public void gettingRegistrationPageWithLoggedInUserShouldResultInRedirectOnly() throws Exception {


        setLoggedInUser(newUserResource().withRolesGlobal(singletonList(
                newRoleResource().withName("testrolename").withUrl("testrolename/dashboard").build()
        )).build());

        mockMvc.perform(get("/registration/register")
                .cookie(organisationCookie)
        ).andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/testrolename/dashboard"));

    }

    @Test
    public void postingRegistrationWithLoggedInUserShouldResultInRedirectOnly() throws Exception {
        setLoggedInUser(
                newUserResource().withRolesGlobal(singletonList(
                        newRoleResource().withName("testrolename").withUrl("testrolename/dashboard").build()
                )).build());

        mockMvc.perform(post("/registration/register")
                .cookie(organisationCookie)
        ).andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/testrolename/dashboard"));
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

        when(organisationService.getOrganisationByIdForAnonymousUserFlow(1L)).thenReturn(organisation);
        when(userService.createLeadApplicantForOrganisationWithCompetitionId(userResource.getFirstName(),
                userResource.getLastName(),
                userResource.getPassword(),
                userResource.getEmail(),
                userResource.getTitle() != null ? userResource.getTitle().toString() : null,
                userResource.getPhoneNumber(),
                userResource.getGender() != null ? userResource.getGender().toString() : null,
                userResource.getEthnicity(),
                userResource.getDisability() != null ? userResource.getDisability().toString() : null,
                1L, null, userResource.getAllowMarketingEmails())).thenReturn(serviceFailure(error));

        mockMvc.perform(post("/registration/register")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .cookie(organisationCookie)
                .param("email", userResource.getEmail())
                .param("password", userResource.getPassword())
                .param("retypedPassword", userResource.getPassword())
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
