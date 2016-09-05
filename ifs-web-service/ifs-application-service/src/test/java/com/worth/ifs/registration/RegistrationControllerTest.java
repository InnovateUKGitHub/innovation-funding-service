package com.worth.ifs.registration;

import com.worth.ifs.BaseControllerMockMVCTest;
import com.worth.ifs.commons.error.Error;
import com.worth.ifs.commons.error.exception.GeneralUnexpectedErrorException;
import com.worth.ifs.exception.ErrorControllerAdvice;
import com.worth.ifs.filter.CookieFlashMessageFilter;
import com.worth.ifs.user.resource.OrganisationResource;
import com.worth.ifs.user.resource.UserResource;
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
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

import static com.worth.ifs.commons.error.CommonErrors.notFoundError;
import static com.worth.ifs.commons.error.CommonFailureKeys.USERS_EMAIL_VERIFICATION_TOKEN_EXPIRED;
import static com.worth.ifs.commons.error.CommonFailureKeys.USERS_EMAIL_VERIFICATION_TOKEN_NOT_FOUND;
import static com.worth.ifs.commons.rest.RestResult.restFailure;
import static com.worth.ifs.commons.rest.RestResult.restSuccess;
import static com.worth.ifs.user.builder.OrganisationResourceBuilder.newOrganisationResource;
import static com.worth.ifs.user.builder.RoleResourceBuilder.newRoleResource;
import static com.worth.ifs.user.builder.UserResourceBuilder.newUserResource;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
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

        registrationController.setValidator(new LocalValidatorFactoryBean());

        when(userService.findUserByEmail(anyString())).thenReturn(restSuccess(new UserResource()));
        when(userService.findUserByEmailForAnonymousUserFlow(anyString())).thenReturn(restSuccess(new UserResource()));
        when(userService.createLeadApplicantForOrganisation(anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), anyLong())).thenReturn(restSuccess(new UserResource()));

        inviteHashCookie = new Cookie(AcceptInviteController.INVITE_HASH, INVITE_HASH);
        usedInviteHashCookie = new Cookie(AcceptInviteController.INVITE_HASH, ACCEPTED_INVITE_HASH);
        organisationCookie = new Cookie("organisationId", "1");
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
                .andExpect(view().name("registration-register"));
    }

    @Test
    public void onGetRequestRegistrationViewIsReturnedWithInviteEmail() throws Exception {
        OrganisationResource organisation = newOrganisationResource().withId(1L).withName("Organisation 1").build();
        when(organisationService.getOrganisationByIdForAnonymousUserFlow(1L)).thenReturn(organisation);

        mockMvc.perform(get("/registration/register")
                .cookie(inviteHashCookie, organisationCookie)
        )
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("registration-register"))
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
        when(userService.verifyEmail(eq(hash))).thenReturn(restSuccess());

        mockMvc.perform(get("/registration/verify-email/" + hash))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/registration/verified"));
    }

    @Test
    public void testVerifyEmailInvalid() throws Exception {
        final String hash = UUID.randomUUID().toString();
        when(userService.verifyEmail(eq(hash))).thenReturn(restFailure(new Error(USERS_EMAIL_VERIFICATION_TOKEN_NOT_FOUND)));

        mockMvc.perform(get("/registration/verify-email/" + hash))
                .andExpect(status().isAlreadyReported())
                .andExpect(view().name(ErrorControllerAdvice.URL_HASH_INVALID_TEMPLATE));
    }

    @Test
    public void testVerifyEmailExpired() throws Exception {
        final String hash = UUID.randomUUID().toString();
        when(userService.verifyEmail(eq(hash))).thenReturn(restFailure(new Error(USERS_EMAIL_VERIFICATION_TOKEN_EXPIRED)));

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
        when(userService.findUserByEmailForAnonymousUserFlow(email)).thenReturn(restSuccess(new UserResource()));

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
                .andExpect(model().attributeHasFieldErrors("registrationForm", "title"))
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
        OrganisationResource organisation = newOrganisationResource().withId(1L).withName("Organisation 1").build();
        when(organisationService.getOrganisationByIdForAnonymousUserFlow(1L)).thenReturn(organisation);

        String testEmailAddress = "tester@tester.com";
        when(userService.findUserByEmailForAnonymousUserFlow(anyString())).thenReturn(restFailure(notFoundError(UserResource.class, testEmailAddress)));

        Error error = Error.fieldError("password", "INVALID_PASSWORD", BAD_REQUEST.getReasonPhrase());
        when(userService.createLeadApplicantForOrganisationWithCompetitionId(anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), anyLong(), anyLong())).thenReturn(restFailure(error));

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
                .withTitle("Mr")
                .withPhoneNumber("0123456789")
                .withEmail("test@test.test")
                .withId(1L)
                .build();


        when(organisationService.getOrganisationByIdForAnonymousUserFlow(1L)).thenReturn(organisation);
        when(userService.createLeadApplicantForOrganisationWithCompetitionId(userResource.getFirstName(),
                userResource.getLastName(),
                userResource.getPassword(),
                userResource.getEmail(),
                userResource.getTitle(),
                userResource.getPhoneNumber(),
                1L,
                null)).thenReturn(restSuccess(userResource));
        when(userService.findUserByEmailForAnonymousUserFlow("test@test.test")).thenReturn(restFailure(notFoundError(UserResource.class, "test@test.test")));

        mockMvc.perform(post("/registration/register")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .cookie(organisationCookie)
                .param("email", userResource.getEmail())
                .param("password", userResource.getPassword())
                .param("retypedPassword", userResource.getPassword())
                .param("title", userResource.getTitle())
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
        OrganisationResource organisation = newOrganisationResource().withId(1L).withName("Organisation 1").build();

        UserResource userResource = newUserResource()
                .withPassword("password")
                .withFirstName("firstName")
                .withLastName("lastName")
                .withTitle("Mr")
                .withPhoneNumber("0123456789")
                .withEmail("invited@email.com")
                .withId(1L)
                .build();

        when(organisationService.getOrganisationByIdForAnonymousUserFlow(1L)).thenReturn(organisation);
        when(userService.createLeadApplicantForOrganisationWithCompetitionId(userResource.getFirstName(),
                userResource.getLastName(),
                userResource.getPassword(),
                userResource.getEmail(),
                userResource.getTitle(),
                userResource.getPhoneNumber(),
                1L,
                null)).thenReturn(restSuccess(userResource));
        when(userService.findUserByEmailForAnonymousUserFlow(eq("invited@email.com"))).thenReturn(restFailure(notFoundError(UserResource.class, "invited@email.com")));
        when(inviteRestService.acceptInvite(eq(INVITE_HASH),anyLong())).thenReturn(restSuccess());
        
        mockMvc.perform(post("/registration/register")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .cookie(inviteHashCookie, organisationCookie)
                .param("password", userResource.getPassword())
                .param("retypedPassword", userResource.getPassword())
                .param("title", userResource.getTitle())
                .param("firstName", userResource.getFirstName())
                .param("lastName", userResource.getLastName())
                .param("phoneNumber", userResource.getPhoneNumber())
                .param("termsAndConditions", "1")
        )
                .andExpect(view().name("redirect:/registration/success"))
                .andExpect(status().is3xxRedirection());

    }

    @Test
    public void correctOrganisationNameIsAddedToModel() throws Exception {
        OrganisationResource organisation = newOrganisationResource().withId(4L).withName("uniqueOrganisationName").build();

        when(organisationService.getOrganisationByIdForAnonymousUserFlow(4L)).thenReturn(organisation);
        
        organisationCookie = new Cookie("organisationId", "4");

        mockMvc.perform(post("/registration/register")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .cookie(organisationCookie)
        ).andExpect(model().attribute("organisationName", "uniqueOrganisationName"));
    }

    @Test
    public void gettingRegistrationPageWithLoggedInUserShouldResultInRedirectOnly() throws Exception {
        when(userAuthenticationService.getAuthenticatedUser(isA(HttpServletRequest.class))).thenReturn(
                newUserResource().withRolesGlobal(singletonList(
                        newRoleResource().withName("testrolename").withUrl("testrolename/dashboard").build()
                )).build()
        );

        mockMvc.perform(get("/registration/register")
        				.cookie(organisationCookie)
        		).andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/testrolename/dashboard"));

    }

    @Test
    public void postingRegistrationWithLoggedInUserShouldResultInRedirectOnly() throws Exception {
        when(userAuthenticationService.getAuthenticatedUser(isA(HttpServletRequest.class))).thenReturn(
                newUserResource().withRolesGlobal(singletonList(
                        newRoleResource().withName("testrolename").withUrl("testrolename/dashboard").build()
                )).build()
        );
        
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
                .withTitle("Mr")
                .withPhoneNumber("0123456789")
                .withEmail("test@test.test")
                .withId(1L)
                .build();

        Error error = new Error("errorname", BAD_REQUEST);

        when(organisationService.getOrganisationByIdForAnonymousUserFlow(1L)).thenReturn(organisation);
        when(userService.createLeadApplicantForOrganisationWithCompetitionId(userResource.getFirstName(),
                userResource.getLastName(),
                userResource.getPassword(),
                userResource.getEmail(),
                userResource.getTitle(),
                userResource.getPhoneNumber(),
                1L, null)).thenReturn(restFailure(error));
        
        mockMvc.perform(post("/registration/register")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .cookie(organisationCookie)
                        .param("email", userResource.getEmail())
                        .param("password", userResource.getPassword())
                        .param("retypedPassword", userResource.getPassword())
                        .param("title", userResource.getTitle())
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
